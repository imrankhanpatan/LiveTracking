package com.toolyt.location.Utils;

public class ToolytUser {
    private String userid;
    private String userName;
    private String companyId;
    private String color;


    public ToolytUser(String user_id, String user_name, String company_id, String color) {
        this.userid = user_id;
        this.userName = user_name;
        this.companyId = company_id;
        this.color = color;
        App.getInstance().setUserid(userid);
        App.getInstance().setUserName(userName);
        App.getInstance().setCompanyId(companyId);
        App.getInstance().setColor(color);
    }


    public static class Builder {
        private String userId;
        private String userName;
        private String companyId;
        private String color;

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setCompanyId(String companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public ToolytUser build() {
            return new ToolytUser(userId, userName, companyId, color);
        }
    }
}
