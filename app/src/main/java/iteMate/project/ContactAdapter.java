package iteMate.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Contact> contacts;
    private Context context;

    /**
     * Stores the id of the clicked Contact, if any.
     */
    private static Contact clickedContact;

    /**
     * Returns the id of the clicked Contact in order to display the correct Contact in the detail view.
     * @return the id of the clicked Contact
     */
    public static Contact getClickedContact() {
        return clickedContact;
    }

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
        holder.contactName.setText(contact.getFirstName() + " " + contact.getLastName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackDetailActivity.class);
            intent.putExtra("contact", contact);
            clickedContact = contact;
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView contactEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactcard_header_text);
            contactEmail = itemView.findViewById(R.id.contactcard_subheader_text);
        }
    }
}