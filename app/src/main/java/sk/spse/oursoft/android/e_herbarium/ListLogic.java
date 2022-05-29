package sk.spse.oursoft.android.e_herbarium;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItem;

public class ListLogic {
    static List<Item> list = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    static Context context = null;
    static String user="";

    @RequiresApi(api = Build.VERSION_CODES.O)
    static void begin(ArrayList<Item> newObject, Context context,String user) {
        list = new ArrayList<>();
        JSONObject object = null;
        ListLogic.user=user;
        ListLogic.context = context;
        try {
            if (newObject.size() == 0) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("EHerbarium", MODE_PRIVATE);
                if (sharedPreferences.contains(user)) {
                    Log.d("EH", "Begin sharedpreferences");
                    object = new JSONObject(sharedPreferences.getString(user, "{}"));
                    Log.d("EH", object.toString());
                } else {
                    Log.d("EH", "Begin startingtemplate");
                    InputStream is = context.getAssets().open("startingTemplate.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    object = new JSONObject(new String(buffer, StandardCharsets.UTF_8));
                }
                Iterator<String> temp = object.keys();
                while (temp.hasNext()) {
                    String key = temp.next();
                    JSONArray value = (JSONArray) object.get(key);

                    List<SubItem> items = new ArrayList<>();
                    for (int i = 0; i < value.length(); i++) {
                        JSONObject item = value.getJSONObject(i);

                        SubItem subItem = new SubItem(item.getString("id"), item.getString("name"), item.getString("description"), item.getInt("icon"),item.getString("image"));

                        items.add(subItem);
                    }
                    list.add(new Item(key, items));
                }
            } else {
                list = newObject;
                Log.d("EH", "get from database");
                Log.d("EH", newObject.toString());
            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }
        //Log.d("EH", list.toString());
    }

    static void addOne(SubItem item, int index) {
        list.get(index).addSubItem(item);
        saveAll();
    }

    public static void deleteOne(int index, String category) {
        for (Item tmp : list) {
            if (tmp.getItemTitle().equals(category))
                tmp.getSubItemList().remove(index);
        }
    }

    static boolean addCategory(Item category) {
        for (Item tmp : list) {
            if (tmp.getItemTitle().equals(category.getItemTitle()))
                return false;
        }
        list.add(category);
        return true;

    }

    public static void editCategory(int index, String name) {
        list.get(index).setItemTitle(name);
    }

    public static void editOne(String category, int index, SubItem subItem) {
        for (Item tmp : list) {
            if (tmp.getItemTitle().equals(category))
                tmp.getSubItemList().add(index, subItem);
        }
    }


    static void deleteCategory(String category) {
        for (int i = 0; i < list.toArray().length; i++) {
            if (list.get(i).getItemTitle().equals(category)) {
                list.remove(i);
                return;
            }
        }
    }

    public static JSONObject getObject() throws JSONException {
        StringBuilder listText = new StringBuilder(list.toString());
        listText.setCharAt(0, '{');
        listText.setCharAt(listText.length() - 1, '}');
        return new JSONObject(listText.toString());
    }

    public static List<Item> getList() {
        return list;
    }

    public static void saveAll() {
        Log.d("EH", "saving");
        StringBuilder listText = new StringBuilder(list.toString());
        listText.setCharAt(0, '{');
        listText.setCharAt(listText.length() - 1, '}');
        Log.d("EH", listText.toString());
        SharedPreferences sharedPreferences = context.getSharedPreferences("EHerbarium", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(user, listText.toString());
        long timestamp = new Date().getTime();
        myEdit.putLong("timestamp", timestamp);
        myEdit.apply();
    }


}
