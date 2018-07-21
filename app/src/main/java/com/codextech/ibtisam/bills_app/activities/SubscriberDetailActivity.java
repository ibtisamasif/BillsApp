package com.codextech.ibtisam.bills_app.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.bills_app.R;
import com.codextech.ibtisam.bills_app.models.BPSubscriber;

public class SubscriberDetailActivity extends AppCompatActivity {
    public static final String KEY_SUBSCRIBER_ID = "subcriber_id";
    private TextView tvName;
    private TextView tvNickName;
    private TextView tvReferenceNo;
    private TextView tvMerchantName;
    private TextView tvDuesStatus;
    private TextView tvDues;
    private TextView tvDueDate;
    private BPSubscriber selectedSubscriber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsriber_detail);

        tvName = findViewById(R.id.tvName);
        tvNickName = findViewById(R.id.tvNickName);
        tvReferenceNo = findViewById(R.id.tvReferenceNo);
        tvMerchantName = findViewById(R.id.tvMerchantName);
        tvDuesStatus = findViewById(R.id.tvDuesStatus);
        tvDues = findViewById(R.id.tvDues);
        tvDueDate = findViewById(R.id.tvDueDate);

        Button bPay = findViewById(R.id.bPay);
        bPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SubscriberDetailActivity.this, "Paying Bill", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Long subscriberId = bundle.getLong(KEY_SUBSCRIBER_ID);
            selectedSubscriber = BPSubscriber.findById(BPSubscriber.class, subscriberId);
        }
        if (selectedSubscriber != null) {
            if (selectedSubscriber.getNickname() != null) {
                tvName.setText(selectedSubscriber.getNickname());
            }
            if (selectedSubscriber.getNickname() != null) {
                tvNickName.setText(selectedSubscriber.getNickname());
            }
            if (selectedSubscriber.getReferenceno() != null) {
                tvReferenceNo.setText(selectedSubscriber.getReferenceno());
            }
            if (selectedSubscriber.getMerchant() != null) {
                if (selectedSubscriber.getMerchant().getName() != null)
                    tvMerchantName.setText(selectedSubscriber.getMerchant().getName());
            }
            if (selectedSubscriber.getDuesStatus() != null) {
                if (selectedSubscriber.getDuesStatus().equalsIgnoreCase(BPSubscriber.SUBSCRIBER_BILL_UNPAID)) {
                    tvDuesStatus.setText("UNPAID");
                    tvDuesStatus.setTextColor(Color.parseColor("#E53935"));
                } else if (selectedSubscriber.getDuesStatus().equalsIgnoreCase(BPSubscriber.SUBSCRIBER_BILL_PAID)) {
                    tvDuesStatus.setText("PAID");
                    tvDuesStatus.setTextColor(Color.parseColor("#000000"));
                }
            }else {
                tvDuesStatus.setText("Paid");
                tvDuesStatus.setTextColor(Color.parseColor("#000000"));
            }
            if (selectedSubscriber.getBalance() != null) {
                tvDues.setText(selectedSubscriber.getBalance());
            }
            if (selectedSubscriber.getDuesDate() != null) {
                tvDueDate.setText("28/08/2018");
            }
        }
    }
}
