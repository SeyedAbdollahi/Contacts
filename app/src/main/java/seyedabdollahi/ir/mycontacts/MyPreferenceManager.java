package seyedabdollahi.ir.mycontacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

import com.google.gson.Gson;
import seyedabdollahi.ir.mycontacts.Models.ContactList;

public class MyPreferenceManager {

    private static MyPreferenceManager instance = null;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    public static MyPreferenceManager getInstance(Context context){
        if (instance == null){
            instance = new MyPreferenceManager(context);
        }
        return instance;
    }

    private MyPreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences("my_preference" , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void putContactList(ContactList contactList){
        editor.clear().commit();
        Gson gson = new Gson();
        String contactListJson = gson.toJson(contactList , ContactList.class);
        editor.putString(Keys.CONTACTS_LIST , contactListJson);
        editor.apply();
    }

    public ContactList getContactList(){
        Gson gson = new Gson();
        String contactListJson = sharedPreferences.getString(Keys.CONTACTS_LIST , null);
        if (contactListJson == null){
            return new ContactList();
        }
        return gson.fromJson(contactListJson , ContactList.class);
    }

    public void putRealSize(Point point){
        editor.putInt(Keys.WIDTH, point.x);
        editor.putInt(Keys.HEIGHT , point.y);
        editor.apply();
    }

    public Point getRealSize(){
        Point point = new Point();
        point.x = sharedPreferences.getInt(Keys.WIDTH , 1);
        point.y = sharedPreferences.getInt(Keys.HEIGHT , 1);
        return point;
    }
}
