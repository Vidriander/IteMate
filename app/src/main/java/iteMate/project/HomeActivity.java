package iteMate.project;

import android.os.Bundle;

public class HomeActivity extends MainActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // content of main-screen follows here
    }

    @Override
    void setLayoutResID() {
        layoutResID = R.layout.activity_main;
    }

    @Override
    void setBottomNavID() {
        bottomNavID = R.id.home;
    }


}
