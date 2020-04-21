package com.rizki.blogapp.Models;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content, uid, uname, uimg;
    private Object timestampe;

    public Comment() {
    }

    public Comment(String content, String uid, String uname, String uimg) {
        this.content = content;
        this.uid = uid;
        this.uname = uname;
        this.uimg = uimg;
        this.timestampe = ServerValue.TIMESTAMP;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public Object getTimestampe() {
        return timestampe;
    }

    public void setTimestampe(Object timestampe) {
        this.timestampe = timestampe;
    }
}
