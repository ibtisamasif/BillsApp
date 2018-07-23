package com.codextech.ibtisam.bills_app.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.bills_app.R;
import com.codextech.ibtisam.bills_app.SessionManager;
import com.codextech.ibtisam.bills_app.adapters.MerchantRecyclerAdapter;
import com.codextech.ibtisam.bills_app.models.BPMerchant;

import java.util.ArrayList;
import java.util.List;

public class MerchantsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MerchantRecyclerAdapter adapter;
    FloatingActionButton add_fab;
    List<BPMerchant> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchants);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Merchants");

        add_fab = findViewById(R.id.add_fab);

        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMerchant();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(MerchantsActivity.this));

        list = BPMerchant.listAll(BPMerchant.class);

        adapter = new MerchantRecyclerAdapter(list, MerchantsActivity.this);

        recyclerView.setAdapter(adapter);

        SessionManager sessionManager = new SessionManager(this);
        View view = findViewById(R.id.include2);
        TextView tvValue = view.findViewById(R.id.tvValue);
        tvValue.setText(sessionManager.getKeyLoginWalletBalance());
    }

    private void addMerchant() {
        final Dialog dialog = new Dialog(MerchantsActivity.this);
        dialog.setContentView(R.layout.add_merchant);
        dialog.setCancelable(true);
        dialog.show();
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
                EditText name = dialog.findViewById(R.id.etName);
                EditText account = dialog.findViewById(R.id.etAccount);
                EditText address = dialog.findViewById(R.id.etAddress);
                if (name.getText().toString().isEmpty()) {
                    name.setError("Please enter Name!");
                } else if (account.getText().toString().isEmpty()) {
                    account.setError("Please enter Account Number!");
                } else if (address.getText().toString().isEmpty()) {
                    address.setError("Please enter Location!");
                } else {
                    BPMerchant merchant = new BPMerchant();
                    merchant.setName(name.getText().toString());
                    merchant.setAccountNo(account.getText().toString());
                    merchant.setAddress(address.getText().toString());
                    if (merchant.save() > 0) {
                        Toast.makeText(MerchantsActivity.this, "Merchant saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        list = BPMerchant.listAll(BPMerchant.class);
                        adapter.updateList(list);
                    } else {
                        Toast.makeText(MerchantsActivity.this, "Error not saved something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
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
}
