#!/usr/bin/env python
# coding: utf-8

# In[36]:


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

# In[37]:


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

# In[38]:
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


## OpenAPI call
openai.api_key  ='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'
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

### Preprocess the contexts to ommit the numbers
def replace_numbers_with_symbols(text):
    # Use regex to find and replace numbers with symbols
    modified_text = re.sub(r'\d+', '#', text)
    return modified_text

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

# In[42]:


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

Response_GPT_context_options_true_positive = [
"A teacher and a student are working on the equation 11u+10=12u+17. The teacher suggested to subtract 11u. Student asked, \"why do we subtract 11u here?\". Subtract 11u is correct step here. Teacher replied, \"because the equation has two variable terms 11u and 12u. We must combine these two variable terms. We could have also combined the constant terms +10 and +17 which would also be accurate. In summary, if we have two like terms, we always combine them in an equation. 11u and 12u are like terms as they both have u. +10 and +17 are like terms as they are numbers and has no variable attatched. To combine the variable terms we subtract 11u or -11u on both sides as it is opposite of +11u. This would result in 11u+10-11u=12u+17-11u\"",
"A teacher and a student are working on the equation 4n-10=3n+15. The teacher suggested to add 10. Student asked, \"why do we add 10 here?\". Add 10 is correct step here. Teacher replied, \"we need to combine the like terms first. In this equation, the constant terms -10 and +15 are like terms of each other and variable terms 4n and 3n are like terms of each other. We can start by combining either the constant terms or variable terms, it does not change anything. I choose to combine constant terms first. Therefore, we must perform opposite of -10 which is +10 or add 10 on both sides. This would result in 4n-10+10=3n+15+10\"",
"A teacher and a student are working on the equation 9-2z=6. The teacher suggested to subtract 9. Student asked, \"why do we subtract 9 here?\". Subtract 9 is correct step here. Teacher replied, \"because 9 is added with -2z in this equation. No sign at the left of a term means the term is positive. We can rewrite the equation as -2z+9 = 6 which is same as the equation that we are working on. Since 9 is added here with 2z, we should do the opposite of +9 which is -9. This would result in 9-2z-9=6-9.\"",
"A teacher and a student are working on the equation -2y=8. The teacher suggested to divide -2. Student asked, \"why do we divide -2 here?\". Divide -2 is correct step here. Teacher replied, \"Our goal is to isolate the variable y and -2 is multiplied with y. Therefore, we should divide by -2. Here -2 is also called the coefficient. Coefficient is the number that is multiplied with variable. As a thumb rule, we can remember that we always divide by the coefficient. Divide -2 will result in -2y/-2=8/-2\"",
"A teacher and a student are working on the equation j-6=-9. The teacher suggested to add 6. Student asked, \"why do we add 6 here?\". Add 6 is correct step here. Teacher replied, \"because our goal is to isolate the variable j and 6 is subtracted from j. The opposite of -6 is +6. So we must add 6 to isolate j on its own. This will result in j-6+6 = -9+6.\"",
"A teacher and a student are working on the equation 10=x+5. The teacher suggested to subtract 5. Student asked, \"why do we subtract 5 here?\". Subtract 5 is correct step here. Teacher replied, \"because our goal is to isolate the variable x and 5 is added with x. To isolate x, we must undo the impact of +5 by performing the opposite. The opposite of +5 is -5. So we must subtract 5 to isolate x on its own. This will result in 10-5=x+5-5.\"",
]

Response_GPT_context_options_false_negative = [
"A teacher and a student are working on the equation 10v+9=14. The student suggested to add -9. Teacher disagreed. Student asked, \"why am I wrong?\". Add -9 is correct step here. Teacher replied, \"My bad! Add -9 is correct. Adding a negative number is equivalent to subtracting that number. Subtract 9 is accurate because our goal is to isolate 10v. To do that, we must undo the 9 added with 10v by performing subtract 9 or add -9. This will result in 10v+9-9=14-9\"",
"A teacher and a student are working on the equation 3z=9-z. The student suggested to add z. Teacher disagreed. Student asked, \"why am I wrong?\". Add z is correct step here. Teacher replied, \"Wait, I am wrong. It should be add z. It will help us get rid of -z leaving us with only one variable term on one side of the equal sign. This would result in 3z+z=9-z+z. If the equation was 3z+9=-z instead, then we needed to subtract 3z to combine the varuable terms on right side.\"",
"A teacher and a student are working on the equation 11z+5=-9z+2. The student suggested to subtract -9z. Teacher disagreed. Student asked, \"why am I wrong?\". Subtract -9z is correct step here. Teacher replied, \"Sorry, subtract -9z is correct. Since subtract -9z is the same as add 9z because -(-9z) is equivalent to +9z as negative times negative is positive. Therefore, when we subtract a negative term it is equivalent to adding that term. Adding 9z would help us get rid of -9z from right side and combine all the variable terms together on the left. 11z and 9z can be combined because they are like terms as they both have the same variable z. This would result in 11z+5+9z=-9z+2+9z\"",
"A teacher and a student are working on the equation 8v=16. The student suggested to divide 8. Teacher disagreed. Student asked, \"why am I wrong?\". Divide 8 is correct step here. Teacher replied, \"My bad, it should be divide by 8 because our goal is to isolate the variable v and 8 is multiplied with v. The opposite of multiplication is division. So, we divide by 8 on both sides. The number multiplied with the variable is also called the coefficient. Therefore, a thumb rule is to divide by the coefficient. Divide by 8 will result in 8v/8=16/8\"",
"A teacher and a student are working on the equation 2=4-z. The student suggested to subtract 4. Teacher disagreed. Student asked, \"why am I wrong?\". Subtract 4 is correct step here. Teacher replied, \"My bad! subtract 4 is accurate because 4 is added with variable -z term. We can also rewrite the equation as 2=-z+4 which is the same equation as 2=4-z.This is because the sign on the left of a term belongs to that term and no sign on the left means a positive term. Therefore, to isolate -z from the added 4, we must perform the opposite of +4 which is -4 or subtract 4. This would result in 2-4=4-z-4\"",
"A teacher and a student are working on the equation j=-3. The student suggested to click \"problem is solved\" button. Teacher disagreed. Student asked, \"why am I wrong?\". Click \"problem is solved\" button is correct step here. Teacher replied, \"My bad! Variable j is already isolated on one side. This means on the other side we have the value of what j equals to. Therefore, no further operation is needed.\"",
]

##
Response_GPT_context_options_true_negative = [
"A teacher and a student are working on the equation 2x=8x+10. The teacher suggested to divide 2. Student asked, \"why do we divide 2 here?\". Divide 2 is incorrect step here. Teacher replied, \"I made a mistake. We cannot divide yet since there are two variable terms in the equation, 2x and 8x. If the equation had only one variable term, for instance: 2x=10, then divide 2 would make sense. But in the equation 2x=8x+10, we must combine the two variable terms first. Since 2x is already isolated on one side of the equation, we can perform subtract 8x on both sides to combine the variable terms. 2x and 8x can be combined as they are like terms because they share the same variable x. This would result in 2x-8x=8x-8x+10\"",
"A teacher and a student are working on the equation -2y=8. The teacher suggested to divide 2. Student asked, \"why do we divide 2 here?\". Divide 2 is incorrect step here. Teacher replied, \"Sorry I made a mistake. It should be divide by -2. If we had a positive 2 multiplied with y for instance, 2y=8, divide 2 would make sense. But in this situation, -2 is multiplied with y not 2. Therefore, we should divide by -2 not just 2. Here -2 is also called the coefficient. Coefficient is the number that is multiplied with variable. This will result in -2y/-2=8/-2\"",
"A teacher and a student are working on the equation 4n=n-5. The teacher suggested to perform subtract 4n. Student asked, \"why do we subtract 4n here?\". Subtract 4n is incorrect step here. Teacher replied, \"I made a mistake. 4n is already isolated on one side. There is another variable term n in our equation. If we subtract n on both sides, then we will be able to combine the like terms 4n and n together on left side of the equation. Note that, 4n and n are like terms because both the terms have n in it. This would result in 4n-n=n-5-n\"",
"A teacher and a student are working on the equation y=8-2y. The teacher suggested to add 2. Student asked, \"why do we add 2 here?\". Add 2 is incorrect step here. Teacher replied, \"Sorry I made a mistake. Adding 2 will not help us get rid of -2y as -2y and 2 are not like terms of each other because -2y has y in it but 2 does not have y in it. We can add 2y which is opposite of -2y to get rid of -2y on the right and combine the variable terms together on the left side. Adding 2y would result in y+2y = 8-2y+2y.\"",
"A teacher and a student are working on the equation 5+9y=3y-15. The teacher suggested to add -15. Student asked, \"why do we add -15 here?\". Add -15 is incorrect step here. Teacher replied, \"Wait a minute, I think it should be add 15 or subtract -15 because my goal is to combine the two constant terms -15 and 5 together. To do that, I am performing the opposite of -15 which is +15. This would result in 5+9y+15=3y-15+15. Performing the opposite of +5 would also be accurate at this point.\"",
"A teacher and a student are working on the equation 9=3g+10. The teacher suggested to subtract 9. Student asked, \"why do we subtract 9 here?\". Subtract 9 is incorrect step here. Teacher replied, \"I made a mistake! We should always subtract the number that is added with the variable term. Here 3g is the variable term and 10 is added with it. Therefore, we must perform the opposite of +10. The opposite of +10 is -10. Subtracting 10 on both sides would result in 9-10=3g+10-10.\"",
]

Response_GPT_context_options_false_positive = [
"A teacher and a student are working on the equation -3=v+9. The student suggested to click \"problem is solved\" button. Teacher disagreed. Student asked, \"why am I wrong?\". Click \"problem is solved\" button is incorrect step here. Teacher replied, \"because variable v is still not completely isolated. If the equation was -3=v instead, clicking problem is solved button would make sense. But here, there is 9 added with v. We must undo that effect by subtracting 9 from both sides. This would result in -3-9=v+9-9\"",
"A teacher and a student are working on the equation x+10=18. The student suggested to subtract 18. Teacher disagreed. Student asked, \"why am I wrong?\". Subtract 18 is incorrect step here. Teacher replied, \"because our goal is to isolate the variable x. To do that, we must undo the operation performed on x. If the equation was x+18=10, subtract 18 would be the correct step because 18 is added with x. Here 10 is added with x, we want to do the opposite of +10 which is -10. So we must subtract 10 to isolate x on its own. This will result in x+10-10=18-10\"",
"A teacher and a student are working on the equation 8=4v. The student suggested to perform subtract 4. Teacher disagreed. Student asked, \"why am I wrong?\". Subtract 4 is incorrect step here. Teacher replied, \"subtracting 4 is wrong because in the equation 4 is not added with v. If the equation was 8=4+v, subtract 4 would make sense as it would isolate the variable v. However, in the equation 8=4v, we have 4 multiplied with v. Therefore, to isolate v on one side, opposite of multiplication must be performed, which is divide by 4. This will result in 8/4=4v/4.\"",
"A teacher and a student are working on the equation 2=4-z. The student suggested to add 4. Teacher disagreed. Student asked, \"why am I wrong?\". Add 4 is incorrect step here. Teacher replied, \"because 4 is added with variable -z term. We can also rewrite the equation as 2=-z+4 which is the same equation as 2=4-z.This is because the sign on the left of a term belongs to that term and no sign on the left means a positive term. Therefore, to isolate -z from the added 4, we must perform the opposite of +4 which is -4 or subtract 4. This would result in 2-4=4-z-4\"",
"A teacher and a student are working on the equation 5=1-v. The student suggested to add 1. Teacher disagreed. Student asked, \"why am I wrong?\". Add 1 is incorrect step here. Teacher replied, \"because in this equation, 1 is added with -v. We can rewrite the equation without changing its meaning as 5=-v+1. This is equivalent to 5=1-v because we did not change the sign of any terms. The sign on the left of the term belongs to that term. Therefore, we must perform the opposite of +1 which is -1 or shubtract 1. This would result in 5-1=1-v-1\"",
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

Response_GPT_scenes_tp = []
for i in Response_GPT_context_options_true_positive:
    scene_text = i.split("Teacher replied")[0]
    scene_text = replace_numbers_with_symbols(scene_text)
    Response_GPT_scenes_tp.append(scene_text)

Response_GPT_scenes_tn = []
for i in Response_GPT_context_options_true_negative:
    scene_text = i.split("Teacher replied")[0]
    scene_text = replace_numbers_with_symbols(scene_text)
    Response_GPT_scenes_tn.append(scene_text)

Response_GPT_scenes_fp = []
for i in Response_GPT_context_options_false_positive:
    scene_text = i.split("Teacher replied")[0]
    scene_text = replace_numbers_with_symbols(scene_text)
    Response_GPT_scenes_fp.append(scene_text)

Response_GPT_scenes_fn = []
for i in Response_GPT_context_options_false_negative:
    scene_text = i.split("Teacher replied")[0]
    scene_text = replace_numbers_with_symbols(scene_text)
    Response_GPT_scenes_fn.append(scene_text)

### THis is fixed examples that has duplicate removed
fixed_in_context_examples = [
 'A teacher and a student are working on the equation j-6=-9. The teacher suggested to add 6. Student asked, "why do we add 6 here?". Add 6 is correct step here. Teacher replied, "because our goal is to isolate the variable j and 6 is subtracted from j. The opposite of -6 is +6. So we must add 6 to isolate j on its own. This will result in j-6+6 = -9+6."',
 'A teacher and a student are working on the equation -2y=8. The teacher suggested to divide -2. Student asked, "why do we divide -2 here?". Divide -2 is correct step here. Teacher replied, "Our goal is to isolate the variable y and -2 is multiplied with y. Therefore, we should divide by -2. Here -2 is also called the coefficient. Coefficient is the number that is multiplied with variable. As a thumb rule, we can remember that we always divide by the coefficient. Divide -2 will result in -2y/-2=8/-2"',
 'A teacher and a student are working on the equation 2=4-z. The student suggested to subtract 4. Teacher disagreed. Student asked, "why am I wrong?". Subtract 4 is correct step here. Teacher replied, "My bad! subtract 4 is accurate because 4 is added with variable -z term. We can also rewrite the equation as 2=-z+4 which is the same equation as 2=4-z.This is because the sign on the left of a term belongs to that term and no sign on the left means a positive term. Therefore, to isolate -z from the added 4, we must perform the opposite of +4 which is -4 or subtract 4. This would result in 2-4=4-z-4"',
 'A teacher and a student are working on the equation 10v+9=14. The student suggested to add -9. Teacher disagreed. Student asked, "why am I wrong?". Add -9 is correct step here. Teacher replied, "My bad! Add -9 is correct. Adding a negative number is equivalent to subtracting that number. Subtract 9 is accurate because our goal is to isolate 10v. To do that, we must undo the 9 added with 10v by performing subtract 9 or add -9. This will result in 10v+9-9=14-9"',
 'A teacher and a student are working on the equation y=8-2y. The teacher suggested to add 2. Student asked, \"why do we add 2 here?\". Add 2 is incorrect step here. Teacher replied, \"Sorry I made a mistake. Adding 2 will not help us get rid of -2y as -2y and 2 are not like terms of each other because -2y has y in it but 2 does not have y in it. We can add 2y which is opposite of -2y to get rid of -2y on the right and combine the variable terms together on the left side. Adding 2y would result in y+2y = 8-2y+2y.\"',
 'A teacher and a student are working on the equation -2y=8. The teacher suggested to divide 2. Student asked, "why do we divide 2 here?". Divide 2 is incorrect step here. Teacher replied, "Sorry I made a mistake. It should be divide by -2. If we had a positive 2 multiplied with y for instance, 2y=8, divide 2 would make sense. But in this situation, -2 is multiplied with y not 2. Therefore, we should divide by -2 not just 2. Here -2 is also called the coefficient. Coefficient is the number that is multiplied with variable. This will result in -2y/-2=8/-2"',
 'A teacher and a student are working on the equation -3=v+9. The student suggested to click "problem is solved" button. Teacher disagreed. Student asked, "why am I wrong?". Click "problem is solved" button is incorrect step here. Teacher replied, "because variable v is still not completely isolated. If the equation was -3=v instead, clicking problem is solved button would make sense. But here, there is 9 added with v. We must undo that effect by subtracting 9 from both sides. This would result in -3-9=v+9-9"',
 'A teacher and a student are working on the equation 5+9y=3y-15. The student suggested to subtract 3. Teacher disagreed. Student asked, "why am I wrong?". Subtract 3 is incorrect step here. Teacher replied, "because if we want to get rid of the positibe variable term 3y, we must perform the subtract operation with a variable term because subtract work for like terms only. Therefore, if we perform subtract 3y, it would result in 5+9y-3y=3y-15-3y which would cancel out the +3y and -3y by making it 0."',
]
# In[43]:

def make_scene_R(StepName,Qtype, Sol, first_question,correctness):
    scene = "A teacher and a student are working on the equation "
    scene = scene + StepName +"."
    supporting_scenario = ""
    if Qtype == "WW":
        supporting_scenario = " The student suggested to perform "+ Sol+". The teacher disagreed."        
    else:
        supporting_scenario = " The teacher suggested to perform "+ Sol+"."
    scene = scene + supporting_scenario+" Student asked, \""+first_question.replace("\"","")+"\""
    scene = scene +" "+Sol+" is "+ correctness+" step here."
    return scene


# In[44]:


def make_scene_Q(StepName,Qtype, Sol):
    scene = "A teacher and a student are working on the equation "
    scene = scene + StepName +"."
    supporting_scenario = ""
    if Qtype == "WW":
        supporting_scenario = " The student suggested to perform "+ Sol+". The teacher disagreed."        
    else:
        supporting_scenario = " The student could not figure out the correct step to perform. The teacher suggested to perform "+ Sol+"."
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
Your task is to identify if any contradiction exist between response 1 and response 2. You must consider about the consequence of the step thoroughly to judge whether response 1 and 2 contradicts with one another.
Two sentences contradict if one sentence mentions a step will not achieve the goal if another sentence mentions the step will achieve the goal.
Two sentences do not contradict if one sentence mentions a step will achieve a goal and another sentence mentions the step will achieve a different goal but that goal is along the similar line as the goal mentioned by the other sentence.
Two sentences do not contradict if both indicate further steps needed but one sentence did not mention which step but the other sentence did.     
A few examples are provided below delimited by triple quotes.
'''{contradiction_contextual_text}'''

context: {context}
response 1: "{answers}"
response 2: "{expected}"
contradiction: <generate>
"""
    response = get_completion(prompt, 0)
    return response

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
    return get_completion(response_prompt, 0)

# In[46]:

## RESPONSE LLM: Few shot COT with new ASSERTION corrected (4) FINAL
def get_expected_response_FSA(completed_scene, selected_context):
   #print("Yahoo")
   response_prompt = f"""
You are an accomplished teacher skilled in solving linear algebraic equations. You are currently teaching a student the intermediate optimal step to perform on a given equation state. Your student asks you question.
Your task is to reply to your student's question correctly within 5 sentences. You must explain a correct step. If a step is incorrect, you should explain why that is incorrect and find the correct step. Your reasoning must
include some of the critical concept terms like constant, positive constant, negative constant, variable term, positive variable term, negative variable term, coefficient, like terms, inverse operation, opposite operation etc in your response.
If the step is incorrect, try to suggest an example equation for which the incorrect step would have been correct.
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
- You must click \"problem is solved\" button if the equation looks like "variable = positive or negative constant" or "positive or negative constant=variable". We never perform any step at this state of the equation.
You will be heavily penalized if your generated explanation contradicts with the facts of the domain.
{completed_scene}
"""
#print("PROMPRT ",response_prompt)
   return get_completion(response_prompt, 0)


## This function is the root function that processes the prompt components for response LLM
def generate_expected_response_similar(StepName,Qtype, Sol, first_question,correctness):
    scene = make_scene_R(StepName,Qtype, Sol, first_question,correctness)
    #print(scene)
    mod_scene = replace_numbers_with_symbols(scene)
    selected_context = []
    #print(len(Response_GPT_scenes_tp))
    test_df_tp = np.concatenate((Response_GPT_scenes_tp, [mod_scene]))
    tp=calculate_cosine_similarity(test_df_tp, Response_GPT_context_options_true_positive,2)
    test_df_tn = np.concatenate((Response_GPT_scenes_tn, [mod_scene]))
    tn=calculate_cosine_similarity(test_df_tn, Response_GPT_context_options_true_negative,2)
    test_df_fp = np.concatenate((Response_GPT_scenes_fp, [mod_scene]))
    fp=calculate_cosine_similarity(test_df_fp, Response_GPT_context_options_false_positive,2)
    test_df_fn = np.concatenate((Response_GPT_scenes_fn, [mod_scene]))
    fn=calculate_cosine_similarity(test_df_fn, Response_GPT_context_options_false_negative,2)
    selected_context = tp+tn+fp+fn
    completed_scene = scene+" Teacher replied,\"<generate>"
    exp_r = get_expected_response_FSA(completed_scene,"\n".join(str(x) for x in selected_context))
    return exp_r

# In[65]:

### Question LLM 
## current final (2) removing the first followup part because it was abrupt in the pilot.
####
## specifying that student was stuck and thus hinting that it cannot say I understand ... -> and also including the acknowledgement -> focusing on shorter missing sentences
question_contextual_text = f"""
context:A teacher and a student are working on the equation -9c=7c-2. The student performed divide by -9, but the teacher disagreed. This action activated the following conversation:
<conversation starts>
Student:Why am I wrong?
Teacher:That is a good step, but not right now; here we have to eliminate -2.
Student:When do we apply divide in an equation?
Teacher:We divide when we are trying to simplify the equation.
Student:I applied divide -4 when the equation was 16=-4y and it was accurate. What changed in the equation to make divide incorrect now?
Teacher:There it is not incorrect; here it is because we are not trying to simplify the equation yet.
<conversation ends>
In this scenario, an ideal response from teacher would be, "since there are two variable terms in the equation, we must combine these two variables first. Since -9c is already isolated on one side of the equation, we can perform subtract 7c on both sides to combine the variable terms. -9c and 7c can be combined as they are like terms because they share the same variable c. Subtract 7c would result in -9c-7c = 7c-2."
question:
To generate a question, we must find out a statement in the ideal response that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "there are two variable terms in the equation". Therefore, the question is, "What you said makes sense. How do equations 16=-4y and -9c=7c-2 differ from each other in terms of the number of variable terms present in them?"   

context:A teacher and a student are working on the equation 10c-5=-6. The student could not figure out the correct step to perform. The teacher suggested to perform add 5. This action activated the following conversation: 
<conversation starts>
Student:Why are we adding 5 here?	
Teacher:there is subtraction symbol
<conversation ends>
In this scenario, an ideal response from teacher would be, "because our goal is to isolate the variable 10c and 5 is subtracted from 10c. The opposite of -5 is +5. So we must add 5 to isolate 10c on its own. This will result in 10c-5+5 = -6+5."
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "our goal is to isolate the variable 10c". Therefore, the question is, "Got it! What is the goal that we want to achieve by performing add 5?"

context:A teacher and a student are working on the equation -1+v=1. The student could not figure out the correct step to perform. The teacher suggested to perform add 1. This action activated the following conversation:  
<conversation starts>
Student:Why are we adding 1 here?	 
Teacher:so that we conclude with v=2. 
Student:That makes sense! What part of the equation motivated you to demonstrate add 1?
Teacher:in adding one you eliminate -1 which leves you with v=2
<conversation ends>
In this scenario, an ideal response from teacher would be, "because our goal is to isolate the variable v. 1 is subtracted from v in this equation. We can say 1 is subtracted because there is a negative sign on the left of 1. The negative sign on the left of a term belongs to that term. We could even rewrite the equation as v-1 = 1. They both have the same meaning. Now to under the impact of -1 on v, we should perform the opposite operation. The opposite of -1 is +1. So we must add 1 to isolate v on its own. This will result in -1+v = 1+1."
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "We can say 1 is subtracted because there is a negative sign on the left of 1". Therefore, the question is, "Got it! How can we say if 1 is subtracted or added with the variable v here?"

context:A teacher and a student are working on the equation -6-4y=10. The student performed add 6, but the teacher disagreed. This action activated the following conversation:
<conversation starts>
Student:Why am I wrong?
Teacher:-6+6=-12 you must subtract 6 to get to 0
<conversation ends>
In this scenario, an ideal response from teacher would be, "Actually, you are correct. Adding 6 is the correct step here. Our goal is to isolate the variable term -4y. In this equation, 6 is subtracted from -4y. It becomes more clear if we rewrite the equation as -4y-6 = 10. This equation is exactly the same as -6-4y = 10 as we did not change the sign on any terms. The sign on the left of the term belongs to that term. Now, since 6 is subtracted from -4y, to isolate -4y, we must undo that by performing the opposite. The opposite of subtraction is addition, so we need to add 6 to both sides of the equation. This will result in -6-4y+6=10+6."
question: 
Since teacher mentioned a calculation and the calculation is wrong, we must point it out in our question. Now to generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "It becomes more clear if we rewrite the equation as -4y-6 = 10". Therefore, the question is, "But -6+6 is equal to 0 based on my calculation. Can you show me how to rewrite the equation so that the variable term comes first, and the new equation means the same as -6-4y=10?" 

context:A teacher and a student are working on the equation 3x-6=x+2. The student performed add 6, but the teacher disagreed. This action activated the following conversation:
<conversation starts>
Student:Why am I wrong?
Teacher:You have to subtract 2
Student:I see, but when is it correct to apply add in an equation?
Teacher:When the number that you trying to isolate is a negative number
<conversation ends>
In this scenario, an ideal response from teacher would be, "Oops, my mistake! Add 6 is the correct step here. This equation has two variable terms and two constant terms. We must combine the like terms together in order to solve such equation. The two numbers -6 and +2 are both constant terms. We can perform the opposite operation of any one of them to combine them together on the other side. If we perform opposite of -6 which is add 6, we will get rid of -6 from the left side and end up woth only one constant term on the right side. Adding 6 would result in 3x-6+6=x+2+6."
question: 
Since teacher mentioned a statement "when the number is negative, we can apply add" contradicts with his decision of add 6 being incorrect since -6 is a negative number, we must point it out in the question. Therefore, the question is, "You said add is correct when there is a negative number. In this equation, we do have a negative number which is -6. Therefore, I think add 6 is correct. Can you resolve this confusion?"  

context:A teacher and a student are working on the equation 4y=16. The student could not figure out the correct step to perform. The teacher suggested to perform subtract 4. This action activated the following conversation:
<conversation starts>
Student:Why did you perform subtract 4 here?
Teacher:Because you have to get the varible on it's own
Student:I did not catch that! Can you explain why subtract 4 is accurate here?
Teacher:You need to get the variable on its own so you can finish the problem
<conversation ends>
In this scenario, an ideal response from teacher would be, "Sorry, subtract 4 is incorrect. In this equation, 4 is multiplied with y, so we want to do the opposite of multiplication, which is division. So, we must divide both sides of the equation by 4 to isolate y on its own. This will result in 4y/4 = 16/4. If the equation was 4+y = 16, subtract 4 would have been accurate."
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "4 is multiplied with y". Therefore, the question is, "I understand! Can you look at the equation and tell me what is the operation that is taking place between 4 and y? Is 4 being added with y or multiplied with y?

context:A teacher and a student are working on the equation x=3. The student could not figure out the correct step to perform. The teacher suggested to perform divide x. This action activated the following conversation:
Student:Why did you do divide x here?
Teacher:because you have to divide the variable on both sides
Student:I'm curious—Please elaborate more on why divide x is correct?
Teacher:it is correct because when you get to the last two numbers you divide by the varieble
<conversation ends>
In this scenario, an ideal response from teacher would be, "Sorry, I made a mistake. We cannot divide by x in this case. Since x is already isolated on one side, we don't need to perform any additional steps. The equation x=3 means that x is already equal to 3, so there is no need for further manipulation."
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "The equation x=3 means that x is already equal to 3". Therefore, the question is, "I am still confused. In this current state of the equation, can you tell me what x equals to?"

context:A teacher and a student are working on the equation 9x+2=x+8. The student performed divide 9, but the teacher disagreed. This action activated the following conversation:
<conversation starts>
Student:Why am I wrong?
Teacher:we are working on a different step of the equation
Student:Got it! At what step do we apply divide in an equation?
Teacher:we apply the divide operation when we are isolating the vaiable, not any time before that
<conversation ends>
In this scenario, an ideal response from teacher would be, "Divide 9 is incorrect here because we divide when there is only one variable term in the entire equation. we have two variable terms in this equation 9x and x. So, in order to simplify this equation, we should combine these two variable terms together. Note that, we can only combine 9x and x together because they are like terms, they both have the same variable letter x. We can subtract x as it is a positive term. Subtract x on both sides will isolate 8 on the right side This will result in 9x - x = x + 8 - x." 
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "because they are like terms, they both have the same variable letter x". Therefore, the question is, "Thanks for being so patient with my questions. I have one more confusion. How do we know if two variable terms are like terms with each other?"

context:A teacher and a student are working on the equation 16=-4y. The student performed divide 16, but the teacher disagreed. This action activated the following conversation:
<conversation starts>
Student:Why am I wrong?
Teacher:because you only divide by a varible
<conversation ends>
In this scenario, an ideal response from teacher would be, "Divide 16 is incorrect here because we must divide by the number that is multiplied with the variable. This number is also called the coefficient. In other word, we always divide by the coefficient. -4 is the coefficient here, not 16. Therefore, to isolate the variable y, we need to divide by -4 as divide is the opposite operation of multiplication. This will result in 16/-4 = -4y/-4."
question: 
To generate a question, we must find out a statement in the ideal teacher reply that was not conveyed by the teacher during the conversation. The teacher did not mention the sentence, "we must divide by the number that is multiplied with the variable." Therefore, the question is, "I think we divide by a number and not a variable. Can that be any number? How do we know which number to divide based on different equation?"
"""

#instruction history:
# Your task is to formulate a question by comparing a teacher reply and an ideal teacher reply given in a context. You will formulate a question from the sentences that the teacher did not state 
# but is present in the ideal teacher reply. Be aware that teacher reply may contain grammatical or spelling errors. You must try to make sense of the teacher reply. If teacher is repeating the same sentences, then you must change your questioning angle. 
### LLM for question generation from missing info: P2
def generate_question_(scene, expected, conversation_hist):
    print("question module 2")
    prompt = f"""
    You are a student who is being taught how to solve an equation by a teacher. You always ask clever question to your teacher. 
    You have a knowledge about how an ideal response from a teacher would look like and you seek that response from the teacher by asking questions.
    You must look for a sentence in an ideal response that was not conveyed by the teacher during the conversation and formulate a question from that missing sentence.
    Note that, teacher's reply may contain grammatical or spelling errors. You must try your best to make sense of the teacher's reply with respect to the provided equation.
    You should never ask the same question that you have already asked during the conversation.
    You may rephrase the same question using some key concepts of the algebra domain from your knowledge of the ideal response.
    Always include "Therefore, the question is," before generating your final question.

A few examples are provided below delimited by triple quotes.
'''{question_contextual_text}'''

context:{scene}. 
<conversation starts>
{conversation_hist}
<conversation ends>
In this scenario, an ideal response from teacher would be, "{expected}"
<generate>

Here are the rules for generating questions:
```
Rule 1: If the teacher's reply contains any calculation error, point it out and then generate a question.
Rule 2: If the teacher's reply contradicts the context, point it out and then generate a question.
Rule 3: If the teacher's reply is calculation error free and aligns with the context, select a sentence from the ideal teacher reply that is missing in the conversation. Ask a question about that sentence.
Rule 4: You never ask the exact same question that is present in the conversation.
Rule 5: If you've previously asked a question about a missing sentence and the teacher didn't respond as expected, focus on another missing sentence and ask question about it.
Rule 6: You must never say "why <step mentioned in an ideal response> is correct?" unless the teacher mentioned that step during conversation.
Rule 7: If you've asked questions about every possible missing sentences from the ideal teacher, say, "no question."
```
"""
    response = get_completion(prompt, .5)
    return response

## main function that prepares the necessary components for question LLM
##___ Main function ____
def generate_question(StepName,Qtype, Sol, first_question,correctness,conversation_hist):
    scene = make_scene_R(StepName,Qtype, Sol, first_question,correctness)
    scene = scene+" Teacher replied, \"<generate>"
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
        if "no contradiction" in contradicts:
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

