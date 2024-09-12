package iteMate.project.models;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

/**
 * Track class to store detail of a lend out item.
 */
public class Track implements Parcelable {

    private Date giveOutDate;
    private Date returnDate;
    private Contact contact;
    private List<Item> LendList;
    private List<String> lentItemIDs = new ArrayList<>();;
    private String ownerID;  // #TODO setter for ownerID

    public Track() {
        this.LendList = Arrays.asList(new Item(), new Item());
        this.returnDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7);
    }

    public Track(Date giveOutDate, Date returnDate, Contact contact, List<Item> LendList) {
        this.giveOutDate = giveOutDate;
        this.returnDate = returnDate;
        this.contact = contact;
        this.LendList = LendList;
    }

    public Date getGiveOutDate() {
        return giveOutDate;
    }

    public Contact getContact() {
        return contact;
    }

    public List<Item> getLendList() {
        return LendList;
    }

    /**
     * Method to get the number of items in the list
     * @return the number of items
     */
    public int getNumberOfItems() {
        return LendList != null? LendList.size() : 0;
    }

    /**
     * Method to get the number of days left for the item to be returned
     * @return the number of days left
     */
    public int getDaysLeft() {
//        return 0;
        // TODO: returnDate seems to be a NullPointer (Data not loaded fully or correctly from firestore?)
        return (int) ((returnDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(giveOutDate != null ? giveOutDate.getTime() : -1);
        dest.writeLong(returnDate != null ? returnDate.getTime() : -1);
        dest.writeParcelable(contact, flags);
        dest.writeTypedList(LendList);
    }

    protected Track(Parcel in) {
        long giveOutDateLong = in.readLong();
        giveOutDate = giveOutDateLong != -1 ? new Date(giveOutDateLong) : null;
        long returnDateLong = in.readLong();
        returnDate = returnDateLong != -1 ? new Date(returnDateLong) : null;
        contact = in.readParcelable(Contact.class.getClassLoader());
        LendList = in.createTypedArrayList(Item.CREATOR);
        lentItemIDs = in.createStringArrayList();
    }
}