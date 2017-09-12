package com.tonyliu.entity;

import java.io.Serializable;

/**
 * Created by tao on 8/3/17.
 */
public class Xblock implements Serializable{

    private int id;
    private String xblockId;
    private String typeOfXblock;
    private String title;
    private String subTitle;
    private String text;
    private String question;
    private String choices;
    private String imageUrl;
    private String correctAnswer;
    private String hint;
    private String problemName;
    private String skillName;
    private String htmlUrl;
    private String brdUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXblockId() {
        return xblockId;
    }

    public void setXblockId(String xblockId) {
        this.xblockId = xblockId;
    }

    public String getTypeOfXblock() {
        return typeOfXblock;
    }

    public void setTypeOfXblock(String typeOfXblock) {
        this.typeOfXblock = typeOfXblock;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getBrdUrl() {
        return brdUrl;
    }

    public void setBrdUrl(String brdUrl) {
        this.brdUrl = brdUrl;
    }
}
