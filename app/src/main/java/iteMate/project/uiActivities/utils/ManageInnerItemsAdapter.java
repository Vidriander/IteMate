package iteMate.project.uiActivities.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;

public class ManageInnerItemsAdapter extends RecyclerView.Adapter<ManageInnerItemsAdapter.ViewHolder> {
    private List<Item> checkedItems;
    private List<Item> allItems;
    private List<Item> itemList;
    private Context context;

    public ManageInnerItemsAdapter(List<Item> checkedItems, List<Item> allItems, Context context) {
        this.checkedItems = checkedItems;
        this.allItems = allItems;
        this.context = context;

        setUpLists();
    }

    private void setUpLists() {
        itemList = new ArrayList<>();
        itemList.addAll(this.checkedItems);
        for (Item item : this.allItems) {
            if (checkedItems.stream().map(Item::getNfcTag).noneMatch(tag -> tag.equals(item.getNfcTag()))) {
                itemList.add(item);
            }
        }
    }

    public void notifyItemsAvailable(List<Item> items) {
        allItems = items;
        itemList = new ArrayList<>();
        itemList.addAll(this.checkedItems);
        for (Item item : this.allItems) {
            if (checkedItems.stream().map(Item::getNfcTag).noneMatch(tag -> tag.equals(item.getNfcTag()))) {
                itemList.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ManageInnerItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_inner_items_card, parent, false);
        return new ManageInnerItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageInnerItemsAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Setting the item name:
        holder.itemName.setText(item.getTitle());
        // Setting the NFC tag number:
        holder.tagNumber.setText(String.valueOf(item.getNfcTag()));
        // Setting the image:
        GenericRepository.setImageForView(context, item.getImage(), holder.itemImage);
        // Setting the checkbox:
        holder.checkBox.setChecked(checkedItems.stream().map(Item::getNfcTag).anyMatch(tag -> tag.equals(item.getNfcTag())));

        // Items currently not clickable. Is that desired?
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ItemsDetailActivity.class);
//            intent.putExtra("item", item);
//            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * ViewHolder class for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView tagNumber;
        public ImageView itemImage;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.manageInnerItems_title);
            tagNumber = itemView.findViewById(R.id.manageInnerItems_subheader);
            itemImage = itemView.findViewById(R.id.manageInnerItems_image);
            checkBox = itemView.findViewById(R.id.manageInnerItems_checkbox);
        }
    }
}
