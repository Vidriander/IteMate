package iteMate.project.uiActivities.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;

/**
 * Adapter for the RecyclerView in the ItemsActivity.
 * This adapter is responsible for displaying the items in the RecyclerView.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> items;
    private Context context;

    public ItemAdapter(List<Item> items, Context context) {
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
        holder.itemName.setText(item.getTitle());
        holder.tagNumber.setText(String.valueOf(item.getDescription()));

        GenericRepository.setImageForView(context, item.getImage(), holder.itemImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemsDetailActivity.class);
            intent.putExtra("item", item);
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
        public TextView tagNumber;
        public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemcard_header_text);
            tagNumber = itemView.findViewById(R.id.itemcard_subheader_text);
            itemImage = itemView.findViewById(R.id.itemcard_image);
        }
    }
}
