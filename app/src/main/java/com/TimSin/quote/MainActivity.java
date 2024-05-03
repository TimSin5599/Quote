package com.TimSin.quote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Dialog dialog;
    RecyclerViewAdapter recyclerViewAdapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        dialog = new Dialog(MainActivity.this);

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(view -> {
            showCustomDialog();
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> dataList = new ArrayList<>();
                recyclerViewAdapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data = snapshot.getValue(String.class);
                    dataList.add(data);
                }

                Collections.reverse(dataList);

                for (String data : dataList) {
                    recyclerViewAdapter.addItem(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showCustomDialog() {
        dialog.setContentView(R.layout.dialog_alert_add);
        dialog.setCancelable(true);

        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(view -> dialog.dismiss());

        Button buttonOK = dialog.findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(view -> {
            EditText editText = dialog.findViewById(R.id.editText);
            recyclerViewAdapter.addItem(editText.getText().toString());
            addDataToFirebase(editText.getText().toString());
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        int id = item.getItemId();
        if (id == R.id.aboutProgram) {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogAlertTheme);
            builder.setTitle("О программе")
                    .setMessage("Данная программа создана для записи цитат")
                    .setPositiveButton("ОК", (dialog, id1) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();*/
            return true;
        } else if (id == R.id.something) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDataToFirebase(String newData) {
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(newData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

