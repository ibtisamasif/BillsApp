package com.codextech.ibtisam.bills_app.models;

import com.orm.SugarRecord;

import java.util.Date;

public class BPBiller extends SugarRecord {

    private String nickname;
    private String referenceno;
    private BPMerchant university;
    private String balance;
    private String status;
    private Date updatedAt;

    public BPBiller() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getReferenceno() {
        return referenceno;
    }

    public void setReferenceno(String referenceno) {
        this.referenceno = referenceno;
    }

    public BPMerchant getUniversity() {
        return university;
    }

    public void setUniversity(BPMerchant university) {
        this.university = university;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
