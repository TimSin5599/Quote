package com.TimSin.quote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.content.res.ColorStateList;
import android.graphics.Color;

public class Login extends AppCompatActivity {
    FirebaseAuth mAuth;
    private String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = getSharedPreferences("AUTH_PREFS", MODE_PRIVATE);
        String userEmail = preferences.getString("user_email", null);
        if (userEmail != null) {
            Intent intent = new Intent(Login.this, Rooms.class);
            startActivity(intent);
            finish();
        }


        mAuth = FirebaseAuth.getInstance();

        Button button_auth = findViewById(R.id.button_auth);
        Button button_register = findViewById(R.id.button_register);
        TextView textview_noacc = findViewById(R.id.textview_noacc);
        TextView textview_acc = findViewById(R.id.textview_acc);
        EditText login = findViewById(R.id.login);
        EditText password = findViewById(R.id.password);
        EditText password_two = findViewById(R.id.password_two);

        textview_noacc.setOnClickListener(v -> {
            button_register.setEnabled(true);
            button_register.setVisibility(Button.VISIBLE);
            password_two.setEnabled(true);
            password_two.setVisibility(Button.VISIBLE);
            button_auth.setEnabled(false);
            button_auth.setVisibility(Button.INVISIBLE);
            textview_noacc.setEnabled(false);
            textview_noacc.setVisibility(Button.INVISIBLE);
            textview_acc.setEnabled(true);
            textview_acc.setVisibility(Button.VISIBLE);
        });

        textview_acc.setOnClickListener(v -> {
            button_auth.setEnabled(true);
            button_auth.setVisibility(Button.VISIBLE);
            button_register.setEnabled(false);
            button_register.setVisibility(Button.INVISIBLE);
            password_two.setEnabled(false);
            password_two.setVisibility(Button.GONE);
            textview_acc.setEnabled(false);
            textview_acc.setVisibility(Button.INVISIBLE);
            textview_noacc.setEnabled(true);
            textview_noacc.setVisibility(Button.VISIBLE);
        });

        button_auth.setOnClickListener(v -> {
            if (checkmaillandpass(login.getText().toString(), password.getText().toString(), password_two.getText().toString()))
                return;
            FirebasesignInWithEmailAndPassword(login.getText().toString(), password.getText().toString());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user_email", login.getText().toString());
            editor.apply();

            Intent intent = new Intent(Login.this, Rooms.class);
            startActivity(intent);
        });

        button_register.setOnClickListener(v -> {
            if (checkmaillandpass(login.getText().toString(), password.getText().toString(), password_two.getText().toString()))
                return;
            FirebaseCreateUserWithEmailAndPassword(login.getText().toString(), password.getText().toString());
        });



    }

    private boolean checkmaillandpass(String mail, String pass, String passtwo) {
        EditText login = findViewById(R.id.login);
        EditText password = findViewById(R.id.password);
        EditText password_two = findViewById(R.id.password_two);
        boolean flag = false;
        if (mail == null || mail.trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            login.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            if (!flag)
                Toast.makeText(this, "Проверьте корректность почты", Toast.LENGTH_SHORT).show();
            flag = true;
        } else login.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
        if (pass == null || pass.trim().isEmpty()) {
            password.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            if (!flag)
                Toast.makeText(this, "Проверьте корректность пароля", Toast.LENGTH_SHORT).show();
            flag = true;
        } else password.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
        if (password_two.isEnabled() && (passtwo == null || passtwo.trim().isEmpty())) {
            password_two.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            if (!flag)
                Toast.makeText(this, "Проверьте корректность подтвердения пароля", Toast.LENGTH_SHORT).show();
            flag = true;
        } else password_two.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
        if (password_two.isEnabled() && !passtwo.equals(pass)) {
            password.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            password_two.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            if (!flag)
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            flag = true;
        }
        return flag;
    }

    private void FirebaseCreateUserWithEmailAndPassword(String mail, String pass) {
        mAuth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void FirebasesignInWithEmailAndPassword(String mail, String pass) {
        mAuth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Ошибка входа", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}