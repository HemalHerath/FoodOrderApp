package com.example.crowderia.eatit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.crowderia.eatit.Common.Common;
import com.example.crowderia.eatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtName,edtPhone, edtPassword, edtSecureCode;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtSecureCode = (MaterialEditText) findViewById(R.id.edtSecureCode);


        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        btnSignUp.setTypeface(typeface);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectedToInternet(getBaseContext())) {

                    String name = edtName.getText().toString();
                    String phone = edtPhone.getText().toString();
                    String password = edtPassword.getText().toString();
                    CreateAccount(name, phone, password);
                } else {
                    Toast.makeText(SignUpActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    private void CreateAccount(String name, String phone, String password) {

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(SignUpActivity.this, "All fields required", Toast.LENGTH_LONG).show();
        } else {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference table_user = database.getReference("User");

            final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
            dialog.setMessage("Please wait...");
            dialog.show();

            table_user.child(edtPhone.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        dialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Phone Number Already Exists", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        User user = new User(edtName.getText().toString(), edtPassword.getText().toString(),
                                edtPhone.getText().toString(), edtSecureCode.getText().toString());
                        table_user.child(edtPhone.getText().toString()).setValue(user);
                        Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                        Intent homeIntent = new Intent(SignUpActivity.this, HomeActivity.class);
                        Common.currentUser = user;
                        startActivity(homeIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
