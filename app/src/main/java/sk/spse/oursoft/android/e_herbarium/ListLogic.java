package sk.spse.oursoft.android.e_herbarium;

import static android.content.Context.MODE_PRIVATE;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import herbariumListOperation.Item;
import herbariumListOperation.SubItem;

public class ListLogic {
    private JSONObject object = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public ListLogic(JSONObject newObject, Context context) {
        if (newObject == null) {
            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences("EHerbarium", MODE_PRIVATE);
                if (sharedPreferences.contains("items")) {
                    object = new JSONObject(sharedPreferences.getString("items", null));
                    return;
                }

                InputStream is = context.getAssets().open("startingTemplate.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                object = new JSONObject(new String(buffer, StandardCharsets.UTF_8));
            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
            }
        } else
            object = newObject;
    }

    void clearAll() {
        object = null;
    }

    void addOne(JSONObject object, String category) {
        try {
            ((JSONArray) object.get(category)).put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void deleteOne(int index, String category) {
        try {
            ((JSONArray) object.get(category)).remove(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void addCategory(String category) {

    }


    void deleteCategory(int index, String category) {
        object.remove(category);
    }

    JSONObject getObject() {
        return object;
    }
    List<Item> getList() throws JSONException {

        List<Item> list = new ArrayList<>();
        Iterator<String> temp = object.keys();
        while (temp.hasNext()) {
            String key = temp.next();
            JSONArray value = (JSONArray) object.get(key);
            List<SubItem> items = new ArrayList<>();
            for(int i=0;i< value.length();i++){
                JSONObject item = value.getJSONObject(i);
                SubItem subItem = new SubItem(item.getString("id"),item.getString("name"),item.getString("description"),0);
                items.add(subItem);
            }
            Item category = new Item(key,items);
            list.add(category);
        }
        return list;
    }
    void saveAll(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("EHerbarium", MODE_PRIVATE);

        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("items", object.toString());

        myEdit.apply();
    }
}
