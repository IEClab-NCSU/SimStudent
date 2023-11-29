#!/usr/bin/env python
# coding: utf-8

# In[23]:


import openai
import os 
from nltk.corpus import stopwords 
from nltk.tokenize import word_tokenize
from sklearn.feature_extraction.text import CountVectorizer
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
## Reading the excel file
import io
import pandas as pd
import nltk
import re

#nltk.download('all')

# In[24]:


#user_input = input("Enter StepName, Qtype (WW/WR), Sol, first_question,correctness and conversation history separated by : ")
# example.py
import sys
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

# In[26]:



if sys_args == 0:
    StepName="4=4y"
    Qtype="WW" # WW = if the questions is why am I wrong WR = if the question is why did you do transformation?
    Sol= "click \"problem is solved\" button"
    first_question= "Why am I wrong?"
    correctness="incorrect" ## oracle correctness lowercase
    conversation_hist=f"""
    Student:Why am I wrong?
    Teacher:you need to get the varible on its own
    Student:It feels like I have a misconception regarding when NOT to click the \"problem is solved\" button in an equation. Please explain when it is incorrect to click that using the key terms in the Unit Overview tab?
    Teacher:You haven't solved the problem all the way
    Student:Still no luck for me! Would you please try one more time to explain when it is incorrect to say the \"problem is solved\"? Please explain using the key terms mentioned in the Unit Overview tab.
    Teacher:You need to add one more step
    """

# In[27]:


## OpenAPI call
openai.api_key  ='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'
def get_completion(prompt, model="gpt-3.5-turbo"):
    messages = [{"role": "user", "content": prompt}]
    response = openai.ChatCompletion.create(
        model=model,
        messages=messages,
        temperature=0, # this is the degree of randomness of the model's output
    )
    return response.choices[0].message["content"]

# In[28]:


## Preprocess the contexts to ommit the numbers
def replace_numbers_with_symbols(text):
    # Use regex to find and replace numbers with symbols
    modified_text = re.sub(r'\d+', '#', text)
    return modified_text

# In[29]:


## Calculate cosine similarity to get the most similar positive and negative demonstrations
def calculate_cosine_similarity(test_df, Response_GPT_context_options, top):
   count_vectorizer = CountVectorizer(stop_words="english")
   count_vectorizer = CountVectorizer()
   sparse_matrix = count_vectorizer.fit_transform(test_df)

   doc_term_matrix = sparse_matrix.todense()
   df = pd.DataFrame(
      doc_term_matrix,
      columns=count_vectorizer.get_feature_names_out(),
   )
   #top = 2
   arr = cosine_similarity(df.iloc[:-1, :], df.tail(1))
   #print(arr)
   arr = sorted(range(len(arr)),key=arr.__getitem__,reverse=True)
   #print(arr)
   #for i in arr:
   #   print(Response_GPT_context_options[i])
   selected_context = []
   for i in arr[:top]:
      #selected_context = selected_context+Response_GPT_context_options[i]+"\n"
      selected_context.append(Response_GPT_context_options[i])
   return selected_context

# In[30]:


## 11 Positive examples
Response_GPT_context_options_positive = [
"A teacher and a student are working on the equation 10v+9=14. The student suggested to add -9. Teacher disagreed. Student asked, \"why am I wrong?\". Add -9 is correct step here. Teacher replied, \"My bad! Add -9 is correct. Adding a negative number is equivalent to subtracting that number. Subtract 9 is accurate because our goal is to isolate 10v. To do that, we must undo the 9 added with 10v by performing subtract 9 or add -9. This will result in 10v+9-9=14-9\"",
"A teacher and a student are working on the equation 3z=9-z. The student suggested to add z. Teacher disagreed. Student asked, \"why am I wrong?\". Add z is correct step here. Teacher replied, \"Wait, I am wrong. It should be add z. It will help us get rid of -z leaving us with only one variable term on one side of the equal sign. This would result in 3z+z=9-z+z. If the equation was 3z+9=-z instead, then we needed to subtract 3z to combine the varuable terms on right side.\"",
"A teacher and a student are working on the equation 11z+5=-9z+2. The student suggested to subtract -9z. Teacher disagreed. Student asked, \"why am I wrong?\". Subtract -9z is correct step here. Teacher replied, \"Sorry, subtract -9z is correct. Since subtract -9z is the same as add 9z because -(-9z) is equivalent to +9z as negative times negative is positive. Therefore, when we subtract a negative term it is equivalent to adding that term. Adding 9z would help us get rid of -9z from right side and combine all the variable terms together on the left. 11z and 9z can be combined because they are like terms as they both have the same variable z. This would result in 11z+5+9z=-9z+2+9z\"",
"A teacher and a student are working on the equation 8v=16. The student suggested to divide 8. Teacher disagreed. Student asked, \"why am I wrong?\". Divide 8 is correct step here. Teacher replied, \"My bad, it should be divide by 8 because our goal is to isolate the variable v and 8 is multiplied with v. The opposite of multiplication is division. So, we divide by 8 on both sides. The number multiplied with the variable is also called the coefficient. Therefore, a thumb rule is to divide by the coefficient. Divide by 8 will result in 8v/8=16/8\"",
"A teacher and a student are working on the equation 11u+10=12u+17. The teacher suggested to subtract 11u. Student asked, \"why do we subtract 11u here?\". Subtract 11u is correct step here. Teacher replied, \"because the equation has two variable terms 11u and 12u. We must combine these two variable terms. We could have also combined the constant terms +10 and +17 which would also be accurate. In summary, if we have two like terms, we always combine them in an equation. 11u and 12u are like terms as they both have u. +10 and +17 are like terms as they are numbers and has no variable attatched. To combine the variable terms we subtract 11u or -11u on both sides as it is opposite of +11u. This would result in 11u+10-11u=12u+17-11u\"",
"A teacher and a student are working on the equation 4n-10=3n+15. The teacher suggested to add 10. Student asked, \"why do we add 10 here?\". Add 10 is correct step here. Teacher replied, \"we need to combine the like terms first. In this equation, the constant terms -10 and +15 are like terms of each other and variable terms 4n and 3n are like terms of each other. We can start by combining either the constant terms or variable terms, it does not change anything. I choose to combine constant terms first. Therefore, we must perform opposite of -10 which is +10 or add 10 on both sides. This would result in 4n-10+10=3n+15+10\"",
"A teacher and a student are working on the equation 9-2z=6. The teacher suggested to subtract 9. Student asked, \"why do we subtract 9 here?\". Subtract 9 is correct step here. Teacher replied, \"because 9 is added with -2z in this equation. No sign at the left of a term means the term is positive. We can rewrite the equation as -2z+9 = 6 which is same as the equation that we are working on. Since 9 is added here with 2z, we should do the opposite of +9 which is -9. This would result in 9-2z-9=6-9.\"",
"A teacher and a student are working on the equation -2y=8. The teacher suggested to divide -2. Student asked, \"why do we divide -2 here?\". Divide -2 is correct step here. Teacher replied, \"Our goal is to isolate the variable y and -2 is multiplied with y. Therefore, we should divide by -2. Here -2 is also called the coefficient. Coefficient is the number that is multiplied with variable. As a thumb rule, we can remember that we always divide by the coefficient. Divide -2 will result in -2y/-2=8/-2\"",
"A teacher and a student are working on the equation j-6=-9. The teacher suggested to add 6. Student asked, \"why do we add 6 here?\". Add 6 is correct step here. Teacher replied, \"because our goal is to isolate the variable j and 6 is subtracted from j. The opposite of -6 is +6. So we must add 6 to isolate j on its own. This will result in j-6+6 = -9+6.\"",
"A teacher and a student are working on the equation 10=x+5. The teacher suggested to subtract 5. Student asked, \"why do we subtract 5 here?\". Subtract 5 is correct step here. Teacher replied, \"because our goal is to isolate the variable x and 5 is added with x. To isolate x, we must undo the impact of +5 by performing the opposite. The opposite of +5 is -5. So we must subtract 5 to isolate x on its own. This will result in 10-5=x+5-5.\"",
"A teacher and a student are working on the equation 2=4-z. The student suggested to \"subtract 4\". Teacher disagreed. Student asked, \"why am I wrong?\". Subtract 4 is correct step here. Teacher replied, \"My bad! subtract 4 is accurate because 4 is added with variable -z term. We can also rewrite the equation as 2=-z+4 which is the same equation as 2=4-z.This is because the sign on the left of a term belongs to that term and no sign on the left means a positive term. Therefore, to isolate -z from the added 4, we must perform the opposite of +4 which is -4 or subtract 4. This would result in 2-4=4-z-4\"",
]

## 11 Negative examples
Response_GPT_context_options_negative = [
"A teacher and a student are working on the equation 2x=8x+10. The teacher suggested to divide 2. Student asked, \"why do we divide 2 here?\". Divide 2 is incorrect step here. Teacher replied, \"I made a mistake. We cannot divide yet since there are two variable terms in the equation, 2x and 8x. If the equation had only one variable term, for instance: 2x=10, then divide 2 would make sense. But in the equation 2x=8x+10, we must combine the two variable terms first. Since 2x is already isolated on one side of the equation, we can perform subtract 8x on both sides to combine the variable terms. 2x and 8x can be combined as they are like terms because they share the same variable x. This would result in 2x-8x=8x-8x+10\"",
"A teacher and a student are working on the equation -3=v+9. The student suggested to click \"problem is solved\" button. Teacher disagreed. Student asked, \"why am I wrong?\". Click \"problem is solved\" button is incorrect step here. Teacher replied, \"because variable v is still not completely isolated. If the equation was -3=v instead, clicking problem is solved button would make sense. But here, there is 9 added with v. We must undo that effect by subtracting 9 from both sides. This would result in -3-9=v+9-9\"",
"A teacher and a student are working on the equation -2y=8. The teacher suggested to divide 2. Student asked, \"why do we divide 2 here?\". Divide 2 is incorrect step here. Teacher replied, \"Sorry I made a mistake. It should be divide by -2. If we had a positive 2 multiplied with y for instance, 2y=8, divide 2 would make sense. But in this situation, -2 is multiplied with y not 2. Therefore, we should divide by -2 not just 2. Here -2 is also called the coefficient. Coefficient is the number that is multiplied with variable. This will result in -2y/-2=8/-2\"",
"A teacher and a student are working on the equation x+10=18. The student suggested to subtract 18. Teacher disagreed. Student asked, \"why am I wrong?\". Subtract 18 is incorrect step here. Teacher replied, \"because our goal is to isolate the variable x. To do that, we must undo the operation performed on x. If the equation was x+18=10, subtract 18 would be the correct step because 18 is added with x. Here 10 is added with x, we want to do the opposite of +10 which is -10. So we must subtract 10 to isolate x on its own. This will result in x+10-10=18-10\"",
"A teacher and a student are working on the equation 4n=n-5. The teacher suggested to perform subtract 4n. Student asked, \"why do we subtract 4n here?\". Subtract 4n is incorrect step here. Teacher replied, \"I made a mistake. 4n is already isolated on one side. There is another variable term n in our equation. If we subtract n on both sides, then we will be able to combine the like terms 4n and n together on left side of the equation. Note that, 4n and n are like terms because both the terms have n in it. This would result in 4n-n=n-5-n\"",
"A teacher and a student are working on the equation 8=4v. The student suggested to perform subtract 4. Teacher disagreed. Student asked, \"why am I wrong?\". Subtract 4 is incorrect step here. Teacher replied, \"subtracting 4 is wrong because in the equation 4 is not added with v. If the equation was 8=4+v, subtract 4 would make sense as it would isolate the variable v. However, in the equation 8=4v, we have 4 multiplied with v. Therefore, to isolate v on one side, opposite of multiplication must be performed, which is divide by 4. This will result in 8/4=4v/4.\"",
"A teacher and a student are working on the equation y=8-2y. The teacher suggested to add 2. Student asked, \"why do we add 2 here?\". Add 2 is incorrect step here. Teacher replied, \"Sorry I made a mistake. Adding 2 will not help us get rid of -2y as -2y and 2 are not like terms of each other because -2y has y in it but 2 does not have y in it. We can add 2y which is opposite of -2y to get rid of -2y on the right and combine the variable terms together on the left side. Adding 2y would result in y+2y = 8-2y+2y.\"",
"A teacher and a student are working on the equation 2=4-z. The student suggested to \"add 4\". Teacher disagreed. Student asked, \"why am I wrong?\". Add 4 is incorrect step here. Teacher replied, \"because 4 is added with variable -z term. We can also rewrite the equation as 2=-z+4 which is the same equation as 2=4-z.This is because the sign on the left of a term belongs to that term and no sign on the left means a positive term. Therefore, to isolate -z from the added 4, we must perform the opposite of +4 which is -4 or subtract 4. This would result in 2-4=4-z-4\"",
"A teacher and a student are working on the equation 5=1-v. The student suggested to \"add 1\". Teacher disagreed. Student asked, \"why am I wrong?\". Add 1 is incorrect step here. Teacher replied, \"because in this equation, 1 is added with -v. We can rewrite the equation without changing its meaning as 5=-v+1. This is equivalent to 5=1-v because we did not change the sign of any terms. The sign on the left of the term belongs to that term. Therefore, we must perform the opposite of +1 which is -1 or shubtract 1. This would result in 5-1=1-v-1\"",
"A teacher and a student are working on the equation 5+9y=3y-15. The teacher suggested to add -15. Student asked, \"why do we add -15 here?\". Add -15 is incorrect step here. Teacher replied, \"Wait a minute, I think it should be add 15 or subtract -15 because my goal is to combine the two constant terms -15 and 5 together. To do that, I am performing the opposite of -15 which is +15. This would result in 5+9y+15=3y-15+15. Performing the opposite of +5 would also be accurate at this point.\"",
"A teacher and a student are working on the equation 5+9y=3y-15. The student suggested to subtract 3. Teacher disagreed. Student asked, \"why am I wrong?\". Subtract 3 is incorrect step here. Teacher replied, \"because if we want to get rid of the positibe variable term 3y, we must perform the subtract operation with a variable term because subtract work for like terms only. Therefore, if we perform subtract 3y, it would result in 5+9y-3y=3y-15-3y which would cancel out the +3y and -3y by making it 0.\"",
]

Response_GPT_scenes_positive = []
for i in Response_GPT_context_options_positive:
    scene_text = i.split("Teacher replied")[0]
    scene_text = replace_numbers_with_symbols(scene_text)
    Response_GPT_scenes_positive.append(scene_text)

Response_GPT_scenes_negative = []
for i in Response_GPT_context_options_negative:
    scene_text = i.split("Teacher replied")[0]
    scene_text = replace_numbers_with_symbols(scene_text)
    Response_GPT_scenes_negative.append(scene_text)


# In[31]:


def make_scene(StepName,Qtype, Sol, first_question,correctness):
    scene = "A teacher and a student are working on the equation "
    scene = scene + StepName +"."
    supporting_scenario = ""
    if Qtype == "WW":
        supporting_scenario = " The student suggested to perform "+ Sol+". The teacher disagreed."        
    else:
        supporting_scenario = " The teacher suggested to perform "+ Sol+"."
    scene = scene + supporting_scenario+" Student asked, \""+first_question+"\""
    scene = scene +" "+Sol+" is "+ correctness+" step here. "
    return scene

# In[41]:


## RESPONSE LLM: Few shot COT prompt to generate expected response
def get_expected_response_FS(completed_scene, selected_context):
    response_prompt = f"""
You are an accomplished teacher skilled in solving linear algebraic equations. You are currently teaching a student the intermediate optimal step to perform on a given equation state. Your student asks you question.
Your task is to reply to your student's question correctly within 5 sentences. You must explain a correct step. If a step is incorrect, you should explain why that is incorrect and find the correct step. Your reasoning must 
include some of the critical concept terms like constant, positive constant, negative constant, variable term, positive variable term, negative variable term, coefficient, like terms, inverse operation, opposite operation etc in your response.
If the step is incorrect, try to suggest an example equation for which when the step would have been accurate.
A few examples of your responses are provided below delimited by triple quotes.
'''{selected_context}'''
{completed_scene}
"""
    #print(response_prompt)
    return get_completion(response_prompt)

# In[39]:


## This function is the root function that processes the prompt components for response LLM
def generate_expected_response(scene):
    positive_scenes = Response_GPT_scenes_positive
    negative_scenes = Response_GPT_scenes_negative
    mod_scene = replace_numbers_with_symbols(scene)
    test_df_pos = np.concatenate((positive_scenes, [mod_scene]))
    selected_positive__context = calculate_cosine_similarity(test_df_pos, Response_GPT_context_options_positive,2)
    test_df_neg = np.concatenate((negative_scenes, [mod_scene]))
    selected_negative__context = calculate_cosine_similarity(test_df_neg, Response_GPT_context_options_negative,2)
    selected_context = selected_positive__context+selected_negative__context
    completed_scene = scene+" Teacher replied, <generate>"
    exp_response = get_expected_response_FS(completed_scene,",".join(str(x) for x in selected_context))
    print("The expected response--", exp_response) # Must keep this print prefix fixed as it is used for regex map in the java code
    return exp_response

# In[36]:


### Question LLM 
## consequence oriented question:
question_contextual_text = f"""
context:
A teacher and a student are working on the equation -9c=7c-2. The student performed divide by -9, but the teacher disagreed. This action activated the following conversation:
Student: "Why am I wrong?"
Teacher: "That is a good step, but not right now; here we have to eliminate -2."
Student: "When do we apply 'divide' in an equation?"
Teacher: "We divide when we are trying to simplify the equation."
Student: "Still confused! I applied 'divide -4' when the equation was '16=-4y' and it was accurate. Why?"
Teacher: "There it is not incorrect; here it is because we are not trying to simplify the equation yet."
Student: "Your explanation was too specific. Please explain when 'divide' can be applied?"
Teacher: "We are not simplifying yet."
In this scenario, an ideal teacher would reply, "since there are two variable terms in the equation, we must combine these two variables first. Since -9c is already isolated on one side of the equation, we can perform subtract 7c on both sides to combine the variable terms. -9c and 7c can be combined as they are like terms because they share the same variable c. Subtract 7c would result in -9c-7c = 7c-2."
question:
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "since there are two variable terms in the equation, we must combine these two variables first". Therefore, the question is, "How do equations 16=-4y and -9c=7c-2 differ from each other in terms of the number of variable terms present in them?"   

context: A teacher and a student are working on the equation 10c-5=-6. The teacher suggested to perform add 5. This action activated the following conversation: 
Student: "Why are we adding 5 here?" 	
Teacher: "there is subtraction symbol"
In this scenario, an ideal teacher would reply, "because our goal is to isolate the variable 10c and 5 is subtracted from 10c. The opposite of -5 is +5. So we must add 5 to isolate 10c on its own. This will result in 10c-5+5 = -6+5.
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "because our goal is to isolate the variable 10c and 5 is subtracted from 10c". Therefore, the question is, "There are two subtraction symbols in the equation -6 and -5. How do we know which one would help us isolate the variable term 10c?"

context: A teacher and a student are working on the equation -1+v=1. The teacher suggested to perform add 1. This action activated the following conversation:  
Student: "Why are we adding 1 here?"	 
Teacher: "so that we conclude with v=2. 
Student:I want to strengthen my basic understanding. What motivated you to demonstrate "add 1"?
Teacher:in adding one you eliminate -1 which leves you with v=2
In this scenario, an ideal teacher would reply, "because our goal is to isolate the variable v. 1 is subtracted from v in this equation. We can say 1 is subtracted because there is a negative sign on the left of 1. The negative sign on the left of a term belongs to that term. We could even rewrite the equation as v-1 = 1. They both have the same meaning. Now to under the impact of -1 on v, we should perform the opposite operation. The opposite of -1 is +1. So we must add 1 to isolate v on its own. This will result in -1+v = 1+1.
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "We could even rewrite the equation as v-1 = 1. They both have the same meaning.". Therefore, the question is, "Can you rewrite the equation in such a way so that the v comes at the beginning and the rewritten equation means the same equation as -1+v=1?"

context: A teacher and a student are working on the equation -6-4y=10. The student performed add 6, but the teacher disagreed. This action activated the following conversation:
Student:Why am I wrong?
Teacher:-6+6=-12 you must subtract 6 to get to 0
In this scenario, an ideal teacher would reply, "Actually, you are correct. Adding 6 is the correct step here. Our goal is to isolate the variable term -4y. In this equation, 6 is subtracted from -4y. It becomes more clear if we rewrite the equation as -4y-6 = 10. This equation is exactly the same as -6-4y = 10 as we did not change the sign on any terms. The sign on the left of the term belongs to that term. Now, since 6 is subtracted from -4y, to isolate -4y, we must undo that by performing the opposite. The opposite of subtraction is addition, so we need to add 6 to both sides of the equation. This will result in -6-4y+6=10+6."
question: 
Since teacher mentioned a calculation and the calculation is wrong, we must point it out in our question. Now to generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "Now, since 6 is subtracted from -4y, to isolate -4y, we must undo that by performing the opposite". Therefore, the question is, "But -6+6 is equal to 0 based on my calculation. What is the operation that is taking place between -4y and 6?" 

context: A teacher and a student are working on the equation 3x-6=x+2. The student performed add 6, but the teacher disagreed. This action activated the following conversation:
Student:Why am I wrong?
Teacher:You have to subtract 2
Student:When is it correct to apply "add" in an equation?
Teacher:When the number that you trying to isolate is a negative number
In this scenario, an ideal teacher would reply, "Oops, my mistake! Add 6 is the correct step here. This equation has two variable terms and two constant terms. We must combine the like terms together in order to solve such equation. The two numbers -6 and +2 are both constant terms. We can perform the opposite operation of any one of them to combine them together on the other side. If we perform opposite of -6 which is add 6, we will get rid of -6 from the left side and end up woth only one constant term on the right side. Adding 6 would result in 3x-6+6=x+2+6."
question: 
Since teacher mentioned a statement "when the number is negative, we can apply add" contradicts with his decision of add 6 being incorrect since -6 is a negative number, we must point it out in the question. Therefore, the question is, "You said add is correct when there is a negative number. In this equation, we do have a negative number which is -6. Therefore, I think add 6 is correct. Can you resolve this confusion?"  

context: A teacher and a student are working on the equation 4y=16. The teacher suggested to perform subtract 4. This action activated the following conversation:
Student:Why did you perform "subtract 4" here?
Teacher:Because you have to get the varible on it's own
Student:I am trying to understand what makes "subtract 4" correct in depth. Would you please explain using the key terms mentioned in the Unit Overview tab?
Teacher:You need to get the variable on its own so you can finish the problem
In this scenario, an ideal teacher would reply, "Sorry, subtract 4 is incorrect. If the equation was 4+y = 16, subtract 4 would have been accurate. In this equation, 4 is multiplied with y, so we want to do the opposite of multiplication, which is division. So, we must divide both sides of the equation by 4 to isolate y on its own. This will result in 4y/4 = 16/4."
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "In this equation, 4 is multiplied with y, so we want to do the opposite of multiplication, which is division". Therefore, the question is, "Can you look at the equation and tell me what is the operation that is taking place between 4 and y? Is 4 being added with y or multiplied with y?

context: A teacher and a student are working on the equation x=3. The teacher suggested to perform divide x. This action activated the following conversation:
Student:Why did you do "divide x" here?
Teacher:because you have to divide the variable on both sides
Student:Please elaborate more on why "divide x" is correct?
Teacher:it is correct because when you get to the last two numbers you divide by the varieble
Student:Can you explain why "divide x" works, in general?
Teacher:whats a unit overlab"
In this scenario, an ideal teacher would reply, "Sorry, I made a mistake. We cannot divide by x in this case. Since x is already isolated on one side, we don't need to perform any additional steps. The equation x=3 means that x is already equal to 3, so there is no need for further manipulation."
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "The equation x=3 means that x is already equal to 3, so there is no need for further manipulation.". Therefore, the question is, "In this current nstate of the equation, can you tell me what x equals to?"

context: A teacher and a student are working on the equation 9x+2=x+8. The student performed divide 9, but the teacher disagreed. This action activated the following conversation:
Student:Why am I wrong?
Teacher:we are working on a different step of the equation
Student:When do we apply "divide" in an equation?
Teacher:we apply the divide operation when we are isolating the vaiable, not any time before that
Student:I am trying to understand a generalized rule of when to apply "divide". Can you explain when "divide" can be applied using the key terms in the Unit Overview tab?
Teacher:I just told you
In this scenario, an ideal teacher would reply, "Divide 9 is incorrect here because we divide when there is only one variable term in the entire equation. we have two variable terms in this equation 9x and x. So, in order to simplify this equation, we should combine these two variable terms together. Note that, we can only combine 9x and x together because they are like terms, they both have the same variable letter x. We can subtract x as it is a positive term. Subtract x on both sides will isolate 8 on the right side This will result in 9x - x = x + 8 - x." 
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "Note that, we can only combine 9x and x together because they are like terms, they both have the same variable letter x". Therefore, the question is, "How do we know if two variable terms are like terms with each other?"

context: A teacher and a student are working on the equation 16=-4y. The student performed divide 16, but the teacher disagreed. This action activated the following conversation:
Student:Why am I wrong?
Teacher:because you only divide by a varible
Student:There might be alternative correct actions but that does not justify "divide 16" is incorrect. Can you explain when applying "divide 16" in an equation is wrong?
Teacher:because you only divide by a variable term
In this scenario, an ideal teacher would reply, "Divide 16 is incorrect here because we must divide by the number that is multiplied with the variable. This number is also called the coefficient. In other word, we always divide by the coefficient. -4 is the coefficient here, not 16. Therefore, to isolate the variable y, we need to divide by -4 as divide is the opposite operation of multiplication. This will result in 16/-4 = -4y/-4."
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "Divide 16 is incorrect here because we must divide by the number that is multiplied with the variable. This number is also called the coefficient." Therefore, the question is, "What do we call those numbers that are multiplied with a variable?"
"""
#instruction history:
# Your task is to formulate a question by comparing a teacher reply and an ideal teacher reply given in a context. You will formulate a question from the sentences that the teacher did not state 
# but is present in the ideal teacher reply. Be aware that teacher reply may contain grammatical or spelling errors. You must try to make sense of the teacher reply. If teacher is repeating the same sentences, then you must change your questioning angle. 
### LLM for question generation from missing info: P2
def generate_question_(scene, expected, conversation_hist):
    prompt = f"""
Given a context, your goal is to generate a question by analysing the teacher's response in the given context. 
Note that, teacher's reply may contain grammatical or spelling errors. You must try your best to make sense of the teacher's reply with respect to the provided equation.
Once you understand teacher's reply, you must apply the rules given below delimited by triple backticks to formulate your question. 
You must never repeat or rephrase the same question asked by the student once in the given context.
Your should never ask a question that has been already answered by the teacher in the conversation. You must always generate a question that encourage new reply from the teacher.   

A few examples are provided below delimited by triple quotes.
'''{question_contextual_text}'''

context: {scene}. 
{conversation_hist}
In this scenario, an ideal teacher would reply, "{expected}"
question: <generate>

Here are the rules for generating questions:
```
- Rule 1: If the teacher reply contains any calculation error, you must point it out and then generate a question.
- Rule 2: If the teacher reply contradicts with the context, you must point it out and then generate a question.
- Rule 3: If the teacher reply does not contain any calculation error and does not contradict, then you must pick a sentence from the ideal teacher reply that is missing in the conversation between teacher and student. Once you find out a sentence, ask question about that sentence.
- Rule 4: To pick the sentence from the ideal teacher reply that is missing in the conversation between teacher and student, you must go in order. Between two missing sentences, pick the one that comes first. Do not pick multiple sentences.
```
"""
    response = get_completion(prompt)
    return response

# In[37]:


## main function that prepares the necessary components for question LLM
##___ Main function ____
def generate_question(StepName,Qtype, Sol, first_question,correctness,conversation_hist):
    scene = make_scene(StepName,Qtype, Sol, first_question,correctness)
    exp_r = ""
    if expected_response == "":
        exp_r = generate_expected_response(scene)
    else:
        exp_r = expected_response
    print("KBR is ",exp_r)

    if len(str(exp_r)) > 1:
        q = generate_question_(scene, exp_r, conversation_hist)
        print("the q is---",q) # Must keep this print prefix fixed as it is used for regex map in the java code
        return q
    else:
        print("the q is---: No question") # Must keep this print prefix fixed as it is used for regex map in the java code
        return "No question"
        #print("q---", q)
    

# In[40]:


generate_question(StepName,Qtype, Sol, first_question,correctness,conversation_hist)
