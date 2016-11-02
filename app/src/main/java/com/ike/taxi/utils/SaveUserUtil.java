package com.ike.taxi.utils;

import android.content.Context;

import com.ike.taxi.entity.User;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Min on 2016/11/1.
 */

public class SaveUserUtil {
    public static String USERMESSAGE="user_message";

    public static void saveUserMessage(Context context, User user){
        File file=new File(context.getFilesDir(),USERMESSAGE);
        if(file.exists()){
            file.delete();
        }
        try {
            ObjectOutputStream oos=new ObjectOutputStream(context.openFileOutput(USERMESSAGE,context.MODE_PRIVATE));
            oos.writeObject(user);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User loadUserMessage(Context context){
        User user=null;
        File file=new File(context.getFilesDir(),USERMESSAGE);
        if(file.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(context.openFileInput(USERMESSAGE));
                user = (User) ois.readObject();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(user==null){
            user=new User();
        }
        return user;
    }
}
