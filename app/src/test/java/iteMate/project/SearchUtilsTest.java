package iteMate.project;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import iteMate.project.models.Contact;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.utils.SearchUtils;

public class SearchUtilsTest {
    /**
     * List of items to search through
     */
    List<Item> itemList;
    /**
     * List of tracks to search through
     */
    List<Track> trackList;
    /**
     * List of contacts to search through
     */
    List<Contact> contactList;
    Contact contact1, contact2, contact3, contact4, contact5;

    @Before
    public void setUp(){
        itemList = new ArrayList<>(Arrays.asList(
                new Item("1","TAG x","Fahrrad","heute","Image 1",false,new ArrayList<>(),new ArrayList<>(),"Owner 1",null),
                new Item("2","TAG y","Roller-ß","ist","Image 2",false,new ArrayList<>(),new ArrayList<>(),"Owner 2",null),
                new Item("3","TAG z","Schuhe","ein","Image 3",false,new ArrayList<>(),new ArrayList<>(),"Owner 3",null),
                new Item("4","TAG ß","Schrank","schöner","Image 4",false,new ArrayList<>(),new ArrayList<>(),"Owner 4",null),
                new Item("5","TAG ü","Münze","tag","Image 5",false,new ArrayList<>(),new ArrayList<>(),"Owner 5",null)
        ));
        contact1 = new Contact("David", "Müller", "123456789", "david@müller.de", "Straße 1", "Stadt 1", "12345", "Owner 1");
        contact1.setId("1");
        contact2 = new Contact("Chris", "Müller", "204975448", "none", "Straße 1", "Stadt 1", "12345", "Owner 1");
        contact2.setId("2");
        contact3 = new Contact("Leo", "Gentry", "1", "leo@leo.leo", "Straße 1", "Stadt 1", "12345", "Owner 1");
        contact3.setId("3");
        contact4 = new Contact("Hershel", "Fershel", "2", "none", "Straße 1", "Stadt 1", "12345", "Owner 2");
        contact4.setId("4");
        contact5 = new Contact("Mohamad", "Farah", "3", "none", "Straße 1", "Stadt 1", "12345", "Owner 1");
        contact5.setId("5");
        contactList = new ArrayList<>(Arrays.asList(contact1, contact2, contact3, contact4, contact5));

        trackList = new ArrayList<>(Arrays.asList(
                new Track(null, null, contact1,"1",new ArrayList<>(),new ArrayList<>(),"Owner 1","nothing to see here"),
                new Track(null, null, contact2,"1",new ArrayList<>(),new ArrayList<>(),"Owner 1","nothing to see here"),
                new Track(null, null, contact3,"1",new ArrayList<>(),new ArrayList<>(),"Owner 1","nothing to see here"),
                new Track(null, null, contact4,"1",new ArrayList<>(),new ArrayList<>(),"Owner 1","nothing to see here"),
                new Track(null, null, contact5,"1",new ArrayList<>(),new ArrayList<>(),"Owner 1","nothing to see here")
                ));

    }

    // region searchItems tests
    @Test
    public void searchItemsNoMatchTest(){
        List<Item> result = SearchUtils.searchItems(itemList, "23908475");
        assert(result.isEmpty());
    }

    @Test
    public void searchItemsMatchInTitleTest(){
        List<Item> result = SearchUtils.searchItems(itemList, "Schrank");
        assert(result.size() == 1);
        assert(result.get(0).equals(itemList.get(3)));
    }

    @Test
    public void searchItemsMatchInDescriptionTest(){
        List<Item> result = SearchUtils.searchItems(itemList, "schöner");
        assert(result.size() == 1);
        assert(result.get(0).equals(itemList.get(3)));
    }

    @Test
    public void searchItemsMatchInNfcTagTest(){
        List<Item> result = SearchUtils.searchItems(itemList, "TAG y");
        assert(result.size() == 1);
        assert(result.get(0).equals(itemList.get(1)));
    }

    @Test
    public void searchItemsMultipleMatchesTest(){
        List<Item> result = SearchUtils.searchItems(itemList, "ß");
        assert(result.size() == 2);
        assert(result.contains(itemList.get(1)));
        assert(result.contains(itemList.get(3)));
    }

    @Test
    public void searchItemsCaseInsensitiveTest(){
        List<Item> result = SearchUtils.searchItems(itemList, "mÜNze");
        assert(result.size() == 1);
        assert(result.get(0).equals(itemList.get(4)));
    }
    // endregion

    // region searchTracks tests
    @Test
    public void searchTracksNoMatchTest(){
        List<Track> result = SearchUtils.searchTracks(trackList, "23908475");
        assert(result.isEmpty());
    }

    @Test
    public void searchTracksMatchInContactNameTest(){
        List<Track> result = SearchUtils.searchTracks(trackList, "David");
        assert(result.size() == 1);
        assert(result.get(0).equals(trackList.get(0)));
    }

    @Test
    public void searchTracksMatchInContactPhoneNumberTest(){
        List<Track> result = SearchUtils.searchTracks(trackList, "204975448");
        assert(result.size() == 1);
        assert(result.get(0).equals(trackList.get(1)));
    }

    @Test
    public void searchTracksMatchInContactEmailTest(){
        List<Track> result = SearchUtils.searchTracks(trackList, "leo@leo.leo");
        assert(result.size() == 1);
        assert(result.get(0).equals(trackList.get(2)));
    }

    @Test
    public void searchTracksMultipleMatchesTest(){
        List<Track> result = SearchUtils.searchTracks(trackList, "none");
        assert(result.size() == 3);
        assert(result.contains(trackList.get(1)));
        assert(result.contains(trackList.get(3)));
        assert(result.contains(trackList.get(4)));
    }

    @Test
    public void searchTracksCaseInsensitiveTest(){
        List<Track> result = SearchUtils.searchTracks(trackList, "mohamad");
        assert(result.size() == 1);
        assert(result.get(0).equals(trackList.get(4)));
    }
    // endregion

    // region searchContact tests
    @Test
    public void searchContactNoMatchTest(){
        List<Contact> result = SearchUtils.searchContact(contactList, "23908475");
        assert(result.isEmpty());
    }

    @Test
    public void searchContactMatchInLastNameTest(){
        List<Contact> result = SearchUtils.searchContact(contactList, "Müller");
        assert(result.size() == 2);
        assert(result.contains(contact1));
        assert(result.contains(contact2));
    }

    @Test
    public void searchContactMatchInFirstNameTest(){
        List<Contact> result = SearchUtils.searchContact(contactList, "Leo");
        assert(result.size() == 1);
        assert(result.get(0).equals(contact3));
    }

    @Test
    public void searchContactMatchInPhoneNumberTest(){
        List<Contact> result = SearchUtils.searchContact(contactList, "3");
        assert(result.size() == 2);
        assert(result.contains(contact1));
        assert(result.contains(contact5));
    }

    @Test
    public void searchContactMatchInEmailTest(){
        List<Contact> result = SearchUtils.searchContact(contactList, "leo@leo.leo");
        assert(result.size() == 1);
        assert(result.get(0).equals(contactList.get(2)));
    }

    @Test
    public void searchContactMultipleMatchesTest(){
        List<Contact> result = SearchUtils.searchContact(contactList, "none");
        assert(result.size() == 3);
        assert(result.contains(contactList.get(1)));
        assert(result.contains(contactList.get(3)));
        assert(result.contains(contactList.get(4)));
    }

    @Test
    public void searchContactCaseInsensitiveTest(){
        List<Contact> result = SearchUtils.searchContact(contactList, "mohamad");
        assert(result.size() == 1);
        assert(result.get(0).equals(contactList.get(4)));
    }
    // endregion
}
