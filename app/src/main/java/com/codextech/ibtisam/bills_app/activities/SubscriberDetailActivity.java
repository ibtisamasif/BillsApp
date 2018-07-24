package com.codextech.ibtisam.bills_app.activities;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.bills_app.R;
import com.codextech.ibtisam.bills_app.SessionManager;
import com.codextech.ibtisam.bills_app.models.BPSubscriber;
import com.codextech.ibtisam.bills_app.models.BPTransac;

import org.w3c.dom.Text;

import java.util.Calendar;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details");

        tvName = findViewById(R.id.tvName);
        tvNickName = findViewById(R.id.tvNickName);
        tvReferenceNo = findViewById(R.id.tvReferenceNo);
        tvMerchantName = findViewById(R.id.tvMerchantName);
        tvDuesStatus = findViewById(R.id.tvDuesStatus);
        tvDues = findViewById(R.id.tvDues);
        tvDueDate = findViewById(R.id.tvDueDate);

        SessionManager sessionManager = new SessionManager(this);
        View view = findViewById(R.id.include2);
        TextView tvValue = view.findViewById(R.id.tvValue);
        tvValue.setText(sessionManager.getKeyLoginWalletBalance());
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
                    tvDuesStatus.setTextColor(Color.parseColor("#00897b"));
                }
            } else {
                tvDuesStatus.setText("PAID");
                tvDuesStatus.setTextColor(Color.parseColor("#00897b"));
            }
            if (selectedSubscriber.getBalance() != null) {
                tvDues.setText(selectedSubscriber.getBalance());
            }
            if (selectedSubscriber.getDuesDate() != null) {
                tvDueDate.setText(selectedSubscriber.getDuesDate());
            }
        }

        Button bPay = findViewById(R.id.bPay);
        bPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSubsriberDues();
                Toast.makeText(SubscriberDetailActivity.this, "Paying Bill..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubsriberDues() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.update_subscriber);
        dialog.setCancelable(true);
        dialog.show();

        TextView tvName = dialog.findViewById(R.id.tvName);
        TextView tvReference = dialog.findViewById(R.id.tvReference);
        TextView tvMerchantName = dialog.findViewById(R.id.tvMerchantName);
        final EditText etDues = dialog.findViewById(R.id.etDues);
        if (selectedSubscriber != null) {
            if (selectedSubscriber.getNickname() != null)
                tvName.setText(selectedSubscriber.getNickname());
            if (selectedSubscriber.getReferenceno() != null)
                tvReference.setText(selectedSubscriber.getReferenceno());
            if (selectedSubscriber.getMerchant() != null) {
                if (selectedSubscriber.getMerchant().getName() != null) {
                    tvMerchantName.setText(selectedSubscriber.getMerchant().getName());
                }
            }
            if (selectedSubscriber.getBalance() != null)
                etDues.setText(selectedSubscriber.getBalance());
        }

        Button bSave = (Button) dialog.findViewById(R.id.bSave);
        Button bCancel = (Button) dialog.findViewById(R.id.bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSubscriber != null) {
                    if (etDues.getText().toString().isEmpty()) {
                        etDues.setError("Entered dues are not valid!");
                    } else {
                        selectedSubscriber.setBalance(etDues.getText().toString());
                        selectedSubscriber.setUpdatedAt(Calendar.getInstance().getTime());
                        if (selectedSubscriber.save() > 0) {
                            Toast.makeText(SubscriberDetailActivity.this, "Bill payed", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(SubscriberDetailActivity.this, "Error not saved something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    finish();
                    Toast.makeText(SubscriberDetailActivity.this, "Subscriber has been deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
