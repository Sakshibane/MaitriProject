# maitri-ai-service-python/main.py

from flask import Flask, request, jsonify, make_response
from flask_cors import CORS
from ollama import Client
import re

app = Flask(__name__)
CORS(app) # Enable CORS for all routes

# --- Ollama Configuration ---
client = Client(host='http://localhost:11434')
MODEL = 'llama3'

# --- Helper Functions ---

def predict_critical_risk(wellbeing_score: int) -> bool:
    """Simulates a predictive model to flag critical risk based on the score (3 or below)."""
    return wellbeing_score <= 3

def detailed_analysis(text: str) -> dict:
    """Performs full multi-faceted analysis using Ollama."""

    # --- 1. Sentiment Analysis ---
    sentiment_prompt = (
        f"Analyze the overall sentiment of the entry. Respond with ONLY one word: 'Positive', 'Negative', or 'Mixed'. "
        f"Entry: '{text}'"
    )
    sentiment_response = client.generate(model=MODEL, prompt=sentiment_prompt, options={'temperature': 0.1})
    sentiment = sentiment_response['response'].strip()

    # --- 2. Key Theme Extraction ---
    theme_prompt = (
        f"Identify the SINGLE most important, high-level theme or subject. "
        f"Respond with a concise phrase of 2-4 words. Entry: '{text}'"
    )
    theme_response = client.generate(model=MODEL, prompt=theme_prompt, options={'temperature': 0.5})
    theme = theme_response['response'].strip()

    # --- 3. Wellbeing Score ---
    score_prompt = (
        f"Assess the user's current well-being on a scale of 1 (crisis) to 10 (peak). "
        f"Respond with ONLY the integer score. Entry: '{text}'"
    )
    score_response = client.generate(model=MODEL, prompt=score_prompt, options={'temperature': 0.1})
    score_text = score_response['response'].strip()

    try:
        match = re.search(r'\d+', score_text)
        wellbeing_score = int(match.group(0)) if match else 5
        wellbeing_score = max(1, min(10, wellbeing_score))
    except (ValueError, TypeError):
        wellbeing_score = 5

        # --- 4. Risk Factors ---
    risk_prompt = (
        f"List potential risk factors (e.g., 'Isolation, Stress'). If none, respond with 'None'. "
        f"Entry: '{text}'"
    )
    risk_response = client.generate(model=MODEL, prompt=risk_prompt, options={'temperature': 0.3})
    risk_factors = risk_response['response'].strip()

    # --- 5. Named Entity/Keywords Extraction (NEW) ---
    ner_prompt = (
        f"Extract the main NAMES (people, places) and key TOPICS/keywords. Respond with a comma-separated list. "
        f"Entry: '{text}'"
    )
    ner_response = client.generate(model=MODEL, prompt=ner_prompt, options={'temperature': 0.1})
    named_entities = ner_response['response'].strip()

    # --- 6. Abstractive Summary (NEW) ---
    summary_prompt = (
        f"Provide a concise, abstractive summary of the journal entry in one sentence (max 20 words). "
        f"Entry: '{text}'"
    )
    summary_response = client.generate(model=MODEL, prompt=summary_prompt, options={'temperature': 0.7})
    summary = summary_response['response'].strip()

    # --- 7. Critical Risk Prediction ---
    critical_risk = predict_critical_risk(wellbeing_score)

    # --- 8. Return Combined Result ---
    return {
        'sentiment': sentiment,
        'theme': theme,
        'wellbeingScore': wellbeing_score,
        'riskFactors': risk_factors,
        'criticalRisk': critical_risk,
        'namedEntities': named_entities,
        'summary': summary
    }

# --- API Routes ---

@app.route('/analyze', methods=['POST'])
def analyze():
    data = request.json
    if not data or 'text' not in data:
        return jsonify({"error": "Missing 'text' field in request body"}), 400

    text = data['text']
    analysis_result = detailed_analysis(text)

    return jsonify(analysis_result)

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.json
    if not data or 'prompt' not in data:
        return jsonify({"error": "Missing 'prompt' field in request body"}), 400

    prompt = data['prompt']

    recommendation_response = client.generate(
        model=MODEL,
        prompt=prompt,
        options={'temperature': 0.8}
    )
    recommendation = recommendation_response['response'].strip()

    # Return as plain text for the Recommendation API
    response = make_response(recommendation, 200)
    response.mimetype = "text/plain"
    return response

if __name__ == '__main__':
    # Flask will run on http://localhost:5001
    app.run(port=5001, debug=True, threaded=True)