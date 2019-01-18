package seyedabdollahi.ir.mycontacts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ClassBase64 {

    public static String bitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes , Base64.DEFAULT);
    }

    public static Bitmap base64ToImage(String base64){
        byte[] bytes = Base64.decode(base64 , Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes , 0 , bytes.length);
    }
}
