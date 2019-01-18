package seyedabdollahi.ir.mycontacts.Dialogs;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import seyedabdollahi.ir.mycontacts.ClassBase64;
import seyedabdollahi.ir.mycontacts.Models.Contact;
import seyedabdollahi.ir.mycontacts.Models.ContactList;
import seyedabdollahi.ir.mycontacts.MyPreferenceManager;
import seyedabdollahi.ir.mycontacts.R;
import seyedabdollahi.ir.mycontacts.RandomNumber;
import seyedabdollahi.ir.mycontacts.listeners.SyncContactsListener;

public class SyncContactsDialog extends Dialog {

    private SyncContactsListener listener;
    private TextView foundTxt;
    private TextView progressTxt;
    private ProgressBar progressBar;
    private int width;
    private int height;
    private int i;
    private float percent;

    public SyncContactsDialog(@NonNull Context context , SyncContactsListener listener) {
        super(context);
        this.listener = listener;
        setWidthHeight(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_contact_dialog);
        findViews();
        getWindow().setLayout(width , height);
        //**************************************************************
        ContentResolver cr = getContext().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        //**************************************************************
        new syncContact(cr , cursor).execute();
    }

    private void setWidthHeight(Context context){
        width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.28);
    }

    private void findViews(){
        foundTxt = findViewById(R.id.sync_found_txt);
        progressTxt = findViewById(R.id.sync_progress_txt);
        progressBar = findViewById(R.id.sync_progress_bar);
    }

    private class syncContact extends AsyncTask<Void , Integer , Void>{
        ContentResolver cr;
        Cursor cursor;

        private syncContact(ContentResolver cr, Cursor cursor) {
            this.cr = cr;
            this.cursor = cursor;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("TAG" , "[SyncContactDialog] - [onPreExecute] contact count is: " + cursor.getCount());
            percent = (float) cursor.getCount()/100;
            foundTxt.setText(getContext().getString(R.string.count_contacts , cursor.getCount()));
            progressTxt.setText(getContext().getString(R.string.percent_analyzing , 0));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("Cursor" , DatabaseUtils.dumpCursorToString(cursor));
            ContactList contactList = new ContactList();
            String id;
            List<String> typeNumbers = new ArrayList<>();
            List<String> numbers = new ArrayList<>();
            i = 0;
            if ((cursor != null ? cursor.getCount() : 0) > 0) {
                while (cursor != null && cursor.moveToNext()) {
                    i++;
                    publishProgress(i);
                    Contact contact = new Contact();
                    contact.setColor(RandomNumber.get());
                    id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    contact.setId(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                    contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                    if (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)) != null){
                        Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));
                        try{
                            Bitmap imageBitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri));
                            contact.setEncodedBase64ImageOriginal(ClassBase64.bitmapToBase64(imageBitmap));
                            contact.setEncodedBase64Image80dp(ClassBase64.bitmapToBase64(Bitmap.createScaledBitmap(imageBitmap , 80 , 80 , false)));
                        }catch (Exception e){
                            Log.d("TAG" , "[SyncContactDialog] - [doInBackground] Exception error");
                        }
                    }
                    else {
                        Bitmap imageBitmap = BitmapFactory.decodeResource(getContext().getResources() , R.drawable.background5);
                        switch (contact.getColor()) {
                            case 0:
                                break;
                            case 1:
                                imageBitmap = BitmapFactory.decodeResource(getContext().getResources() , R.drawable.background0);
                                break;
                            case 2:
                                imageBitmap = BitmapFactory.decodeResource(getContext().getResources() , R.drawable.background3);
                                break;
                            case 3:
                                imageBitmap = BitmapFactory.decodeResource(getContext().getResources() , R.drawable.background6);
                                break;
                            case 4:
                                imageBitmap = BitmapFactory.decodeResource(getContext().getResources() , R.drawable.background4);
                                break;
                            case 5:
                                imageBitmap = BitmapFactory.decodeResource(getContext().getResources() , R.drawable.background2);
                                break;
                            case 6:
                                imageBitmap = BitmapFactory.decodeResource(getContext().getResources() , R.drawable.background1);
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
                    contactList.addContactToList(contact);
                }
            }
            contactList.syncBirthday(getContext());
            contactList.finish();
            MyPreferenceManager.getInstance(getContext()).putContactList(contactList);
            if(cursor!=null){
                cursor.close();
            }
            return null;
        }

        //Main thread
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(Math.round(values[0]/percent));
            progressTxt.setText(getContext().getString(R.string.percent_analyzing , Math.round(values[0]/percent)));
            Log.d("TAG" , "[SyncContactDialog] - [onProgressUpdate] : progress is: " + progressBar.getProgress());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.finish();
        }
    }
}
