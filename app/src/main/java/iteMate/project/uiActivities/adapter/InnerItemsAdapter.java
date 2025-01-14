package iteMate.project.uiActivities.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iteMate.project.R;
import iteMate.project.documentController.ItemController;
import iteMate.project.model.Item;
import iteMate.project.databaseManager.ItemRepository;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;

/**
 * Adapter for the RecyclerView in the ManageInnerItemsActivity.
 * This adapter is responsible for displaying the items in the RecyclerView.
 */
public class InnerItemsAdapter extends RecyclerView.Adapter<InnerItemsAdapter.ViewHolder> {
    private final List<Item> items;
    private final Context context;

    /**
     * Adapter for the RecyclerView in the ManageInnerItemsActivity.
     * This adapter is responsible for displaying the items in the RecyclerView.
     */
    public InnerItemsAdapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;
        ItemRepository itemRepository = new ItemRepository();

        // Set the contained and associated items for each of the contained and associated items.
        // This ensures that the contained and associated items are already loaded when the user clicks on one.
        for (Item item : items) {
            itemRepository.setContainedAndAssociatedItems(item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contained_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.itemName.setText(item.getTitle());

        // TODO check if this can be replaced to separate adapter and controller
        ItemController itemController = ItemController.getControllerInstance();
        itemController.setImageForView(context, item.getImage(), holder.itemImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemsDetailActivity.class);
            ItemController.getControllerInstance().setCurrentItem(item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder class for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public ImageView itemImage;

        /**
         * ViewHolder class for the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.containeditem_title);
            itemImage = itemView.findViewById(R.id.containeditemcard_image);
        }
    }
}