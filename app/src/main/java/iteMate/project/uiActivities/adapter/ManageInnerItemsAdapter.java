package iteMate.project.uiActivities.adapter;

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

/**
 * Adapter for the RecyclerView in the ManageInnerItemsActivity.
 * This adapter is responsible for displaying the items in the RecyclerView.
 */
public class ManageInnerItemsAdapter extends RecyclerView.Adapter<ManageInnerItemsAdapter.ViewHolder> {
    /**
     * List of items that are checked, given by the context
     */
    private final List<Item> checkedItems;
    /**
     * List of all items that are available to be checked, all items available in the context
     */
    private List<Item> allItems;
    /**
     * List of items that are displayed in the RecyclerView
     */
    private List<Item> itemList;
    /**
     * Context of the activity
     */
    private final Context context;

    /**
     * Set to store IDs of checked items
     */
    private final Set<String> checkedItemTags = new HashSet<>();

    /**
     * Constructor for the ManageInnerItemsAdapter.
     *
     * @param checkedItems List of items that are checked, given by the context
     * @param allItems     List of all items that are available to be checked, all items available in the context
     * @param context      Context of the activity
     */
    public ManageInnerItemsAdapter(List<Item> checkedItems, List<Item> allItems, Context context) {
        this.checkedItems = checkedItems;
        this.allItems = allItems;
        this.context = context;

        // Initialize the set with the IDs of checked items
        for (Item item : checkedItems) {
            checkedItemTags.add(item.getId());
        }

        setUpLists();
    }

    /**
     * This method sets up the itemList by adding the checked items first and then the rest of the items.
     */
    private void setUpLists() {
        itemList = new ArrayList<>();

        // Add checked items to the list first
        for (Item item : this.allItems) {
            if (checkedItemTags.contains(item.getId())) {
                itemList.add(item);
            }
        }
        // Add the rest of the items to the list
        for (Item item : this.allItems) {
            if (!checkedItemTags.contains(item.getId())) {
                itemList.add(item);
            }
        }
    }

    /**
     * This method returns all items whose checkbox is checked.
     *
     * @return List of items whose checkbox is checked
     */
    public List<Item> getNewCheckedItems() {
        List<Item> newCheckedItems = new ArrayList<>();
        for (Item item : itemList) {
            if (checkedItemTags.contains(item.getId())) {
                newCheckedItems.add(item);
            }
        }
        return newCheckedItems;
    }

    /**
     * Set the list of items to be displayed in the RecyclerView.
     *
     * @param searchList List of items to be displayed
     */
    public void setSearchList(List<Item> searchList) {
        this.allItems = searchList;
        setUpLists();
        notifyDataSetChanged();
    }

    /**
     * Set the list of items to be displayed in the RecyclerView.
     *
     * @param items List of items to be displayed
     */
    public void notifyItemsAvailable(List<Item> items) {
        allItems = items;
        itemList = new ArrayList<>();
        itemList.addAll(this.checkedItems);
        for (Item item : this.allItems) {
            if (checkedItems.stream().map(Item::getId).noneMatch(tag -> tag.equals(item.getId()))) {
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
        // Setting the Description
        holder.itemDescription.setText(String.valueOf(item.getDescription()));
        // Setting the image:
        GenericRepository.setImageForView(context, item.getImage(), holder.itemImage);

        // Handle checkbox state change
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedItemTags.add(item.getId()); // Add tag to the checked set
            } else {
                checkedItemTags.remove(item.getId()); // Remove tag from the checked set
            }
        });

        // Setting the checkbox:
        holder.checkBox.setChecked(checkedItemTags.contains(item.getId()));
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
        public TextView itemDescription;
        public ImageView itemImage;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.manageInnerItems_title);
            itemDescription = itemView.findViewById(R.id.manageInnerItems_subheader);
            itemImage = itemView.findViewById(R.id.manageInnerItems_image);
            checkBox = itemView.findViewById(R.id.manageInnerItems_checkbox);
        }
    }
}
