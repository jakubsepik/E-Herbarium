package sk.spse.oursoft.android.e_herbarium.misc;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import sk.spse.oursoft.android.e_herbarium.ListLogic;
import sk.spse.oursoft.android.e_herbarium.R;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItem;
import sk.spse.oursoft.android.e_herbarium.database_objects.User;

public class DatabaseTools {
    private FirebaseDatabase database;
    private Context context;
    private DatabaseReference myRef;
    private static final String TAG = "MyActivity";
    private FirebaseUser user;
    private Activity activity;
    private ArrayList<Item> items;
    private FirebaseStorage storage;

    private final int NETWORK_STATUS_NOT_CONNECTED = 0;
    private final int NETWORK_STATUS_CONNECTED = 1;

    private Uri defaultURI;

    public DatabaseTools(Context context, Activity activity) {
        this.activity = activity;
        this.context = context;
        items = new ArrayList<>();
        database = FirebaseDatabase.getInstance("https://e-herbar-default-rtdb.europe-west1.firebasedatabase.app");

        defaultURI = (new Uri.Builder())
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(context.getResources().getResourcePackageName(R.drawable.tree_placeholder))
                .appendPath(context.getResources().getResourceTypeName(R.drawable.tree_placeholder))
                .appendPath(context.getResources().getResourceEntryName(R.drawable.tree_placeholder))
                .build();


    }

    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();

            long Json_time = (long) System.currentTimeMillis();

            user = getCurrentUser();

            if (user != null) {
                DatabaseReference time_ref = database.getReference().child("LAST_CHANGE");

                time_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long Database_time = (long) dataSnapshot.getValue();
                        Log.i("DATABASE_TIME", String.valueOf(Database_time));

                        if (Database_time > Json_time) {
                            Toast.makeText(context, "Loading values from Database", Toast.LENGTH_SHORT).show();

                            //write setting json from database
                        } else {
                            //database = json
                            ArrayList<Item> JsonItems = new ArrayList<>();
                            Toast.makeText(context, "Loading values from Json", Toast.LENGTH_SHORT).show();

                            try {
                                JSONObject json_database = ListLogic.getObject();
                                Log.e("DATABASE", String.valueOf(json_database));

                                for (Iterator<String> it = json_database.keys(); it.hasNext(); ) {
                                    String key = it.next();

                                    List<SubItem> subItems = new ArrayList<>();
                                    JSONArray values = (JSONArray) json_database.get(key);

                                    for (int i = 0; i < values.length(); i++) {
                                        JSONObject item = values.getJSONObject(i);
                                        SubItem subItem = new SubItem(item.getString("id"),
                                                item.getString("name"),
                                                item.getString("description"),
                                                item.getInt("icon"),
                                                item.getString("image"));
                                        subItems.add(subItem);
                                    }

                                    JsonItems.add(new Item(key, subItems));
                                }

                                for (Item item : JsonItems) {

                                    System.out.println(item.getItemTitle());
                                    addEditItem(item);

                                }

                            } catch (JSONException e) {
                                Toast.makeText(context, "Failed to load database from Internal storage" + e.getStackTrace(), Toast.LENGTH_SHORT);
                                e.printStackTrace();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
                Log.i("add/edit subitem", "user not signed in");
            }


            try {
                Log.i("NETWORK ON", ListLogic.getObject().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onLost(Network network) {
            Toast.makeText(context, "Lost network connection", Toast.LENGTH_SHORT).show();
        }
    };

    public void initializeNetworkCallback() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }

    }


    //tests the internet connectin status
    public int isConnected() {
        user = getCurrentUser();

        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        int status = NETWORK_STATUS_NOT_CONNECTED;
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            status = NETWORK_STATUS_CONNECTED;
        }
        return status;
    }

    public void addEditItem(Item item) {
        int networkStatus = isConnected();
        if (networkStatus == NETWORK_STATUS_NOT_CONNECTED) {
            Toast.makeText(context, "No internet Connection", Toast.LENGTH_SHORT).show();
            Log.i("get subitems", "user not connected to internet");
        } else {

            user = getCurrentUser();
            if (user != null) {

                myRef = database.getReference().child("users").child(user.getEmail().split("\\.")[0]).child("herbarium").child(item.getItemTitle());

                try {
                    myRef.setValue(null);
                    for (SubItem subItem : item.getSubItemList()) {
                        System.out.println(subItem + " ============");
                        myRef.child(subItem.getHerbId()).setValue(subItem);
                    }
                    database.getReference().child("LAST_CHANGE").setValue(System.currentTimeMillis());


                } catch (Exception e) {
                    Log.e("DATABASE", "Error writing data from JSON to database");
                }


                Log.i("add/edit subitem", "success");


            } else {
                Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
                Log.i("add/edit subitem", "user not signed in");
            }
        }
    }

    //Adds an item to a group if the group doesn't exist creates the group and adds it there
    public void addEditSubItem(Item item, SubItem subItem) {

        user = getCurrentUser();
        if (user != null) {

            myRef = database.getReference().child("users").child(user.getEmail().split("\\.")[0]).child("herbarium").child(item.getItemTitle()).child(subItem.getHerbId());
            myRef.setValue(subItem);

            Log.i("add/edit subitem", "success");

            database.getReference().child("LAST_CHANGE").setValue(System.currentTimeMillis());


        } else {
            Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
            Log.i("add/edit subitem", "user not signed in");
        }
    }

    //deletes a sub item
    public void deleteSub(Item item, SubItem subItem) {

        int networkStatus = isConnected();
        if (networkStatus == NETWORK_STATUS_NOT_CONNECTED) {
            Toast.makeText(context, "No internet Connection", Toast.LENGTH_SHORT).show();
            Log.i("get subitems", "user not connected to internet");
        } else {

            user = getCurrentUser();

            if (user != null) {
                myRef = database.getReference().child("users").child(user.getEmail().split("\\.")[0]).child("herbarium").child(item.getItemTitle());
                myRef.child(subItem.getHerbId()).removeValue();

                database.getReference().child("LAST_CHANGE").setValue(System.currentTimeMillis());

            } else {
                Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
                Log.i("delete subItem", "user not signed in");
            }
        }
    }

    //deletes an item
    public void deleteItem(Item item, SubItem subItem) {

        int networkStatus = isConnected();
        if (networkStatus == NETWORK_STATUS_NOT_CONNECTED) {
            Toast.makeText(context, "No internet Connection", Toast.LENGTH_SHORT).show();
            Log.i("get subitems", "user not connected to internet");
        } else {

            user = getCurrentUser();

            if (user != null) {

                myRef = database.getReference().child("users").child(user.getEmail().split("\\.")[0]).child("herbarium");
                myRef.child(item.getItemTitle()).removeValue();

                database.getReference().child("LAST_CHANGE").setValue(System.currentTimeMillis());
            } else {
                Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
                Log.i("delete item", "user not signed in");
            }
        }
    }


    //adds the user registry to the database
    public void registerUser(String email, String password) {
        myRef = database.getReference("users");
        //removed letters that can't be added to a database it is a useless line, but I don't want to delete it
        email = email.replaceAll("/[$,#\\[\\]]/gm", "");
        Log.e("database", email);
        myRef.child(email.split("\\.")[0]).setValue(new User(email.split("\\.")[0], "herbarium", password));

    }


    //returns an arraylist of plants
    public void getUserItems(final UserListCallback myCallback) {

            user = getCurrentUser();
            if (user != null) {
                //change to fit new objects by changing regex
                myRef = database.getReference(("users/" + user.getEmail().split("\\.")[0] + "/herbarium"));
                myRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //clear the groups so updated data is written

                                items = new ArrayList<>();
                                for (DataSnapshot itemDataSnapshot : dataSnapshot.getChildren()) {
                                    try {
                                        Item item = new Item(itemDataSnapshot.getKey());
                                        for (DataSnapshot subItemDataSnapshot : itemDataSnapshot.getChildren()) {
                                            SubItem subItem = subItemDataSnapshot.getValue(SubItem.class);
                                            item.addSubItem(subItem);
                                        }
                                        items.add(item);
                                    } catch (com.google.firebase.database.DatabaseException e) {
                                        Log.e("DATABASE", "Values in database are formatted wrongly" + Arrays.toString(e.getStackTrace()));
                                    } catch (Exception e) {
                                        Log.e("DATABASE", "Error when loading value with key" + Arrays.toString(e.getStackTrace()));
                                    }
                                }
                                myCallback.onCallback(items);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("Database", "Error with the database");
                            }

                        });
            } else {
                Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
                Log.i("get subitems", "user not signed in");
            }
        if (items != null) {
            Log.i("Values", String.valueOf(items));
            Log.i("Success", "The data was successfully gotten from the db");
        }

    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void addItemToDatabase(Item item, SubItem subItem) {
        int networkStatus = isConnected();

        if (networkStatus == NETWORK_STATUS_NOT_CONNECTED) {
            Toast.makeText(context, "No internet Connection", Toast.LENGTH_SHORT).show();
            Log.i("get subitems", "user not connected to internet");
        } else {
            user = getCurrentUser();

            if (user != null) {
                myRef = database.getReference().child("users").child(user.getEmail().split("\\.")[0]).child("herbarium").child(item.getItemTitle());
                myRef.setValue(subItem);

                Toast.makeText(context, "YEEEEEEEEEEEEEEe", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
                Log.e("save Database", "user not signed in ");
            }

        }
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }


    //    get ID method returns an id as input it takes the name of the group where the item is located
    public String getSubItemID(Item item) {

        user = getCurrentUser();
        //if user not signed in returns null
        if (user != null) {
            myRef = database.getReference().child("users").child(user.getEmail().split("\\.")[0]).child("herbarium").child(item.getItemTitle());
            return myRef.push().getKey();
        } else {
            Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
            Log.e("get id", "user not signed in ");

            return null;


        }


    }

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void saveImage(Uri imageUri, String ItemName) {

        user = getCurrentUser();
        String userName = user.getEmail().split("\\.")[0];


        if (user != null) {

            storage = FirebaseStorage.getInstance();
            //this is some scuffed code here
            String[] imageName = imageUri.toString().split("/");
            String path = "fireImages/" + userName + "/" + ItemName + "/" + imageName[imageName.length - 1];
            StorageReference storageRef = storage.getReference(path);
            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("caption", "made by " + user.getEmail()).build();

            String pathFind = "fireImages/" + userName + "/" + ItemName + "/imageRef/" + imageName[imageName.length - 1];
            StorageReference storageRefFind = storage.getReference(pathFind);

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bm = Bitmap.createBitmap(1, 1, conf);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();


            UploadTask uploadReferenceTask = storageRefFind.putBytes(data);
            uploadReferenceTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    Toast.makeText(context, downloadUrl.toString(), Toast.LENGTH_SHORT).show();

                }
            });

            try {
                UploadTask uploadTask = storageRef.putFile(imageUri, metadata);

                uploadTask.addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Log.i("MA", "Upload complete");

                    }
                });
                Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(
                        new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return storageRef.getDownloadUrl();
                            }
                        }
                );
                getDownloadUriTask.addOnCompleteListener(activity, new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downlaodUri = task.getResult();

                            Toast.makeText(context, downlaodUri.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println("AAAAAAAAAAAAAAAAAAAAAA");
            }
        } else {
            Toast.makeText(context, "Sign in to save", Toast.LENGTH_SHORT).show();
        }


    }

    public void synchronizeInternalStorageToDatabase() {
        user = getCurrentUser();
        String userName = user.getEmail().split("\\.")[0];

        if (user != null) {

            try {
                storage = FirebaseStorage.getInstance();

                File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                ArrayList<Item> database_items = (ArrayList<Item>) ListLogic.getList();

                //this is going to be some shit code
                for (Item item : database_items) {
                    synchronizeImagesFromItem(item);
                }
            } catch (Exception e) {
                Log.e("DATABASE", "PAIN");
            }

        } else {
            Toast.makeText(context, "Sign in to save", Toast.LENGTH_SHORT).show();
        }

    }


    private void synchronizeImagesFromItem(Item item) {

        try {

            for (int i = item.getSubItemList().size() - 1; i >= 0; i--) {
                try {
                    SubItem subItem = item.getSubItemList().get(i);
                    tryToUploadImage(subItem, item);

                } catch (StorageException e) {
                    System.out.println("STPRAGE EXCEPTION");
                } catch (Exception e) {
                    System.out.println("STPRAGE EXCEPTION" + Arrays.toString(e.getStackTrace()));
                }
            }

        } catch (Exception e) {
            System.out.println("111 " + Arrays.toString(e.getStackTrace()));
        }


    }

    public void tryToUploadImage(SubItem subItem, Item item) throws StorageException {

        user = getCurrentUser();
        String userName = user.getEmail().split("\\.")[0];

        storage = FirebaseStorage.getInstance();

        ArrayList<Item> database_items = (ArrayList<Item>) ListLogic.getList();

        String ItemName = item.getItemTitle();


        String[] imageUri = subItem.getImageUri().split("/");
        String imageName = imageUri[imageUri.length - 1];
        String pathFind = "fireImages/" + userName + "/" + ItemName + "/imageRef/" + imageName;
        StorageReference storageRef = storage.getReference(pathFind);

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File TempLocalFile = new File(storageDir + "/" + imageName);

        if (TempLocalFile.exists()) {
            try {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println("THe image is here " + uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        try {
                            Uri ImageURI = Uri.parse(subItem.getImageUri());
                            saveImage(ImageURI, item.getItemTitle());

                            System.out.println("THIS IS RUN COZ FILE NOT FOUND");
                        } catch (Exception e) {
                            Log.e("NOOOO", "nooo");
                        }

                    }
                });

            } catch (Exception e) {
                System.out.println("error here ");
            }
        } else {
            subItem.setImageUri(defaultURI.toString());
            //delete that image from firebase
            //delete the URI from the item
            Log.i("IMAGE EXISTENCE", "Image doesn't exist at file" + TempLocalFile);
        }
    }
}


