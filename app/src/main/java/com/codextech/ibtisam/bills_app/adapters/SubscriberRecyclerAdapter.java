package com.codextech.ibtisam.bills_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.bills_app.R;
import com.codextech.ibtisam.bills_app.activities.SubscriberDetailActivity;
import com.codextech.ibtisam.bills_app.models.BPSubscriber;

import java.util.List;

public class SubscriberRecyclerAdapter extends RecyclerView.Adapter<SubscriberRecyclerAdapter.OrganizationViewHolder> {

    private Context mContext;
    private List<BPSubscriber> list;

    public SubscriberRecyclerAdapter(List<BPSubscriber> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public void updateList(List<BPSubscriber> newlist) {
        list.clear();
        list.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subscriber_row, parent, false);
        return new OrganizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position) {
        final BPSubscriber subscriber = list.get(position);
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsActivityIntent = new Intent(mContext, SubscriberDetailActivity.class);
                detailsActivityIntent.putExtra(SubscriberDetailActivity.KEY_SUBSCRIBER_ID, subscriber.getId());
                mContext.startActivity(detailsActivityIntent);
            }
        });
        holder.cl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "Delete Subscriber dialog", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        if (subscriber != null) {
            if (subscriber.getNickname() != null) {
                holder.tvName.setText(subscriber.getNickname());
            }
            if (subscriber.getMerchant() != null) {
                holder.tvMerchantName.setText(subscriber.getMerchant().getName());
            }
            if (subscriber.getReferenceno() != null) {
                holder.tvReferenceNo.setText(subscriber.getReferenceno());
            }
            if (subscriber.getBalance() != null) {
                holder.tvBalance.setText(subscriber.getBalance());
            }
        }
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
        TextView tvName;
        TextView tvReferenceNo;
        TextView tvMerchantName;
        TextView tvBalance;

        OrganizationViewHolder(View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cl);
            tvName = itemView.findViewById(R.id.tvName);
            tvReferenceNo = itemView.findViewById(R.id.tvReferenceNo);
            tvMerchantName = itemView.findViewById(R.id.tvMerchantName);
            tvBalance = itemView.findViewById(R.id.tvDues);
        }
    }
}
