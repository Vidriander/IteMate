package iteMate.project.uiActivities.contactScreens;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import iteMate.project.R;
import iteMate.project.models.Contact;
import iteMate.project.repositories.ContactRepository;

/**
 * Activity to edit a contact
 */
public class ContactEditActivity extends AppCompatActivity {

    /**
     * Contact to display in the edit view
     */
    private static Contact contactToDisplay;
    /**
     * ContactRepository to interact with database
     */
    private ContactRepository contactRepository;

    private TextView title;
    /**
     * EditTexts for the contact's first name
     */
    private EditText firstName;
    /**
     * EditTexts for the contact's last name
     */
    private EditText lastName;
    /**
     * EditTexts for the contact's address
     */
    private EditText street;
    /**
     * EditTexts for the contact's zip code
     */
    private EditText zip;
    /**
     * EditTexts for the contact's city
     */
    private EditText city;
    /**
     * EditTexts for the contact's phone number
     */
    private EditText phone;
    /**
     * EditTexts for the contact's email
     */
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_contact_edit);

        // Initialize ContactRepository
        contactRepository = new ContactRepository();

        // Get the contact to edit from the intent
        contactToDisplay = getIntent().getParcelableExtra("contact");

        if (contactToDisplay == null) {
            Log.e("ContactEditActivity", "contactToDisplay is null");
            finish(); // Close the activity if contactToDisplay is null
            return;
        }

        // Initialize EditTexts
        title = findViewById(R.id.contact_editcard_title);
        firstName = findViewById(R.id.contact_edit_first_name);
        lastName = findViewById(R.id.contact_edit_last_name);
        street = findViewById(R.id.contact_edit_street);
        zip = findViewById(R.id.contact_edit_zip);
        city = findViewById(R.id.contact_edit_city);
        phone = findViewById(R.id.contact_edit_phone);
        email = findViewById(R.id.contact_edit_email);

        // Set the contents of the edit view
        setEditViewContents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // on click listener for cancel button
        findViewById(R.id.contact_edit_cancel_btn).setOnClickListener(v -> finish());

        // on click listener for save button
        findViewById(R.id.contact_edit_save_btn).setOnClickListener(v -> {
            saveChangesToContact();
            Log.d("ContactEditActivity", "Saving changes to contact: " + contactToDisplay.toString());

            // Add or update the contact in database
            if (contactToDisplay.getId() == null || contactToDisplay.getId().isEmpty()) {
                contactRepository.addDocumentToDatabase(contactToDisplay);
            } else {
                contactRepository.updateDocumentInDatabase(contactToDisplay);
            }
            finish();
        });

        // on click listener for delete button
        findViewById(R.id.contact_edit_delete_btn).setOnClickListener(v -> {
            contactRepository.deleteDocumentFromDatabase(contactToDisplay);
            finish();
        });
    }

    /**
     * Set the contents of the edit view
     */
    private void setEditViewContents() {
        if (contactToDisplay != null) {
            String titleText = contactToDisplay.getFirstName() + " " + contactToDisplay.getLastName();
            if (titleText.trim().isEmpty()) {
                titleText = "New Contact";
            }
            title.setText(titleText);
            firstName.setText(contactToDisplay.getFirstName());
            lastName.setText(contactToDisplay.getLastName());
            street.setText(contactToDisplay.getStreet());
            zip.setText(contactToDisplay.getZip());
            city.setText(contactToDisplay.getCity());
            phone.setText(contactToDisplay.getPhone());
            email.setText(contactToDisplay.getEmail());
        } else {
            Log.e("ContactEditActivity", "contactToDisplay is null in setEditViewContents");
        }
    }

    /**
     * Save the changes made to the contact in the edit view
     */
    private void saveChangesToContact() {
        contactToDisplay.setFirstName(firstName.getText().toString());
        contactToDisplay.setLastName(lastName.getText().toString());
        contactToDisplay.setStreet(street.getText().toString());
        contactToDisplay.setZip(zip.getText().toString());
        contactToDisplay.setCity(city.getText().toString());
        contactToDisplay.setPhone(phone.getText().toString());
        contactToDisplay.setEmail(email.getText().toString());
    }
}