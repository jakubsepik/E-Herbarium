package sk.spse.oursoft.android.e_herbarium;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    }

    //tests the internet connectin status
    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void addItem() {

    }

    public ArrayList<Object> getItems() {

        return null;
    }

    public void registerUser(String email, String password) {
        myRef = database.getReference("users");
        email = email.replaceAll("/[$,#\\[\\]]/gm", "");
        Log.e("database", email);
        myRef.child(email).setValue(new User(email.split("\\.")[0], "herbarium", password));

    }

    private ArrayList<Plant> plants;
    public ArrayList<Plant> getUserItems() {
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
                                //Get map of users in datasnapshot
                                setPlants(collectPlants((Map<String, Object>) dataSnapshot.getValue()));
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }

                        });
            }

        } else {
            Log.i(TAG, "Internet Connection error couldn't connect");
        }
        if(getPlants()!= null) {
            Log.i("Success", "The data was successfully gotten from the db");
        }
        return getPlants();
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }
    public void setPlants(ArrayList<Plant> plants){
        this.plants = plants;
    }
    public ArrayList<Plant> getPlants(){
        return plants;
    }

    //Sets the
    private ArrayList<Plant> collectPlants(Map<String, Object> users) {
        ArrayList<Plant> plants = new ArrayList<>();
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            Map values = (Map) entry.getValue();
            Plant plant = new Plant(values.get("id").toString(), values.get("name").toString(), values.get("description").toString(), values.get("author").toString());
            plants.add(plant); }

        return plants;
    }
}
