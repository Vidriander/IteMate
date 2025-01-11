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


import iteMate.project.R;
import iteMate.project.documentController.ContactController;
import iteMate.project.model.Contact;

/**
 * Activity to display a contact's details
 */
public class ContactDetailActivity extends AppCompatActivity {

    /**
     * Contact to display in the detail view
     */
    private Contact contactToDisplay;
    /**
     * Singleton instance of ContactController
     */
    private static final ContactController contactController = ContactController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_contact_detail);

        // Get the contact to display from the intent:
        contactToDisplay = contactController.getCurrentContact();

        if(contactToDisplay == null) {
            finish();
            return;
        }
        setDetailViewContents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // on click listener for back button
        findViewById(R.id.detailcontact_back_button).setOnClickListener(v -> finish());

        // on click listener for edit button
        findViewById(R.id.detailcontact_edit_button).setOnClickListener(v -> {
            Intent intent = new Intent(ContactDetailActivity.this, ContactEditActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactToDisplay = contactController.getCurrentContact();
        setDetailViewContents();
    }

    /**
     * Sets the contents of the detail view
     */
    private void setDetailViewContents() {
        if (contactToDisplay != null) {
            String title = contactToDisplay.getFirstName() + " " + contactToDisplay.getLastName();
            ((TextView) findViewById(R.id.contact_detailcard_title)).setText(title);
            ((TextView) findViewById(R.id.contact_detail_first_name)).setText(contactToDisplay.getFirstName());
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

}