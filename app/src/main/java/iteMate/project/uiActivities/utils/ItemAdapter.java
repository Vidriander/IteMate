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
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items;
    private Context context;

    /**
     * Stores the id of the clicked item, if any.
     * NFC tag numbers are used as ids.
     */
    private static Item clickedItem;

    /**
     * Returns the id of the clicked item in order to display the correct item in the detail view.
     * @return the id of the clicked item
     */
    public static Item getClickedItem() {
        return clickedItem;
    }

    public ItemAdapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;
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
        holder.tagNumber.setText(String.valueOf(item.getNfcTag()));
        holder.itemImage.setImageResource(item.getImage());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemsDetailActivity.class);
            intent.putExtra("item", item);
            clickedItem = item;
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

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