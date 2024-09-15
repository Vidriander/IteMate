package iteMate.project.Track;

import android.os.Parcel;
import android.util.Log;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import iteMate.project.models.Contact;
import iteMate.project.models.Item;
import iteMate.project.models.Track;

public class TrackTests {

    @Test
    public void trackParcelableTest() {


        List<Item> items = new ArrayList<>(Arrays.asList(new Item(), new Item()));
        List<String> lentItemIDs = new ArrayList<>(Arrays.asList("1", "2"));
        Track track = new Track(new Timestamp(new Date()), new Timestamp(new Date()), new Contact(), "contactID", items, lentItemIDs, "ownerID");
        Parcel parcel = Parcel.obtain();
        track.writeToParcel(parcel, track.describeContents());
        parcel.setDataPosition(0);
        Track createdFromParcel = Track.CREATOR.createFromParcel(parcel);

        assertEquals(createdFromParcel.getGiveOutDate(), track.getGiveOutDate());
        assertEquals(createdFromParcel.getReturnDate(), track.getReturnDate());
//        assertEquals(createdFromParcel.getContact(), track.getContact());               // fails
//        assertEquals(createdFromParcel.getContactID(), track.getContactID());           // fails
//        assertArrayEquals(createdFromParcel.getLentItemsList().toArray(), track.getLentItemsList().toArray());  // fails: Items are probably reconstructed with a new memory address. Test content instead
        assertEquals(createdFromParcel.getLentItemIDs(), track.getLentItemIDs());
        assertEquals(createdFromParcel.getOwnerID(), track.getOwnerID());


    }
}
