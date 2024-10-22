package iteMate.project.uiActivities.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.ItemController;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;

/**
 * Adapter for the RecyclerView in the ManageScanActivity.
 * This adapter is responsible for displaying the items in the RecyclerView.
 */
public class ManageScanAdapter extends RecyclerView.Adapter<ManageScanAdapter.ViewHolder> {

    private final List<Item> items;
    private final Context context;

    public ManageScanAdapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;

        // Ensure the cache directory exists
        File cacheDir = new File(context.getCacheDir(), "images");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();  // Create the directory if it doesn't exist
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        // setting the item name
        holder.itemName.setText(item.getTitle());
        // setting the item description
        holder.tagNumber.setText(String.valueOf(item.getDescription()));
        // setting the item image
        GenericRepository.setImageForView(context, item.getImage(), holder.itemImage);

        // setting the onClickListener for the cardview
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

    public void setItems(List<Item> lentItemsList) {
        items.clear();
        items.addAll(lentItemsList);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView itemName;
        public TextView tagNumber;
        public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_cardview);
            itemName = itemView.findViewById(R.id.itemcard_header_text);
            tagNumber = itemView.findViewById(R.id.itemcard_subheader_text);
            itemImage = itemView.findViewById(R.id.itemcard_image);
        }
    }
}