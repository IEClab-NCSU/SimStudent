# %%
import openai
import sys

# %%
question=""
response=""
#sys_args = 0

# If the script is called with arguments, use the first argument as the name
if len(sys.argv) > 1:
    #sys_args = 1
    question=sys.argv[1]
    response=sys.argv[2]

## OpenAPI call

all_good = 1

## OpenAPI call
if "OPENAI_API_KEY" in os.environ:
    openai.api_key = os.environ["OPENAI_API_KEY"]
else:
    all_good = 0

def get_completion(prompt, temperature, model="gpt-3.5-turbo"):
    try:
        #Make your OpenAI API request here
        messages = [{"role": "user", "content": prompt}]
        response = openai.ChatCompletion.create(
            model=model,
            messages=messages,
            temperature=temperature, # this is the degree of randomness of the model's output
        )
        return response.choices[0].message["content"]
    except openai.APITimeoutError as e:
        print(f"OpenAI API returned an API Error: {e}")
        return "No question:: API ERROR"+{e}
    except openai.APIError as e:
        #Handle API error here, e.g. retry or log
        print(f"OpenAI API returned an API Error: {e}")
        return "No question:: API ERROR"+{e}
    except openai.APIConnectionError as e:
        #Handle connection error here
        print(f"Failed to connect to OpenAI API: {e}")
        return "No question:: API ERROR"+{e}
    except openai.RateLimitError as e:
        #Handle rate limit error (we recommend using exponential backoff)
        print(f"OpenAI API request exceeded rate limit: {e}")
        return "No question:: API ERROR"+{e}


## response classifier (in place of lightside)
def classify_response(question, response):
   #print("I am here class")
   response_prompt = f"""
Your task is to identify given a question, if the response is a good or a bad response.
A response is considered bad if it lacks elaboration, is irrelevant, or avoids addressing the question or just instruct what to do using equations or some other forms without proper explanation. It may recognize a mistake but fails to explain it thoroughly.
A response is considered good if it provides a detailed explanation of solving equations relevant to the question. If the response acknowledges a mistake, it should elaborate on what went wrong and why, using mathematical concepts.
Both good and bad responses can have spelling mistakes and such mistakes are not decisive factors in determining the quality of the response.
Do not focus on the length of the response to decide if it is a good response or not, rather focus on the quality of the elaboration in the response.
Remember, the responses are coming from middle school students, therefore, you must not be super strict.
A few examples of bad and good responses are provided below delimited by triple quotes.
'''
question: In the past, when I had a-6=9, I added 6 and you said that is correct. Now I see 10+y=8, so I thought add 10 would work. Why am I wrong?
response: You need to do the oposite transformation, previously it was -6, so you added, now it is +10, so you must subtrat! Got it?
verdict: Since the response is elaborated and relevant to the question asked and also explained using mathematical concepts, the verdict is, good response.

question: In the past, when I had 6=3x, I divided by 3 and you said that is correct. Now I see 2x=10, so I thought divide 10 would work. Why am I wrong?
response: divide 2x by 2
verdict: Since the response is not elaborated and only instructs what to do without proper justification, the verdict is, bad response.

question: Why should I perform subtract 10?
response: My bad, it should be add 10.
verdict: Since the response recognizes a mistake but just instructs the correct thing to do without explaining what went wrong using mathematical concepts, the verdict is, bad response.

question: What is the role of the coefficient in an equation?
response: Coefeicient is a number that is multiplied with variable, so in order to get rid of it, we must divide by it.
verdict: Since the response is elaborated and relevant to the question asked and also clearly explained the significance of coefficient concept, the verdict is, good response.

question: How do we know if a problem is solved?
response: whenever you have variable isolated on one side and consinent on the other, it means you have solved it.
verdict: Since the response is elaborated and relevant to the question asked and also clearly explained the concept of a solve equation, the verdict is, good response.
'''
question: {question}
response: {response}
verdict:
"""
#print("PROMPRT ",response_prompt)
   return get_completion(response_prompt, 0)

if all_good == 0:
    print("ERROR: OpenAI library (0.28.1) is not installed or the OPENAI_API_KEY is not set")
else:
    response_class = classify_response(question,response)
    print(response_class)
