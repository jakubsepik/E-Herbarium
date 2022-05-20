package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

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

    public DatabaseTools(Context context,Activity activity) {
        this.activity = activity;
        this.context = context;
        database = FirebaseDatabase.getInstance("https://e-herbar-default-rtdb.europe-west1.firebasedatabase.app");

    }

    //tests the internet connectin status
    public boolean isConnected() {
        user =  getCurrentUser();

        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    //Adds an item to a group if the group doesn't exist creates the group and adds it there
    public void addEditSubItem(Item item, SubItem subItem) {

        user =  getCurrentUser();

        if(user != null) {
            myRef = database.getReference().child("users").child(user.getEmail().split("@")[0]);
            myRef.child(item.getItemTitle()).child(subItem.getHerbId()).setValue(subItem);
        }
        else{
            Toast.makeText(context,"User not signed in",Toast.LENGTH_SHORT).show();
            Log.i("add/edit subitem","user not signed in");
        }
    }

//deletes a sub item
    public void deleteSub(Item item, SubItem subItem) {

        user =  getCurrentUser();

        if(user != null) {
            myRef = database.getReference().child("users").child(user.getEmail().split("@")[0]).child(item.getItemTitle());
            myRef.child(subItem.getHerbId()).removeValue();
        }
         else{
            Toast.makeText(context,"User not signed in",Toast.LENGTH_SHORT).show();
            Log.i("delete subItem","user not signed in");
        }
    }

    //deletes an item
    public void deleteItem(Item item, SubItem subItem) {

        user =  getCurrentUser();

        if(user != null) {
            myRef = database.getReference().child("users").child(user.getEmail().split("@")[0]);
            myRef.child(item.getItemTitle()).removeValue();
        }
        else{
            Toast.makeText(context,"User not signed in",Toast.LENGTH_SHORT).show();
            Log.i("delete item","user not signed in");
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
    public void getUserItems(ArrayList<Item> items) {
        if (isConnected()) {

            user =  getCurrentUser();

            if (user != null) {
                //change to fit new objects by changing regex
                myRef = database.getReference(("users/" + user.getEmail().split("\\.")[0] + "/herbarium"));
                myRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //clear the groups so updated data is written
                                items.clear();
                                for (DataSnapshot itemDataSnapshot : dataSnapshot.getChildren()) {
                                    Item item = new Item(itemDataSnapshot.getKey());
                                    for (DataSnapshot subItemDataSnapshot : itemDataSnapshot.getChildren()) {
                                        SubItem subItem = subItemDataSnapshot.getValue(SubItem.class);
                                        item.addSubItem(subItem);
                                    }
                                    items.add(item);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("Database", "Error with the database");
                            }

                        });
            }else{
                Toast.makeText(context,"User not signed in",Toast.LENGTH_SHORT).show();
                Log.i("get subitems","user not signed in");
            }

        } else {
            Log.i(TAG, "Internet Connection error couldn't connect");
        }
        if (items != null) {
            Log.i("Values", String.valueOf(items));
            Log.i("Success", "The data was successfully gotten from the db");
        }
    }


    public FirebaseDatabase getDatabase() {
        return database;
    }


//    get ID method returns an id as input it takes the name of the group where the item is located
    public String getSubItemID(Item item) {

        user =  getCurrentUser();
        //if user not signed in returns null
        if(user != null) {
            myRef = database.getReference().child("users").child(user.getEmail().split("@")[0]).child(item.getItemTitle());
            return myRef.push().getKey();
        }
        else{
            Toast.makeText(context,"User not signed in",Toast.LENGTH_SHORT).show();
            Log.e("get id","user not signed in ");

            return null;
        }


    }
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void saveImage(Uri imageUri) {

        user = getCurrentUser();
       String userName = user.getEmail().split("@")[0];

        if (user != null) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            //this is some scuffed code here
            String[] temp = imageUri.toString().split("/");

            String path = "fireImages/" + temp[temp.length - 1] + ".png";

            StorageReference storageRef = storage.getReference(path);
            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("caption", "made by " + user.getEmail()).build();
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
        }else{
            Toast.makeText(context,"Sign in to save",Toast.LENGTH_SHORT).show();
        }
    }



}
