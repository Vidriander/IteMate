package iteMate.project.uiActivities.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;

public class ManageInnerItemsAdapter extends RecyclerView.Adapter<ManageInnerItemsAdapter.ViewHolder> {
    private List<Item> checkedItems;
    private List<Item> allItems;
    private List<Item> itemList;
    private Context context;

    // A set to hold checked item tags
    private Set<Double> checkedItemTags = new HashSet<Double>();

    public ManageInnerItemsAdapter(List<Item> checkedItems, List<Item> allItems, Context context) {
        this.checkedItems = checkedItems;
        this.allItems = allItems;
        this.context = context;

        setUpLists();

        // Initialize the set with the NFC tags of checked items
        for (Item item : checkedItems) {
            checkedItemTags.add(item.getNfcTag());
        }
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

    // This method returns all items whose checkbox is checked
    public List<Item> getNewCheckedItems() {
        List<Item> newCheckedItems = new ArrayList<>();
        for (Item item : itemList) {
            if (checkedItemTags.contains(item.getNfcTag())) {
                newCheckedItems.add(item);
            }
        }
        return newCheckedItems;
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
        holder.checkBox.setChecked(checkedItemTags.contains(item.getNfcTag()));

        // Handle checkbox state change
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedItemTags.add(item.getNfcTag()); // Add tag to the checked set
            } else {
                checkedItemTags.remove(item.getNfcTag()); // Remove tag from the checked set
            }
        });

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
