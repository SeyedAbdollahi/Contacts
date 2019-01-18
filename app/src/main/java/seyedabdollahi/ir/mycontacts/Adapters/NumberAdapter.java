package seyedabdollahi.ir.mycontacts.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import seyedabdollahi.ir.mycontacts.Events.ConnectionClicked;
import seyedabdollahi.ir.mycontacts.Models.Contact;
import seyedabdollahi.ir.mycontacts.R;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.ViewHolder> {

    private Contact contact;
    private List<String> numbers;
    private List<String> typeNumbers;

    public NumberAdapter(Contact contact) {
        this.contact = contact;
        numbers = new ArrayList<>();
        typeNumbers = new ArrayList<>();
        numbers.addAll(contact.getNumbers());
        typeNumbers.addAll(contact.getTypeNumbers());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_number_row, parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.number.setText(numbers.get(position));
        if (typeNumbers.get(position) != null){
            switch (typeNumbers.get(position)){
                case "1":
                    holder.type.setText(R.string.type_home);
                    break;
                case "2":
                    holder.type.setText(R.string.type_mobile);
                    break;
                case "3":
                    holder.type.setText(R.string.type_work);
                    break;
                case "4":
                    holder.type.setText(R.string.type_fax_work);
                    break;
                case "5":
                    holder.type.setText(R.string.type_fax_home);
                    break;
                case "6":
                    holder.type.setText(R.string.type_pager);
                    break;
                case "7":
                    holder.type.setText(R.string.type_other);
                    break;
                case "8":
                    holder.type.setText(R.string.type_call_back);
                    break;
                case "9":
                    holder.type.setText(R.string.type_car);
                    break;
                case "10":
                    holder.type.setText(R.string.type_company_main);
                    break;
                case "11":
                    holder.type.setText(R.string.type_isdn);
                    break;
                case "12":
                    holder.type.setText(R.string.type_main);
                    break;
                case "13":
                    holder.type.setText(R.string.type_other_fax);
                    break;
                case "14":
                    holder.type.setText(R.string.type_radio);
                    break;
                case "15":
                    holder.type.setText(R.string.type_telex);
                    break;
                case "16":
                    holder.type.setText(R.string.type_tty_tdd);
                    break;
                case "17":
                    holder.type.setText(R.string.type_work_mobile);
                    break;
                case "18":
                    holder.type.setText(R.string.type_work_pager);
                    break;
                case "19":
                    holder.type.setText(R.string.type_assistant);
                    break;
                case "20":
                    holder.type.setText(R.string.type_mms);
                    break;
            }
        }
        holder.callImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ConnectionClicked("call" , numbers.get(position)));
            }
        });
        holder.messageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ConnectionClicked("message" , numbers.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder{

        TextView number;
        TextView type;
        //TextView call;
        //TextView message;
        ImageView callImage;
        ImageView messageImage;

        private ViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.template_number_number);
            type = itemView.findViewById(R.id.template_number_type);
            //call = itemView.findViewById(R.id.template_number_call_txt);
            //message = itemView.findViewById(R.id.template_number_message_txt);
            callImage = itemView.findViewById(R.id.template_number_call_img);
            messageImage = itemView.findViewById(R.id.template_number_message_img);
        }
    }
}
