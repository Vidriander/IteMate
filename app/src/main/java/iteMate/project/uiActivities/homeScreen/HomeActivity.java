package iteMate.project.uiActivities.homeScreen;

import android.os.Bundle;

import iteMate.project.R;
import iteMate.project.uiActivities.MainActivity;

public class HomeActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // content of main-screen follows here
    }

    @Override
    public void setLayoutResID() {
        layoutResID = R.layout.activity_main;
    }

    @Override
    public void setBottomNavID() {
        bottomNavID = R.id.home;
    }


}
