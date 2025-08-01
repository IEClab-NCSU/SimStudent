import sys
from pathlib import Path
import os
import re

from dotenv import load_dotenv
from openai import OpenAI

# Initialize Knowledge Base
PRODUCTION_RULES = ""
FEATURE_PREDICATES = ""

# SimStudent Prompt Template
SIMSTUDENT_PROMPT = """I am a middle school student learning to solve algebra equations. My math teacher told my class to pair up with our friends and teach each other. {scene}

I wonder what I should ask next. Can you help me think about the question that I should follow up with? 

BTW, please remember that I’m a middle school student and I don’t even know if what my friend said is correct or not. If my friend’s response is consistent with my knowledge base, just say “no question.” Provide justification if and ONLY if you say “no question”. Try not to ask any questions that don't sound like something I would ask. For example:

We are working on 5+6x=11. My friend suggests subtracting 5 and I ask why. They explain: “Because subtract 5 will cancel the constant term. This way we can isolate the variable on one side and combine like terms on the other side.” In this case, an example of a bad question is “Why do we have to subtract 5 from both sides of the equation?” because the concept of “subtracting from both sides” has not already been mentioned in the conversation, so it is very unlikely that I would mention that in a question. Also, my friend’s answer already makes sense so there is no point in following up. 

Please ask questions if my friend says anything that sounds unfamiliar to me, or contradicts with familiar concepts. Encourage them to elaborate if something sounds vague or unfamiliar. Familiar concepts include anything, including skills, terms, operations, and definitions, that is mentioned in my knowledge base. A concept does not need to be EXPLICITLY mentioned in my knowledge base for me to be familiar with it. Since it is impossible for me to exhaustively list every concept I’m familiar with, consider this: If a concept is a prerequisite to a skill that I know or synonymous to concept I know then it is something I am familiar with.

Make sure to phrase your question this way: "Therefore, the question is, (question)." Be very careful that when you come up with the question, it is grounded strictly in concepts that have already been introduced in the conversation and the knowledge I’ve already learned. My knowledge is given to you below, in the format of rules related to solving algebra equations, written in the JESS programming language. These describe everything that my friend has taught me so far:

{production_rules}

To help you understand what the rules mean, I have included an (incomplete) list of descriptions for some known feature predicates, which the above rules may reference. These feature predicates explicitly AND implicitly describe skills and concepts that I am familiar with:
{feature_predicates}

{dup_add}{williams_add}{figure_add}
"""

# SimStudent Prompt Template
SIMSTUDENT_FOLLOWUP_PROMPT = """My friend responded to your question, {question}, with the following response: {answer}.

I wonder what I should ask next. Can you help me think about the question that I should follow up with? Again, please remember that I’m a middle school student and I don’t even know if what my friend said is correct or not. If my friend’s response is consistent with my knowledge base, just say “no question.”  Please provide justification if you said “no question”. Try not to ask any questions that don't sound like something I would ask. 

Make sure to phrase your question this way: "Therefore, the question is, (question)." Be very careful that when you come up with the question, it is grounded strictly in concepts that have already been introduced in the conversation and the knowledge I’ve already learned. My knowledge has already been given to you, in the format of rules related to solving algebra equations, written in the JESS programming language. Now, give me a context-appropriate question.

{dup_add}{williams_add}{figure_add}
"""

# Dictionary of all feature predicates and their descriptions
ALL_FEATURE_PREDICATES = {
    "distinctive": "a boolean function that returns true if the argument contains two distinct facts.",
    "same-row": "a boolean function that returns true if the argument contains two objects that are table cells and have the same row position.",
    "has-coefficient": "a boolean function that returns true if the argument contains a coefficient. A coefficient is a number that multiplies a variable term in an algebraic expression.",
    "has-var-term": "the argument is an algebraic expression that contains multiple terms with one being a variable term. A variable is a symbol used to represent an unknown value in an algebraic expression.",
    "is-polynomial": "a boolean function that returns true if the argument is a polynomial meaning the expression has more than just one term.",
    "is-lastconstterm-negative": "a boolean function that returns true if the argument contains a negative constant term at its end",
    "is-constant": "a boolean function that returns true if the argument contains only a constant term. A constant is a fixed value that does not change, such as a number.",
    "is-fraction-term": "a boolean function that returns true if the argument is a fraction, which represents a part of a whole and consists of two parts: the numerator, which indicates how many parts are taken, and the denominator, which shows the total number of equal parts the whole is divided into.",
    "get-first-integer": "returns a string; the first integer in the argument. An integer is a whole number that can be positive, negative, or zero.",
    "get-const-term": "a function that returns a string; the last constant term in the argument",
    "get-var-term": "returns the first variable term in an algebraic expression.",
    "get-operand": "returns the string operand from the third column. For example, '4x' from 'add 4x'",
    "sub-term": "returns the result of subtracting the second argument from the first. Note that only like terms can be subtracted from each other. Like terms are terms that have the same variables raised to the same power. Combining like terms can help simplify an expression. To 'simplify' means to reduce the number of terms remaining in an expression.",
    "add-term": "returns the result of adding two arguments. Note that only like terms can be added to each other. Like terms are terms that have the same variables raised to the same power. Combining like terms can help simplify an expression. To 'simplify' means to reduce the number of terms remaining in an expression.",
    "div-term": "returns the result of dividing the first argument by the second.",
    "skill-add": "return the string ‘add x’ where x is the string value of the argument.",
    "skill-subtract": "return the string ‘subtract x’ where x is the string value of the argument.",
    "skill-divide": "return the string ‘divide x’ where x is the string value of the argument.",
    "is-skill-divide": "returns true if the argument contains the string 'divide x' where x may be any term.",
    "is-skill-subtract": "returns true if the argument contains the string 'subtract x' where x may be any term.",
    "is-skill-add": "returns true if the argument contains the string 'add x' where x may be any term."
}

## Make sure you follow instructions in the README.md file
# to store your OpenAI API key in the environment variable OPENAI_API_KEY
MODEL = "o4-mini-2025-04-16"
load_dotenv()
if "OPENAI_API_KEY" not in os.environ:
    print("No question :: OpenAI library is not installed or the OPENAI_API_KEY is not set")
    exit(1)
client = OpenAI()
client.api_key = os.environ["OPENAI_API_KEY"]

def get_completion(prompt, temperature=1, model=MODEL):
    try:
        #Make your OpenAI API request here
        response = client.responses.create(
            model=model,
            reasoning={"effort": "high"},
            temperature=temperature, # this is the degree of randomness of the model's output
            input=prompt
            # top_p = 0.4,
        )
        return response.output_text

    except Exception as e:
        print(f"No question :: OpenAI API returned an API Error: {e}")
        exit(1)
        # return f"No question:: API ERROR {e}"


def get_feature_predicates(content):
    """
    Extracts feature predicates from the JESS rules.

    Args:
        rules (list of str): List of JESS rules.

    Returns:
        list of str: List of feature predicates found in the rules, followed by their descriptions.
    """
    feature_predicates = []

    for predicate, desc in ALL_FEATURE_PREDICATES.items():
        if predicate in content:
            feature_predicates.append(f"{predicate}: {desc}")

    return feature_predicates


def get_rules(content):
    """
    Extracts each defrule block from the content string.

    Args:
        content (str): The full text of the .pr file.

    Returns:
        list of str: A list of rule strings.
    """
    # Regular expression to match a complete (defrule ... ) block
    # This assumes balanced parentheses inside rules
    pattern = re.compile(r'\(defrule\b.*?(?=\n\(defrule|\Z)', re.DOTALL)
    rules = pattern.findall(content)
    return [rule.strip() for rule in rules]


def make_scene_q(stepname, qtype, sol, convo_history):
    """
    This function takes the input variables and returns the scene for the question LLM
    Args:
        stepName (str): The name of the step in the conversation.
        qtype (str): The type of question being asked (WW or WR).
        sol (str): The proposed solution to the step.
        convo_history (str): The conversation history between SimStudent and Tutor.
        selection (list): Used to determine whether a transformation or typein is being proposed.
    Returns:
        str: The scene for the question LLM.
    """
    supporting_scenario = "Thus, I am learning from my friend, " \
                          f"who is teaching me how to solve the equation {stepname}. "
    sol = "that the problem is done" if "done" in sol else f"to perform {sol}"

    if qtype == "WR":
        supporting_scenario += f"They initially proposed {sol}; however, I don’t understand why."
    else: # qtype == "WW"
        supporting_scenario += f"I proposed {sol}, but my friend disagrees with me."

    scene = f"{supporting_scenario} This initiated the following conversation between us:\n{convo_history}"
    return scene


def generate_question_(stepname, qtype, sol, convo_history, dup_q=None):
    """
    This function generates a question for the CTI component of SimStudent based on:
    provided step name, question type, solution, selection, conversation history.
    """
    dup_add = ""
    if dup_q:
        dup_add = f"Be careful, you have already asked this question before: {dup_q}. Do not ask it again.\n"

    williams_add = ""
    tutor_last_response = convo_history.split("Friend: ")[-1].strip()
    if ("william" in tutor_last_response.lower()):
        williams_add = "My friend has mentioned our helper, Mr. Williams, in response to your question. Do NOT include Mr. Williams in your next question. You will be penalized heavily if you do. Always address my friend directly.\n"

    figure_add = ""
    if "figure" in convo_history.lower():
        figure_add = "My friend has mentioned a figure in response to your question. Do NOT reference any figures in your next question. You will be penalized heavily if you do.\n"

    prompt = SIMSTUDENT_PROMPT.format(
        scene = make_scene_q(stepname, qtype, sol, convo_history),
        production_rules = PRODUCTION_RULES,
        feature_predicates = FEATURE_PREDICATES,
        dup_add = dup_add,
        williams_add = williams_add,
        figure_add = figure_add
    )

    # print(prompt + "\n\n")  # Debugging output to see the prompt being sent
    return get_completion(prompt)


## main function that prepares the necessary components for question LLM
##___ Main function ____
def generate_question(stepname, qtype, sol, convo_history):

    q = generate_question_(stepname=stepname, qtype=qtype, sol=sol, convo_history=convo_history)
    dup_q = q if q and q.lower().replace("therefore, the question is, ", "", 1).strip() in convo_history.lower() else None

    stop = 1
    while stop <= 3 and (not q or ("no question" not in q.lower() and "?" not in q) or dup_q):
        if stop == 3:
            q = f"Therefore, the question is, no question."
            break
        q = generate_question_(stepname=stepname, qtype=qtype, sol=sol, convo_history=convo_history, dup_q=dup_q)
        dup_q = q if q.lower() in convo_history.lower() else None
        stop += 1

    print(q) # Must keep this print prefix fixed as it is used for regex map in the java code
    # return "the q is---" + q


if len(sys.argv) >= 6 and all(sys.argv):
    _, Username, StepName, Qtype, Sol, Convo_hist = sys.argv[:6]

    try:
        RULES_FILE = next(Path(__file__).resolve().parent.glob(f"log/APLUS*/productionRules-{Username}.pr"), None)
        RULES_FILE_CONTENT = RULES_FILE.read_text(encoding="utf-8") # Read rules from JESS file
        PRODUCTION_RULES = "\n".join(get_rules(RULES_FILE_CONTENT)) # Parsed production rules from file
        FEATURE_PREDICATES = "\n".join(get_feature_predicates(RULES_FILE_CONTENT)) # Feature predicates referenced in the file
    except Exception as e:
        print(f"No question :: Error accessing JESS production rules (.pr) file. Check filename. {e}")
        exit(1)

    generate_question(StepName, Qtype, Sol, Convo_hist)

else:
    print("no question")

