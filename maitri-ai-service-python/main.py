from flask import Flask, jsonify, request, g, Response, stream_with_context
from flask_cors import CORS
import sqlite3
import requests
import json

# --- Configuration ---
DATABASE = 'maitri.db'
app = Flask(__name__)

# Ollama configuration
OLLAMA_API_URL = "http://localhost:11434/api/generate"
OLLAMA_MODEL = "mistral" # Ensure this model is pulled locally

# Configure CORS to allow Next.js (3000) and Spring Boot (8080)
CORS(app, resources={r"/api/*": {"origins": ["http://localhost:3000", "http://localhost:8080"]},
                     r"/analyze": {"origins": ["http://localhost:8080"]}})

# --- Database Functions ---

def get_db():
    """Establishes a database connection or returns the existing one."""
    if 'db' not in g:
        g.db = sqlite3.connect(DATABASE)
        g.db.row_factory = sqlite3.Row
    return g.db

@app.teardown_appcontext
def close_db(e=None):
    """Closes the database connection."""
    db = g.pop('db', None)
    if db is not None:
        db.close()

def init_db():
    """Initializes the database schema and adds test data."""
    with app.app_context():
        db = get_db()
        cursor = db.cursor()

        # Astronauts Table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS astronauts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                mission_role TEXT NOT NULL
            );
        ''')
        # Health Logs Table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS health_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                astronaut_id INTEGER,
                log_type TEXT NOT NULL,
                data TEXT NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (astronaut_id) REFERENCES astronauts (id)
            );
        ''')

        # Add a test astronaut if the table is empty
        cursor.execute("SELECT COUNT(*) FROM astronauts")
        if cursor.fetchone()[0] == 0:
            db.execute("INSERT INTO astronauts (name, mission_role) VALUES (?, ?)", ('Commander Orion', 'Flight Commander'))
            db.execute("INSERT INTO health_logs (astronaut_id, log_type, data) VALUES (?, ?, ?)",
                       (1, 'mood', '{"level": "calm", "note": "Routine check."}'))
            db.commit()

init_db()

# ------------------------------------------------
# --- API Endpoints ---
# ------------------------------------------------

@app.route('/')
def home():
    """Simple health check endpoint."""
    return jsonify({"message": "MAITRI Python Backend API (Port 5001) is running!", "status": "OK"})

@app.route('/api/v1/health_logs', methods=['GET'])
def get_health_logs():
    """Fetches health logs for the physical well-being dashboard."""
    db = get_db()
    logs = db.execute('SELECT * FROM health_logs ORDER BY timestamp DESC LIMIT 10').fetchall()
    logs_list = [dict(log) for log in logs]
    return jsonify({"health_logs": logs_list, "count": len(logs_list)})


@app.route('/api/v1/maitri/chat', methods=['POST'])
def maitri_chat():
    """Endpoint for direct conversational chat (Next.js -> Flask -> Ollama)."""
    data = request.get_json()
    user_prompt = data.get('prompt')

    if not user_prompt:
        return jsonify({"error": "No prompt provided"}), 400

    system_prompt = (
        "You are MAITRI, an empathetic, helpful AI companion for an astronaut on a long-duration space mission. "
        "Keep your tone encouraging and professional. Respond to the user's prompt."
    )

    ollama_payload = {
        "model": OLLAMA_MODEL,
        "prompt": f"System: {system_prompt}\nUser: {user_prompt}",
        "stream": True
    }

    try:
        ollama_response = requests.post(OLLAMA_API_URL, json=ollama_payload, stream=True, timeout=30)
        ollama_response.raise_for_status()

        def generate():
            for line in ollama_response.iter_lines():
                if line:
                    try:
                        chunk = line.decode('utf-8')
                        data_json = json.loads(chunk)

                        if 'response' in data_json:
                            yield data_json['response']

                        if data_json.get('done'):
                            break
                    except json.JSONDecodeError:
                        continue

        return Response(stream_with_context(generate()), mimetype='text/plain')

    except requests.exceptions.ConnectionError:
        return jsonify({"error": "Failed to connect to Ollama. Is it running?"}), 503
    except requests.exceptions.RequestException as e:
        return jsonify({"error": f"Ollama request failed: {e}"}), 500


@app.route('/analyze', methods=['POST'])
def analyze_text():
    """NEW: Endpoint called by the Spring Boot backend for Journal Entry analysis."""
    data = request.get_json()
    text_to_analyze = data.get('text', '')

    if not text_to_analyze:
        return jsonify({"error": "No text provided for analysis"}), 400

    # Define the simplified sentiment analysis prompt
    ollama_payload = {
        "model": OLLAMA_MODEL,
        # Ask the model to classify the sentiment and only return a single word/phrase.
        "prompt": f"System: You are a quick text analysis service. Analyze the following journal entry for its overall sentiment. Respond ONLY with one of the following words: 'Positive', 'Negative', 'Neutral', 'Mixed'.\n\nEntry: '{text_to_analyze}'",
        "stream": False,
        "options": {"temperature": 0.1} # Low temperature for deterministic output
    }

    try:
        ollama_response = requests.post(OLLAMA_API_URL, json=ollama_payload)
        ollama_response.raise_for_status()

        response_data = ollama_response.json()
        raw_sentiment = response_data.get('response', 'Neutral').strip()

        # Simple cleanup
        sentiment = raw_sentiment.split('\n')[0].replace('.', '')

        # Return the structured JSON expected by the Spring Boot AnalysisService
        return jsonify({"sentiment": sentiment})

    except requests.exceptions.ConnectionError:
        return jsonify({"sentiment": "Analysis Failed - Ollama Down"}), 503
    except Exception as e:
        print(f"Analysis Error: {e}")
        return jsonify({"sentiment": "Analysis Failed"}), 500


# ------------------------------------------------
# --- Main Run Block (Port 5001) ---
# ------------------------------------------------
if __name__ == '__main__':
    # Flask runs on the new stable port 5001
    app.run(debug=True, host='0.0.0.0', port=5001)