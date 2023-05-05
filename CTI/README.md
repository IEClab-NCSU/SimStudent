
# Tutors-Knowledge-Building-Response-Classification

## Abstract:

Students learn by teaching a teachable agent, a phenomenon called tutor learning. Literature suggests that tutor learning happens when students (who tutor the teachable agent) actively reflect on their knowledge when responding to the teachable agent’s inquiries (aka knowledge-building). However, most students often lean towards delivering what they already know instead of reflecting on their knowledge (aka knowledge-telling). The knowledge-telling behavior weakens the effect of tutor learning. We hypothesize that the teachable agent can help students commit to knowledge-building by being inquisitive and asking follow-up inquiries when students engage in knowledge-telling. Despite the known benefits of knowledge-building, no prior work has operationalized the identification of knowledge-building and knowledge-telling features from students’ responses to teachable agent’s inquiries and governed them toward knowledge-building. We propose a Constructive Tutee Inquiry that aims to provide follow-up inquiries to guide students toward knowledge-building when they provide a knowledge-telling response. Results from an evaluation study show that students who were treated by Constructive Tutee Inquiry not only outperformed those who were not treated but also learned to engage in knowledge-building without the aid of follow-up inquiries over time.


## Tutor Response data

APLUS is a learning by teaching environment where middle school students act as tutors to teach a synthetic tutee named SimStudent. In our current work, SimStudent is taught how to solve linear algebraic equations. SimStudent often ask questions regarding solution steps demonstrated to it by the tutors.
The "tutor_response.csv" contains 2676 human tutor responses collected over years and categorized by two human coders into three main classes mentioned in the paper. R0 class represents the ill-formed or irrelevant responses and responses that are relevant but not why informative, R1 class represents the response where tutors acknowledges their mistakes and finally R2 class represents relevant descriptive why informative responses. Based on Cohen’s Kappa coefficient, the inter-coder reliability for this coding showed κ = 0.81. Disagreements were resolved through discussion.


## How to train own LightSide model using provided response data

1. Download LightSide: https://www.cs.cmu.edu/~cprose/LightSIDE.html
2. LightSide provides convenient GUI to upload the .csv format dataset to be used as the input data.
3. Follow the LightSide guidelines to extract feature and build the model. For the best accuracy, I applied all the provided features except ignoring all stopwords and stetchy patterns and trained a SVM model using cross validations.
4. Once the model is trained, you can export the model in .xml format. LightSide provides APIs to upload the trained model and start a server that can take appropriate inputs and consult with the trained model to generate the label of that input.


## How to use the Response Classifier model

You may use our provided model "response_classifier_svm_f.model" trained on the response dataset (accuracy: 93%). Follow step 4 of the previous section to be able to use this model in your application.
