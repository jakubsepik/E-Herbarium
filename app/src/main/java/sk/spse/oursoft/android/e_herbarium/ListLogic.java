package sk.spse.oursoft.android.e_herbarium;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import sk.spse.oursoft.android.e_herbarium.database_objects.User;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.ItemAdapter;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItem;
import sk.spse.oursoft.android.e_herbarium.misc.DatabaseTools;
import sk.spse.oursoft.android.e_herbarium.misc.UserListCallback;

public class ListLogic extends AppCompatActivity {
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    static List<Item> list = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    static Context context = null;
    static String user = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    static void begin(ArrayList<Item> newObject, Context context, String user, long timestamp) {
        list = new ArrayList<>();
        JSONObject object;
        ListLogic.user = user;
        ListLogic.context = context;
        Log.d("EH", "database: " + timestamp + " \nlocal: " + getTimestamp());
        try {
            if (newObject == null || newObject.size() == 0 || timestamp < getTimestamp()) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("EHerbarium", MODE_PRIVATE);
                if (sharedPreferences.contains(user)) {
                    Log.d("EH", "Begin sharedpreferences");
                    object = new JSONObject(sharedPreferences.getString(user, "{}"));
                } else {
                    Log.d("EH", "Begin startingtemplate");
                    InputStream is = context.getAssets().open("startingTemplate.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    object = new JSONObject(new String(buffer, StandardCharsets.UTF_8));
                }
                Log.d("EH", object.toString());
                Iterator<String> temp = object.keys();
                while (temp.hasNext()) {
                    String key = temp.next();
                    JSONArray value = (JSONArray) object.get(key);

                    List<SubItem> items = new ArrayList<>();
                    for (int i = 0; i < value.length(); i++) {
                        JSONObject item = value.getJSONObject(i);
                        SubItem subItem = new SubItem(item.getString("id"), item.getString("name"), item.getString("description"), item.getInt("icon"), item.getString("image"));
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
        saveAll();
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
        saveAll();
        return true;
    }

    public static void editCategory(int index, String name) {
        saveAll();
        list.get(index).setItemTitle(name);
    }

    public static void editOne(String category, int index, SubItem subItem) {
        saveAll();
        for (Item tmp : list) {
            if (tmp.getItemTitle().equals(category))
                tmp.getSubItemList().add(index, subItem);
        }
    }

    static void deleteCategory(String category) {
        saveAll();
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

    public static void setList(List<Item> List) {
        list = List;
        saveAll();
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

    public static void exportHerbarium(String currentUserName) throws JSONException {



        JSONObject jsonObject = getObject();
        try {

            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            String timeStamp = new SimpleDateFormat("ddHHmmssSSS").format(new Date());

            File TempLocalFile = new File(storageDir, currentUserName + "_Herbarium_" + timeStamp + "_export.txt");
            FileWriter writer = new FileWriter(TempLocalFile);

            String tempObject = jsonObject.toString();
            tempObject = tempObject.substring(1, tempObject.length() - 1);

            writer.append("{");
            writer.append("\"").append(currentUserName).append("\":[{}],");
            writer.append(tempObject);
            writer.append("}");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void exportGroup(Item item, String currentUserName) throws JSONException {
        JSONObject json_database = getObject();

        //make a little window for this shit so it asks you for the name
        for (Iterator<String> it = json_database.keys(); it.hasNext(); ) {

            String key = it.next();
            if (key.equals(item.getItemTitle())) {
                try {

                    // getExternalStoragePublicDirectory() represents root of external storage, we are using DOWNLOADS
                    // We can use following directories: MUSIC, PODCASTS, ALARMS, RINGTONES, NOTIFICATIONS, PICTURES, MOVIES

                    //File storageDir = MediaStore.Downloads;
                    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    String timeStamp = new SimpleDateFormat("ddHHmmssSSS").format(new Date());
                    File TempLocalFile = new File(storageDir, currentUserName + "_" + item.getItemTitle() + "_" + timeStamp + "_export.txt");
                    FileWriter writer = new FileWriter(TempLocalFile);
                    JSONArray values = (JSONArray) json_database.get(key);

                    writer.append("{");
                    writer.append("\"").append(currentUserName).append("\":[{}],");
                    writer.append("\"" + key).append("\":").append(String.valueOf(values));
                    writer.append("}");
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
            }
            JSONArray values = (JSONArray) json_database.get(key);
        }
    }


    public static void importHerbarium(byte[] fileContent,ItemAdapter itemAdapter) throws IOException, JSONException {

        JSONObject jsonObject = null;
        String fileData = new String(fileContent);

        Log.i("Database", fileData);

        DatabaseTools databaseTools = new DatabaseTools(context);

        try {
            jsonObject = new JSONObject(fileData);
        } catch (JSONException err) {
            Log.d("Error Json", err.toString());
        }

        ArrayList<Item> itemList = new ArrayList<>();
        String UserName = "";
        Boolean gotName = false;
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();

            if (!gotName) {
                UserName = key;
                System.out.println("USER NAME IS " + UserName);
                gotName = true;
                continue;
            }
            List<SubItem> subItems = new ArrayList<>();
            JSONArray values = (JSONArray) jsonObject.get(key);
            for (int i = 0; i < values.length(); i++) {

                try {
                    JSONObject item = values.getJSONObject(i);
                    SubItem subItem = new SubItem(item.getString("id"),
                            item.getString("name"),
                            item.getString("description"),
                            item.getInt("icon"),
                            item.getString("image"));
                    subItems.add(subItem);


                } catch (Exception e) {
                    Log.e("Import error", e.getMessage());
                }

            }

            for(SubItem subitem : subItems){
                System.out.println(subitem);
            }
            itemList.add(new Item(key, subItems));

        }
        for (Item item : itemList) {
            System.out.println("VALUE " + item.getItemTitle());
            for (SubItem subItem : item.getSubItemList()) {
                System.out.println(subItem);
            }
        }
        for (Item item : itemList) {
            if (ItemNotInList(item)) {
                list.add(item);
            }
            int ItemPosition = findItemPosition(item.getItemTitle(), list);

            for (SubItem subItem : item.getSubItemList()) {

                try {
                    if (URLUtil.isValidUrl(subItem.getImageUri()) && !subItem.getImageUri().equals(databaseTools.getDefaultURI().toString())) {
                        databaseTools.ImportImagesFromSubItem(UserName, item, subItem, new UserListCallback() {
                            @Override
                            public void onDataCallback(ArrayList<Item> value) {
                            }

                            @Override
                            public void onImageCallback(Uri uri) {
                                System.out.println("SAVED THE IMAGE AT THE URI " + uri);
                                subItem.setImageUri(uri.toString());

                                databaseTools.saveImage(uri, item.getItemTitle());
                                databaseTools.addEditSubItem(item, subItem);
                                addOne(subItem, ItemPosition);
                                saveAll();

                                itemAdapter.notifyItemChanged(findSubItemPosition(subItem.getHerbName(),item.getSubItemList()));
                            }
                        });

                    } else {
                        subItem.setImageUri(databaseTools.getDefaultURI().toString());
                        databaseTools.addEditSubItem(item, subItem);
                        addOne(subItem, ItemPosition);
                        itemAdapter.notifyItemChanged(findSubItemPosition(subItem.getHerbName(),item.getSubItemList()));

                        saveAll();

                    }
                } catch (Exception e) {
                    //remove all those things I just added , pain xdxdxdxdxdxdxs
                }
            }

        }
        System.out.println("List-Database" + list);
    }

    public static int findItemPosition(String itemTitle, List<Item> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemTitle.equals(itemList.get(i).getItemTitle())) {
                return i;
            }
        }
        return -1;
    }
    public static int findSubItemPosition(String subItemTitle, List<SubItem> subItemList){

        for (int i = 0; i < subItemList.size(); i++){
            if (subItemTitle.equals(subItemList.get(i).getHerbName())){
                return i;
            }
        }
        return -1;
    }



    private static boolean ItemNotInList(Item ItemToFind) {
        for (Item item : list) {
            if (item.getItemTitle().equals(ItemToFind.getItemTitle())) {
                return false;
            }
        }
        return true;
    }

    public static long getTimestamp() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("EHerbarium", MODE_PRIVATE);
        long timestamp = sharedPreferences.getLong("timestamp", 0);
        return timestamp;


    }
}
