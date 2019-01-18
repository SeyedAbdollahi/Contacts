package seyedabdollahi.ir.mycontacts.Events;

import seyedabdollahi.ir.mycontacts.Models.Contact;

public class ContactClicked {

    private Contact contact;

    public ContactClicked(Contact contact) {
        this.contact = contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}
