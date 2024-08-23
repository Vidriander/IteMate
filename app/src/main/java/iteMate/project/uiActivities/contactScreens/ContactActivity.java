package iteMate.project.uiActivities.contactScreens;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import iteMate.project.models.Contact;
import iteMate.project.uiActivities.utils.ContactAdapter;
import iteMate.project.R;
import iteMate.project.repositories.ContactRepository;


public class ContactActivity extends AppCompatActivity implements ContactRepository.OnContactsFetchedListener {

    private RecyclerView recyclerViewContact;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private ContactRepository contactRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact); // Ensure this line is present

        // Initialize ContactRepository
        contactRepository = new ContactRepository();

        // Initialize RecyclerView
        recyclerViewContact = findViewById(R.id.recyclerViewContacts);
        recyclerViewContact.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Contact list
        contactList = new ArrayList<>();

        // Initialize ContactAdapter
        contactAdapter = new ContactAdapter(contactList, this);
        recyclerViewContact.setAdapter(contactAdapter);

        // Fetch contacts from Firestore
        contactRepository.getAllContactsFromFirestore(this);
    }

    @Override
    public void onContactsFetched(List<Contact> contacts) {
        contactList.clear();
        contactList.addAll(contacts);
        contactAdapter.notifyDataSetChanged();
    }
}