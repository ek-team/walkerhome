package com.pharos.walker.beans;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;


/**
 * Created by zhanglun on 2020/6/3
 * Describe:
 */
@Entity(nameInDb = "USER")
public class UserBean implements Parcelable {
    @Id
    private Long id;                 //ID
    //    @Unique
    private long userId;                 //用户唯一ID
    @Index(unique = true)
    private long keyId;                 //唯一ID
    @NotNull
    private String name;            //姓名
    @NotNull
    @Unique
    private String caseHistoryNo;   //病历号
    @NotNull
    private int age;                //年龄
    private String date;            //手术时间
    @NotNull
    private int sex;                //性别  男-1  女-0
    @NotNull
    private String diagnosis;       //诊断结果
    private String photo;
    private String doctor;           //医生
    private String hospitalName;           //医院名称
    private String hospitalAddress;           //医院地址
    private String address;         //联系地址
    private String telephone;       //联系方式
    private String linkman;         //联系人
    private String pingYin;
    private String createDate;      // 创建时间
    private String updateDate;      // 更新时间
    private String remark;      // 备注
    @NotNull
    private String weight;      // 体重
    private float evaluateWeight;      // 评估负重
    private String account;      // 用户名
    private String password;      // 密码
    private String str;      // 保留
    private int isUpload;                //上传状态  0-未上传  1-上传到局域网 2-上传到云端
    private int isFirstRun;                //上传状态  0-第一次  1-已经运行过
    private int isRecordUpdate;                //记录状态  0-有更新  1-没有更新
    @Transient
    private String macAdd;      // 设备Id
    @Generated(hash = 1073133717)
    public UserBean(Long id, long userId, long keyId, @NotNull String name,
            @NotNull String caseHistoryNo, int age, String date, int sex, @NotNull String diagnosis,
            String photo, String doctor, String hospitalName, String hospitalAddress, String address,
            String telephone, String linkman, String pingYin, String createDate, String updateDate,
            String remark, @NotNull String weight, float evaluateWeight, String account,
            String password, String str, int isUpload, int isFirstRun, int isRecordUpdate) {
        this.id = id;
        this.userId = userId;
        this.keyId = keyId;
        this.name = name;
        this.caseHistoryNo = caseHistoryNo;
        this.age = age;
        this.date = date;
        this.sex = sex;
        this.diagnosis = diagnosis;
        this.photo = photo;
        this.doctor = doctor;
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.address = address;
        this.telephone = telephone;
        this.linkman = linkman;
        this.pingYin = pingYin;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.remark = remark;
        this.weight = weight;
        this.evaluateWeight = evaluateWeight;
        this.account = account;
        this.password = password;
        this.str = str;
        this.isUpload = isUpload;
        this.isFirstRun = isFirstRun;
        this.isRecordUpdate = isRecordUpdate;
    }
    @Generated(hash = 1203313951)
    public UserBean() {
    }

    public String getMacAdd() {
        return macAdd;
    }

    public void setMacAdd(String macAdd) {
        this.macAdd = macAdd;
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
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCaseHistoryNo() {
        return this.caseHistoryNo;
    }
    public void setCaseHistoryNo(String caseHistoryNo) {
        this.caseHistoryNo = caseHistoryNo;
    }
    public int getAge() {
        return this.age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getSex() {
        return this.sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    public String getDiagnosis() {
        return this.diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    public String getPhoto() {
        return this.photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getDoctor() {
        return this.doctor;
    }
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
    public String getHospitalName() {
        return this.hospitalName;
    }
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    public String getHospitalAddress() {
        return this.hospitalAddress;
    }
    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getTelephone() {
        return this.telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getLinkman() {
        return this.linkman;
    }
    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }
    public String getPingYin() {
        return this.pingYin;
    }
    public void setPingYin(String pingYin) {
        this.pingYin = pingYin;
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
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getWeight() {
        return this.weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public float getEvaluateWeight() {
        return this.evaluateWeight;
    }
    public void setEvaluateWeight(float evaluateWeight) {
        this.evaluateWeight = evaluateWeight;
    }
    public String getAccount() {
        return this.account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getStr() {
        return this.str;
    }
    public void setStr(String str) {
        this.str = str;
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
        dest.writeString(this.name);
        dest.writeString(this.caseHistoryNo);
        dest.writeInt(this.age);
        dest.writeString(this.date);
        dest.writeInt(this.sex);
        dest.writeString(this.diagnosis);
        dest.writeString(this.photo);
        dest.writeString(this.doctor);
        dest.writeString(this.hospitalName);
        dest.writeString(this.hospitalAddress);
        dest.writeString(this.address);
        dest.writeString(this.telephone);
        dest.writeString(this.linkman);
        dest.writeString(this.pingYin);
        dest.writeString(this.createDate);
        dest.writeString(this.updateDate);
        dest.writeString(this.remark);
        dest.writeString(this.weight);
        dest.writeFloat(this.evaluateWeight);
        dest.writeString(this.account);
        dest.writeString(this.password);
        dest.writeString(this.str);
        dest.writeInt(this.isUpload);
        dest.writeInt(this.isFirstRun);
        dest.writeInt(this.isRecordUpdate);
    }
    public long getKeyId() {
        return this.keyId;
    }
    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }
    public int getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }
    public int getIsFirstRun() {
        return this.isFirstRun;
    }
    public void setIsFirstRun(int isFirstRun) {
        this.isFirstRun = isFirstRun;
    }
    public int getIsRecordUpdate() {
        return this.isRecordUpdate;
    }
    public void setIsRecordUpdate(int isRecordUpdate) {
        this.isRecordUpdate = isRecordUpdate;
    }

    protected UserBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.userId = in.readLong();
        this.keyId = in.readLong();
        this.name = in.readString();
        this.caseHistoryNo = in.readString();
        this.age = in.readInt();
        this.date = in.readString();
        this.sex = in.readInt();
        this.diagnosis = in.readString();
        this.photo = in.readString();
        this.doctor = in.readString();
        this.hospitalName = in.readString();
        this.hospitalAddress = in.readString();
        this.address = in.readString();
        this.telephone = in.readString();
        this.linkman = in.readString();
        this.pingYin = in.readString();
        this.createDate = in.readString();
        this.updateDate = in.readString();
        this.remark = in.readString();
        this.weight = in.readString();
        this.evaluateWeight = in.readFloat();
        this.account = in.readString();
        this.password = in.readString();
        this.str = in.readString();
        this.isUpload = in.readInt();
        this.isFirstRun = in.readInt();
        this.isRecordUpdate = in.readInt();
    }

    public static final Parcelable.Creator<UserBean> CREATOR = new Parcelable.Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
