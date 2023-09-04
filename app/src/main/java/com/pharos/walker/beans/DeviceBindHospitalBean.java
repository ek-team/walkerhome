package com.pharos.walker.beans;

import java.util.List;

public class DeviceBindHospitalBean {

    /**
     * code : 0
     * data : {"area":"鼓楼区","city":"福州市","createTime":"2021-09-09 14:58:40","id":20,"locatorRegionIds":"100000;350000;350100;350102","locatorRegions":[100000,350000,350100,350102],"name":"测试医院","province":"福建省"}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * area : 鼓楼区
         * city : 福州市
         * createTime : 2021-09-09 14:58:40
         * id : 20
         * locatorRegionIds : 100000;350000;350100;350102
         * locatorRegions : [100000,350000,350100,350102]
         * name : 测试医院
         * province : 福建省
         */

        private String area;
        private String city;
        private String createTime;
        private int id;
        private String locatorRegionIds;
        private String name;
        private String province;
        private List<Integer> locatorRegions;

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

        public String getLocatorRegionIds() {
            return locatorRegionIds;
        }

        public void setLocatorRegionIds(String locatorRegionIds) {
            this.locatorRegionIds = locatorRegionIds;
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

        public List<Integer> getLocatorRegions() {
            return locatorRegions;
        }

        public void setLocatorRegions(List<Integer> locatorRegions) {
            this.locatorRegions = locatorRegions;
        }
    }
}
