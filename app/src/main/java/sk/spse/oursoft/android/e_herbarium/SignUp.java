package sk.spse.oursoft.android.e_herbarium;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sk.spse.oursoft.android.e_herbarium.database_objects.User;

public class SignUp {

    private User user;
    private static final String TAG = "MyActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("users");

    public void getData(){

        Log.d(TAG,database.toString());
        myRef.setValue("Hello, World!");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        myRef.setValue("Hel8, 8!");

        myRef.setValue("yuoi, jkl!");
        myRef.setValue("nm,, v,m!");

    }

    public void setMyRef(String s) {
        this.myRef.setValue(s) ;
    }
}
