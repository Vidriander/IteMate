package iteMate.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.google.android.material.navigation.NavigationBarView;


public class TrackActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // content of track-screen follows here
    }

    @Override
    void setLayoutResID() {
        layoutResID = R.layout.activity_track;
    }

    @Override
    void setBottomNavID() {
        bottomNavID = R.id.track;
    }
}
