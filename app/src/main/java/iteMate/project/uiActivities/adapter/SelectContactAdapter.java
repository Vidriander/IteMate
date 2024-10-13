package iteMate.project.uiActivities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Contact;


public class SelectContactAdapter extends RecyclerView.Adapter<SelectContactAdapter.ViewHolder> {

    /**
     * Contact that is selected by the user
     */
    private Contact selectedContact;

    /**
     * Context of the activity
     */
    private final Context context;

    /**
     * List of all contacts that are displayed in the RecyclerView
     */
    private List<Contact> contactList;

    public SelectContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;

        selectedContact = TrackController.getControllerInstance().getCurrentTrack().getContact();
    }

    @NonNull
    @Override
    public SelectContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.select_contact_card, parent, false);
        return new SelectContactAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectContactAdapter.ViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        // setting the contact name
        holder.contactName.setText(contact.getName());

        // setting the contact address
        holder.contactAddress.setText(contact.getEmail());

        // setting the listener for the checkbox
        holder.checkBox.setOnCheckedChangeListener(null); // Remove previous listener

        String currentContactId =  contact.getId();
        String selectedContactId = selectedContact.getId();
        boolean check = currentContactId.equals(selectedContactId);
        holder.checkBox.setChecked(check); // Set checkbox state

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedContact = contact;
                // Uncheck every other checkbox
                notifyDataSetChanged();
            } else {
                selectedContact = null;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }

    /**
     * ViewHolder class for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView contactAddress;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactNameText_selectContact);
            contactAddress = itemView.findViewById(R.id.mailadressText_selectContact);
            checkBox = itemView.findViewById(R.id.selectContact_checkbox);
        }
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public Contact getSelectedContact() {
        return selectedContact;
    }
}
