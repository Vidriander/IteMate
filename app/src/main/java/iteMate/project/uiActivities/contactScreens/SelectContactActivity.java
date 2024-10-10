package iteMate.project.uiActivities.contactScreens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Contact;
import iteMate.project.uiActivities.utils.ManageInnerItemsAdapter;
import iteMate.project.uiActivities.utils.SelectContactAdapter;

public class SelectContactActivity extends AppCompatActivity {
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
        adapter = new SelectContactAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
