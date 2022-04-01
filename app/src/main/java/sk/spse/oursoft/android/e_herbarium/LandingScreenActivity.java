package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sk.spse.oursoft.android.e_herbarium.database_objects.Plant;
import sk.spse.oursoft.android.e_herbarium.database_objects.User;

public class LandingScreenActivity extends Activity {
    private Button button;
    private Button login;
    private Plant plant;
    private FirebaseDatabase database ;
    private DatabaseReference myRef ;
    private static final String TAG = "MyActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);
        login = (Button) findViewById(R.id.login_button);
        addListenerOnButton();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_login();
            }
        });



    }
    private void go_login(){
       startActivity(new Intent(LandingScreenActivity.this,LoginActivity.class));
    }

    public void addListenerOnButton(){
        final Context context = this;

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HerbariumViewActivity.class);
                startActivity(intent);
            }
        });
    }

    public void test_connection(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            database = FirebaseDatabase.getInstance("https://e-herbar-default-rtdb.europe-west1.firebasedatabase.app");

            myRef = database.getReference("users/"+user.getEmail().split("@")[0]+"/herbarium");
            String plantRef = myRef.push().getKey();

            Log.d(TAG,plantRef);
            myRef.child(plantRef).setValue(new Plant(plantRef,"Ľubovník bodkovaný","Ľubovník je trváca bylina s holou oblou vzpriamenou a v hornej časti rozkonárenou stonkou, vysokou až 80cm. Listy sú sediace, protistojné, podlhovasté a celistvookrajové. V protisve","autor"));


            Log.d(TAG,"THE VALUE WAS ADDED ");

        } else {
            Toast.makeText(this,"you are not signed in",Toast.LENGTH_SHORT).show();
        }

    }
}
