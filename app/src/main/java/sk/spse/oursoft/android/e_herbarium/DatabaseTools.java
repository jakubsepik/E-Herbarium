package sk.spse.oursoft.android.e_herbarium;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import sk.spse.oursoft.android.e_herbarium.database_objects.Plant;
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
        plants = new ArrayList<>();
    }

    //tests the internet connectin status
    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    //Adds an item to a group if the group doesn't exist creates the group and adds it there
    public void addItem(String group, Plant plant) {

        System.out.println("I am here");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //need to change the @ to a . but haven't made the database compatable yet

        myRef = database.getReference().child("users").child(user.getEmail().split("@")[0]).child(group);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //gets the id of the plant and sets it to the plant
                String plantRef = myRef.push().getKey();
                plant.setId(plantRef);
                if (dataSnapshot.exists()) {
                    myRef.child(plantRef).setValue(plant);
                } else {
                    myRef.child(group).child(plantRef).setValue(plant);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        });

    }

    public ArrayList<Object> getItems() {

        return null;
    }

    //adds the user registry to the database
    public void registerUser(String email, String password) {
        myRef = database.getReference("users");
        //removed letters that can't be added to a database it is a useless line, but I don't want to delete it
        email = email.replaceAll("/[$,#\\[\\]]/gm", "");
        Log.e("database", email);
        myRef.child(email).setValue(new User(email.split("\\.")[0], "herbarium", password));

    }

    private ArrayList<Plant> plants;

    //returns an arraylist of plants
    public ArrayList<Plant> getUserItems() {
        if (!plants.isEmpty()) {
            Log.i("aaaaa", String.valueOf(plants.get(0).getAuthor()));
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (isConnected()) {
            if (user != null) {
                //change to fit new objects by changing regex
                myRef = database.getReference(("users/" + user.getEmail().split("@")[0] + "/herbarium"));
                Log.i("THis", String.valueOf(myRef.getRoot()));

                myRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot groupDataSnapshot : dataSnapshot.getChildren()) {
                                    for (DataSnapshot plantDataSnapshot : groupDataSnapshot.getChildren()) {
                                        Plant plant = plantDataSnapshot.getValue(Plant.class);
                                        addPlant(plant);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("Database", "Erro with the database");
                            }

                        });
            }

        } else {
            Log.i(TAG, "Internet Connection error couldn't connect");
        }
        if (getPlants() != null) {
            Log.i("Success", "The data was successfully gotten from the db");
        }
        return getPlants();
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public ArrayList<Plant> getPlants() {
        return plants;
    }

}
