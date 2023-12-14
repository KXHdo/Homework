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
import java.util.List;

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
            Log.d("Serialization", "1Data loaded successfully.item count" + data.size());
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
            Log.d("Serialization", "1Data is serialized and saved.");
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    final String DATA_FILENAME2 = "targetweeklies.data";

    public ArrayList<TargetWeekly> LoadTargetWeeklies(Context context) {
        ArrayList<TargetWeekly> data = new ArrayList<>();
        try {
            FileInputStream fileIn = context.openFileInput(DATA_FILENAME2);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            data = (ArrayList<TargetWeekly>) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            Log.d("Serialization", "2Data loaded successfully.item count" + data.size());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void SaveTargetWeeklies(Context context, ArrayList<TargetWeekly> targetWeeklies) {
        try {
            FileOutputStream fileOut = context.openFileOutput(DATA_FILENAME2, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(targetWeeklies);
            out.close();
            fileOut.close();
            Log.d("Serialization", "2Data is serialized and saved.");
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

    private static final String COMPLETED_TASKS_FILE = "completed_tasks.data";
    public void saveCompletedTask(Context context, CompletedTask completedTask) {
        ArrayList<CompletedTask> completedTasks = loadCompletedTasks(context);
        completedTasks.add(completedTask);

        try {
            FileOutputStream fileOut = context.openFileOutput(COMPLETED_TASKS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(completedTasks);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CompletedTask> loadCompletedTasks(Context context) {
        ArrayList<CompletedTask> completedTasks;

        try {
            FileInputStream fileIn = context.openFileInput(COMPLETED_TASKS_FILE);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            completedTasks = (ArrayList<CompletedTask>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            completedTasks = new ArrayList<>(); // 如果无法加载数据，则创建一个新的空列表
        }

        return completedTasks;
    }
    public void clearCompletedTasks(Context context) {
        ArrayList<CompletedTask> emptyList = new ArrayList<>();

        try {
            FileOutputStream fileOut = context.openFileOutput(COMPLETED_TASKS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(emptyList); // 保存空列表
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String AWARDS_FILE = "awards.data";

    public ArrayList<AwardItem> LoadAwardItem(Context context) {
        ArrayList<AwardItem> data = new ArrayList<>();
        try {
            FileInputStream fileIn = context.openFileInput(AWARDS_FILE);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            data = (ArrayList<AwardItem>) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            Log.d("Serialization", "1Data loaded successfully.item count" + data.size());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void SaveAwardItems(Context context, ArrayList<AwardItem> awardItems) {
        try {
            FileOutputStream fileOut = context.openFileOutput(AWARDS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(awardItems);
            out.close();
            fileOut.close();
            Log.d("Serialization", "1Data is serialized and saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}