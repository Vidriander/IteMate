package iteMate.project.uiActivities.trackScreens;
//package com.gtappdevelopers.kotlingfgproject;

import android.app.DatePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
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

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.TrackRepository;
import iteMate.project.uiActivities.MainActivity;
import iteMate.project.uiActivities.utils.ContainedItemAdapter;
import iteMate.project.uiActivities.utils.TrackAdapter;

public class TrackEditActivity extends AppCompatActivity implements TrackRepository.OnTracksFetchedListener, ItemRepository.OnItemsFetchedListener {

    private Track trackToDisplay;
    private RecyclerView horizontalRecyclerView;
    private ContainedItemAdapter horizontalAdapter;
    private List<Item> itemList;

    private View.OnClickListener datePicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get instance of calendar
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Variable for date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(

                    TrackEditActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // on below line we are setting date to our text view.
                            ((TextView)v).setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    },
                    // Passing year, month and day for selected date in date picker
                    year, month, day);
            // Calling show to display date picker dialog
            datePickerDialog.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Get the Track object from the intent
        trackToDisplay = getIntent().getParcelableExtra("track");

        if (trackToDisplay == null) {
            Log.e("TrackEditActivity", "trackToDisplay is null");
            finish(); // Close the activity if trackToDisplay is null
        }
        setDetailViewContents();

        // Initialize ItemRepository
        ItemRepository itemRepository = new ItemRepository();

        // Initialize Item list
        itemList = new ArrayList<>();

        // Fetch all items from Firestore
        itemRepository.getAllItemsFromFirestore(this);

        // Initialize RecyclerView for horizontal list of items
        horizontalRecyclerView = findViewById(R.id.trackedit_recycler);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapter = new ContainedItemAdapter(itemList, this, true);
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        // on click listener for back button


        // Adding click listeners for our pick date buttons
        TextView lendDate = findViewById(R.id.lentOnDateEdit);
        lendDate.setOnClickListener(datePicker);
        lendDate.setPaintFlags(lendDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        TextView returnDate = findViewById(R.id.returnDateEdit);
        returnDate.setOnClickListener(datePicker);
        returnDate.setPaintFlags(returnDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void setDetailViewContents() {
        if (trackToDisplay != null) {
            ((TextView) findViewById(R.id.trackedit_title)).setText(trackToDisplay.getContact().getFirstName() + trackToDisplay.getContact().getLastName());
        } else {
            Log.e("ItemsDetailActivity", "itemToDisplay is null in setDetailViewContents");
        }
    }

    @Override
    public void onItemsFetched(List<Item> items) {
        itemList.clear();
        itemList.addAll(items);
        horizontalAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTracksFetched(List<Track> tracks) {

    }
}