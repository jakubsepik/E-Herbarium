package sk.spse.oursoft.android.e_herbarium;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListLogic {
    static JSONObject object=null;

    static void createList(JSONObject newArray) {
        object = newArray;
    }

    static void clearAll() {
        object = null;
    }

    static void addOne(JSONObject object, String category) {
        try {
            ((JSONArray) ListLogic.object.get(category)).put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void deleteOne(int index, String category) {
        try {
            ((JSONArray) ListLogic.object.get(category)).remove(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void addCategory(String category) {

    }

    static void deleteCategory(int index, String category) {
        object.remove(category);
    }

    static JSONObject getList() {
        return object;
    }

    static void updateOne() {

    }
}
