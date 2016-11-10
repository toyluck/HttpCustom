package com.example.hyc.httpcustom.models;

import com.example.hyc.httpcustom.source.IEntity;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

/**
 * Created by hyc on 2016/11/9.
 */

public class User implements IEntity {
    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * ret : 200
     * msg : 有心课堂,传递给你的不仅仅是技术✈️
     * data : {"token":"@","id":"1","email":"@","avatar":"@","account":"stay4it","username":"stay4it"}
     */

    private int      ret;
    private String   msg;
    private UserBean data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserBean getData() {
        return data;
    }

    public void setData(UserBean data) {
        this.data = data;
    }

    @Override
    public void readObj(JsonReader reader) throws IOException {
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("ret".equals(name)) {
                this.setRet(reader.nextInt());
            } else if ("msg".equals(name)) {
                this.setMsg(reader.nextString());
            } else if ("data".equals(name)) {
                this.setData(readUserBean(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private UserBean readUserBean(JsonReader reader) throws IOException {
        UserBean userBean = new UserBean();
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            String s    = reader.nextString();
            if ("token".equals(name)) {
                userBean.token = s;
            } else if ("id".equals(name)) {
                userBean.id = s;
            } else if ("avatar".equals(name)) {
                userBean.avatar = s;
            } else if ("account".equals(name)) {
                userBean.account = s;
            } else if ("username".equals(name)) {
                userBean.username = s;
            } else if ("email".equals(name)) {
                userBean.email = s;
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return userBean;
    }

    public static class UserBean {
        /**
         * token : @
         * id : 1
         * email : @
         * avatar : @
         * account : stay4it
         * username : stay4it
         */

        private String token;
        private String id;
        private String email;
        private String avatar;
        private String account;
        private String username;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
