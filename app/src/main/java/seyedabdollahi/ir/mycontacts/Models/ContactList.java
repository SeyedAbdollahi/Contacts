package seyedabdollahi.ir.mycontacts.Models;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import seyedabdollahi.ir.mycontacts.MyPreferenceManager;

public class ContactList {

    private List<Contact> contactList;
    private int i = 0;

    public ContactList() {
        contactList = new ArrayList<>();
    }

    public void addContactToList(Contact contact){
        contactList.add(contact);
    }

    public void updateContact(Contact updateContact){
        i = 0;
        for(Contact contact : contactList){
            //Check ID
            if (contact.getId().equals(updateContact.getId())){
                updateContact.setColor(contactList.get(i).getColor());
                if (contactList.get(i).getBirthday() != null){
                    updateContact.setBirthday(contactList.get(i).getBirthday());
                }
                contactList.set(i , updateContact);
            }
            i++;
        }
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void finish(){
        sortContactList();
        setAlphabet();
    }

    public int getSize(){
        return contactList.size();
    }

    public void deleteContact(String id){
        i = 0;
        for(Contact contact: contactList){
            if (contact.getId().equals(id)){
                contactList.remove(i);
                break;
            }
            i++;
        }
    }

    public void syncBirthday(Context context){
        Log.d("TAG" , "syncBirthday start");
        ContactList oldContactList = MyPreferenceManager.getInstance(context).getContactList();
        for(Contact oldContact: oldContactList.getContactList()){
            if (oldContact.getBirthday() != null){
                for (Contact newContact: contactList){
                    if(newContact.getId().equals(oldContact.getId())){
                        newContact.setBirthday(oldContact.getBirthday());
                    }
                }
            }
        }
        Log.d("TAG" , "syncBirthday End");
    }

    private void sortContactList(){
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact x, Contact y) {
                return x.getName().compareTo(y.getName());
            }
        });
    }

    private void setAlphabet(){
        int i = 0;
        char firstAlphabet = ' ';
        for (Contact contact : contactList){
            try {
                if (firstAlphabet != contact.getName().toUpperCase().charAt(0)){
                    firstAlphabet = contact.getName().toUpperCase().charAt(0);
                    contact.setIsNewAlphabet(true);
                    contactList.set(i , contact);
                }else {
                    contact.setIsNewAlphabet(false);
                }
            }catch (Exception e){
                Log.d("TAG" , "[ContactList]-[setAlphabet] Exception Error");
                contact.setIsNewAlphabet(false);
            }
            i++;
        }
    }
}
