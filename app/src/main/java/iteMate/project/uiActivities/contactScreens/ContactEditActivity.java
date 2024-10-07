package iteMate.project.uiActivities.contactScreens;

import android.content.Intent;
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


public class ContactEditActivity extends AppCompatActivity {

    private static Contact contactToDisplay;
    private ContactRepository contactRepository;

    private EditText firstName;
    private EditText lastName;
    private EditText street;
    private EditText zip;
    private EditText city;
    private EditText phone;
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
        findViewById(R.id.contact_edit_cancel_btn).setOnClickListener(v -> onBackPressed());

        // on click listener for save button
        findViewById(R.id.contact_edit_save_btn).setOnClickListener(v -> {
            saveChangesToContact();
            Log.d("ContactEditActivity", "Saving changes to contact: " + contactToDisplay.toString());

            // Add or update the contact in Firestore
            if (contactToDisplay.getId() == null || contactToDisplay.getId().isEmpty()) {
                contactRepository.addDocumentToFirestore(contactToDisplay);
            } else {
                contactRepository.updateDocumentInFirestore(contactToDisplay);
            }
            finish();
        });

        // on click listener for delete button
        findViewById(R.id.contact_edit_delete_btn).setOnClickListener(v -> {
            contactRepository.deleteDocumentFromFirestore(contactToDisplay);
            Intent intent = new Intent(ContactEditActivity.this, ContactActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setEditViewContents() {
        if (contactToDisplay != null) {
            firstName.setText(contactToDisplay.getFirstName());
            lastName.setText(contactToDisplay.getLastName());
            street.setText(contactToDisplay.getStreet());
            zip.setText(String.valueOf(contactToDisplay.getZip()));
            city.setText(contactToDisplay.getCity());
            phone.setText(contactToDisplay.getPhone());
            email.setText(contactToDisplay.getEmail());
        } else {
            Log.e("ContactEditActivity", "contactToDisplay is null in setEditViewContents");
        }
    }

    private void saveChangesToContact() {
        contactToDisplay.setFirstName(firstName.getText().toString());
        contactToDisplay.setLastName(lastName.getText().toString());
        contactToDisplay.setStreet(street.getText().toString());
        contactToDisplay.setZip(Integer.parseInt(zip.getText().toString()));
        contactToDisplay.setCity(city.getText().toString());
        contactToDisplay.setPhone(phone.getText().toString());
        contactToDisplay.setEmail(email.getText().toString());
    }
}