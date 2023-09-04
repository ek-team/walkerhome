package com.pharos.walker.beans;

import java.util.List;

public class HospitalBean {

    /**
     * code : 0
     * data : [{"area":"河东区","city":"市辖区","createTime":"2021-08-13 15:41:42","id":6,"name":"天津二院","province":"天津市","updateTime":"2021-08-14 17:20:42"},{"area":"河东区","city":"市辖区","createTime":"2021-08-13 15:48:54","id":7,"name":"天津三院","province":"天津市","updateTime":"2021-08-14 17:20:56"}]
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
         * area : 河东区
         * city : 市辖区
         * createTime : 2021-08-13 15:41:42
         * id : 6
         * name : 天津二院
         * province : 天津市
         * updateTime : 2021-08-14 17:20:42
         */

        private String area;
        private String city;
        private String createTime;
        private int id;
        private String name;
        private String province;
        private String updateTime;

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
