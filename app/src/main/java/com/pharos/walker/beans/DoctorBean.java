package com.pharos.walker.beans;

import java.util.List;

public class DoctorBean {

    /**
     * code : 0
     * data : [{"avatar":"","city":"","confirmOrder":0,"country":"","createTime":"2021-08-12 19:24:06","delFlag":"0","deptId":449,"gender":"","id":334,"language":"","lockFlag":"0","nickname":"顾意","phone":"18221983024","province":"","realName":"顾意","updateTime":"2021-08-16 11:15:00"}]
     */

    private int code;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * avatar :
         * city :
         * confirmOrder : 0
         * country :
         * createTime : 2021-08-12 19:24:06
         * delFlag : 0
         * deptId : 449
         * gender :
         * id : 334
         * language :
         * lockFlag : 0
         * nickname : 顾意
         * phone : 18221983024
         * province :
         * realName : 顾意
         * updateTime : 2021-08-16 11:15:00
         */

        private String avatar;
        private String city;
        private int confirmOrder;
        private String country;
        private String createTime;
        private String delFlag;
        private int deptId;
        private String gender;
        private int id;
        private String language;
        private String lockFlag;
        private String nickname;
        private String phone;
        private String province;
        private String realName;
        private String updateTime;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getConfirmOrder() {
            return confirmOrder;
        }

        public void setConfirmOrder(int confirmOrder) {
            this.confirmOrder = confirmOrder;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getDelFlag() {
            return delFlag;
        }

        public void setDelFlag(String delFlag) {
            this.delFlag = delFlag;
        }

        public int getDeptId() {
            return deptId;
        }

        public void setDeptId(int deptId) {
            this.deptId = deptId;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getLockFlag() {
            return lockFlag;
        }

        public void setLockFlag(String lockFlag) {
            this.lockFlag = lockFlag;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
