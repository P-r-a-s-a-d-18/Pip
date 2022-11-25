package com.example.pip.auth.screens;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.pip.Models.UserModel;
import com.example.pip.R;
import com.example.pip.constants.NetworkFunctions;
import com.example.pip.databinding.ActivityRegisterUserBinding;
import com.example.pip.screens.HomeScreen;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterUserScreen extends AppCompatActivity {
    private EditText Edt_UserName, EdtEmailId, EdtPassword;
    private Button BtnSignUp;
    private TextView Signin;
    private FirebaseAuth firebaseAuth;
    private ActivityRegisterUserBinding binding;
//    private boolean choiceDifferentUserName = false;
//    private long totalUsers;
    private boolean connectivity;


    public RegisterUserScreen() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        int modeFlag = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (modeFlag) {
            case Configuration.UI_MODE_NIGHT_YES:
                setUiAsNightMode();
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                setUiAsLightMode();
                break;
        }

        firebaseAuth = FirebaseAuth.getInstance();

        BtnSignUp = findViewById(R.id.idBtnSignUp);

        Signin = findViewById(R.id.idTVSignin);

        EdtEmailId = findViewById(R.id.idEdtEmailId);
        Edt_UserName = findViewById(R.id.idEdtUserName);
        EdtPassword = findViewById(R.id.idEdtPassword);


        BtnSignUp.setOnClickListener(view -> {
            String emailLength = EdtEmailId.getText().toString().trim();
            String passwordLength = EdtPassword.getText().toString().trim();
            String nameLength = Edt_UserName.getText().toString().trim();

            connectivity = new NetworkFunctions().checkNetworkStatus(this);

            if (connectivity) {
                if (passwordLength.isEmpty()) {
                    EdtPassword.setError("Fill the passwords");
                    EdtPassword.requestFocus();
                } else if (emailLength.isEmpty()) {
                    EdtEmailId.setError("Fill your email section");
                    EdtEmailId.requestFocus();
                } else if (!emailLength.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    EdtEmailId.setError("Enter a valid email");
                    EdtEmailId.requestFocus();
                } else if (passwordLength.length() <= 7) {
                    EdtPassword.setError("Your password must be 8 (Letters , Numbers , and other) mixing");
                    EdtPassword.requestFocus();
                } else if (nameLength.isEmpty()) {
                    Edt_UserName.setError("Fill your username");
                    Edt_UserName.requestFocus();
                } else {

//                FirebaseDatabase.getInstance().getReference("user").child("UserInfo").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            totalUsers = snapshot.getChildrenCount();
//                            Toast.makeText(RegisterUserScreen.this, String.valueOf(totalUsers), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                FirebaseDatabase.getInstance().getReference("user").child("UserInfo")
//                        .addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                                if (snapshot.exists()) {
//                                    UserModel userModel = snapshot.getValue(UserModel.class);
//                                    assert userModel != null;
//                                    if (Objects.equals(userModel.usName, nameLength)) {
//                                        Toast.makeText(RegisterUserScreen.this, "User find" + totalUsers, Toast.LENGTH_SHORT).show();
//                                        choiceDifferentUserName = !choiceDifferentUserName;
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });


//                FirebaseDatabase.getInstance().getReference("user").child("UserInfo")


//                if (!choiceDifferentUserName && totalUsers != 0) {
//                    Toast.makeText(this, String.valueOf(choiceDifferentUserName), Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(emailLength, passwordLength)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    UserModel userModel1 = new UserModel(emailLength, passwordLength, nameLength);
                                    FirebaseDatabase.getInstance().getReference("user").child("UserInfo")
                                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                            .setValue(userModel1).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(RegisterUserScreen.this, "Successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(RegisterUserScreen.this, HomeScreen.class);
                                                    startActivity(intent);
                                                    binding.progressBar.setVisibility(View.GONE);
                                                    finish();
                                                } else {
                                                    Toast.makeText(RegisterUserScreen.this, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                } else {
                                    Toast.makeText(RegisterUserScreen.this, "Failed to register", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }
                            });
//                }
                }
            } else {
                Toast.makeText(RegisterUserScreen.this, "Check Your Network Connection !", Toast.LENGTH_SHORT).show();
            }
        });

        Signin.setOnClickListener(view -> {
            Intent loginpage = new Intent(this, LoginUserScreen.class);
            startActivity(loginpage);

        });

    }

    void setUiAsNightMode() {
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.arrow_back);
        assert upArrow != null;
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Create your account");
    }

    void setUiAsLightMode() {
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.arrow_back);
        assert upArrow != null;
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Create your account</font>"));
    }
}