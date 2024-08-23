package iteMate.project.uiActivities.trackScreens;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import iteMate.project.R;
import iteMate.project.models.Track;

public class TrackDetailActivity extends AppCompatActivity {

    private Track trackToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_track_detail);

        // Get the track to display from the intent:
        trackToDisplay = getIntent().getParcelableExtra("track");

        if (trackToDisplay == null) {
            Log.e("TrackDetailActivity", "trackToDisplay is null");
            finish(); // Close the activity if trackToDisplay is null
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
        if (trackToDisplay != null) {
            ((TextView)findViewById(R.id.track_detailcard_sideheader)).setText(trackToDisplay.getGiveOutDate().toString());
            ((TextView)findViewById(R.id.track_detailcard_title)).setText(trackToDisplay.getContact().getFirstName() + " " + trackToDisplay.getContact().getLastName());
        } else {
            Log.e("TrackDetailActivity", "trackToDisplay is null in setDetailViewContents");
        }
    }
}