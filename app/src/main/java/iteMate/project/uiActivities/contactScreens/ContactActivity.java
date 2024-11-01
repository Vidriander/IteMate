package iteMate.project.uiActivities.contactScreens;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.controller.ContactController;
import iteMate.project.repositories.listeners.OnMultipleDocumentsFetchedListener;
import iteMate.project.utils.SearchUtils;
import iteMate.project.models.Contact;
import iteMate.project.uiActivities.adapter.ContactAdapter;
import iteMate.project.R;

/**
 * This class is the main activity for the Contact screen. It displays a list of contacts
 * and allows the user to search for contacts. The user can also add a new contact.
 */
public class ContactActivity extends AppCompatActivity implements OnMultipleDocumentsFetchedListener<Contact> {

    private ContactAdapter contactAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Contact> contactList;
    private final ContactController contactController = ContactController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Initialize RecyclerView
        RecyclerView recyclerViewContact = findViewById(R.id.recyclerViewContacts);
        recyclerViewContact.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Contact list and search list
        contactList = new ArrayList<>();

        // Initialize ContactAdapter
        contactAdapter = new ContactAdapter(contactList, this);
        recyclerViewContact.setAdapter(contactAdapter);

        // Fetch contacts from DB
        contactController.fetchAllContactsFromDatabase(this);

        // Set up onClickListener for back button
        findViewById(R.id.contact_back_button).setOnClickListener(v -> finish());

        // Set up onClickListener for the add contact button
        findViewById(R.id.contact_add_button).setOnClickListener(v -> {
            Contact newContact = new Contact();
            contactController.setCurrentContact(newContact);
            Intent intent = new Intent(ContactActivity.this, ContactEditActivity.class);
            startActivity(intent);
        });

        // Set up on refresh listener for swipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutContacts);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            contactController.fetchAllContactsFromDatabase(this);
            swipeRefreshLayout.setRefreshing(false);
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
        contactController.fetchAllContactsFromDatabase(this);
    }

    @Override
    public void onDocumentsFetched(List<Contact> documents) {
        contactList.clear();
        contactList.addAll(documents);
        contactAdapter.notifyDataSetChanged();
    }
}