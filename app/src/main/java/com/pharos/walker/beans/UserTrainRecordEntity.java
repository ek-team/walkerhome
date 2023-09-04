package com.pharos.walker.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * Created by zhanglun on 2021/5/11
 * Describe:
 */
@Entity(nameInDb = "USER_TRAIN_RECORD")
public class UserTrainRecordEntity {
    @Id
    private Long id;                 //ID
    private long userId;//用户唯一id 静态方法获取唯一id编号
    @Index(unique = true)
    private long keyId;                 //唯一ID
    private int  successTime;//成功次数
    private int  warningTime;//警告次数
    private int  trainTime;//训练时间
    private int  score;//得分
    private int painLevel;//疼痛等级
    private String  adverseReactions;//不良反应
    private int targetLoad;//目标负重
    private long createDate;
    private int frequency;//每天次数
    private String diagnostic;//患病类型
    private String str;//保留
    private long planId;
    private int classId;
    private int isUpload;                //上传状态  0-未上传  1-上传到局域网 2-上传到云端
    private String dateStr;
    @Transient
    private List<TrainDataEntity> trainDataList;

    public List<TrainDataEntity> getTrainDataList() {
        return trainDataList;
    }

    public void setTrainDataList(List<TrainDataEntity> trainDataList) {
        this.trainDataList = trainDataList;
    }

    @Generated(hash = 649976034)
    public UserTrainRecordEntity(Long id, long userId, long keyId, int successTime,
            int warningTime, int trainTime, int score, int painLevel,
            String adverseReactions, int targetLoad, long createDate, int frequency,
            String diagnostic, String str, long planId, int classId, int isUpload,
            String dateStr) {
        this.id = id;
        this.userId = userId;
        this.keyId = keyId;
        this.successTime = successTime;
        this.warningTime = warningTime;
        this.trainTime = trainTime;
        this.score = score;
        this.painLevel = painLevel;
        this.adverseReactions = adverseReactions;
        this.targetLoad = targetLoad;
        this.createDate = createDate;
        this.frequency = frequency;
        this.diagnostic = diagnostic;
        this.str = str;
        this.planId = planId;
        this.classId = classId;
        this.isUpload = isUpload;
        this.dateStr = dateStr;
    }
    @Generated(hash = 74912295)
    public UserTrainRecordEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public int getSuccessTime() {
        return this.successTime;
    }
    public void setSuccessTime(int successTime) {
        this.successTime = successTime;
    }
    public int getWarningTime() {
        return this.warningTime;
    }
    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
    }
    public int getTrainTime() {
        return this.trainTime;
    }
    public void setTrainTime(int trainTime) {
        this.trainTime = trainTime;
    }
    public int getScore() {
        return this.score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public int getPainLevel() {
        return this.painLevel;
    }
    public void setPainLevel(int painLevel) {
        this.painLevel = painLevel;
    }
    public String getAdverseReactions() {
        return this.adverseReactions;
    }
    public void setAdverseReactions(String adverseReactions) {
        this.adverseReactions = adverseReactions;
    }
    public int getTargetLoad() {
        return this.targetLoad;
    }
    public void setTargetLoad(int targetLoad) {
        this.targetLoad = targetLoad;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public int getFrequency() {
        return this.frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public String getDiagnostic() {
        return this.diagnostic;
    }
    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }
    public String getStr() {
        return this.str;
    }
    public void setStr(String str) {
        this.str = str;
    }
    public long getPlanId() {
        return this.planId;
    }
    public void setPlanId(long planId) {
        this.planId = planId;
    }
    public int getClassId() {
        return this.classId;
    }
    public void setClassId(int classId) {
        this.classId = classId;
    }
    public String getDateStr() {
        return this.dateStr;
    }
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
    public int getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }
    public long getKeyId() {
        return this.keyId;
    }
    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }
    


}
