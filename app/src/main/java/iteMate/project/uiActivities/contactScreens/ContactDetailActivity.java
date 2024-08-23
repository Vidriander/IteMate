package iteMate.project.uiActivities.contactScreens;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import iteMate.project.R;
import iteMate.project.models.Contact;

public class ContactDetailActivity extends AppCompatActivity {

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
    }

    private void setDetailViewContents() {
        if(contactToDisplay != null) {
            ((TextView)findViewById(R.id.contact_detailcard_sideheader)).setText(contactToDisplay.getFirstName() + " " + contactToDisplay.getLastName());
            ((TextView)findViewById(R.id.contact_detailcard_title)).setText(contactToDisplay.getEmail());
        } else {
            Log.e("ContactDetailActivity", "contactToDisplay is null in setDetailViewContents");
        }
    }
}