package com.TimSin.quote;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.widget.SearchView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    ItemsAdapter recyclerViewQuotesAdapter, recyclerViewJokesAdapter, recyclerViewIdeasAdapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        int currentNightMode = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            toolbar.setNavigationIcon(R.drawable.white_menu);
        } else if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            toolbar.setNavigationIcon(R.drawable.black_menu);
        }
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        // For items quotes
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewJokesAdapter = new ItemsAdapter(this, "Jokes");
        recyclerViewQuotesAdapter = new ItemsAdapter(this, "Quotes");
        recyclerViewIdeasAdapter = new ItemsAdapter(this, "Ideas");
        recyclerView.setAdapter(recyclerViewQuotesAdapter);


        registerForContextMenu(recyclerView);

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(view -> showCustomDialog());

        databaseReference.child("Quotes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Case> dataList = new ArrayList<>();
                recyclerViewQuotesAdapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Case data = snapshot.getValue(Case.class);
                    assert data != null;
                    dataList.add(new Case(data.getOwner(), data.getStatus(), data.getText(), snapshot.getKey()));
                }

                Collections.reverse(dataList);

                for (Case item : dataList) {
                    recyclerViewQuotesAdapter.addItem(item.getOwner(), item.getStatus(), item.getText(), item.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        databaseReference.child("Jokes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Case> dataList = new ArrayList<>();
                recyclerViewJokesAdapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Case data = snapshot.getValue(Case.class);
                    assert data != null;
                    dataList.add(new Case(data.getOwner(), data.getStatus(), data.getText(), snapshot.getKey()));
                }

                Collections.reverse(dataList);

                for (Case item : dataList) {
                    recyclerViewJokesAdapter.addItem(item.getOwner(), item.getStatus(), item.getText(), item.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        databaseReference.child("Ideas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Case> dataList = new ArrayList<>();
                recyclerViewIdeasAdapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Case data = snapshot.getValue(Case.class);
                    assert data != null;
                    dataList.add(new Case(data.getOwner(), data.getStatus(), data.getText(), snapshot.getKey()));
                }

                Collections.reverse(dataList);

                for (Case item : dataList) {
                    recyclerViewIdeasAdapter.addItem(item.getOwner(), item.getStatus(), item.getText(), item.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Добавляем функциональность поиска
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterRecyclerView(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRecyclerView(newText);
                return false;
            }
        });

        return true;
    }

    private void filterRecyclerView(String query) {
        ItemsAdapter adapter = (ItemsAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.getFilter().filter(query);
        }
    }

    private void showCustomDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_alert_add);
        dialog.setCancelable(true);

        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(view -> dialog.dismiss());

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button buttonOK = dialog.findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(view -> {
            EditText editTextQuote = dialog.findViewById(R.id.editText);
            EditText editTextAuthor = dialog.findViewById(R.id.edittext_author_layout_add_dialog);
            addQuoteToFirebase(editTextQuote.getText().toString(), editTextAuthor.getText().toString());
            dialog.dismiss();
        });

        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        int id = item.getItemId();
        if (id == R.id.more) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogAlertTheme);
            builder.setTitle("О программе")
                    .setMessage("Данная программа разработана недооцененным специалистом, будущим владельцем Яндекса и прочей шалупони")
                    .setPositiveButton("ОК", (dialog, id1) -> dialog.cancel())
                    .setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addQuoteToFirebase(String text, String owner) {
        if (text == null || text.trim().isEmpty() || owner == null || owner.trim().isEmpty()) {
            Toast.makeText(this, "Текст и автор должны быть заполнены ", Toast.LENGTH_SHORT).show();
            return;
        }
        ItemsAdapter adapter = (ItemsAdapter) recyclerView.getAdapter();
        assert adapter != null;
        String key = databaseReference.child(adapter.getCategory()).push().getKey();
        adapter.addItem(text, "0", owner, key);
        assert key != null;
        databaseReference.child(adapter.getCategory()).child(key).setValue(new Case(owner, "0", text));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ItemsAdapter adapter = (ItemsAdapter) recyclerView.getAdapter();
        assert adapter != null;
        int position;
        try {
            position = adapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        System.out.println(adapter.getCategory() + " " + adapter.getPosition());
        int itemID = item.getItemId();

        if (itemID == R.id.delete) {
            showDeleteConfirmationDialog(adapter, position);
        } else if (itemID == R.id.change) {
            Dialog dialog1 = new Dialog(MainActivity.this);
            dialog1.setContentView(R.layout.dialog_alert_change);
            dialog1.setCancelable(true);
            Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Case object = adapter.getObject(position);

            EditText editTextQuote = dialog1.findViewById(R.id.editTextChangeQuote);
            editTextQuote.setText(object.getText());

            EditText editTextAuthor = dialog1.findViewById(R.id.editTextChangeAuthor);
            editTextAuthor.setText(object.getOwner());

            Button buttonCancel = dialog1.findViewById(R.id.buttonCancelChangeDialog);
            buttonCancel.setOnClickListener(view -> dialog1.dismiss());

            Button buttonChange = dialog1.findViewById(R.id.buttonChangeDialog);
            buttonChange.setOnClickListener(view -> {
                adapter.changeItem(editTextAuthor.getText().toString(), object.getStatus(), editTextQuote.getText().toString(), position);
                HashMap<String, Object> result = new HashMap<>();
                result.put("owner", editTextAuthor.getText().toString());
                result.put("text", editTextQuote.getText().toString());
                databaseReference.child(adapter.getCategory()).child(adapter.getObject(position).getKey()).updateChildren(result);
                dialog1.dismiss();
            });

            dialog1.show();
        }
        return super.onContextItemSelected(item);
    }

    private void showDeleteConfirmationDialog(ItemsAdapter adapter, int position) {
        // Создаем билдер диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Получаем LayoutInflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_alert_delete, null);
        builder.setView(dialogView);

        // Настраиваем заголовок и сообщение
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        title.setText("Удаление");
        message.setText("Удалить этот элемент?");

        // Настраиваем кнопки
        Button positiveButton = dialogView.findViewById(R.id.positive_button);
        Button negativeButton = dialogView.findViewById(R.id.negative_button);

        AlertDialog dialog = builder.create();

        positiveButton.setOnClickListener(v -> {
            databaseReference.child(adapter.getCategory()).child(adapter.getObject(position).getKey()).removeValue();
            adapter.deleteObject(position);
            Toast.makeText(this, "Элемент удален", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int ItemId = item.getItemId();

        if (ItemId == R.id.QuotesCategory) {
            recyclerView.setAdapter(recyclerViewQuotesAdapter);
            AppBarLayout appBarLayout = findViewById(R.id.AppBarLayout);
            androidx.appcompat.widget.Toolbar toolbar = appBarLayout.findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.quotes);
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.closeDrawers();
            return true;
        } else if (ItemId == R.id.JokesCategory) {
            recyclerView.setAdapter(recyclerViewJokesAdapter);
            AppBarLayout appBarLayout = findViewById(R.id.AppBarLayout);
            androidx.appcompat.widget.Toolbar toolbar = appBarLayout.findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.jokes);
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.closeDrawers();
            return true;
        } else if (ItemId == R.id.IdeasCategory) {
            recyclerView.setAdapter(recyclerViewIdeasAdapter);
            AppBarLayout appBarLayout = findViewById(R.id.AppBarLayout);
            androidx.appcompat.widget.Toolbar toolbar = appBarLayout.findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.ideas);
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.closeDrawers();
            return true;
        }
        return false;
    }
}

