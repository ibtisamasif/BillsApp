package com.codextech.ibtisam.bills_app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codextech.ibtisam.bills_app.R;
import com.codextech.ibtisam.bills_app.models.BPMerchant;
import com.codextech.ibtisam.bills_app.models.BPTransac;

import java.util.List;

public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.OrganizationViewHolder> {

    private Context mContext;
    private List<BPTransac> list;

    public TransactionRecyclerAdapter(List<BPTransac> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public void updateList(List<BPTransac> newlist) {
        list.clear();
        list.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_row, parent, false);
        return new OrganizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position) {
        BPTransac transactions = list.get(position);
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent detailsActivityIntent = new Intent(mContext, OrganizationDetailsTabActivity.class);
//                long transactionsId = transactions.getId();
//                detailsActivityIntent.putExtra(OrganizationDetailsTabActivity.KEY_ORGANIZATION_ID, transactionsId + "");
//                mContext.startActivity(detailsActivityIntent);
            }
        });
        holder.transaction.setText(transactions.getTransacName());
        holder.transactionType.setText(transactions.getTransacType());
        holder.date.setText(transactions.getTransacDate());
        holder.remarks.setText(transactions.getTransacRemarks());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class OrganizationViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl;
        TextView transaction;
        TextView transactionType;
        TextView date;
        TextView remarks;

        OrganizationViewHolder(View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cl);
            transaction = itemView.findViewById(R.id.tvTransaction);
            transactionType = itemView.findViewById(R.id.tvTransactionType);
            date = itemView.findViewById(R.id.tvDate);
            remarks = itemView.findViewById(R.id.tvRemarks);
        }
    }


}
