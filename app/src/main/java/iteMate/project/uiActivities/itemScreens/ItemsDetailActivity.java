package iteMate.project.uiActivities.itemScreens;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Item;
import iteMate.project.R;
import iteMate.project.uiActivities.utils.ContainedItemAdapter;
import iteMate.project.uiActivities.utils.ItemAdapter;

public class ItemsDetailActivity extends AppCompatActivity {

    private Item itemToDisplay;
    private RecyclerView horizontalRecyclerView;
    private ContainedItemAdapter horizontalAdapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_items_detail);

        // Get the item to display from the intent:
        itemToDisplay = getIntent().getParcelableExtra("item");
//        itemToDisplay = ItemAdapter.getClickedItem(); // another way of getting the item to display

        if (itemToDisplay == null) {
            Log.e("ItemsDetailActivity", "itemToDisplay is null");
            finish(); // Close the activity if itemToDisplay is null
            return;
        }
        setDetailViewContents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize Item list
        itemList = new ArrayList<>();
        itemList.add(new Item(111111, "Rose Backroad", "Description 1", R.drawable.rose_bike, true, null));
        itemList.add(new Item(222222, "Nukeproof Digger", "Description 2", R.drawable.bikepacking, true, null));
        itemList.add(new Item(333333, "Cube Nuroad", "Description 3", R.drawable.rose2, true, null));

        horizontalRecyclerView = findViewById(R.id.itemdetailview_containeditems_recyclerview);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapter = new ContainedItemAdapter(itemList,this);
        horizontalRecyclerView.setAdapter(horizontalAdapter);
    }

    private void setDetailViewContents() {
        if (itemToDisplay != null) {
             ((ImageView)findViewById(R.id.item_detailcard_image)).setImageResource(itemToDisplay.getImage());
            ((TextView)findViewById(R.id.item_detailcard_title)).setText(itemToDisplay.getTitle());
            ((TextView)findViewById(R.id.item_detailcard_sideheader)).setText(String.valueOf(itemToDisplay.getNfcTag()));
        } else {
            Log.e("ItemsDetailActivity", "itemToDisplay is null in setDetailViewContents");
        }
    }
}