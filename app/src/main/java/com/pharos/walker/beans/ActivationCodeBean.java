package com.pharos.walker.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "ACTIVATION_CODE")
public class ActivationCodeBean {
    @Id(autoincrement = true)
    private Long id;                 //ID
    private String macAddress;
    private long createDate;
    private long recordDate;
    private String publicKey;
    private String activationCode;
    private String qrcodeLink;
    @Generated(hash = 1525779526)
    public ActivationCodeBean(Long id, String macAddress, long createDate,
            long recordDate, String publicKey, String activationCode,
            String qrcodeLink) {
        this.id = id;
        this.macAddress = macAddress;
        this.createDate = createDate;
        this.recordDate = recordDate;
        this.publicKey = publicKey;
        this.activationCode = activationCode;
        this.qrcodeLink = qrcodeLink;
    }
    @Generated(hash = 752200252)
    public ActivationCodeBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMacAddress() {
        return this.macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public long getRecordDate() {
        return this.recordDate;
    }
    public void setRecordDate(long recordDate) {
        this.recordDate = recordDate;
    }
    public String getPublicKey() {
        return this.publicKey;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    public String getActivationCode() {
        return this.activationCode;
    }
    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
    public String getQrcodeLink() {
        return this.qrcodeLink;
    }
    public void setQrcodeLink(String qrcodeLink) {
        this.qrcodeLink = qrcodeLink;
    }


}
