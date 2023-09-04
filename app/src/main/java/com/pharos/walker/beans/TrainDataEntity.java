package com.pharos.walker.beans;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

/**
 * Created by zhanglun on 2021/5/20
 * Describe:
 */
@Entity(nameInDb = "TRAIN_DATA")
public class TrainDataEntity implements Parcelable {
    @Id
    private Long id;                 //ID
    private long userId;//用户唯一id 静态方法获取唯一id编号
    @Index(unique = true)
    private long keyId;                 //唯一ID
    private long createDate;
    private int frequency;//当天第几次
    private int targetLoad;//目标负重
    private int realLoad;//实际负重
    private long planId;
    private int classId;
    private int isUpload;
    private String dateStr;
    @Generated(hash = 1500104720)
    public TrainDataEntity(Long id, long userId, long keyId, long createDate, int frequency, int targetLoad,
            int realLoad, long planId, int classId, int isUpload, String dateStr) {
        this.id = id;
        this.userId = userId;
        this.keyId = keyId;
        this.createDate = createDate;
        this.frequency = frequency;
        this.targetLoad = targetLoad;
        this.realLoad = realLoad;
        this.planId = planId;
        this.classId = classId;
        this.isUpload = isUpload;
        this.dateStr = dateStr;
    }
    @Generated(hash = 1535441825)
    public TrainDataEntity() {
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
    public int getTargetLoad() {
        return this.targetLoad;
    }
    public void setTargetLoad(int targetLoad) {
        this.targetLoad = targetLoad;
    }
    public int getRealLoad() {
        return this.realLoad;
    }
    public void setRealLoad(int realLoad) {
        this.realLoad = realLoad;
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
    public int getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }
    public String getDateStr() {
        return this.dateStr;
    }
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeLong(this.userId);
        dest.writeLong(this.keyId);
        dest.writeLong(this.createDate);
        dest.writeInt(this.frequency);
        dest.writeInt(this.targetLoad);
        dest.writeInt(this.realLoad);
        dest.writeLong(this.planId);
        dest.writeInt(this.classId);
        dest.writeInt(this.isUpload);
        dest.writeString(this.dateStr);
    }
    public long getKeyId() {
        return this.keyId;
    }
    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    protected TrainDataEntity(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.userId = in.readLong();
        this.keyId = in.readLong();
        this.createDate = in.readLong();
        this.frequency = in.readInt();
        this.targetLoad = in.readInt();
        this.realLoad = in.readInt();
        this.planId = in.readLong();
        this.classId = in.readInt();
        this.isUpload = in.readInt();
        this.dateStr = in.readString();
    }

    public static final Parcelable.Creator<TrainDataEntity> CREATOR = new Parcelable.Creator<TrainDataEntity>() {
        @Override
        public TrainDataEntity createFromParcel(Parcel source) {
            return new TrainDataEntity(source);
        }

        @Override
        public TrainDataEntity[] newArray(int size) {
            return new TrainDataEntity[size];
        }
    };
}
