package iteMate.project;
import android.os.Parcel;
import android.os.Parcelable;

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

    // Default Constructor
    public Track() {
    }

    // Constructor
    public Track(Date giveOutDate, Date returnDate, Contact contact, List<Item> LendList) {
        this.giveOutDate = giveOutDate;
        this.returnDate = returnDate;
        this.contact = contact;
        this.LendList = LendList;
    }

    // Getter
    public Date getGiveOutDate() {
        return giveOutDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public Contact getContact() {
        return contact;
    }

    public List<Item> getLendList() {
        return LendList;
    }

    // Setter
    public void setGiveOutDate(Date giveOutDate) {
        this.giveOutDate = giveOutDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setLendList(List<Item> LendList) {
        this.LendList = LendList;
    }

    public void addToLendList(Item item) {
        LendList.add(item);
    }

    public void removeFromLendList(Item item) {
        LendList.remove(item);
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
    }

}