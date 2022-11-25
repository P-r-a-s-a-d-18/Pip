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

import com.example.pip.R;
import com.example.pip.databinding.ActivityLoginUserBinding;
import com.example.pip.screens.HomeScreen;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginUserScreen extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private ActivityLoginUserBinding binding;
    private EditText Edt_Email_id, Edt_Passwd;
    private TextView TVCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        Button btnLogin = findViewById(R.id.idBtnLogin);

        TVCreate = findViewById(R.id.idTVCreate);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        int modeFlag = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (modeFlag) {
            case Configuration.UI_MODE_NIGHT_YES:
                setUiAsNightMode();
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                setUiAsLightMode();
                break;
        }


        btnLogin.setOnClickListener(view -> {
            Edt_Email_id = findViewById(R.id.idEdtEmail_id);
            Edt_Passwd = findViewById(R.id.idEdtPasswd);
            String ust = Edt_Email_id.getText().toString().trim();
            String pt = Edt_Passwd.getText().toString().trim();
            if (pt.isEmpty()) {
                Edt_Passwd.setError("Fill the passwords");
                Edt_Passwd.requestFocus();
            } else if (ust.isEmpty()) {
                Edt_Email_id.setError("Fill the passwords");
                Edt_Email_id.requestFocus();
            } else if (!ust.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                Edt_Email_id.setError("Enter a valid email");
                Edt_Email_id.requestFocus();
            } else if (pt.length() <= 7) {
                Edt_Passwd.setError("Your password must be 8 (Letters , Numbers , and other) mixing");
                Edt_Passwd.requestFocus();
            } else {
                binding.progressBar2.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(ust, pt).addOnCompleteListener(LoginUserScreen.this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginUserScreen.this, "Failed", Toast.LENGTH_SHORT).show();
                        binding.progressBar2.setVisibility(View.GONE);
                    } else {
                        Intent intent = new Intent(LoginUserScreen.this, HomeScreen.class);
                        startActivity(intent);
                        binding.progressBar2.setVisibility(View.GONE);
                    }
                });
            }

        });

        TVCreate.setOnClickListener(view -> {
            Intent accountcreate = new Intent(this, RegisterUserScreen.class);
            startActivity(accountcreate);
        });

    }

    void setUiAsNightMode() {
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.arrow_back);
        assert upArrow != null;
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Login your account");
    }

    void setUiAsLightMode() {
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.arrow_back);
        assert upArrow != null;
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.bg1), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Login your account</font>"));
    }
}