package com.TimSin.quote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class Rooms extends AppCompatActivity {

    public enum LogReg {
        LOGIN,
        CREATE
    }
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rooms);

        SharedPreferences preferences = getSharedPreferences("AUTH_PREFS", MODE_PRIVATE);
        String userRoom = preferences.getString("user_room", null);
        if (userRoom != null) {
            Intent intent = new Intent(Rooms.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        Button button_auth = findViewById(R.id.button_auth);
        Button button_create = findViewById(R.id.button_create);
        TextView textview_inroom = findViewById(R.id.textview_inroom);
        TextView textview_createroom = findViewById(R.id.textview_createroom);
        EditText code_room = findViewById(R.id.code_room);

        textview_createroom.setOnClickListener(v -> {
            button_create.setEnabled(true);
            button_create.setVisibility(Button.VISIBLE);
            button_auth.setEnabled(false);
            button_auth.setVisibility(Button.INVISIBLE);
            textview_inroom.setEnabled(true);
            textview_inroom.setVisibility(Button.VISIBLE);
            textview_createroom.setEnabled(false);
            textview_createroom.setVisibility(Button.INVISIBLE);
            code_room.setEnabled(false);
            code_room.setText(generateCode(6));

        });

        textview_inroom.setOnClickListener(v -> {
            button_create.setEnabled(false);
            button_create.setVisibility(Button.INVISIBLE);
            button_auth.setEnabled(true);
            button_auth.setVisibility(Button.VISIBLE);
            textview_inroom.setEnabled(false);
            textview_inroom.setVisibility(Button.INVISIBLE);
            textview_createroom.setEnabled(true);
            textview_createroom.setVisibility(Button.VISIBLE);
            code_room.setEnabled(true);
            code_room.setText("");
        });

        button_auth.setOnClickListener(v -> {
            String key = code_room.getText().toString();
            checkAndSaveKey(key, LogReg.LOGIN);
        });

        button_create.setOnClickListener(v -> {
            String key = code_room.getText().toString();
            checkAndSaveKey(key, LogReg.CREATE);
        });

    }

    private void checkAndSaveKey(String key, LogReg param) {

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Rooms");
        databaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (param == LogReg.LOGIN){
                        saveKeyToSharedPreferences(key);
                        Toast.makeText(getApplicationContext(), "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                    } else if (param == LogReg.CREATE) {
                        Toast.makeText(getApplicationContext(), "Такой ключ уже существует", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (param == LogReg.LOGIN){
                        Toast.makeText(getApplicationContext(), "Такого ключа нет", Toast.LENGTH_SHORT).show();
                    } else if (param == LogReg.CREATE) {
                        saveKeyToSharedPreferences(key);
                        Toast.makeText(getApplicationContext(), "Ключ создан", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Ошибка при проверке ключа", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveKeyToSharedPreferences(String key) {
        SharedPreferences preferences = getSharedPreferences("AUTH_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_room", key);
        editor.apply();
        Intent intent = new Intent(Rooms.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public static String generateCode(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int type = random.nextInt(3);
            switch (type) {
                case 0:
                    code.append((char) (random.nextInt(26) + 'a'));
                    break;
                case 1:
                    code.append((char) (random.nextInt(26) + 'A'));
                    break;
                case 2:
                    code.append(random.nextInt(10));
                    break;
            }
        }
        return code.toString();
    }

}

