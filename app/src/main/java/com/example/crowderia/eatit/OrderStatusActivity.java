package com.example.crowderia.eatit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.crowderia.eatit.Common.Common;
import com.example.crowderia.eatit.Interface.ItemClickListener;
import com.example.crowderia.eatit.Model.Order;
import com.example.crowderia.eatit.Model.Request;
import com.example.crowderia.eatit.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatusActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        if(getIntent() == null) {
            loadOrders(Common.currentUser.getPhone());
//        } else {
//            loadOrders(getIntent().getStringExtra("userPhone"));
//        }
    }

    private void loadOrders(String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>
                (Request.class, R.layout.order_layout, OrderViewHolder.class, requests.orderByChild("phone").equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.orderId.setText(adapter.getRef(position).getKey());
                viewHolder.orderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.orderAddress.setText(model.getAddress());
                viewHolder.orderPhone.setText(model.getPhone());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent mapIntent = new Intent(OrderStatusActivity.this, TrackingOrderActivity.class);
                        startActivity(mapIntent);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
}
