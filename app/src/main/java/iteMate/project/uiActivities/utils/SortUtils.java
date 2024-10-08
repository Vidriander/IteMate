package iteMate.project.uiActivities.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import iteMate.project.models.Item;

public class SortUtils {

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
}
