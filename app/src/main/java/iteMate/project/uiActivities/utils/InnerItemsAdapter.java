package iteMate.project.uiActivities.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.scanScreen.ScanActivity;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InnerItemsAdapter extends RecyclerView.Adapter<InnerItemsAdapter.ViewHolder> {
    private List<Item> items;
    private Context context;

    /**
     * True if the adapter is used in the edit screen, false otherwise.
     */
    private boolean inEditScreen;

    private ItemRepository itemRepository;

    public InnerItemsAdapter(List<Item> items, Context context, boolean inEditScreen) {
        this.items = items;
        this.context = context;
        this.inEditScreen = inEditScreen;
        this.itemRepository = new ItemRepository();

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

    // #TODO umschreiben - methode sehr lang!
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.itemName.setText(item.getTitle());

        boolean deprecated = true;
        if (this.inEditScreen && position == 0 && !deprecated) {
            holder.itemName.setText("Add New Item");
            holder.itemImage.setImageResource(R.drawable.baseline_add_24);

            // adding on click listener to the add new item card
            holder.itemView.setOnClickListener(v -> {
                // Handle add new item click
                Intent intent = new Intent(context, ScanActivity.class);
                intent.putExtra("IntentOrigin", IntentOrigins.TRACK_EDIT_ACTIVITY);
                context.startActivity(intent);
            });

        } else {
            // Get the StorageReference of the image
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReference().child(item.getImage());

            // Check if image exists in the local cache
            File localFile = new File(context.getCacheDir(), "images/" + item.getImage());
            File localDir = localFile.getParentFile();
            if (localDir != null && !localDir.exists()) {
                localDir.mkdirs();  // Create the directory if it doesn't exist
            }

            if (localFile.exists()) {
                // Load the image from the local file
                Glide.with(context)
                        .load(localFile)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.placeholder_image)  // image in drawables
                        .error(R.drawable.error_image)  // image in drawables
                        .into(holder.itemImage);
            } else {
                // Download the image from Firebase Storage and save it locally
                imageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    // Once downloaded, load the image from the local file
                    Glide.with(context)
                            .load(localFile)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(holder.itemImage);
                    Log.d("ContainedItemAdapter", "Image downloaded and cached: " + localFile.getPath());
                }).addOnFailureListener(exception -> {
                    // Handle the error gracefully, e.g., show a placeholder image
                    Glide.with(context)
                            .load(R.drawable.error_image)
                            .into(holder.itemImage);
                    Log.w("ContainedItemAdapter", "Error getting download URL", exception);
                });
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ItemsDetailActivity.class);
                intent.putExtra("item", item);
                context.startActivity(intent);
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.containeditem_title);
            itemImage = itemView.findViewById(R.id.containeditemcard_image);
        }
    }
}