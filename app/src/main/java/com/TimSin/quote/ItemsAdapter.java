package com.TimSin.quote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> implements Filterable {

    Context context;
    List<Case> data;
    List<Case> filteredData;
    private int position;
    private final String category;

    public ItemsAdapter(Context context, String category) {
        this.data = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        this.context = context;
        this.category = category;
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_quote_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemsAdapter.ViewHolder holder, int position) {
        Case item = filteredData.get(position);
        holder.textView.setText(item.getText());
        holder.owner.setText(item.getOwner());
        if (Objects.equals(item.getStatus(), "1")) {
            holder.buttonStar.setBackgroundResource(R.drawable.star_gold);
        } else {
            holder.buttonStar.setBackgroundResource(R.drawable.star_grey);
        }

        holder.itemView.setOnLongClickListener(view -> {
            setPosition(position);
            return false;
        });

        holder.itemView.setOnClickListener(view -> setPosition(position));
    }

    public void sortItemsByOwnerUp() {
        Collections.sort(data, (o1, o2) -> o1.getOwner().compareToIgnoreCase(o2.getOwner()));
        Collections.sort(filteredData, (o1, o2) -> o1.getOwner().compareToIgnoreCase(o2.getOwner()));
        notifyDataSetChanged();
    }

    public void sortItemsByOwnerDown() {
        Collections.sort(data, (o1, o2) -> o2.getOwner().compareToIgnoreCase(o1.getOwner()));
        Collections.sort(filteredData, (o1, o2) -> o2.getOwner().compareToIgnoreCase(o1.getOwner()));
        notifyDataSetChanged();
    }

    public void sortItemsByTextUp() {
        Collections.sort(data, (o1, o2) -> o1.getText().compareToIgnoreCase(o2.getText()));
        Collections.sort(filteredData, (o1, o2) -> o1.getText().compareToIgnoreCase(o2.getText()));
        notifyDataSetChanged();
    }

    public void sortItemsByTextDown() {
        Collections.sort(data, (o1, o2) -> o2.getText().compareToIgnoreCase(o1.getText()));
        Collections.sort(filteredData, (o1, o2) -> o2.getText().compareToIgnoreCase(o1.getText()));
        notifyDataSetChanged();
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
        Case item = filteredData.get(position);
        data.remove(item);
        filteredData.remove(position);
        this.notifyDataSetChanged();
    }

    Case getObject(int position) {
        return filteredData.get(position);
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public void addItem(String owner, String status, String text, String key) {
        Case item = new Case(owner, status, text, key);
        data.add(item);
        filteredData.add(item);
        this.notifyDataSetChanged();
    }

    public void changeItem(String owner, String status, String text, int position) {
        Case item = filteredData.get(position);
        item.setOwner(owner);
        item.setText(text);
        this.notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        filteredData.clear();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();
                List<Case> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = data;
                } else {
                    for (Case item : data) {
                        if (item.getText().toLowerCase().contains(query)) {
                            filtered.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (List<Case>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button buttonStar;
        TextView owner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textBlock);
            buttonStar = itemView.findViewById(R.id.buttonStar);
            owner = itemView.findViewById(R.id.owner);

            buttonStar.setOnClickListener(view -> {
                int position = getAdapterPosition();
//                String category = getCategory();
                Case item = filteredData.get(position);

                if (Objects.equals(item.getStatus(), "0")) {
//                    buttonStar.setBackgroundResource(R.drawable.star_gold);
                    ((MainActivity) itemView.getContext()).changeItemStatus(position, new Case(item.getOwner(), "1", item.getText()));


                } else {
//                    buttonStar.setBackgroundResource(R.drawable.star_grey);
                    ((MainActivity) itemView.getContext()).changeItemStatus(position, new Case(item.getOwner(), "0", item.getText()));
                }
            });
        }
    }
}
