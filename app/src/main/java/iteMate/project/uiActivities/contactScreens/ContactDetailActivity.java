package iteMate.project.uiActivities.contactScreens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Contact;
import iteMate.project.repositories.ContactRepository;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;
import iteMate.project.uiActivities.trackScreens.TrackEditActivity;

public class ContactDetailActivity extends AppCompatActivity implements ContactRepository.OnContactsFetchedListener {

    private Contact contactToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_contact_detail);

        // Get the contact to display from the intent:
        contactToDisplay = getIntent().getParcelableExtra("contact");

        if(contactToDisplay == null) {
            Log.e("ContactDetailActivity", "contactToDisplay is null");
            finish(); // Close the activity if contactToDisplay is null
            return;
        }
        setDetailViewContents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // on click listener for back button
        findViewById(R.id.detailtrack_back_button).setOnClickListener(v -> onBackPressed());

        // on click listener for edit button
        findViewById(R.id.detailtrack_edit_button).setOnClickListener(v -> {
            Intent intent = new Intent(ContactDetailActivity.this, ContactEditActivity.class);
            intent.putExtra("contact", contactToDisplay);
            startActivity(intent);
        });

    }

    private void setDetailViewContents() {
        if (contactToDisplay != null) {
            // ((TextView) findViewById(R.id.contact_detailcard_sideheader)).setText(contactToDisplay.getFirstName() + " " + contactToDisplay.getLastName());
            ((TextView) findViewById(R.id.contact_detailcard_title)).setText(contactToDisplay.getFirstName() + " " + contactToDisplay.getLastName());            ((TextView) findViewById(R.id.contact_detail_first_name)).setText(contactToDisplay.getFirstName());
            ((TextView) findViewById(R.id.contact_detail_last_name)).setText(contactToDisplay.getLastName());
            ((TextView) findViewById(R.id.contact_detail_street)).setText(contactToDisplay.getStreet());
            ((TextView) findViewById(R.id.contact_detail_zip)).setText(String.valueOf(contactToDisplay.getZip()));
            ((TextView) findViewById(R.id.contact_detail_city)).setText(contactToDisplay.getCity());
            ((TextView) findViewById(R.id.contact_detail_phone)).setText(contactToDisplay.getPhone());
            ((TextView) findViewById(R.id.contact_detail_email)).setText(contactToDisplay.getEmail());
        } else {
            Log.e("ContactDetailActivity", "contactToDisplay is null in setDetailViewContents");
        }
    }

    /**
     * Called when the contacts are fetched from Firestore
     * @param contacts the list of contacts fetched
     */
    @Override
    public void onContactsFetched(List<Contact> contacts) {
        Log.d("ContactDetailActivity", "onContactsFetched called with " + contacts.size() + " contacts");
    }

}