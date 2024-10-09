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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.TrackRepository;
import iteMate.project.uiActivities.utils.ButtonController;
import iteMate.project.uiActivities.utils.InnerItemsAdapter;

public class TrackEditActivity extends AppCompatActivity implements GenericRepository.OnDocumentsFetchedListener<Item> {

    private Track trackToDisplay;
    private RecyclerView horizontalRecyclerView;
    private InnerItemsAdapter horizontalAdapter;
    private List<Item> itemList;

    /**
     * Date picker dialog for selecting date
     */
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
                            String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            ((TextView)v).setText(date);

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

        // Initialize RecyclerView for horizontal list of items
        horizontalRecyclerView = findViewById(R.id.trackedit_recycler);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapter = new InnerItemsAdapter(trackToDisplay.getLentItemsList(), this, true);
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        // Adding click listeners for our pick date buttons
        TextView lendDate = findViewById(R.id.lentOnDateEdit);
        lendDate.setOnClickListener(datePicker);
        lendDate.setPaintFlags(lendDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        TextView returnDate = findViewById(R.id.returnDateEdit);
        returnDate.setOnClickListener(datePicker);
        returnDate.setPaintFlags(returnDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Setting on click for cancel button
        Button cancelButton = findViewById(R.id.trackedit_cancel);
        cancelButton.setOnClickListener(click -> {
            ButtonController.exitActivityWithoutSaving(this);
        });
        // setting on click for save button
        Button saveButton = findViewById(R.id.track_edit_save);
        saveButton.setOnClickListener(click -> {
            ButtonController.exitActivityWithSaving(this);
        });
    }

    private void setDetailViewContents() {
        if (trackToDisplay != null) {
            String displayableName = trackToDisplay.getContact().getFirstName() + " " + trackToDisplay.getContact().getLastName();
            // setting title
            ((TextView) findViewById(R.id.trackedit_title)).setText(displayableName);
            // setting "lent to" field & underline
            TextView contact = findViewById(R.id.trackEditLentToText);
            contact.setText(displayableName);
            contact.setPaintFlags(contact.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            // setting lent on date
            ((TextView) findViewById(R.id.lentOnDateEdit)).setText(LocalDateTime.ofInstant(trackToDisplay.getGiveOutDate().toDate().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
            // setting return date
            ((TextView) findViewById(R.id.returnDateEdit)).setText(LocalDateTime.ofInstant(trackToDisplay.getReturnDate().toDate().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
            // setting additional info field
            ((EditText) findViewById(R.id.trackEditAdditionalInfo)).setText(trackToDisplay.getAdditionalInfo());

        } else {
            Log.e("ItemsDetailActivity", "itemToDisplay is null in setDetailViewContents");
        }
    }

    @Override
    public void onDocumentFetched(Item document) {
    }

    @Override
    public void onDocumentsFetched(List<Item> documents) {
        itemList.clear();
        itemList.addAll(documents);
        horizontalAdapter.notifyDataSetChanged();
    }
}