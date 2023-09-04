package com.pharos.walker.beans;

import java.util.List;

public class ServerTrainDataBean {

    /**
     * code : 0
     * data : {"current":1,"pages":0,"records":[{"adverseReactions":"","classId":0,"createDate":null,"dateStr":"","diagnostic":"","frequency":0,"id":0,"isUpload":0,"keyId":0,"painLevel":0,"planId":0,"score":0,"str":"","successTime":0,"targetLoad":0,"trainDataList":[{"classId":0,"createDate":null,"dateStr":"","frequency":0,"id":0,"isUpload":0,"keyId":0,"planId":0,"realLoad":0,"recordId":0,"targetLoad":0,"userId":0}],"trainTime":null,"userId":0,"warningTime":0}],"searchCount":true,"size":10,"total":0}
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
         * pages : 0
         * records : [{"adverseReactions":"","classId":0,"createDate":null,"dateStr":"","diagnostic":"","frequency":0,"id":0,"isUpload":0,"keyId":0,"painLevel":0,"planId":0,"score":0,"str":"","successTime":0,"targetLoad":0,"trainDataList":[{"classId":0,"createDate":null,"dateStr":"","frequency":0,"id":0,"isUpload":0,"keyId":0,"planId":0,"realLoad":0,"recordId":0,"targetLoad":0,"userId":0}],"trainTime":null,"userId":0,"warningTime":0}]
         * searchCount : true
         * size : 10
         * total : 0
         */

        private int current;
        private int pages;
        private boolean searchCount;
        private int size;
        private int total;
        private List<UserTrainRecordEntity> records;

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

        public List<UserTrainRecordEntity> getRecords() {
            return records;
        }

        public void setRecords(List<UserTrainRecordEntity> records) {
            this.records = records;
        }


    }
}
