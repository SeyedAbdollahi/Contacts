package seyedabdollahi.ir.mycontacts.Events;

public class WriteContactPermission {

    private boolean status;

    public WriteContactPermission(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
