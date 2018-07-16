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

import java.util.List;

public class MerchantRecyclerAdapter extends RecyclerView.Adapter<MerchantRecyclerAdapter.MerchantViewHolder> {

    private Context mContext;
    private List<BPMerchant> list;

    public MerchantRecyclerAdapter(List<BPMerchant> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public void updateList(List<BPMerchant> newlist) {
        list.clear();
        list.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MerchantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_row, parent, false);
        return new MerchantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantViewHolder holder, int position) {
        BPMerchant university = list.get(position);
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent detailsActivityIntent = new Intent(mContext, OrganizationDetailsTabActivity.class);
//                long universityId = university.getId();
//                detailsActivityIntent.putExtra(OrganizationDetailsTabActivity.KEY_ORGANIZATION_ID, universityId + "");
//                mContext.startActivity(detailsActivityIntent);
            }
        });
        if (university != null) {
            if (university.getName() != null) {
                holder.tvName.setText(university.getName());
            }
            if (university.getAccountNo() != null) {
                holder.tvAccount.setText(university.getAccountNo());
            }
            if (university.getAddress() != null) {
                holder.tvLocation.setText(university.getAddress());
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

    class MerchantViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl;
        TextView tvName;
        TextView tvAccount;
        TextView tvLocation;

        MerchantViewHolder(View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cl);
            tvName = itemView.findViewById(R.id.tvName);
            tvAccount = itemView.findViewById(R.id.tvAccount);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}
