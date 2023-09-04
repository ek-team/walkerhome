package com.pharos.walker.beans;

public class VersionInfoBean {

    /**
     * code : 0
     * data : {"createTime":"2021-08-10 15:26:12","id":1,"updateTime":"2021-08-17 15:18:13","url":"https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/apk/Walker2_V13-20210729_R.apk","userId":1,"version":"3.4"}
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
         * createTime : 2021-08-10 15:26:12
         * id : 1
         * updateTime : 2021-08-17 15:18:13
         * url : https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/apk/Walker2_V13-20210729_R.apk
         * userId : 1
         * version : 3.4
         */

        private String createTime;
        private int id;
        private String updateTime;
        private String url;
        private int userId;
        private String version;

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

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
