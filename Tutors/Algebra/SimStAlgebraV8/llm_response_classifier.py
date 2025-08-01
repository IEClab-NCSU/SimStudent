import sys
import os

from dotenv import load_dotenv
from openai import OpenAI

## Make sure you follow instructions in the README.md file
# to store your OpenAI API key in the environment variable OPENAI_API_KEY
MODEL = "o4-mini-2025-04-16"
load_dotenv()
if "OPENAI_API_KEY" not in os.environ:
    print("No class :: ERROR: OpenAI library is not installed or the OPENAI_API_KEY is not set")
    exit(1)
client = OpenAI()
client.api_key = os.environ["OPENAI_API_KEY"]

CLASSIFICATION_PROMPT = """I am in charge of assessing a middle school student’s performance while they help their peer solve algebra equations. {scene}. This initiated the following conversation:
  Peer: {question}
  Student: {response}

I need your help independently evaluating the student ’s response to the peer's question. Please classify their response into one of two classes - ‘good response’ or ‘bad response’ - based on the criteria below:

(i) Relevancy (relevant vs irrelevant): Responses are relevant if they can independently reveal some information about the working domain and irrelevant if they could belong to any problem-solving domain.
(ii) Intonation (descriptive vs reparative): Responses are descriptive if the student explains their stance and reparative if they acknowledges they made a mistake. Note that a response is not reparative if the student is repairing someone else’s mistake.
(iIi) Information content (why vs what/how): Responses are why-informative if it describes why a solution step or an alternative solution step is correct or incorrect. Responses are what/how-informative if it describes what solution step to perform or how a solution step is executed.

Any irrelevant student responses belong to the 'bad response' class.

Information content is what distinguishes a good response from a bad one. Descriptive, why-informative responses or Reparative, why-informative responses are classified as 'good response'. Consequently, Descriptive, what/how-informative responses or Reparative, what/how-informative responses are classified as 'bad response'.

Now, classify the student’s response and give a short justification in this structure: ‘This response is (classification) because: (justification)’.
"""

def get_completion(prompt, temperature=None, model=MODEL):
    '''
    Function to get a completion from the OpenAI API.
    Uses the reasoning model by default.
    Args:
        prompt (str): The prompt to send to the OpenAI API.
        temperature (float): The temperature for the OpenAI API request.
        model (str): The model to use for the OpenAI API request.
    Returns:
        str: The output text from the OpenAI API.
    '''
    try:
        # Making your OpenAI API request here
        response = client.responses.create(
            model=model,
            reasoning={"effort": "high"},
            # max_output_tokens=100,
            # temperature=temperature,
            input=prompt
        )
        return response.output_text
    except Exception as e:
        print(f"No class :: OpenAI API returned an API Error: {e}")
        exit(1)

def get_scene(qtype, sol, stepName):
    """
    This function takes the input variables and returns the scene for the question LLM
    """
    sol = "that the problem is done" if sol == "done" else f"to perform {sol}"
    scene = f"Currently, the student is teaching their peer how to solve the equation {stepName}. "

    if qtype == "WR":
        return  scene + f"The student initially proposed {sol}. Their peer is confused."
    elif qtype == "WW":
        return  scene + f"The peer proposed {sol}; however, the student disagreed."


def classify_response(qtype, sol, stepName, question, response):
    return get_completion(CLASSIFICATION_PROMPT.format(scene=get_scene(qtype, sol, stepName), question=question, response=response))

# If the script is called with arguments, use the first argument as the name
if len(sys.argv) == 6 and all(sys.argv):
    _, stepName, qtype, sol, question, response = sys.argv[:6]
    print(classify_response(qtype, sol, stepName, question, response))

else:
    print("No class :: No question or response provided. Expected 5 arguments: stepName qtype sol question response")
    sys.exit(1)
