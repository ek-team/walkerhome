package com.pharos.walker.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

/**
 * Created by zhanglun on 2021/5/7
 * Describe:
 */
@Entity(nameInDb = "SUB_TRAIN_PLAN")
public class SubPlanEntity {
    @Id
    private Long id;                 //ID
    private long userId;//用户唯一id 静态方法获取唯一id编号
    @Index(unique = true)
    private long keyId;                 //唯一ID
    private long planId;//计划id 静态方法获取唯一id编号
    private int planStatus;//计划状态 0未开始，1进行中，2完成
    private int classId;//阶段Id （1到3）3个阶段
    private int load;//负重
    private int weekNum;//第几周
    private int dayNum;//第几周
    private int trainTime;//训练时间
    private int trainStep;//训练步数
    private int modifyStatus;//修改状态  0未修改，1云端修改，2设备端修改
    private String startDate;
    private String endDate;
    @Generated(hash = 310542023)
    public SubPlanEntity(Long id, long userId, long keyId, long planId,
            int planStatus, int classId, int load, int weekNum, int dayNum,
            int trainTime, int trainStep, int modifyStatus, String startDate,
            String endDate) {
        this.id = id;
        this.userId = userId;
        this.keyId = keyId;
        this.planId = planId;
        this.planStatus = planStatus;
        this.classId = classId;
        this.load = load;
        this.weekNum = weekNum;
        this.dayNum = dayNum;
        this.trainTime = trainTime;
        this.trainStep = trainStep;
        this.modifyStatus = modifyStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Generated(hash = 2137579088)
    public SubPlanEntity() {
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
    public long getKeyId() {
        return this.keyId;
    }
    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }
    public long getPlanId() {
        return this.planId;
    }
    public void setPlanId(long planId) {
        this.planId = planId;
    }
    public int getPlanStatus() {
        return this.planStatus;
    }
    public void setPlanStatus(int planStatus) {
        this.planStatus = planStatus;
    }
    public int getClassId() {
        return this.classId;
    }
    public void setClassId(int classId) {
        this.classId = classId;
    }
    public int getLoad() {
        return this.load;
    }
    public void setLoad(int load) {
        this.load = load;
    }
    public int getWeekNum() {
        return this.weekNum;
    }
    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }
    public int getDayNum() {
        return this.dayNum;
    }
    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }
    public int getTrainTime() {
        return this.trainTime;
    }
    public void setTrainTime(int trainTime) {
        this.trainTime = trainTime;
    }
    public int getTrainStep() {
        return this.trainStep;
    }
    public void setTrainStep(int trainStep) {
        this.trainStep = trainStep;
    }
    public int getModifyStatus() {
        return this.modifyStatus;
    }
    public void setModifyStatus(int modifyStatus) {
        this.modifyStatus = modifyStatus;
    }
    public String getStartDate() {
        return this.startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return this.endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
