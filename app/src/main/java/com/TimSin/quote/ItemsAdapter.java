package com.TimSin.quote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    Context context;
    List<Case> data;
    private int position;
    private String category;

    public ItemsAdapter(Context context, String category) {
        this.data = new ArrayList<>();
        this.context = context;
        this.category = category;
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_quote_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemsAdapter.ViewHolder holder, int position) {
        holder.textView.setText(data.get(position).getText());
        holder.owner.setText(data.get(position).getOwner());

        holder.itemView.setOnLongClickListener(view -> {
            setPosition(position);
            return false;
        });

        holder.itemView.setOnClickListener(view -> setPosition(position));
    }

    private void setPosition(int position) {
        this.position = position;
    }

    public String getCategory() {
        return category;
    }

    int getPosition() {
        return position;
    }

    void deleteObject(int position) {
        data.remove(position);
        this.notifyDataSetChanged();
    }

    Case getObject(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItem(String owner, String status, String text, String key) {
        data.add(new Case(owner, status, text, key));
        this.notifyDataSetChanged();
    }

    public void changeItem(String owner, String status, String text, int position) {
        Case item = data.get(position);
        item.setOwner(owner);
        item.setText(text);
        this.notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button buttonStar;
        TextView owner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Case item = getObject(position);
            textView = itemView.findViewById(R.id.textBlock);
            buttonStar = itemView.findViewById(R.id.buttonStar);
            owner = itemView.findViewById(R.id.owner);
            buttonStar.setOnClickListener(view -> {
                if (Objects.equals(item.getStatus(), "0")) {
                    buttonStar.setBackgroundResource(R.drawable.star_gold);
                    item.setStatus("1");
                } else {
                    buttonStar.setBackgroundResource(R.drawable.star_grey);
                    item.setStatus("0");
                }
            });
        }

    }
}
