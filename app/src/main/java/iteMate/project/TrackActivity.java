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


public class TrackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // Initialize and assign variable
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        navigationBarView.setSelectedItemId(R.id.track);

        // Perform item selected listener
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.items:
                        startActivity(new Intent(getApplicationContext(), ItemsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.track:
                        return true;
                }
                return false;
            }
        });
    }

    private void showPopupMenu(View view) {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_drawer, popupMenu.getMenu());

        // Set click listener for menu items
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_contacts:
//                        startActivity(new Intent(MainActivity.this, FirstActivity.class)); // Replace FirstActivity with the activity you want to start
                        return true;
                    case R.id.nav_settings:
//                        startActivity(new Intent(MainActivity.this, SecondActivity.class)); // Replace SecondActivity with the activity you want to start
                        return true;
                    case R.id.nav_logout:
//                        startActivity(new Intent(MainActivity.this, ThirdActivity.class)); // Replace ThirdActivity with the activity you want to start
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Show the popup menu
        popupMenu.show();
    }
}
