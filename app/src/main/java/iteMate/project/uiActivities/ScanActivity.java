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
        context = getIntent().getExtras().getInt("IntentOrigins");

        // Set functionality of close button
        findViewById(R.id.close_nfcscan).setOnClickListener(v -> finish());
    }

}
