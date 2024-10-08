package iteMate.project.uiActivities.contactScreens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import iteMate.project.uiActivities.utils.SearchUtils;
import iteMate.project.models.Contact;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.utils.ContactAdapter;
import iteMate.project.R;
import iteMate.project.repositories.ContactRepository;


public class ContactActivity extends AppCompatActivity implements GenericRepository.OnDocumentsFetchedListener<Contact> {

    private RecyclerView recyclerViewContact;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private ContactRepository contactRepository;
    /**
     * List of Contacts that will change dynamically based on search
     */
    private List<Contact> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact); // Ensure this line is present

        // Initialize ContactRepository
        contactRepository = new ContactRepository();

        // Initialize RecyclerView
        recyclerViewContact = findViewById(R.id.recyclerViewContacts);
        recyclerViewContact.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Contact list and search list
        contactList = new ArrayList<>();
        searchList = new ArrayList<>(contactList);

        // Initialize ContactAdapter
        contactAdapter = new ContactAdapter(contactList, this);
        recyclerViewContact.setAdapter(contactAdapter);

        // Fetch contacts from Firestore DB
        fetchContacts();

        // Set up onClickListener for back button
        findViewById(R.id.contact_back_button).setOnClickListener(v -> finish());

        // Set up onClickListener for the add contact button
        findViewById(R.id.contact_add_button).setOnClickListener(v -> {
            Intent intent = new Intent(ContactActivity.this, ContactEditActivity.class);
            intent.putExtra("contact", new Contact());
            startActivity(intent);
        });

        // Configure the SearchView
        SearchView searchView = findViewById(R.id.search_view_contacts);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                performSearch(query);
                return true;
            }
        });
    }

    private void fetchContacts() {
        Log.d("ContactActivity", "Fetching contacts from Firestore");
        contactRepository.getAllDocumentsFromFirestore(this);
    }

    /**
     * Perform the search and update the contactList
     * @param query The search query
     */
    private void performSearch(String query) {
        List<Contact> filteredList = SearchUtils.searchContact(contactList, query);
        contactList.clear();
        contactList.addAll(filteredList);
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchContacts();
    }

    @Override
    public void onDocumentFetched(Contact document) {
        // pass
    }

    @Override
    public void onDocumentsFetched(List<Contact> documents) {
        contactList.clear();
        contactList.addAll(documents);

        searchList.clear();
        searchList.addAll(documents);

        contactAdapter.notifyDataSetChanged();
    }
}