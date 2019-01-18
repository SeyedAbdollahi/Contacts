package seyedabdollahi.ir.mycontacts;

import java.util.Random;

public class RandomNumber {

    public static int get(){
        Random random = new Random();
        int i = random.nextInt();
        if (i<0){
            i = i*-1;
        }
        return i%7;
    }
}
