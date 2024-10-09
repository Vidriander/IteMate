package iteMate.project.uiActivities.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import iteMate.project.models.Item;
import iteMate.project.models.Track;

public class SortUtils {
    //region Sort Items
    /**
     * Method to sort items by availability and in those two groups sort by name
     * @param itemList List of items to sort
     * @return List of items sorted by availability and name
     */
    public static List<Item> defaultItemSort(List<Item> itemList) {
        return sortItemsByAvailability(sortItemsByName(itemList));
    }

    /**
     * Method to sort items by name
     * @param itemList List of items to sort
     * @return List of items sorted by name
     */
    public static List<Item> sortItemsByName(List<Item> itemList) {
        List<Item> sortedList = new ArrayList<>(itemList);
        sortedList.sort(Comparator.comparing(item -> item.getTitle().toLowerCase()));
        return sortedList;
    }

    /**
     * Method to sort items by availability: available items first, then unavailable items
     * @param itemList List of items to sort
     * @return List of items sorted by availability
     */
    public static List<Item> sortItemsByAvailability(List<Item> itemList) {
        List<Item> availableList = new ArrayList<>();
        List<Item> unavailableList = new ArrayList<>();
        List<Item> results = new ArrayList<>();

        for (Item item : itemList) {
            if (item.getActiveTrackID() != null && !item.getActiveTrackID().isEmpty()) {
                availableList.add(item);
            } else {
                unavailableList.add(item);
            }
        }
        results.addAll(unavailableList);
        results.addAll(availableList);

        return results;
    }
    //endregion

    //region Sort Tracks

    /**
     * Method to sort tracks by default: active tracks first, then inactive tracks, then by lend out date
     * @param trackList List of tracks to sort
     * @return List of tracks sorted by default
     */
    public static List<Track> defaultTrackSort(List<Track> trackList) {
        return sortTracksByActiveState(sortTracksByLendOutDate(trackList));
    }

    /**
     * Method to sort tracks by state (active or inactive)
     * @param trackList List of tracks to sort
     * @return List of tracks sorted by state
     */
    public static List<Track> sortTracksByActiveState(List<Track> trackList) {
        List<Track> activeList = new ArrayList<>();
        List<Track> inactiveList = new ArrayList<>();
        List<Track> results = new ArrayList<>();

        for (Track track : trackList) {
            if (track.isActive()) {
                activeList.add(track);
            } else {
                inactiveList.add(track);
            }
        }
        results.addAll(activeList);
        results.addAll(inactiveList);

        return results;
    }

    /**
     * Method to sort tracks by lend out date
     * @param trackList List of tracks to sort
     * @return List of tracks sorted by lend out date
     */
    public static List<Track> sortTracksByLendOutDate(List<Track> trackList) {
        List<Track> sortedList = new ArrayList<>(trackList);
        sortedList.sort(Comparator.comparing(Track::getGiveOutDate));
        return sortedList;
    }
    //endregion
}
