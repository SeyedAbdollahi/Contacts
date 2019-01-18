package seyedabdollahi.ir.mycontacts.Adapters;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import seyedabdollahi.ir.mycontacts.ClassBase64;
import seyedabdollahi.ir.mycontacts.Events.ContactClicked;
import seyedabdollahi.ir.mycontacts.Models.Contact;
import seyedabdollahi.ir.mycontacts.R;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{

    private List<Contact> items;

    public ContactAdapter(List<Contact> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_contact_row, parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        if (items.get(position).getIsNewAlphabet()){
            try {
                holder.alphabet.setText(Character.toString(items.get(position).getName().toUpperCase().charAt(0)));
            }
            catch (Exception e){
                Log.d("TAG" , "[ContactAdapter]-[setAlphabetText] Exception Error");
                holder.alphabet.setVisibility(View.GONE);
            }
        }else {
            holder.alphabet.setVisibility(View.GONE);
        }
        holder.name.setText(items.get(position).getName());
        holder.contactImage.setImageBitmap(ClassBase64.base64ToImage(items.get(position).getEncodedBase64Image80dp()));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = items.get(position);
                EventBus.getDefault().post(new ContactClicked(contact));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder{

        private TextView alphabet;
        private TextView name;
        private CircleImageView contactImage;
        private LinearLayout layout;

        private ViewHolder(View itemView) {
            super(itemView);
            alphabet = itemView.findViewById(R.id.template_contact_alphabet);
            name = itemView.findViewById(R.id.template_contact_name);
            contactImage = itemView.findViewById(R.id.template_contact_img);
            layout = itemView.findViewById(R.id.template_contact_layout);
        }
    }
}
