package iteMate.project.uiActivities.scanScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanItemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Item itemToDisplay;
    private Track trackToDisplay;

    public ScanItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanItemFragment newInstance(String param1, String param2) {
        ScanItemFragment fragment = new ScanItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_item, container, false);

        // Set on click listener for item card view
        view.findViewById(R.id.item_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ItemsDetailActivity.class);
            intent.putExtra("item", itemToDisplay);
            startActivity(intent);
        });

        // Set item details for track card view
        view.findViewById(R.id.track_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
            intent.putExtra("track", trackToDisplay);
            startActivity(intent);
        });

        return view;
    }

    void setItemToDisplay(Item item) {
        this.itemToDisplay = item;
    }

    void setTrackToDisplay(Track track) {
        this.trackToDisplay = track;
    }

}