package com.codextech.ibtisam.bills_app.models;

import com.orm.SugarRecord;

public class BPTransac extends SugarRecord {

    private String transacName;
    private String transacDate;
    private String transacType;
    private String transacBeneficiary;
    private String transacRemarks;

    public BPTransac() {
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
}
