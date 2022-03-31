package sk.spse.oursoft.android.e_herbarium;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListLogic {
    static JSONArray array=null;
    static String author=null;

    static void createList(JSONArray newArray,String newAuthor) {
        array = newArray;
        author = newAuthor;
    }
    static String getElement(int index) throws JSONException {
        return  array.get(index).toString();
    }

    static void clearAll(){
        array = null;
        author = null;
    }

    static void addElement(JSONObject object){
        array.put(object);
    }

    static JSONArray getList(){
        return array;
    }
}
