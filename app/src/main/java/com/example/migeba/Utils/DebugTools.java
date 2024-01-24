package com.example.migeba.Utils;

import static com.example.migeba.MainActivity.getAppContext;

import android.util.Log;

import com.example.migeba.BuildConfig;
import com.google.android.material.snackbar.Snackbar;

public class DebugTools {
    public static void print(String message) {
        String tag = "Migeba";
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }



    public static String doubleStringToIntString(String message) {
        while (message.endsWith("0")) {
            message = message.substring(0, message.length() - 1);
        }

        //if string ends with a dot, remove it
        if (message.endsWith(".")) {
            message = message.substring(0, message.length() - 1);
        }
        return message;
    }
}
