package com.example.crowderia.eatit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crowderia.eatit.Common.Common;
import com.example.crowderia.eatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import java.util.zip.Inflater;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity {

    private EditText edtPhone, edtPassword;
    private Button btnSignIn;

    private CheckBox checkBox;

    private TextView forgotPwd;

    private FirebaseDatabase database;
    private DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        forgotPwd = (TextView) findViewById(R.id.txtForgotPwd);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        checkBox = (CheckBox) findViewById(R.id.check_box);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        Paper.init(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        btnSignIn.setTypeface(typeface);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectedToInternet(getBaseContext())) {

                    if(checkBox.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }

                    String phone = edtPhone.getText().toString();
                    String password = edtPassword.getText().toString();
                    LoginToAccount(phone, password);

                } else {
                    Toast.makeText(SignInActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
    }

    private void showForgotPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter Your Secure Code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgotPwdView = inflater.inflate(R.layout.forgot_password_layout, null);
        builder.setView(forgotPwdView);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = (MaterialEditText) forgotPwdView.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCode = (MaterialEditText) forgotPwdView.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Check user availability
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                        if(!dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            Toast.makeText(SignInActivity.this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                        } else {
                            if(user.getSecureCode().equals(edtSecureCode.getText().toString())) {
                                Toast.makeText(SignInActivity.this, "Your Password is: " + user.getPassword(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignInActivity.this, "Secure Code Invalid", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void LoginToAccount(String phone, String password) {

        if(TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(SignInActivity.this, "All fields required", Toast.LENGTH_LONG).show();
        } else {

            final ProgressDialog dialog = new ProgressDialog(SignInActivity.this);
            dialog.setMessage("Please wait...");
            dialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                        dialog.dismiss();
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        user.setPhone(edtPhone.getText().toString());
                        if (user.getPassword().equals(edtPassword.getText().toString())) {
                            Toast.makeText(SignInActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();

                            Intent homeIntent = new Intent(SignInActivity.this, HomeActivity.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();

                        } else {
                            Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        dialog.dismiss();
                        Toast.makeText(SignInActivity.this, "Phone Number Not Exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
