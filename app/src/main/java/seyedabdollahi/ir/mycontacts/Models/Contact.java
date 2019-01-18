package seyedabdollahi.ir.mycontacts.Models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Contact implements Serializable{
    private String id;
    private String name;
    private List<String> typeNumbers;
    private List<String> numbers;
    private Date birthday;
    private String encodedBase64ImageOriginal;
    private String encodedBase64Image80dp;
    private int color;
    private boolean isNewAlphabet;

    public Contact() {
        typeNumbers = new ArrayList<>();
        numbers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTypeNumbers() {
        return typeNumbers;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public String getEncodedBase64ImageOriginal() {
        return encodedBase64ImageOriginal;
    }

    public int getColor() {
        return color;
    }

    public String getEncodedBase64Image80dp() {
        return encodedBase64Image80dp;
    }

    public boolean getIsNewAlphabet() {
        return isNewAlphabet;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeNumbers(List<String> typeNumbers) {
        this.typeNumbers.addAll(typeNumbers);
    }

    public void setNumbers(List<String> numbers) {
        this.numbers.addAll(numbers);
    }

    public void setEncodedBase64ImageOriginal(String encodedBase64ImageOriginal) {
        this.encodedBase64ImageOriginal = encodedBase64ImageOriginal;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setEncodedBase64Image80dp(String encodedBase64Image80dp) {
        this.encodedBase64Image80dp = encodedBase64Image80dp;
    }

    public void setIsNewAlphabet(boolean newAlphabet) {
        this.isNewAlphabet = newAlphabet;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
