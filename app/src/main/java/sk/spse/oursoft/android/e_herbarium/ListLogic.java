package sk.spse.oursoft.android.e_herbarium;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListLogic {
    private JSONObject object=null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public ListLogic(JSONObject newObject, Context context) {
        if(newObject==null){
            try {
                InputStream is = context.getAssets().open("startingTemplate.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                object = new JSONObject(new String(buffer, "UTF-8"));
                Context a=context.getApplicationContext();
            } catch (IOException | JSONException ex) {
                ex.printStackTrace();

            }
        }else
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

    JSONObject getList() {
        return object;
    }

    void updateOne() {

    }
}
