package iteMate.project.uiActivities.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class ButtonController {

    /**
     * Method to exit the activity without saving
     */
    public static void exitActivityWithoutSaving(AppCompatActivity context) {
        context.finish();
    }

    /**
     * Method to exit the activity with saving
     */
    public static void exitActivityWithSaving(AppCompatActivity context) {
        //TODO: Writing the altered date to the database

        context.finish();
    }
}
