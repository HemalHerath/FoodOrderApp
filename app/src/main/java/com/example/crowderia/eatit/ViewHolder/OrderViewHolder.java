package com.example.crowderia.eatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.crowderia.eatit.Interface.ItemClickListener;
import com.example.crowderia.eatit.R;


/**
 * Created by crowderia on 12/21/2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView orderId, orderPhone, orderAddress, orderStatus;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        orderId = (TextView) itemView.findViewById(R.id.order_id);
        orderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        orderAddress = (TextView) itemView.findViewById(R.id.order_address);
        orderStatus = (TextView) itemView.findViewById(R.id.order_status);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
