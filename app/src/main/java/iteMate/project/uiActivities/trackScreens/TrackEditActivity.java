package iteMate.project.uiActivities.trackScreens;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.contactScreens.SelectContactActivity;
import iteMate.project.uiActivities.scanScreen.ManageScanActivity;
import iteMate.project.uiActivities.utils.InnerItemsAdapter;

public class TrackEditActivity extends AppCompatActivity implements GenericRepository.OnDocumentsFetchedListener<Item> {

    private Track trackToDisplay;
    private InnerItemsAdapter horizontalAdapter;
    private List<Item> itemList;
    private final TrackController trackController = TrackController.getControllerInstance();
    private RecyclerView horizontalRecyclerView;

    /**
     * Date picker dialog for selecting date
     */
    private final View.OnClickListener datePicker = v -> {
        // get instance of calendar
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Variable for date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(

                TrackEditActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // on below line we are setting date to our text view.
                    String date = dayOfMonth + "." + (monthOfYear + 1) + "." + year1;
                    ((TextView)v).setText(date);

                },
                // Passing year, month and day for selected date in date picker
                year, month, day);
        // Calling show to display date picker dialog
        datePickerDialog.show();
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
        trackToDisplay = trackController.getCurrentTrack();

        if (trackToDisplay == null) {
            Log.e("TrackEditActivity", "trackToDisplay is null");
            finish(); // Close the activity if trackToDisplay is null
        }

        setDetailViewContents();

        // Initialize RecyclerView for horizontal list of items
        horizontalRecyclerView = findViewById(R.id.trackedit_recycler);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        setUpAdapter();

        // Adding click listeners for our pick date buttons
//        TextView lendDate = findViewById(R.id.lentOnDateEdit);
//        lendDate.setOnClickListener(datePicker);
//        lendDate.setPaintFlags(lendDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        TextView returnDate = findViewById(R.id.returnDateEdit);
        returnDate.setOnClickListener(datePicker);
        returnDate.setPaintFlags(returnDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // setting on click listener for select contact text
        TextView contact = findViewById(R.id.trackEditLentToText);
        contact.setOnClickListener(click -> {
            saveChangesToLocalTrack();
            Intent intent = new Intent(this, SelectContactActivity.class);
            startActivity(intent);
        });

        // setting on click listener for manage items button
        FloatingActionButton manageItemsButton = findViewById(R.id.manage_items_in_track_button);
        manageItemsButton.setOnClickListener(click -> {
            saveChangesToLocalTrack();
            Intent intent = new Intent(this, ManageTrackItemsActivity.class);
            startActivity(intent);
        });

        // Setting on click listener for cancel button
        Button cancelButton = findViewById(R.id.trackedit_cancel);
        cancelButton.setOnClickListener(click -> finish());

        // setting on click for save button
        Button saveButton = findViewById(R.id.track_edit_save);
        saveButton.setOnClickListener(click -> {
            if (trackController.isReadyForUpload()) {
                trackController.saveChangesToTrack(trackToDisplay);
                finish();
            } else {
                Toast toast = Toast.makeText(this, "Please fill out all fields and add at least one item", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // setting on click listener for scan button
        FloatingActionButton scanButton = findViewById(R.id.scan_button_track_edit);
        scanButton.setOnClickListener(click -> {
            Intent intent = new Intent(this, ManageScanActivity.class);
            startActivity(intent);
        });
    }

    private void setUpAdapter() {
        horizontalAdapter = new InnerItemsAdapter(trackToDisplay.getLentItemsList(), this, true);
        horizontalRecyclerView.setAdapter(horizontalAdapter);
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault());
            ((TextView) findViewById(R.id.lentOnDateEdit)).setText(sdf.format(trackToDisplay.getGiveOutDate().toDate()));
            // setting return date
            ((TextView) findViewById(R.id.returnDateEdit)).setText(sdf.format(trackToDisplay.getReturnDate().toDate()));
            // setting additional info field
            ((EditText) findViewById(R.id.trackEditAdditionalInfo)).setText(trackToDisplay.getAdditionalInfo());

        } else {
            Log.e("ItemsDetailActivity", "itemToDisplay is null in setDetailViewContents");
        }
    }

    /**
     * Saves the changes made to the local track object
     */
    private void saveChangesToLocalTrack() {
        // setting the additional info
        String additionalInfo = ((EditText) findViewById(R.id.trackEditAdditionalInfo)).getText().toString();
        trackToDisplay.setAdditionalInfo(additionalInfo);
        // setting the return date
        String returnDateString = ((TextView) findViewById(R.id.returnDateEdit)).getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault());
        try {
            trackToDisplay.setReturnDate(new Timestamp(sdf.parse(returnDateString)));
        } catch (Exception e) {
            Log.e("TrackEditActivity", "Error parsing return date: " + e.getMessage());
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

    @Override
    protected void onResume() {
        super.onResume();
        trackToDisplay = trackController.getCurrentTrack();
        setDetailViewContents();
        setUpAdapter();
    }
}