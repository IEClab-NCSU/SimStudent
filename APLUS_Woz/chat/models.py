from django.db import models

#import django_tables as tables

# Create your models here.
# model session : session_id, student_name(simstudent), teacher_name

# model tutor_tutee convo: convo-id, comment, comment by, timestamp, session-id
# model tutor_metatutor convo: convo-id, comment, comment by, timestamp, session-id


class Session (models.Model):
    tutee_name = models.TextField()
    tutor_name = models.TextField()
    image_name = models.TextField(default="000")
    created_at = models.DateTimeField(auto_now=True);


class MetaHints (models.Model):
    hints = models.TextField()
    tags = models.TextField()


class TutorTuteeConversation(models.Model):

    comment_content = models.TextField()
    comment_by = models.TextField()
    comment_time = models.DateTimeField(auto_now_add=True)
    session_id = models.ForeignKey(Session, related_name='session_tt', on_delete=models.CASCADE)

    def __str__(self):
        return self.comment_by

    def last_10_messages(self):
        return TutorTuteeConversation.objects.order_by('-comment_time').all()[:10]


class TutorMetaTutorConversation(models.Model):

    comment_content = models.TextField()
    comment_by = models.TextField()
    comment_time = models.DateTimeField(auto_now_add=True)
    session_id = models.ForeignKey(Session, related_name='session_tm', on_delete=models.CASCADE)


class ProblemBank(models.Model):

    problem = models.TextField()
    problem_type = models.IntegerField()
    problem_rating = models.IntegerField()


class WorkOutProblems(models.Model):

    dormin_fields = models.TextField()
    dormin_contents = models.TextField()
    session_id = models.ForeignKey(Session, related_name='session_problems', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now=True);


class QuizUpdate(models.Model):

    update_contents = models.TextField()
    session_id = models.ForeignKey(Session, related_name='session_quiz', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now=True);


class ActionLogs(models.Model):

    actions_text = models.TextField()
    cf_action = models.TextField(default="")
    session_id = models.ForeignKey(Session, related_name='session_actions', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now=True);
    selection_tutor = models.TextField(default="")
    action_tutor = models.TextField(default="")
    input_tutor = models.TextField(default="")
    selection_tutee = models.TextField(default="")
    action_tutee = models.TextField(default="")
    input_tutee = models.TextField(default="")
    selection_meta = models.TextField(default="")
    action_meta = models.TextField(default="")
    input_meta = models.TextField(default="")
    new_problem_entered = models.TextField(default="")
    hint_requested = models.TextField(default="")
    hint_given = models.TextField(default="")
    dialogue_from_tutor = models.TextField(default="")
    dialogue_from_tutee = models.TextField(default="")
    current_equation_state = models.TextField(default="")
    is_correct_step = models.TextField(default="")
    dialogue_generated_tag = models.TextField(default="")
    question_id = models.TextField(default="")

class RetainSessionData(models.Model):

    session_id = models.ForeignKey(Session, related_name='session_persist_data', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now=True);
    global_index = models.TextField(default="")
    correctness_tags = models.TextField(default="aaaaaaaaaaaaaaaaaaaaaa")

class QuestionsUnderTypes(models.Model):
    questions = models.TextField();

    class Meta:
        ordering = ('questions',)

    def __str__(self):
        return self.questions


class QuestionBank(models.Model):
    TUTORING_PHASE_CHOICES = (
        (1, 'Pre'),
        (2, 'During'),
        (3, 'Post')
    )
    question_type = models.TextField();
    all_questions = models.ManyToManyField(QuestionsUnderTypes);
    tutoring_phase = models.IntegerField(choices=TUTORING_PHASE_CHOICES, default=1)

    class Meta:
        ordering = ('question_type',)

    def __str__(self):
        return self.question_type
