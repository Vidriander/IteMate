package iteMate.project.uiActivities.contactScreens;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import iteMate.project.models.Contact;
import iteMate.project.uiActivities.utils.ContactAdapter;
import iteMate.project.R; // Add this import statement


public class ContactActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact); // Set the correct layout

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewContacts); // Correct ID reference
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Contact list
        contactList = new ArrayList<>();
        contactList.add(new Contact("John", "Doe", "1234567890", "john@doe.com", "Jumpstreet 21", "Endor"));
        contactList.add(new Contact("Jane", "Doe", "555123987", "jane@doe.com", "Jumpstreet 42", "Endor"));
        contactList.add(new Contact("Steve", "Sali", "0000000000", "steve@sali.com", "Jumpstreet 13", "Endor"));

        // Initialize ContactAdapter
        contactAdapter = new ContactAdapter(contactList, this);
        recyclerView.setAdapter(contactAdapter);
    }
}