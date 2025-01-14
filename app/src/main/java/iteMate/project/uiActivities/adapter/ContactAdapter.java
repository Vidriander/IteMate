package iteMate.project.uiActivities.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import iteMate.project.R;
import iteMate.project.documentController.ContactController;
import iteMate.project.model.Contact;
import iteMate.project.uiActivities.contactScreens.ContactDetailActivity;

/**
 * Adapter for the RecyclerView in the ContactActivity.
 * This adapter is responsible for displaying the contacts in the RecyclerView.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private final List<Contact> contacts;
    private final Context context;

    /**
     * ViewHolder class for the RecyclerView.
     */
    public ContactAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.contactEmail.setText(contact.getEmail());
        String contactName = contact.getFirstName() + " " + contact.getLastName();
        holder.contactName.setText(contactName);

        // Set up onClickListener for the contact card
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ContactDetailActivity.class);
            ContactController.getControllerInstance().setCurrentContact(contact);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    /**
     * ViewHolder class for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView contactEmail;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView View of the item
         */
        public ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactcard_header_text);
            contactEmail = itemView.findViewById(R.id.contactcard_subheader_text);
        }
    }
}