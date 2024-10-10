package iteMate.project.uiActivities.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iteMate.project.R;
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

    public SelectContactAdapter(Context context) {
        this.context = context;
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
        holder.contactAdress.setText(contact.getEmail());

        // setting the listener for the checkbox
        holder.checkBox.setOnCheckedChangeListener(null); // Remove previous listener
        holder.checkBox.setChecked(contact.equals(selectedContact)); // Set checkbox state

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
        return 0;
    }

    /**
     * ViewHolder class for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView contactAdress;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactNameText_selectContact);
            contactAdress = itemView.findViewById(R.id.mailadressText_selectContact);
            checkBox = itemView.findViewById(R.id.selectContact_checkbox);
        }
    }
}
