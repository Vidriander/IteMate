package iteMate.project.uiActivities.utils;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Contact;
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

    /**
     * Method to search for contacts based on a query
     * @param contactList List of contacts to search through
     * @param query Query to search for
     * @return List of contacts that match the query
     */
    public static List<Contact> searchContact(List<Contact> contactList, String query) {
        List<Contact> filteredList = new ArrayList<>();
        for (Contact contact : contactList) {
            String content = contact.getLastName() + " " + contact.getFirstName() + " " + contact.getPhoneNumber() + " " + contact.getEmail();
            if (content.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(contact);
            }
        }
        return filteredList;
    }
}

