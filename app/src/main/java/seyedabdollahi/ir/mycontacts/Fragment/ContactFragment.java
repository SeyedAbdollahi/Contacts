package seyedabdollahi.ir.mycontacts.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

import seyedabdollahi.ir.mycontacts.Adapters.NumberAdapter;
import seyedabdollahi.ir.mycontacts.ClassBase64;
import seyedabdollahi.ir.mycontacts.Events.BackEvent;
import seyedabdollahi.ir.mycontacts.Events.BirthdayUpdated;
import seyedabdollahi.ir.mycontacts.Events.ContactDeleted;
import seyedabdollahi.ir.mycontacts.Events.ContactEdited;
import seyedabdollahi.ir.mycontacts.Events.ConnectionClicked;
import seyedabdollahi.ir.mycontacts.Events.CallPhonePermission;
import seyedabdollahi.ir.mycontacts.Events.UpdateFragment;
import seyedabdollahi.ir.mycontacts.Events.WriteContactPermission;
import seyedabdollahi.ir.mycontacts.Keys;
import seyedabdollahi.ir.mycontacts.Models.Contact;
import seyedabdollahi.ir.mycontacts.R;

public class ContactFragment extends Fragment {

    Contact contact;
    ImageView contactImage;
    TextView contactName;
    RecyclerView recyclerView;
    private TextView txtBirthday;
    private Button btnBirthday1;
    private Button btnBirthday2;
    private boolean updateContactListener = false;
    private String callNumber;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.contact_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArgument();
        findViews(view);
        configViews();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getArgument() {
        Bundle bundle = getArguments();
        contact = (Contact) bundle.getSerializable("contact");
    }

    private void findViews(View view) {
        contactImage = view.findViewById(R.id.contact_img);
        contactName = view.findViewById(R.id.contact_name);
        recyclerView = view.findViewById(R.id.contact_recycle);
        txtBirthday = view.findViewById(R.id.contact_txt_birthday);
        btnBirthday1 = view.findViewById(R.id.contact_btn_birthday1);
        btnBirthday2 = view.findViewById(R.id.contact_btn_birthday2);
    }

    private void configViews() {
        //config contact image
        if (contact.getEncodedBase64ImageOriginal() != null) {
            contactImage.setImageBitmap(ClassBase64.base64ToImage(contact.getEncodedBase64ImageOriginal()));
        }
        else {
            switch (contact.getColor()) {
                case 0:
                    contactImage.setBackgroundColor(getResources().getColor(R.color.background0));
                    DrawableCompat.setTint(getResources().getDrawable(R.drawable.ic_person_40dp), ContextCompat.getColor(getActivity(), R.color.contact0));
                    break;
                case 1:
                    contactImage.setBackgroundColor(getResources().getColor(R.color.background1));
                    DrawableCompat.setTint(getResources().getDrawable(R.drawable.ic_person_40dp), ContextCompat.getColor(getActivity(), R.color.contact1));
                    break;
                case 2:
                    contactImage.setBackgroundColor(getResources().getColor(R.color.background2));
                    DrawableCompat.setTint(getResources().getDrawable(R.drawable.ic_person_40dp), ContextCompat.getColor(getActivity(), R.color.contact2));
                    break;
                case 3:
                    contactImage.setBackgroundColor(getResources().getColor(R.color.background3));
                    DrawableCompat.setTint(getResources().getDrawable(R.drawable.ic_person_40dp), ContextCompat.getColor(getActivity(), R.color.contact3));
                    break;
                case 4:
                    contactImage.setBackgroundColor(getResources().getColor(R.color.background4));
                    DrawableCompat.setTint(getResources().getDrawable(R.drawable.ic_person_40dp), ContextCompat.getColor(getActivity(), R.color.contact4));
                    break;
                case 5:
                    contactImage.setBackgroundColor(getResources().getColor(R.color.background5));
                    DrawableCompat.setTint(getResources().getDrawable(R.drawable.ic_person_40dp), ContextCompat.getColor(getActivity(), R.color.contact5));
                    break;
                case 6:
                    contactImage.setBackgroundColor(getResources().getColor(R.color.background6));
                    DrawableCompat.setTint(getResources().getDrawable(R.drawable.ic_person_40dp), ContextCompat.getColor(getActivity(), R.color.contact6));
                    break;
            }
            contactImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_40dp));
        }
        // config contact name
        contactName.setText(contact.getName());
        // config contact numbers
        NumberAdapter numberAdapter = new NumberAdapter(contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(numberAdapter);
        // config contact birthday
        if (contact.getBirthday() != null){
            contactHaveBirthday();
        }
        else{
            contactNullBirthday();
        }
    }

    com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener onDateSetListener = new com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {
                Date date = simpleDateFormat.parse(String.valueOf(year) + "/" + String.valueOf(monthOfYear+1) + "/" + String.valueOf(dayOfMonth));
                txtBirthday.setText(simpleDateFormat.format(date));
                contact.setBirthday(date);
                EventBus.getDefault().post(new BirthdayUpdated());
                contactHaveBirthday();
            }catch (Exception e){
                txtBirthday.setText("error");
            }
        }
    };

    private void contactHaveBirthday(){
        txtBirthday.setText(simpleDateFormat.format(contact.getBirthday()));
        btnBirthday2.setBackground(getResources().getDrawable(R.drawable.ic_edit_blue_24dp));
        btnBirthday1.setVisibility(View.VISIBLE);
        btnBirthday2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPicker();
            }
        });
        btnBirthday1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact.setBirthday(null);
                EventBus.getDefault().post(new BirthdayUpdated());
                contactNullBirthday();
            }
        });
    }

    private void showDataPicker(){
        new SpinnerDatePickerDialogBuilder()
                .context(getActivity())
                .callback(onDateSetListener)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(2018, 0, 1)
                .maxDate(2020, 0, 1)
                .minDate(1900, 0, 1)
                .build()
                .show();
    }

    private void contactNullBirthday(){
        btnBirthday2.setBackground(getResources().getDrawable(R.drawable.ic_add_blue_24dp));
        btnBirthday1.setVisibility(View.INVISIBLE);
        txtBirthday.setText(R.string.null_birthday);
        btnBirthday2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPicker();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu_contact_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (updateContactListener) {
            updateContactListener = false;
            EventBus.getDefault().post(new ContactEdited(contact.getId()));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_edit_contact:
                updateContactListener = true;
                Intent editContact = new Intent(Intent.ACTION_EDIT);
                editContact.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contact.getId())));
                startActivity(editContact);
                break;
            case R.id.menu_delete_contact:
                if (ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.WRITE_CONTACTS) !=
                        PackageManager.PERMISSION_GRANTED){
                    requestWriteContactPermission();
                }
                else {
                    deleteContact();
                }
                break;
            case android.R.id.home:{
                EventBus.getDefault().post(new BackEvent());
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestCallPermission(){
        ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.CALL_PHONE} , Keys.MY_PERMISSIONS_REQUEST_CALL_PHONE);
    }

    private void requestWriteContactPermission(){
        ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.WRITE_CONTACTS} , Keys.MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFragment(UpdateFragment event) {
        contact = event.getContact();
        configViews();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectionClicked(ConnectionClicked event) {
        switch (event.getConnectionType()) {
            case "message":
                Uri uri = Uri.fromParts("sms", event.getNumber(), null);
                Intent sendMessage = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(sendMessage);
                break;
            case "call":
                callNumber = event.getNumber();
                if (ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED){
                    requestCallPermission();
                }
                else {
                    call();
                }
        }
    }

    private void call(){
        Uri uri = Uri.parse("tel:" + callNumber);
        Intent call = new Intent(Intent.ACTION_CALL , uri);
        startActivity(call);
    }

    private void dial(){
        Uri uri = Uri.parse("tel:" + callNumber);
        Intent dial = new Intent(Intent.ACTION_DIAL , uri);
        startActivity(dial);
    }

    private void deleteContact(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete this contact?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContentResolver cr = getActivity().getContentResolver();
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    if (contact.getId().equals(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)))){
                        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        int i = cr.delete(uri, null, null);
                        cursor.close();
                        if (i == 1){
                            Log.d("TAG" , "Contact Deleted!");
                            EventBus.getDefault().post(new ContactDeleted(contact.getId()));
                        }
                        else {
                            Log.d("TAG" , "can not delete. " + i);
                        }
                        break;
                    }
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog deleteContactDialog = builder.create();
        deleteContactDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void statusCallPhonePermission(CallPhonePermission event) {
        if (event.getStatus()){
            call();
        }
        else {
            dial();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void statusFragment(WriteContactPermission event) {
        if (event.getStatus()){
            deleteContact();
        }
        else {

        }
    }
}
