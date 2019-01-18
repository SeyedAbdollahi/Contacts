package seyedabdollahi.ir.mycontacts.Events;

import seyedabdollahi.ir.mycontacts.Models.Contact;

public class ContactEdited {

    private String id;

    public ContactEdited(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
