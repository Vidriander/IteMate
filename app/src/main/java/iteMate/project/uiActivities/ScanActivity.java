package iteMate.project.uiActivities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import iteMate.project.R;

public class ScanActivity extends AppCompatActivity {

    /**
     * The context of the intent that started this activity.
     */
    private int context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Resolve the context of the intent
        // context = getIntent().getExtras().getInt("IntentOrigins");
        // causes crash, used this instead:
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("IntentOrigins")) {
            context = getIntent().getExtras().getInt("IntentOrigins");
        } else {
            // Handle the case where the key does not exist
            context = -1; // or any default value
        }

        // Set functionality of close button
        findViewById(R.id.close_nfcscan).setOnClickListener(v -> finish());
    }

}
