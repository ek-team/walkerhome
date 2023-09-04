package com.pharos.walker.beans;

public class ServerActivationCodeBean {

    /**
     * code : 0
     * data : {"activationCode":"cc9bbc12a1f7038472217c61af0797a7","createDate":"2021-08-12 11:38:46","deptId":449,"id":135,"liveQrCodeId":"646531b10bef2482cc30ff7344ddedcc","locatorId":5,"macAddress":"40:45:da:96:b6:52","productId":1,"productLockNum":0,"productSn":"test-mac 1235","salesmanId":199,"status":20}
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
         * activationCode : cc9bbc12a1f7038472217c61af0797a7
         * createDate : 2021-08-12 11:38:46
         * deptId : 449
         * id : 135
         * liveQrCodeId : 646531b10bef2482cc30ff7344ddedcc
         * locatorId : 5
         * macAddress : 40:45:da:96:b6:52
         * productId : 1
         * productLockNum : 0
         * productSn : test-mac 1235
         * salesmanId : 199
         * status : 20
         */

        private String activationCode;
        private String createDate;
        private int deptId;
        private int id;
        private String liveQrCodeId;
        private int locatorId;
        private String macAddress;
        private int productId;
        private int productLockNum;
        private String productSn;
        private int salesmanId;
        private int status;

        public String getActivationCode() {
            return activationCode;
        }

        public void setActivationCode(String activationCode) {
            this.activationCode = activationCode;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public int getDeptId() {
            return deptId;
        }

        public void setDeptId(int deptId) {
            this.deptId = deptId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLiveQrCodeId() {
            return liveQrCodeId;
        }

        public void setLiveQrCodeId(String liveQrCodeId) {
            this.liveQrCodeId = liveQrCodeId;
        }

        public int getLocatorId() {
            return locatorId;
        }

        public void setLocatorId(int locatorId) {
            this.locatorId = locatorId;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getProductLockNum() {
            return productLockNum;
        }

        public void setProductLockNum(int productLockNum) {
            this.productLockNum = productLockNum;
        }

        public String getProductSn() {
            return productSn;
        }

        public void setProductSn(String productSn) {
            this.productSn = productSn;
        }

        public int getSalesmanId() {
            return salesmanId;
        }

        public void setSalesmanId(int salesmanId) {
            this.salesmanId = salesmanId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
