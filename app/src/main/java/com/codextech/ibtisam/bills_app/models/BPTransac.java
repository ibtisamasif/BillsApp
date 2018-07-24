package com.codextech.ibtisam.bills_app.models;

import android.database.sqlite.SQLiteException;

import com.orm.SugarRecord;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BPTransac extends SugarRecord {

    private String transacName;
    private String transacDate;
    private String transacType;
    private String transacBeneficiary;
    private String transacRemarks;
    private Date updatedAt;

    public BPTransac() {
    }

    public static List<BPTransac> getTransactionsInDescOrder() {
        try {
            return Select.from(BPTransac.class).orderBy("updated_at DESC").list();
        } catch (SQLiteException e) {
            return new ArrayList<BPTransac>();
        }
    }

    public String getTransacName() {
        return transacName;
    }

    public void setTransacName(String transacName) {
        this.transacName = transacName;
    }

    public String getTransacDate() {
        return transacDate;
    }

    public void setTransacDate(String transacDate) {
        this.transacDate = transacDate;
    }

    public String getTransacType() {
        return transacType;
    }

    public void setTransacType(String transacType) {
        this.transacType = transacType;
    }

    public String getTransacBeneficiary() {
        return transacBeneficiary;
    }

    public void setTransacBeneficiary(String transacBeneficiary) {
        this.transacBeneficiary = transacBeneficiary;
    }

    public String getTransacRemarks() {
        return transacRemarks;
    }

    public void setTransacRemarks(String transacRemarks) {
        this.transacRemarks = transacRemarks;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
