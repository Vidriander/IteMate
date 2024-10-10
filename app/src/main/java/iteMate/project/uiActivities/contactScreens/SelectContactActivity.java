package iteMate.project.uiActivities.contactScreens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import iteMate.project.R;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Contact;
import iteMate.project.models.Item;
import iteMate.project.repositories.ContactRepository;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.utils.SelectContactAdapter;

public class SelectContactActivity extends AppCompatActivity implements GenericRepository.OnDocumentsFetchedListener<Contact> {
    /**
     * Adapter that will be used to display the contacts
     */
    private SelectContactAdapter adapter;

    /**
     * List of Contacts that will change dynamically based on search
     */
    private List<Contact> searchList = new ArrayList<>();

    /**
     * List of all Contacts
     */
    private List<Contact> allContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inner_items);

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.manage_inner_items_recyclerview);
        adapter = new SelectContactAdapter(this, allContacts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ContactRepository contactRepository = new ContactRepository();
        contactRepository.getAllDocumentsFromFirestore(this);

        // Setting on click listener for save button
        findViewById(R.id.manageInnerItemsSavebutton).setOnClickListener(click -> {
            TrackController.getControllerInstance().getCurrentTrack().setContact(adapter.getSelectedContact());
            finish();
        });

        // Setting on click listener for cancel button
        findViewById(R.id.manageInnerItemsCancelbutton).setOnClickListener(click -> {
            finish();
        });
    }

    @Override
    public void onDocumentFetched(Contact document) {

    }

    @Override
    public void onDocumentsFetched(List<Contact> documents) {
        allContacts = documents;
        searchList = new ArrayList<>(allContacts);
        adapter.setContactList(allContacts);
    }
}
