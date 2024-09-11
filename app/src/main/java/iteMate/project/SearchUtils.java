package iteMate.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import iteMate.project.models.Item;
import iteMate.project.models.Track;

public class SearchUtils {

    /**
     * Method to search for items based on a query
     * @param itemList List of items to search through
     * @param query Query to search for
     * @return List of items that match the query
     */
    public static List<Item> searchItems(List<Item> itemList, String query) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            String content = item.getTitle() + " " + item.getDescription() + " " + item.getNfcTag();
            if (content.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    /**
     * Method to search for tracks based on a query
     * @param trackList List of tracks to search through
     * @param query Query to search for
     * @return List of tracks that match the query
     */
    public static List<Track> searchTracks(List<Track> trackList, String query) {
        List<Track> filteredList = new ArrayList<>();
        for (Track track : trackList) {
            String content = track.getContact().getName() + " " + track.getContact().getPhoneNumber() + " " + track.getContact().getEmail();
            if (content.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(track);
            }
        }
        return filteredList;
    }
}

