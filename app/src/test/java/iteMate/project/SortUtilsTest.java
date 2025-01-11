package iteMate.project;

import static org.junit.Assert.assertArrayEquals;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import iteMate.project.models.Contact;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.utils.SortUtils;

public class SortUtilsTest {

    Item item1, item2, item3, item4;
    List<Item> itemList;
    Track track1, track2, track3, track4;
    List<Track> trackList;

    @Before
    public void setUp() {
        itemList = new ArrayList<>();
        trackList = new ArrayList<>();

        // adding items
        item1 = new Item();
        item1.setTitle("Item 1");
        item1.setActiveTrackID(null);
        itemList.add(item1);

        item2 = new Item();
        item2.setTitle("Item 2");
        item2.setActiveTrackID("2");
        itemList.add(item2);

        item3 = new Item();
        item3.setTitle("Item 3");
        item3.setActiveTrackID("3");
        itemList.add(item3);

        item4 = new Item();
        item4.setTitle("Item 4");
        item4.setActiveTrackID(null);
        itemList.add(item4);
        // expected order: item1, item4, item2, item3

        // adding tracks
        track1 = new Track(new Timestamp(1000L,0),null,new Contact(),null,new ArrayList<>(),
                new ArrayList<>(),"some","1");
        track1.setPendingItemIDsList(Arrays.asList("1","2"));
        trackList.add(track1);
        track2 = new Track(new Timestamp(2000L,0),null,new Contact(),null,new ArrayList<>(),
                new ArrayList<>(),"some","2");
        track2.setPendingItemIDsList(Arrays.asList("3","4"));
        trackList.add(track2);
        track3 = new Track(new Timestamp(3000L,0),null,new Contact(),null,new ArrayList<>(),
                new ArrayList<>(),"some","3");
        trackList.add(track3);
        track4 = new Track(new Timestamp(4000L,0),null,new Contact(),null,new ArrayList<>(),
                new ArrayList<>(),"some","4");
        trackList.add(track4);
        // expected order: track2, track1, track3, track4
    }

    @Test
    public void defaultItemSortTest() {
        List<Item> sortedList = SortUtils.defaultItemSort(itemList);
        assertArrayEquals(new Item[]{item1, item4, item2, item3}, sortedList.toArray());
    }

    @Test
    public void sortItemsByNameTest() {
        List<Item> sortedList = SortUtils.sortItemsByName(itemList);
        assertArrayEquals(new Item[]{item1, item2, item3, item4}, sortedList.toArray());
    }

    @Test
    public void sortItemsByAvailabilityTest() {
        List<Item> sortedList = SortUtils.sortItemsByAvailability(itemList);
        assertArrayEquals(new Item[]{item1, item4, item2, item3}, sortedList.toArray());
    }

    @Test
    public void defaultTrackSortTest() {
        List<Track> sortedList = SortUtils.defaultTrackSort(trackList);
        System.out.println(sortedList);
        assertArrayEquals(new Track[]{track2, track1, track4, track3}, sortedList.toArray());
    }

    @Test
    public void sortTracksByActiveStateTest() {
        List<Track> sortedList = SortUtils.sortTracksByActiveState(trackList);
        assertArrayEquals(new Track[]{track1, track2, track3, track4}, sortedList.toArray());
    }

    @Test
    public void sortTracksByLendOutDateTest() {
        List<Track> sortedList = SortUtils.sortTracksByLendOutDate(trackList);
        assertArrayEquals(new Track[]{track4, track3, track2, track1}, sortedList.toArray());
    }
}
