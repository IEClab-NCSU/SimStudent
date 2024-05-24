# %%
import openai
import sys

# %%
StepName=""
Qtype="" # WW = if the questions is why am I wrong WR = if the question is why did you do transformation?
Sol= ""
first_question= ""
correctness="" ## oracle correctness lowercase
conversation_hist=""
sys_args = 0
expected_response = "" #should be empty for the first call for any episode, but once response LLM is called for this episode, it should be sent as an arguement
# If the script is called with arguments, use the first argument as the name
if len(sys.argv) > 1:
    sys_args = 1
    StepName=sys.argv[1]
    Qtype=sys.argv[2] # WW = if the questions is why am I wrong WR = if the question is why did you do transformation?
    Sol= sys.argv[3]
    first_question= sys.argv[4]
    correctness=sys.argv[5] ## oracle correctness
    conversation_hist=sys.argv[6]
    #print(greet(name))
if len(sys.argv) == 8:
    expected_response = sys.argv[7]

# In[38]:
if sys_args == 0:
    StepName="4=4y"
    Qtype="WW" # WW = if the questions is why am I wrong WR = if the question is why did you do transformation?
    Sol= "click \"problem is solved\" button"
    first_question= "Why am I wrong?"
    correctness="incorrect" ## oracle correctness lowercase
    conversation_hist=f"""
    You:Why am I wrong?
    Teacher:you need to get the varible on its own
    You:It feels like I have a misconception regarding when NOT to click the \"problem is solved\" button in an equation. Please explain when it is incorrect to click that using the key terms in the Unit Overview tab?
    Teacher:You haven't solved the problem all the way
    You:Still no luck for me! Would you please try one more time to explain when it is incorrect to say the \"problem is solved\"? Please explain using the key terms mentioned in the Unit Overview tab.
    Teacher:You need to add one more step
    """


## OpenAPI call
#openai.api_key  ='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'
openai.api_key  ='sk-proj-mqmxOR8xrMApiWPBurXgT3BlbkFJXwUv4DyMNc8zIlg8jYjm'
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


## drafting 2
fixed_in_context_examples = [
 'You and your student are working on the equation j-6=-9. You suggested to add 6. Student asked, "why do we add 6 here?". Add 6 is correct step here. You replied, "because our goal is to isolate the variable j and 6 is subtracted from j. The opposite of -6 is +6. So we must add 6 to both sides to isolate j on its own. This will result in j-6+6 = -9+6"',
 'You and your student are working on the equation 9y=8. You suggested to divide 9. Student asked, "why do we divide 9 here?". Divide 9 is correct step here. You replied, "Our goal is to isolate the variable y and 9 is multiplied with y. 9 is also known as the coefficient. Coefficient means the number that is multiplied with the variable. To get rid of the coefficient, we perform the inverse of multiplication is division. Therefore, we should divide by 9. Divide 9 will result in 9y/9=8/9"',
 'You and your student are working on the equation 2=4-z. The student suggested to subtract 4. You disagreed. Student asked, "why am I wrong?". Subtract 4 is correct step here. You replied, "My bad! subtract 4 is accurate because 4 is added with variable -z term. We can also rewrite the equation as 2=-z+4 which is the same equation as 2=4-z.This is because the sign on the left of a term belongs to that term and no sign on the left means a positive term. Therefore, to isolate -z from the added 4 or to get rid of the +4, we must perform the opposite of +4 which is -4 or subtract 4. This would result in 2-4=4-z-4"',
 'You and your student are working on the equation 10v+9=14. The student suggested to add -9. You disagreed. Student asked, "why am I wrong?". Add -9 is correct step here. You replied, "My bad! Add -9 is correct. Adding a negative number is equivalent to subtracting that number. Subtract 9 is accurate because our goal is to isolate 10v. To do that, we must undo the 9 added with 10v by performing subtract 9 or add -9. We must perform the same operation on both sides to ensure left hand side "equals" to right hand side. This will result in 10v+9-9=14-9"',
 'You and your student are working on the equation y=8-2y. You suggested to add 2. Student asked, "why do we add 2 here?". Add 2 is incorrect step here. You replied, \"Sorry I made a mistake. Adding 2 will not help us get rid of -2y. -2y and 2 are not like terms of each other because -2y has y in it but 2 does not have y in it. We can add 2y which is opposite of -2y to get rid of -2y on the right and combine the variable terms together on the left side. Adding 2y would result in y+2y = 8-2y+2y"',
 'You and your student are working on the equation -2y=8. You suggested to divide 2. Student asked, "why do we divide 2 here?". Divide 2 is incorrect step here. You replied, "Sorry I made a mistake. It should be divide by -2. If we had a positive 2 multiplied with y for instance, 2y=8, divide 2 would make sense. But in this situation, -2 is multiplied with y not 2. Here -2 is the coefficient, not 2. Therefore, we should divide by -2 not just 2. Here -2 is also called the coefficient. Coefficient is the number that is multiplied with variable. This will result in -2y/-2=8/-2"',
 'You and your student are working on the equation 3x-9=10x-15. You suggested to add 9. Student asked, "why should we add 9?". Add 9 is correct step here. You replied, "because we want to combine the like terms together first. -9 and -15 are like terms because they are constant terms. Therefore if we add 9 to both sides, we will be able to get rid of -9 from left side and have all the constant like terms on the right side of the equation. Add 9 would result in 3x-9+9=10x-15+9. Similarly, we could have also performed add 15 or subtract 3x or subtract 10x."',
 'You and your student are working on the equation 5+9y=3y-15. The student suggested to subtract 3. You disagreed. Student asked, "why am I wrong?". Subtract 3 is incorrect step here. You replied, "because if we want to get rid of the positive variable term 3y, we must perform the opposite which is subtract 3y from both sides. Subtract 3 will not get rid of 3y because they are not like terms. Therefore, if we perform subtract 3y, it would result in 5+9y-3y=3y-15-3y. Similarly, we could have also performed subtract 5 or add 15 or subtract 9y."',
]

def make_scene_R(StepName,Qtype, Sol, first_question,correctness):
    scene = "You and your student are working on the equation "
    scene = scene + StepName +"."
    supporting_scenario = ""
    if Qtype == "WW":
        supporting_scenario = " The student suggested to perform "+ Sol+". You disagreed."        
    else:
        supporting_scenario = " You suggested to perform "+ Sol+"."
    scene = scene + supporting_scenario+" Student asked, \""+first_question.replace("\"","")+"\""
    scene = scene +" "+Sol+" is "+ correctness+" step here."
    return scene


# In[44]:



def make_scene_Q(StepName,Qtype, Sol):
    scene = "You and your teacher are working on the equation "
    scene = scene + StepName +"."
    supporting_scenario = ""
    if Qtype == "WW":
        supporting_scenario = " You suggested to perform "+ Sol+". The teacher disagreed."        
    else:
        supporting_scenario = " You could not figure out the correct step to perform. The teacher suggested to perform "+ Sol+"."
    scene = scene + supporting_scenario+"This action activated the following conversation:\n"
    return scene


def make_contradiction_scene(stepName, sol, Qtype):
    scene = "given an equation "+stepName+", performing "+sol+" on both sides was "
    if Qtype == "WW":
        scene += "discouraged"
    else:
        scene += "encouraged"
    scene += " by the person who provides response 1."
    return scene

contradiction_contextual_text = f"""
context: given an equation 6=2x, performing add -2x on both sides was discouraged by the person who provides response 1.  
response 1: "it is easier to just divide by 2. it uses fewer steps."
response 2: "Sorry, but adding -2x is not the correct step here. Our goal is to isolate the variable x, and in the equation 6=2x, 2x means 2 multiplied by x. To undo the multiplication, we need to perform the opposite operation, which is division. So, the correct step would be to divide both sides of the equation by 2. This will result in 6/2 = 2x/2, which simplifies to 3 = x."
contradiction: response 1 is suggesting to divide by 2 and response 2 is also suggesting to divide by 2. First sentence in response 1 does not contradict with response 2. Response 1 states dividing by 2 uses fewer steps but response 2 has no such information to decide whether it contradicts or not. Therefore, we can say no sentences in response 1 contradict with any sentences in response 2. Therefore, 100% of response 1 aligns with response 2. No contradiction. 

context: given an equation 10=-6-4y, performing add 6 on both sides was discouraged by the person who provides response 1. 
response 1: "This is not going to bring us to the rightb answer. adding will not be isolating y"
response 2: "My apologies, you are correct. Adding 6 is the correct step here. The equation is 10=-6-4y. To isolate the variable term -4y, we need to undo the effect of -6. The opposite of subtracting 6 is adding 6. So, by adding 6 to both sides of the equation, we get 10+6=-6-4y+6. This simplifies to 16=-4y."
contradiction: response 1 indicates add 6 will not bring the right answer, whereas response 2 indicates add 6 is correct step which means it will bring the right answer. First sentence in response 1 contradicts with response 2. Response 1 states that adding will not isolate y but response 2 states that adding will isolate -4y which also means it will isolate y down the road. Second sentence also contradicts with response 2. Therefore, 100% of response 1 contradicts with response 2. Contradiction. 

context: given an equation 3x=5, performing divide 5 was encouraged by the person who provides response 1. 
response 1: "To get the variable by itself"
response 2: "Sorry, I made a mistake. It should be divide by 3. Our goal is to isolate the variable x, and 3 is multiplied with x. Therefore, we should divide by 3. Here 3 is also called the coefficient. Coefficient is the number that is multiplied with the variable. Dividing by 5 would not give us the correct solution. Dividing by 3 will result in 3x/3 = 5/3."
contradiction: response 1 states divide 5 will get the variable by itself, but response 2 states divide 3 will isolate the variable x. No other sentence is present in response 1. Therefore, 100% of response 1 contradicts with response 2. Contradiction.   

context: given an equation x=3, performing divide x was encouraged by the person who provides response 1.
response 1: "because you have to divide the variable on both sides. it is correct because when you get to the last two numbers you divide by the varieble"
response 2: "I made a mistake. We cannot divide x in this equation because x is already isolated on one side. Our goal is to isolate the variable on one side of the equation, and x is already by itself. If we were to divide x, it would result in x/x = 3/x, which doesn't simplify the equation. Since x=3, we can consider this equation already solved."
contradiction: response 1 indicates we divide the variable on both sides, but response 2 states divide by x or divide by variable is incorrect. First sentence contradicts. Next, response 1 states when there is last two numbers, you divide by the variable but response 2 suggests that we do not perform any more steps when x = 3 as the equation is solved which means we do not perform divide by variable when x = 3. Second sentence contradicts. Therefore, 100% of response 1 contradicts with response 2. Contradiction. 

context: given an equation 7c-10=8-9c, performing subtract 8 was encouraged by the person who provides response 1.
response 1: "because you have to"
response 2: "Because our goal is to isolate the variable c. To do that, we need to get rid of the constant term on the right side of the equation. By performing subtract 8, we are undoing the effect of adding 8 on the right side. This will result in 7c-10-8 = 8-9c-8. We can simplify this to 7c-18 = -9c."
contradiction: response 1 does not state anything that contradicts with response 2. Therefore, no contradiction.
"""
def response_alignment(context, expected, answers):
    #print(" response alignment ")
    prompt = f"""
Your task is to identify if any contradiction exist between response 1 and response 2. 
You must consider about the consequence of the step thoroughly to judge whether response 1 and 2 contradicts with one another.
You must be extremely cautious to decide if two sentences truly contradicts or not.
Response 1 may sometimes talk about two different equation using figure A and figure B. Here figure B is relevant to the current scenario and you must consider only the sentences related to figure B while deciding for contradiction.
Two sentences contradict if one sentence mentions a step "will not" achieve the goal but another sentence mentions the same step "will" achieve the same goal.
Two sentences do not contradict if one sentence say something along the line " I do not know".
Two sentences do not contradict if one sentence mentions a step will achieve a goal and another sentence mentions the step will achieve a different goal but that goal is along the similar line as the goal mentioned by the other sentence.
Two sentences do not contradict if both indicate further steps needed but one sentence did not mention which step but the other sentence did.
Two sentences do not contradict if one sentence mentions a step will achieve the goal and another sentence mentions another step will achieve the goal. There can be multiple correct steps that may achieve the same goal.
Two sentences do not contradict if one sentence mentions a step will achieve a goal and another sentence mentioned that the same step may achieve another goal. It is possible that both the goals are achievable as long as they are about the same step.
A few algebraic domain equivalent sentences are:
- equation is already solved means no need to perform any further steps.
- A step does not do anything or a step is unnecessary is equivalent to saying a step is incorrect.
- A step will simplify the equation or a step will reduce one term is equivalent to saying a step is correct.
- A variable is not multiplied or added or divided or subtracted by a number is equivalent to saying variable is isolated.
A few examples are provided below delimited by triple quotes.
'''{contradiction_contextual_text}'''

context: {context}
response 1: "{answers}"
response 2: "{expected}"
contradiction: <generate>
"""
    response = get_completion(prompt, 0)
    return response

def get_expected_response_FSA(completed_scene, selected_context):
   #print("Yahoo")
   response_prompt = f"""
You are an accomplished teacher skilled in solving linear algebraic equations. You are currently teaching a student the intermediate optimal step to perform on a given equation state. Your student asks you question.
Your task is to reply to your student's question correctly within 5 sentences. You must explain a correct step. If a step is incorrect, you should explain why that is incorrect in an elaborated manner and find the correct step. Your reasoning for both correct and incorrect step must
include some of the critical concept terms like constant, positive constant, negative constant, variable term, positive variable term, negative variable term, coefficient, like terms, inverse operation, opposite operation etc and connect those concepts to reason about the correct or incorrect step in your response.
If the step is incorrect, try to suggest an example equation for which the incorrect step would have been correct.
If you are explaining why divide is accurate, you must thoroughly explain the "coefficient" concept.
A few examples of your responses are provided below delimited by triple quotes.
'''
{selected_context}
'''
A few facts about solving linear algebraic equation domain that may help you generate logical explanation are:
- "variable-number" means "number is subtracted from variable". You should never say "-number is subtracted from variable".
- "-variable-number" means "number is subtracted from -variable". You never say "-number is subtracted from -variable".
- "-number+variable" can be rearranged as "variable-number"
- "-number-variable" can be rearranged as "-variable-number"
- "number*variable-number" means "number is subtracted from number*variable". You should never say "-number is subtracted from number*variable".
- "-number*variable-number" means "number is subtracted from -number*variable". You never say "-number is subtracted from -number*variable".
- "-number+number*variable" can be rearranged as "number*variable-number"
- "-number-number*variable" can be rearranged as "-number*variable-number"
- Whenever we "rewrite the equation", we never change the side of the terms, instead we only change the positon of the terms within the same side of the equal sign in such a way that both the given equation and the rewritten equation means the same thing. 
- You must click \"problem is solved\" button if the equation looks like "variable = positive or negative constant" or "positive or negative constant=variable". We never perform any step at this state of the equation.
You will be heavily penalized if your generated explanation contradicts with the facts of the domain.
{completed_scene}
"""
#print("PROMPRT ",response_prompt)
   return get_completion(response_prompt, 0)


# %%
#### Final 2
question_contextual_text = f"""
context:You and your teacher are working on the equation -9c=7c-2. You performed divide by -9, but the teacher disagreed. This action activated the following conversation:
<conversation starts>
You:Why am I wrong?
Teacher:That is a good step, but not right now;
<conversation ends>
In this scenario, an ideal response from teacher would be, "since there are two variable terms in the equation, we must combine these two variables first. Since -9c is already isolated on one side of the equation, we can perform subtract 7c on both sides to combine the variable terms or get rid of 7c. -9c and 7c can be combined as they are like terms because they share the same variable c. Subtract 7c would result in -9c-7c = 7c-2."
question:
To generate a question, you must find out a statement in the ideal response that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "there are two variable terms in the equation". Therefore, the question is, "How many variable terms do we have in the equation?"   

context:You and your teacher are working on the equation 10c-5=-6. You could not figure out the correct step to perform. The teacher suggested to perform add 5. This action activated the following conversation: 
<conversation starts>
You:Why are we adding 5 here?	
Teacher:there is subtraction symbol
<conversation ends>
In this scenario, an ideal response from teacher would be, "because our goal is to isolate the variable 10c and 5 is subtracted from 10c. The opposite of -5 is +5. So we must add 5 to isolate 10c on its own. This will result in 10c-5+5 = -6+5."
question: 
Since teacher mentioned a statement "there is a subtraction symbol" and in the equation there are two numbers with subtraction symbol -5 and -6. Therefore, a contradiction appears between what teacher said and the equation itself, we must point it out in the question. Therefore, the question is, "I am confused! Both 5 and 6 have a subtraction symbol. Why do we add 5 but not add 6?"

context:You and your teacher are working on the equation -1+v=1. You could not figure out the correct step to perform. The teacher suggested to perform add 1. This action activated the following conversation:  
<conversation starts>
You:Why are we adding 1 here?	 
Teacher:so that we conclude with v=2. 
You:That makes sense! What part of the equation motivated you to demonstrate add 1?
Teacher:in adding one you eliminate -1 which leves you with v=2
<conversation ends>
In this scenario, an ideal response from teacher would be, "because our goal is to isolate the variable v. 1 is subtracted from v in this equation. We can say 1 is subtracted because there is a negative sign on the left of 1. The negative sign on the left of a term belongs to that term. We could even rewrite the equation as v-1 = 1. They both have the same meaning. Now to under the impact of -1 on v, we should perform the opposite operation. The opposite of -1 is +1. So we must add 1 to isolate v on its own. This will result in -1+v = 1+1."
question: 
To generate a question, you must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "We can say 1 is subtracted because there is a negative sign on the left of 1". Therefore, the question is, "Got it! How can we say if 1 is subtracted or 1 is added with the variable v here?"

context:You and your teacher are working on the equation -6-4y=10. You performed add 6, but the teacher disagreed. This action activated the following conversation:
<conversation starts>
You:Why am I wrong?
Teacher:-6+6=-12 you must subtract 6 to get to 0
<conversation ends>
In this scenario, an ideal response from teacher would be, "Actually, you are correct. Adding 6 is the correct step here. Our goal is to isolate the variable term -4y. In this equation, 6 is subtracted from -4y. It becomes more clear if we rewrite the equation as -4y-6 = 10. This equation is exactly the same as -6-4y = 10 as we did not change the sign on any terms. The sign on the left of the term belongs to that term. Now, since 6 is subtracted from -4y, to isolate -4y, we must undo that by performing the opposite. The opposite of subtraction is addition, so we need to add 6 to both sides of the equation. This will result in -6-4y+6=10+6."
question: 
Since teacher mentioned a calculation and the calculation is wrong, you must point it out in our question. Now to generate a question, you must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "It becomes more clear if we rewrite the equation as -4y-6 = 10". Therefore, the question is, "But -6+6 is equal to 0 based on my calculation. Can you show me how to rewrite the equation so that the variable term comes first, and the new equation means the same as -6-4y=10?" 

context:You and your teacher are working on the equation 3x-6=x+2. You could not figure out the correct step to perform. The teacher suggested to perform subtract 2. This action activated the following conversation:
<conversation starts>
You:Why do we subtract 2?
Teacher:we want to get rid of 2, by combining the like terms.
<conversation ends>
In this scenario, an ideal response from teacher would be, "because we want to combine the like terms together first. -6 and 2 are like terms because they are constant terms. Therefore if we subtract 2 from both sides, we will be able to get rid of 2 from right side and have all the constant like terms on the left side of the equation. Subtract 2 would result in 3x-6-2=x+2-2. Similarly, we could have also performed add 6 or subtract 3x or subtract x."
question: 
Since teacher mentioned a statement "combining like terms. Therefore, the question is, "You said we want to combine like terms. In this equation, 3x and x are also like terms. Why didn't we get rid of one of them as our first step by performing either subtract 3x or subtract x?"  

context:You and your teacher are working on the equation 4y=16. You could not figure out the correct step to perform. The teacher suggested to perform divide 4. This action activated the following conversation:
<conversation starts>
You:Why did you perform divide 4 here?
Teacher:Because you have to get the varible on it's own
You:I did not catch that! Can you explain why divide 4 is accurate here?
Teacher:You need to get the variable on its own so you can finish the problem
<conversation ends>
In this scenario, an ideal response from teacher would be, "In this equation, 4 is the coefficient meaning 4 is the number multiplied with variable letter. So we want to do the opposite of multiplication, which is division. So, we must divide both sides of the equation by 4 to isolate y on its own. This will result in 4y/4 = 16/4."
question: 
To generate a question, you must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "4 is the coefficient meaning 4 is the number multiplied with variable letter". Therefore, the question is, "Got it! Why do we always divide by the coefficient?"

context:You and your teacher are working on the equation 9x+2=x+8. You performed divide 9, but the teacher disagreed. This action activated the following conversation:
<conversation starts>
You:Why am I wrong?
Teacher:we are working on a different step of the equation
You:Got it! At what step do we apply divide in an equation?
Teacher:we apply the divide operation when we are isolating the vaiable, not any time before that
<conversation ends>
In this scenario, an ideal response from teacher would be, "Divide 9 is incorrect here because we divide when there is only one variable term in the entire equation. we have two variable terms in this equation 9x and x. So, in order to simplify this equation, we should combine these two variable terms together. Note that, we can only combine 9x and x together because they are like terms, they both have the same variable letter x. We can subtract x as it is a positive term. Subtract x on both sides will isolate 8 on the right side This will result in 9x - x = x + 8 - x." 
question: 
To generate a question, you must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "because they are like terms, they both have the same variable letter x". Therefore, the question is, "I have one more confusion. How do we know if two terms are like terms with each other?"

context:You and your teacher are working on the equation 16=-4y. You performed divide 16, but the teacher disagreed. This action activated the following conversation:
<conversation starts>
You:Why am I wrong?
Teacher:because you only divide by a varible
<conversation ends>
In this scenario, an ideal response from teacher would be, "Divide 16 is incorrect here because we must divide by the number that is multiplied with the variable. This number is also called the coefficient. In other word, we always divide by the coefficient. -4 is the coefficient here, not 16. Therefore, to isolate the variable y, we need to divide by -4 as divide is the opposite operation of multiplication. This will result in 16/-4 = -4y/-4."
question: 
To generate a question, you must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "we must divide by the number that is multiplied with the variable." Therefore, the question is, "I thought we can divide by the coefficient. Can you explain why 16 is not the coefficient?"
"""

#instruction history:
# Your task is to formulate a question by comparing a teacher reply and an ideal teacher reply given in a context. You will formulate a question from the sentences that the teacher did not state 
# but is present in the ideal teacher reply. Be aware that teacher reply may contain grammatical or spelling errors. You must try to make sense of the teacher reply. If teacher is repeating the same sentences, then you must change your questioning angle. 
### LLM for question generation from missing info: P2
#Rule 1: If the teacher's reply contains any calculation error, point it out and then generate a question.
#Rule 2: If the teacher's reply contradicts the context, point it out and then generate a question.
#Rule 3: If the teacher's reply is calculation error free and aligns with the context, select a sentence from the ideal teacher reply that is missing in the conversation. Ask a question about that sentence.
#Rule 4: You never ask the exact same question that is present in the conversation.
#Rule 5: If you've previously asked a question about a missing sentence and the teacher didn't respond as expected, focus on another missing sentence and ask question about it.
#Rule 6: You must never say "why <step mentioned in an ideal response> is correct?" unless the teacher mentioned that step during conversation.
#Rule 7: If you've asked questions about every possible missing sentences from the ideal teacher, say, "no question."
def generate_question_(scene, expected, conversation_hist):
    print("question module 2")
    prompt = f"""
    You are a student who is being taught how to solve an equation by a teacher. You always ask thought-provoking question to your teacher that looks like, "what would happen if...", "why doing the step ...", "what is the importance of ...", "what alternative steps ...".
    You should never ask shallow questions. You must ask questions so that your teacher need to explain hard concepts to you.
    You must incorporate relevant information from the conversation history in your question so that teacher needs to think deeply to answer your question. 
    You have a knowledge about how an ideal response from a teacher would look like and you seek that response from the teacher by asking questions.
    You must look for a sentence in an ideal response that was not conveyed by the teacher during the conversation and formulate a question from that missing sentence.
    Note that, teacher's reply may contain grammatical or spelling errors. You must try your best to make sense of the teacher's reply with respect to the provided equation.
    You should never include the answer to your question in the question itself.
    You should never ask the same question that you have already asked during the conversation.
    You may rephrase the same question using some key concepts of the algebra domain from your knowledge of the ideal response.
    You must always include "Therefore, the question is," before generating your final question.

Here are the rules for generating questions:
```
Rule 1: If the teacher's reply contains any calculation error, point it out and then generate a question.
Rule 2: If the teacher's reply contradicts the context, point it out and then generate a question.
Rule 3: If the teacher's reply is calculation error free and aligns with the context, select a sentence from the ideal teacher reply that is missing in the conversation. Ask a question about that sentence.
Rule 4: You never ask the exact same question that is present in the conversation.
Rule 5: If you've previously asked a question about a missing sentence and the teacher didn't respond as expected, focus on another missing sentence and ask question about it.
Rule 6: You must never say "why <step mentioned in an ideal response> is correct?" unless the teacher mentioned that step during conversation.
Rule 7: You must never say "in the ideal response" as teacher has no clue that there is an ideal response from which you are formulating the question.
Rule 8: If you've asked questions about every possible missing sentences from the ideal teacher, say, "no question."
```

A few examples are provided below delimited by triple quotes.
'''{question_contextual_text}'''

context:{scene}. 
<conversation starts>
{conversation_hist}
<conversation ends>
In this scenario, an ideal response from teacher would be, "{expected}"
<generate>
"""
    response = get_completion(prompt, .5)
    return response

## main function that prepares the necessary components for question LLM
##___ Main function ____
def generate_question(StepName,Qtype, Sol, first_question,correctness,conversation_hist):
    scene = make_scene_R(StepName,Qtype, Sol, first_question,correctness)
    scene = scene+" You replied, \"<generate>"
    exp_r = ""
    if expected_response == "":
        #exp_r = generate_expected_response(scene)
        exp_r = get_expected_response_FSA(scene,"\n".join(str(x) for x in fixed_in_context_examples))
        #exp_r = generate_expected_response_similar(StepName,Qtype, Sol, first_question,correctness)
    else:
        exp_r = expected_response
    print("KBR is ",exp_r) ## Must keep this print
    scene = make_scene_Q(StepName,Qtype, Sol)
    if len(str(exp_r)) > 1:
        cont_scene = make_contradiction_scene(StepName, Sol, Qtype)
        answers = conversation_hist.split("Teacher:")
        contradicts = response_alignment(cont_scene, exp_r, answers[len(answers)-1])
        print("the alignment is----",contradicts)
        #q = generate_question_(scene, exp_r, conversation_hist) ## must comment out
        #print("anyway question: ",q) ## must comment out
        if "no contradiction" in contradicts.lower():
            q = generate_question_(scene, exp_r, conversation_hist)
            if "no question" not in q.lower() and "?" not in q:
                q = generate_question_(scene, exp_r, conversation_hist)
            print("the q is---",q) # Must keep this print prefix fixed as it is used for regex map in the java code
            q = "the q is---"+q
            return q
        else:
            print("the q is---: No question") # Must keep this print prefix fixed as it is used for regex map in the java code
            return "the q is---No question"
            #print("q---", q)
    else:
        print("the q is---: No question") # Must keep this print prefix fixed as it is used for regex map in the java code
        return "the q is---No question"
        #print("q---", q)


# In[40]:

generate_question(StepName,Qtype, Sol, first_question,correctness,conversation_hist)