package seyedabdollahi.ir.mycontacts.Events;

import seyedabdollahi.ir.mycontacts.Models.Contact;

public class UpdateFragment {

    private Contact contact;

    public UpdateFragment(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
