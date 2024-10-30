package iteMate.project.uiActivities.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;

public class TrackItemsAdapter extends ItemAdapter {

    private final TrackController trackController = TrackController.getControllerInstance();

    public TrackItemsAdapter(List<Item> items, Context context) {
        super(items, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        // setting transparency of the cardview to signal that the item was already returned
        if (item.getActiveTrackID() == null || !Objects.equals(item.getActiveTrackID(), trackController.getCurrentTrack().getId())) {
            holder.cardView.setAlpha(0.5f);
        } else {
            holder.cardView.setAlpha(1f);
        }
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
}
