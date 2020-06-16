package com.simon.entity;

import java.io.Serializable;
import java.util.Date;

public class UserBaseExt implements Serializable {
    private static final long serialVersionUID = -6499323200191210049L;
    private Long id;

    private Long stuId;

    private Byte schoolType;

    private Byte enInitTime;

    private Byte learningExperience;

    private String learningExperienceRemark;

    private Byte currentEnlevel;

    private Byte character;

    private Byte parentEnlevel;

    private Byte knowAbout51;

    private Byte childrenNum;

    private Long ftAppointId;

    private String ftStatus;

    private Date ftStartTime;

    private Byte ftBookId;

    private Byte ftPreviewStatus;

    private Byte ftDevice;

    private String teaGenderLike;

    private Byte teaCharacterLike;

    private Byte accompanyWith;

    private String preInform;

    private Byte willToPay;

    private Date updateTime;

    private Date addTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStuId() {
        return stuId;
    }

    public void setStuId(Long stuId) {
        this.stuId = stuId;
    }

    public Byte getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(Byte schoolType) {
        this.schoolType = schoolType;
    }

    public Byte getEnInitTime() {
        return enInitTime;
    }

    public void setEnInitTime(Byte enInitTime) {
        this.enInitTime = enInitTime;
    }

    public Byte getLearningExperience() {
        return learningExperience;
    }

    public void setLearningExperience(Byte learningExperience) {
        this.learningExperience = learningExperience;
    }

    public String getLearningExperienceRemark() {
        return learningExperienceRemark;
    }

    public void setLearningExperienceRemark(String learningExperienceRemark) {
        this.learningExperienceRemark = learningExperienceRemark;
    }

    public Byte getCurrentEnlevel() {
        return currentEnlevel;
    }

    public void setCurrentEnlevel(Byte currentEnlevel) {
        this.currentEnlevel = currentEnlevel;
    }

    public Byte getCharacter() {
        return character;
    }

    public void setCharacter(Byte character) {
        this.character = character;
    }

    public Byte getParentEnlevel() {
        return parentEnlevel;
    }

    public void setParentEnlevel(Byte parentEnlevel) {
        this.parentEnlevel = parentEnlevel;
    }

    public Byte getKnowAbout51() {
        return knowAbout51;
    }

    public void setKnowAbout51(Byte knowAbout51) {
        this.knowAbout51 = knowAbout51;
    }

    public Byte getChildrenNum() {
        return childrenNum;
    }

    public void setChildrenNum(Byte childrenNum) {
        this.childrenNum = childrenNum;
    }

    public Long getFtAppointId() {
        return ftAppointId;
    }

    public void setFtAppointId(Long ftAppointId) {
        this.ftAppointId = ftAppointId;
    }

    public String getFtStatus() {
        return ftStatus;
    }

    public void setFtStatus(String ftStatus) {
        this.ftStatus = ftStatus;
    }

    public Date getFtStartTime() {
        return ftStartTime;
    }

    public void setFtStartTime(Date ftStartTime) {
        this.ftStartTime = ftStartTime;
    }

    public Byte getFtBookId() {
        return ftBookId;
    }

    public void setFtBookId(Byte ftBookId) {
        this.ftBookId = ftBookId;
    }

    public Byte getFtPreviewStatus() {
        return ftPreviewStatus;
    }

    public void setFtPreviewStatus(Byte ftPreviewStatus) {
        this.ftPreviewStatus = ftPreviewStatus;
    }

    public Byte getFtDevice() {
        return ftDevice;
    }

    public void setFtDevice(Byte ftDevice) {
        this.ftDevice = ftDevice;
    }

    public String getTeaGenderLike() {
        return teaGenderLike;
    }

    public void setTeaGenderLike(String teaGenderLike) {
        this.teaGenderLike = teaGenderLike;
    }

    public Byte getTeaCharacterLike() {
        return teaCharacterLike;
    }

    public void setTeaCharacterLike(Byte teaCharacterLike) {
        this.teaCharacterLike = teaCharacterLike;
    }

    public Byte getAccompanyWith() {
        return accompanyWith;
    }

    public void setAccompanyWith(Byte accompanyWith) {
        this.accompanyWith = accompanyWith;
    }

    public String getPreInform() {
        return preInform;
    }

    public void setPreInform(String preInform) {
        this.preInform = preInform;
    }

    public Byte getWillToPay() {
        return willToPay;
    }

    public void setWillToPay(Byte willToPay) {
        this.willToPay = willToPay;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}