package seyedabdollahi.ir.mycontacts.Events;

public class CallPhonePermission {

    private boolean status;

    public CallPhonePermission(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
