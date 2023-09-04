package com.pharos.walker.beans;

import java.util.List;

public class ServerEvaluateEntity {


    /**
     * code : 0
     * data : {"current":1,"pages":1,"records":[{"createDate":1638257455407,"evaluateResult":12,"firstValue":0,"id":1,"isUpload":0,"keyId":0,"secondValue":0,"thirdValue":0,"updateDate":0,"userId":1465583465904295936,"vas":1},{"createDate":1642400458870,"evaluateResult":7,"firstValue":10,"id":2,"isUpload":0,"keyId":1482961124699230208,"secondValue":9,"thirdValue":2,"updateDate":0,"userId":1465583465904295936,"vas":1},{"createDate":1642485612486,"evaluateResult":7,"firstValue":5,"id":3,"isUpload":0,"keyId":1483318284851433472,"secondValue":8,"thirdValue":8,"updateDate":0,"userId":1465583465904295936,"vas":1}],"searchCount":true,"size":4,"total":3}
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
         * current : 1
         * pages : 1
         * records : [{"createDate":1638257455407,"evaluateResult":12,"firstValue":0,"id":1,"isUpload":0,"keyId":0,"secondValue":0,"thirdValue":0,"updateDate":0,"userId":1465583465904295936,"vas":1},{"createDate":1642400458870,"evaluateResult":7,"firstValue":10,"id":2,"isUpload":0,"keyId":1482961124699230208,"secondValue":9,"thirdValue":2,"updateDate":0,"userId":1465583465904295936,"vas":1},{"createDate":1642485612486,"evaluateResult":7,"firstValue":5,"id":3,"isUpload":0,"keyId":1483318284851433472,"secondValue":8,"thirdValue":8,"updateDate":0,"userId":1465583465904295936,"vas":1}]
         * searchCount : true
         * size : 4
         * total : 3
         */

        private int current;
        private int pages;
        private boolean searchCount;
        private int size;
        private int total;
        private List<EvaluateEntity> records;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<EvaluateEntity> getRecords() {
            return records;
        }

        public void setRecords(List<EvaluateEntity> records) {
            this.records = records;
        }


    }
}
