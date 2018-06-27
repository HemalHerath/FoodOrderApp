package com.example.crowderia.eatit;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crowderia.eatit.Common.Common;
import com.example.crowderia.eatit.Database.Database;
import com.example.crowderia.eatit.Model.Notification;
import com.example.crowderia.eatit.Model.Order;
import com.example.crowderia.eatit.Model.Request;
import com.example.crowderia.eatit.Model.Response;
import com.example.crowderia.eatit.Model.Sender;
import com.example.crowderia.eatit.Model.Token;
import com.example.crowderia.eatit.Remote.APIServices;
import com.example.crowderia.eatit.ViewHolder.CartAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlaceOrder;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    APIServices apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        apiServices = Common.getFCMService();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cart.size() > 0) {
                    showAlertDialog();
                } else {
                    Toast.makeText(CartActivity.this, "Your Cart is Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadListFood();
    }

    private void showAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter Your Address: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View orderComment = inflater.inflate(R.layout.order_comment_layout, null);

        final MaterialEditText edtAddress = (MaterialEditText) orderComment.findViewById(R.id.edt_address);
        final MaterialEditText edtComment = (MaterialEditText) orderComment.findViewById(R.id.edt_comment);

        alertDialog.setView(orderComment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //new request
                Request request = new Request(
                        Common.currentUser.getName(),
                        Common.currentUser.getPhone(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart,
                        "0",
                        edtComment.getText().toString()
                );

                String orderNumber = String.valueOf(System.currentTimeMillis());

                //submit to firebase
                requests.child(orderNumber)
                        .setValue(request);
                //Delete Cart
                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(orderNumber);

//                Toast.makeText(CartActivity.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
//                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void sendNotificationOrder(final String orderNumber) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        final Query data = tokens.orderByChild("serviceToken").equalTo(true);

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()) {
                    Token serviceToken = postSnapShot.getValue(Token.class);

                    Notification notification = new Notification("Eat it", "You have new order " + orderNumber);
                    Sender content = new Sender(serviceToken.getToken(), notification);

                    apiServices.sendNotification(content).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                            if(response.code() == 200) {
                                if (response.body().success == 1) {
                                    Toast.makeText(CartActivity.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(CartActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {
                            Log.e("Error", t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        int total = 0;
        for(Order order:cart){
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int order) {

        cart.remove(order);

        new Database(this).cleanCart();
        for(Order item:cart)
            new Database(this).addToCart(item);

        loadListFood();
    }
}
