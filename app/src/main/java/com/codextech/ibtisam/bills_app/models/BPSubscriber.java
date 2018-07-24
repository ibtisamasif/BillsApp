package com.codextech.ibtisam.bills_app.models;

import android.database.sqlite.SQLiteException;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BPSubscriber extends SugarRecord {
    @Ignore
    public static final String SUBSCRIBER_BILL_PAID = "paid";
    public static final String SUBSCRIBER_BILL_UNPAID = "unpaid";

    private String nickname;
    private String referenceno;
    private BPMerchant merchant;
    private String balance;
    private String status;
    private String syncStatus;
    private Date updatedAt;
    private String serverId;

    private String duesStatus;
    private String duesDate;

    public BPSubscriber() {
    }

    public static List<BPSubscriber> getSubscribersInDescOrder() {
        try {
            return Select.from(BPSubscriber.class).orderBy("updated_at DESC").list();
        } catch (SQLiteException e) {
            return new ArrayList<BPSubscriber>();
        }
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

    public BPMerchant getMerchant() {
        return merchant;
    }

    public void setMerchant(BPMerchant merchant) {
        this.merchant = merchant;
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

    public String getDuesStatus() {
        return duesStatus;
    }

    public void setDuesStatus(String duesStatus) {
        this.duesStatus = duesStatus;
    }

    public String getDuesDate() {
        return duesDate;
    }

    public void setDuesDate(String duesDate) {
        this.duesDate = duesDate;
    }
}
