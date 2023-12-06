package com.jnu.student.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DataBank {
    final String DATA_FILENAME = "shopitems.data";

    public ArrayList<ShopItem> LoadShopItems(Context context) {
        ArrayList<ShopItem> data = new ArrayList<>();
        try {
            FileInputStream fileIn = context.openFileInput(DATA_FILENAME);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            data = (ArrayList<ShopItem>) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            Log.d("Serialization", "Data loaded successfully.item count" + data.size());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void SaveShopItems(Context context, ArrayList<ShopItem> shopItems) {
        try {
            FileOutputStream fileOut = context.openFileOutput(DATA_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(shopItems);
            out.close();
            fileOut.close();
            Log.d("Serialization", "Data is serialized and saved.");
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    private static final String PREFERENCES_FILE = "com.jnu.student.preferences";
    private static final String SCORE_KEY = "Score";

    // 加载得分
    public int loadScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return prefs.getInt(SCORE_KEY, 0); // 默认值为 0
    }

    // 保存得分
    public void saveScore(Context context, int score) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SCORE_KEY, score);
        editor.apply();
    }
}