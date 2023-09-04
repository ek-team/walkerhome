package com.pharos.walker.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
@Entity(nameInDb = "TRAIN_PLAN")
public class PlanEntity {
    @Id
    private Long id;                 //ID
    private long userId;//用户唯一id 静态方法获取唯一id编号
    @Index(unique = true)
    private long keyId;                 //唯一ID
    private long planId;//计划id 静态方法获取唯一id编号
    private int planType;//训练计划类型 短期0，中期1，长期2
    private String createDate;//创建时间
    private String updateDate;//更新时间
    private String weight;//患者体重
    private int planTotalDay;//训练周期 （天）
    private int classId;//阶段Id （1到3）3个阶段
    private int planStatus;//计划状态 0未开始，1进行中，2完成
    private String startDate;//开始时间
    private String endDate;//结束时间
    private int timeOfDay;//每天训练次数
    private int countOfTime;//每次训练步数
    private int load;//负重
    private int trainTime;//训练时间
    private int trainType;//训练方式 0按步数，1按时间
    private String str;//保留字段
    private String remark;//备注
    @Transient
    private List<SubPlanEntity> subPlanEntityList;

    public List<SubPlanEntity> getSubPlanEntityList() {
        return subPlanEntityList;
    }

    public void setSubPlanEntityList(List<SubPlanEntity> subPlanEntityList) {
        this.subPlanEntityList = subPlanEntityList;
    }

    @Generated(hash = 849213306)
    public PlanEntity(Long id, long userId, long keyId, long planId, int planType,
            String createDate, String updateDate, String weight, int planTotalDay,
            int classId, int planStatus, String startDate, String endDate,
            int timeOfDay, int countOfTime, int load, int trainTime, int trainType,
            String str, String remark) {
        this.id = id;
        this.userId = userId;
        this.keyId = keyId;
        this.planId = planId;
        this.planType = planType;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.weight = weight;
        this.planTotalDay = planTotalDay;
        this.classId = classId;
        this.planStatus = planStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeOfDay = timeOfDay;
        this.countOfTime = countOfTime;
        this.load = load;
        this.trainTime = trainTime;
        this.trainType = trainType;
        this.str = str;
        this.remark = remark;
    }
    @Generated(hash = 8992154)
    public PlanEntity() {
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
    public long getPlanId() {
        return this.planId;
    }
    public void setPlanId(long planId) {
        this.planId = planId;
    }
    public int getPlanType() {
        return this.planType;
    }
    public void setPlanType(int planType) {
        this.planType = planType;
    }
    public String getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getUpdateDate() {
        return this.updateDate;
    }
    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
    public String getWeight() {
        return this.weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public int getPlanTotalDay() {
        return this.planTotalDay;
    }
    public void setPlanTotalDay(int planTotalDay) {
        this.planTotalDay = planTotalDay;
    }
    public int getClassId() {
        return this.classId;
    }
    public void setClassId(int classId) {
        this.classId = classId;
    }
    public int getPlanStatus() {
        return this.planStatus;
    }
    public void setPlanStatus(int planStatus) {
        this.planStatus = planStatus;
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
    public int getTimeOfDay() {
        return this.timeOfDay;
    }
    public void setTimeOfDay(int timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
    public int getCountOfTime() {
        return this.countOfTime;
    }
    public void setCountOfTime(int countOfTime) {
        this.countOfTime = countOfTime;
    }
    public int getLoad() {
        return this.load;
    }
    public void setLoad(int load) {
        this.load = load;
    }
    public int getTrainTime() {
        return this.trainTime;
    }
    public void setTrainTime(int trainTime) {
        this.trainTime = trainTime;
    }
    public int getTrainType() {
        return this.trainType;
    }
    public void setTrainType(int trainType) {
        this.trainType = trainType;
    }
    public String getStr() {
        return this.str;
    }
    public void setStr(String str) {
        this.str = str;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public long getKeyId() {
        return this.keyId;
    }
    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }


}
