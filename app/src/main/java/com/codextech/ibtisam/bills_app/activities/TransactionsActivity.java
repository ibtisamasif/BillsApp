package com.codextech.ibtisam.bills_app.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codextech.ibtisam.bills_app.R;
import com.codextech.ibtisam.bills_app.adapters.TransactionRecyclerAdapter;
import com.codextech.ibtisam.bills_app.models.BPMerchant;
import com.codextech.ibtisam.bills_app.models.BPTransac;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionRecyclerAdapter adapter;
    FloatingActionButton add_fab;
    List<BPTransac> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        getSupportActionBar().setTitle("Transactions");

        add_fab = findViewById(R.id.add_fab);

        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransaction();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(TransactionsActivity.this));

        list = BPTransac.listAll(BPTransac.class);

        adapter = new TransactionRecyclerAdapter(list, TransactionsActivity.this);

        recyclerView.setAdapter(adapter);
    }

    private void addTransaction() {
        final Dialog dialog = new Dialog(TransactionsActivity.this);
        dialog.setContentView(R.layout.add_transaction);
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
                EditText etTransaction = dialog.findViewById(R.id.etTransaction);
                EditText etTransactionType = dialog.findViewById(R.id.etTransactionType);
                EditText etBeneficiary = dialog.findViewById(R.id.etBeneficiary);
                EditText etRemarks = dialog.findViewById(R.id.etRemarks);
                if (etTransaction.getText().toString().isEmpty()) {
                    etTransaction.setError("Please enter Name!");
                } else if (etTransactionType.getText().toString().isEmpty()) {
                    etTransactionType.setError("Please enter Transaction Type!");
                } else if (etBeneficiary.getText().toString().isEmpty()) {
                    etBeneficiary.setError("Please enter Location!");
                } else if (etRemarks.getText().toString().isEmpty()) {
                    etRemarks.setError("Please enter Location!");
                } else {
                    BPTransac transaction = new BPTransac();
                    transaction.setTransacName(etTransaction.getText().toString());
                    transaction.setTransacType(etTransactionType.getText().toString());
                    transaction.setTransacBeneficiary(etBeneficiary.getText().toString());
                    transaction.setTransacRemarks(etRemarks.getText().toString());
                    if (transaction.save() > 0) {
                        Toast.makeText(TransactionsActivity.this, "Transaction saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        list = BPTransac.listAll(BPTransac.class);
                        adapter.updateList(list);
                    } else {
                        Toast.makeText(TransactionsActivity.this, "Error not saved something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
