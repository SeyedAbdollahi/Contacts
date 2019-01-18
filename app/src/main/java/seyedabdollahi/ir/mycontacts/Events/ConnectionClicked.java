package seyedabdollahi.ir.mycontacts.Events;

public class ConnectionClicked {

    String connectionType;
    String number;

    public ConnectionClicked(String connectionType, String number) {
        this.connectionType = connectionType;
        this.number = number;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public String getNumber() {
        return number;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
