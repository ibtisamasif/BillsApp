package com.codextech.ibtisam.bills_app.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;

public class BPSubscriber extends SugarRecord {

    private String nickname;
    private String referenceno;
    private BPMerchant university;
    private String balance;
    private String status;
    private String syncStatus;
    private Date updatedAt;
    private String serverId;

    public BPSubscriber() {
    }

    public static BPSubscriber getSubscriberFromServerId(String server_id) {
        ArrayList<BPSubscriber> list = null;
        try {
            list = (ArrayList<BPSubscriber>) BPSubscriber.find(BPSubscriber.class, "server_id = ? ", server_id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
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

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
