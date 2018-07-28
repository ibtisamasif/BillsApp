package com.codextech.ibtisam.bills_app.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncStats;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.bills_app.R;
import com.codextech.ibtisam.bills_app.activities.SubscriberDetailActivity;
import com.codextech.ibtisam.bills_app.models.BPSubscriber;
import com.codextech.ibtisam.bills_app.sync.DataSenderAsync;
import com.codextech.ibtisam.bills_app.sync.SyncStatus;

import java.util.List;

import de.halfbit.tinybus.TinyBus;

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
                String nameTextOnDialog;
                if (subscriber.getNickname() != null) {
                    nameTextOnDialog = subscriber.getNickname();
                } else {
                    nameTextOnDialog = subscriber.getReferenceno();
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure to delete " + nameTextOnDialog + ". This will delete associated deals as well.");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subscriber.setSyncStatus(SyncStatus.SYNC_STATUS_SUBSCRIBER_DELETE_NOT_SYNCED);
                        subscriber.save();
                        List<BPSubscriber> list = BPSubscriber.getSubscribersInDescOrder();
                        if (list != null && list.size() > 0)
                            updateList(list);
//                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
//                        dataSenderAsync.run();
                        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;
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
            if (subscriber.getDuesDate() != null) {
                holder.tvDueDate.setText(subscriber.getDuesDate());
            }
            if (subscriber.getDuesStatus() != null) {
                if (subscriber.getDuesStatus().equalsIgnoreCase(BPSubscriber.SUBSCRIBER_BILL_UNPAID)) {
                    holder.bPay.setText("PAY");
                    holder.bPay.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                    holder.bPay.setFocusable(true);
                } else if (subscriber.getDuesStatus().equalsIgnoreCase(BPSubscriber.SUBSCRIBER_BILL_PAID)) {
                    holder.bPay.setText("PAID");
                    holder.bPay.setBackgroundColor(mContext.getResources().getColor(R.color.Color_Default));
                    holder.bPay.setFocusable(false);
                }
            } else {
                holder.bPay.setText("PAID");
                holder.bPay.setBackgroundColor(mContext.getResources().getColor(R.color.Color_Default));
                holder.bPay.setFocusable(false);
            }
            holder.bPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscriber.setBalance("0");
                    subscriber.setDuesStatus("paid");
                    subscriber.save();
                    Toast.makeText(mContext, "Bill Paid successfully.", Toast.LENGTH_SHORT).show();
                }
            });
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
        TextView tvDueDate;
        Button bPay;

        OrganizationViewHolder(View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cl);
            tvName = itemView.findViewById(R.id.tvName);
            tvReferenceNo = itemView.findViewById(R.id.tvReferenceNo);
            tvMerchantName = itemView.findViewById(R.id.tvMerchantName);
            tvBalance = itemView.findViewById(R.id.tvDues);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            bPay = itemView.findViewById(R.id.bPay);
        }
    }
}
