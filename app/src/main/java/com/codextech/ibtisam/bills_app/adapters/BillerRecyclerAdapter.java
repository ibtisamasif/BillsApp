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
import com.codextech.ibtisam.bills_app.models.BPBiller;
import com.codextech.ibtisam.bills_app.models.BPTransac;

import java.util.List;

public class BillerRecyclerAdapter extends RecyclerView.Adapter<BillerRecyclerAdapter.OrganizationViewHolder> {

    private Context mContext;
    private List<BPBiller> list;

    public BillerRecyclerAdapter(List<BPBiller> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public void updateList(List<BPBiller> newlist) {
        list.clear();
        list.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.biller_row, parent, false);
        return new OrganizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position) {
        BPBiller biller = list.get(position);
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent detailsActivityIntent = new Intent(mContext, OrganizationDetailsTabActivity.class);
//                long billerId = biller.getId();
//                detailsActivityIntent.putExtra(OrganizationDetailsTabActivity.KEY_ORGANIZATION_ID, billerId + "");
//                mContext.startActivity(detailsActivityIntent);
            }
        });
        if (biller != null) {
            if (biller.getNickname() != null) {
                holder.tvName.setText(biller.getNickname());
            }
            if (biller.getReferenceno() != null) {
                holder.tvReferenceNo.setText(biller.getReferenceno());
            }
            if (biller.getUniversity() != null) {
                holder.tvMerchantName.setText(biller.getUniversity().getName());
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

        OrganizationViewHolder(View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cl);
            tvName = itemView.findViewById(R.id.tvName);
            tvReferenceNo = itemView.findViewById(R.id.tvReferenceNo);
            tvMerchantName = itemView.findViewById(R.id.tvMerchantName);
        }
    }
}
