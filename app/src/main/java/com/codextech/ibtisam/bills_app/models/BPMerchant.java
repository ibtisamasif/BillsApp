package com.codextech.ibtisam.bills_app.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;

public class BPMerchant extends SugarRecord {

    private String name;
    private String accountNo;
    private String address;
    private String logo;
    private String status;
    private Date updatedAt;
    private String serverId;

    public BPMerchant() {
    }

    public static BPMerchant getMerchantByName(String name) {
        ArrayList<BPMerchant> list = null;
        try {
            list = (ArrayList<BPMerchant>) BPMerchant.find(BPMerchant.class, "name = ? ", name);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static BPMerchant getMerchantFromServerId(String server_id) {
        ArrayList<BPMerchant> list = null;
        try {
            list = (ArrayList<BPMerchant>) BPMerchant.find(BPMerchant.class, "server_id = ? ", server_id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
