package seyedabdollahi.ir.mycontacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import seyedabdollahi.ir.mycontacts.Adapters.ContactAdapter;
import seyedabdollahi.ir.mycontacts.Dialogs.SyncContactsDialog;
import seyedabdollahi.ir.mycontacts.Events.BackEvent;
import seyedabdollahi.ir.mycontacts.Events.BirthdayUpdated;
import seyedabdollahi.ir.mycontacts.Events.ContactDeleted;
import seyedabdollahi.ir.mycontacts.Events.ContactEdited;
import seyedabdollahi.ir.mycontacts.Events.ContactClicked;
import seyedabdollahi.ir.mycontacts.Events.CallPhonePermission;
import seyedabdollahi.ir.mycontacts.Events.UpdateFragment;
import seyedabdollahi.ir.mycontacts.Fragment.ContactFragment;
import seyedabdollahi.ir.mycontacts.Models.Contact;
import seyedabdollahi.ir.mycontacts.Models.ContactList;
import seyedabdollahi.ir.mycontacts.listeners.SyncContactsListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout mainLayout;
    private TextView tittleToolbar;
    private ContactList contactList;
    private RecyclerView recyclerView;
    private Point point;
    private SyncContactsDialog syncContactsDialog;
    private Toolbar toolbar;
    private int countContact;
    private boolean visible = true;
    private boolean addContactListener = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        getDisplayRealSize();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        contactList = MyPreferenceManager.getInstance(MainActivity.this).getContactList();
        countContact = contactList.getSize();
        Log.d("TAG" , "[MainActivity] - [onCreate]: contactList size is: " + countContact);
        if (countContact > 0){
            showContacts(contactList);
        }
        else {
            if(ContextCompat.checkSelfPermission(MainActivity.this , Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermission();
            }
            else {
                openSyncContactsDialog();
            }
        }
    }

    private void findViews(){
        toolbar = findViewById(R.id.toolbar);
        tittleToolbar = findViewById(R.id.tittle_toolbar);
        recyclerView = findViewById(R.id.main_recycler);
        mainLayout = findViewById(R.id.main_layout);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.READ_CONTACTS} , Keys.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Keys.MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted, yay!
                    openSyncContactsDialog();
                }
                else {
                    // permission denied
                }
                break;
            case Keys.MY_PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted, yay!
                    EventBus.getDefault().post(new CallPhonePermission(true));
                }
                else {
                    // permission denied
                    EventBus.getDefault().post(new CallPhonePermission(false));
                }
                break;
            case Keys.MY_PERMISSIONS_REQUEST_WRITE_CONTACTS:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted, yay!
                    EventBus.getDefault().post(new CallPhonePermission(true));
                }
                else {
                    // permission denied
                    EventBus.getDefault().post(new CallPhonePermission(false));
                }
                break;
        }
    }

    private void getDisplayRealSize(){
        point = MyPreferenceManager.getInstance(MainActivity.this).getRealSize();
        if (point.x == 1) {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getRealSize(point);
            MyPreferenceManager.getInstance(MainActivity.this).putRealSize(point);
        }
    }

    private void openSyncContactsDialog(){
        syncContactsDialog = new SyncContactsDialog(MainActivity.this, new SyncContactsListener() {
            @Override
            public void finish() {
                Log.d("TAG" , "[MainActivity] - [openSyncContactsDialog] dialog finish");
                contactList = MyPreferenceManager.getInstance(MainActivity.this).getContactList();
                showContacts(contactList);
                syncContactsDialog.dismiss();
            }
        });
        syncContactsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //for old api
        syncContactsDialog.setCancelable(false);
        syncContactsDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void contactClicked(ContactClicked event) {
        visible = false;
        invalidateOptionsMenu();
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact" , event.getContact());
        ContactFragment contactFragment = new ContactFragment();
        contactFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.main_frame , contactFragment)
                .addToBackStack(null)
                .commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void contactEdited(ContactEdited event){
        Log.d("TAG" , "[MainActivity] - [contactEdited]: I called");
        List<String> typeNumbers = new ArrayList<>();
        List<String> numbers = new ArrayList<>();
        Contact contact = new Contact();
        contact.setId(event.getId());
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            if (contact.getId().equals(id)){
                contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                if (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)) != null){
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));
                    try{
                        Bitmap imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        contact.setEncodedBase64ImageOriginal(ClassBase64.bitmapToBase64(imageBitmap));
                        contact.setEncodedBase64Image80dp(ClassBase64.bitmapToBase64(Bitmap.createScaledBitmap(imageBitmap , 80 , 80 , false)));
                    }catch (Exception e){
                        Log.d("TAG" , "[MainActivity] - [contactEdited]: Exception error");
                    }
                }
                else {
                    Bitmap imageBitmap = BitmapFactory.decodeResource(getResources() , R.drawable.background5);
                    switch (contact.getColor()) {
                        case 0:
                            break;
                        case 1:
                            imageBitmap = BitmapFactory.decodeResource(getResources() , R.drawable.background0);
                            break;
                        case 2:
                            imageBitmap = BitmapFactory.decodeResource(getResources() , R.drawable.background3);
                            break;
                        case 3:
                            imageBitmap = BitmapFactory.decodeResource(getResources() , R.drawable.background6);
                            break;
                        case 4:
                            imageBitmap = BitmapFactory.decodeResource(getResources() , R.drawable.background4);
                            break;
                        case 5:
                            imageBitmap = BitmapFactory.decodeResource(getResources() , R.drawable.background2);
                            break;
                        case 6:
                            imageBitmap = BitmapFactory.decodeResource(getResources() , R.drawable.background1);
                            break;
                    }
                    contact.setEncodedBase64Image80dp(ClassBase64.bitmapToBase64(Bitmap.createScaledBitmap(imageBitmap , 80 , 80 , false)));
                }
                if (cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        typeNumbers.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                        numbers.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    }
                    pCur.close();
                    contact.setTypeNumbers(typeNumbers);
                    contact.setNumbers(numbers);
                    typeNumbers.clear();
                    numbers.clear();
                }
                contactList.updateContact(contact);
                contactList.finish();
                showContacts(contactList);
                Log.d("TAG" , "[MainActivity] - [contactEdited]: contact updated");
                EventBus.getDefault().post(new UpdateFragment(contact));
                break;
            }
        }
    }

    private void showContacts(ContactList contactList){
        MyPreferenceManager.getInstance(MainActivity.this).putContactList(contactList);
        countContact = contactList.getSize();
        ContactAdapter contactAdapter = new ContactAdapter(contactList.getContactList());
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG" , "[MainActivity] - [onStart]");
        EventBus.getDefault().register(this);
        if (addContactListener){
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
            if(cursor.getCount()>countContact){
                Log.d("TAG" , "[MainActivity] - [onStart] contact count Increased");
                addNewContact(cursor , cr);
                contactList.finish();
                showContacts(contactList);
                Toast.makeText(MainActivity.this , "new Contact Added!" , Toast.LENGTH_SHORT).show();
            }
            Log.d("TAG" , "[MainActivity] - [onStart] contact count is: " + cursor.getCount());
            addContactListener = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG" , "[MainActivity] - [onStop]");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (getFragmentManager().getBackStackEntryCount() > 0){
            tittleToolbar.setVisibility(View.VISIBLE);
            getFragmentManager().popBackStack();
            visible = true;
            invalidateOptionsMenu();
        }
        else {super.onBackPressed();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_main_activity, menu);
        if (!visible)
        {
            tittleToolbar.setVisibility(View.INVISIBLE);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_sync_contact:
                if(ContextCompat.checkSelfPermission(MainActivity.this , Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED){
                    requestPermission();
                }
                else {
                    openSyncContactsDialog();
                }
                break;
            case R.id.menu_new_contact:
                addContactListener = true;
                Intent addContact = new Intent(Intent.ACTION_INSERT , ContactsContract.Contacts.CONTENT_URI);
                startActivity(addContact);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewContact(Cursor cursor , ContentResolver cr){
        List<String> typeNumbers = new ArrayList<>();
        List<String> numbers = new ArrayList<>();
        Contact contact = new Contact();
        cursor.moveToLast();
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        contact.setId(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
        contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
        if (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)) != null){
            Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));
            try{
                Bitmap imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                contact.setEncodedBase64ImageOriginal(ClassBase64.bitmapToBase64(imageBitmap));
                contact.setEncodedBase64Image80dp(ClassBase64.bitmapToBase64(Bitmap.createScaledBitmap(imageBitmap , 80 , 80 , false)));
            }catch (Exception e){
                Log.d("TAG" , "[SyncContactDialog] - [doInBackground] Exception error");
            }
        }
        if (cursor.getInt(cursor.getColumnIndex(
                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
            Cursor pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            while (pCur.moveToNext()) {
                typeNumbers.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                numbers.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
            pCur.close();
            contact.setTypeNumbers(typeNumbers);
            contact.setNumbers(numbers);
            typeNumbers.clear();
            numbers.clear();
        }
        contact.setColor(RandomNumber.get());
        contactList.addContactToList(contact);
    }

    //set font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void backEvent(BackEvent event){
        onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void contactDeleted(ContactDeleted event){
        contactList.deleteContact(event.getId());
        contactList.finish();
        showContacts(contactList);
        onBackPressed();
        Snackbar snackbar = Snackbar.make(mainLayout , "Contact Deleted" , Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void birthdayUpdated(BirthdayUpdated event){
        MyPreferenceManager.getInstance(MainActivity.this).putContactList(contactList);
    }
}
