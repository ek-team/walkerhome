package com.pharos.walker.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
@Entity(nameInDb = "OTHER_FEEDBACK")
public class OtherFeedbackBean {
    @Id
    private Long id;//ID
    private long keyId;//唯一ID
    private String macAddress;
    @Index(unique = true)
    private String value;
    private String str;//保留字段
    private long createDate;
    @Generated(hash = 1453257256)
    public OtherFeedbackBean(Long id, long keyId, String macAddress, String value,
            String str, long createDate) {
        this.id = id;
        this.keyId = keyId;
        this.macAddress = macAddress;
        this.value = value;
        this.str = str;
        this.createDate = createDate;
    }
    @Generated(hash = 530408524)
    public OtherFeedbackBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getKeyId() {
        return this.keyId;
    }
    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }
    public String getMacAddress() {
        return this.macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getStr() {
        return this.str;
    }
    public void setStr(String str) {
        this.str = str;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

}
