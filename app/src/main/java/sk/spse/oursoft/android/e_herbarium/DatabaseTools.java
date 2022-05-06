package sk.spse.oursoft.android.e_herbarium;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import herbariumListOperation.Item;
import herbariumListOperation.SubItem;
import sk.spse.oursoft.android.e_herbarium.database_objects.User;

public class DatabaseTools {
    private FirebaseDatabase database;
    private Context context;
    private DatabaseReference myRef;
    private static final String TAG = "MyActivity";
    private User user;


    public DatabaseTools(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance("https://e-herbar-default-rtdb.europe-west1.firebasedatabase.app");
    }

    //tests the internet connectin status
    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    //Adds an item to a group if the group doesn't exist creates the group and adds it there
    public void addEditItem(Item item, SubItem subItem) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference().child("users").child(user.getEmail().split("@")[0]);
        myRef.child(item.getItemTitle()).child(subItem.getHerbId()).setValue(subItem);

    }


    public void deleteItem(Item item, SubItem subItem) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference().child("users").child(user.getEmail().split("@")[0]).child(item.getItemTitle());
        myRef.child(subItem.getHerbId()).removeValue();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (isConnected()) {
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = database.getReference().child("users").child(user.getEmail().split("@")[0]).child(item.getItemTitle());

        return myRef.push().getKey();
    }


}
