package iteMate.project.uiActivities.appScreens;

import android.os.Bundle;

import iteMate.project.R;
import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.uiActivities.MainActivity;

public class HomeActivity extends MainActivity {

    private final ItemController itemController = ItemController.getControllerInstance();
    private final TrackController trackController = TrackController.getControllerInstance();

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
