package iteMate.project.uiActivities.scanScreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import iteMate.project.R;

/**
 * Fragment for displaying idle state of scanning
 */
public class ScanIdleFragment extends Fragment {

    public ScanIdleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_idle, container, false);

        // Load the GIF using Glide
        ImageView awaitingScanImg = view.findViewById(R.id.awaiting_scan_img);
        Glide.with(this)
                .asGif()
                .load(R.drawable.awaiting_scan_gif) // Replace with your GIF resource or URL
                .into(awaitingScanImg);

        return view;
    }
}