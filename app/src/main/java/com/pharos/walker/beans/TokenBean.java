package com.pharos.walker.beans;

import java.util.List;

public class TokenBean {


    /**
     * code : 0
     * data : {"access_token":"2613f71c-58f3-480a-9443-03bf9dc00414","license":"made by cup","expired":false,"scope":["server"],"token_type":"bearer","expires_in":42820}
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
         * access_token : 2613f71c-58f3-480a-9443-03bf9dc00414
         * license : made by cup
         * expired : false
         * scope : ["server"]
         * token_type : bearer
         * expires_in : 42820
         */

        private String access_token;
        private String license;
        private boolean expired;
        private String token_type;
        private int expires_in;
        private List<String> scope;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public boolean isExpired() {
            return expired;
        }

        public void setExpired(boolean expired) {
            this.expired = expired;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }

        public List<String> getScope() {
            return scope;
        }

        public void setScope(List<String> scope) {
            this.scope = scope;
        }
    }
}
