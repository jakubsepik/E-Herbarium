package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
<<<<<<< Updated upstream
=======
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sk.spse.oursoft.android.e_herbarium.database_objects.Plant;
import sk.spse.oursoft.android.e_herbarium.database_objects.User;
>>>>>>> Stashed changes

public class LandingScreenActivity extends Activity {
    private Button button;
    private Button login;
<<<<<<< Updated upstream
=======
    private Plant plant;
    private FirebaseDatabase database ;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef ;
    private static final String TAG = "MyActivity";
>>>>>>> Stashed changes

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);
<<<<<<< Updated upstream
=======
        mAuth = FirebaseAuth.getInstance();

        TextView login_status_text = (TextView) findViewById(R.id.login_status_text);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            login_status_text.setText(user.getEmail().split("@")[0]);
        } else {
            login_status_text.setText("no user signed in");
        }

        login = (Button) findViewById(R.id.login_button);
>>>>>>> Stashed changes
        addListenerOnButton();

        login = (Button) findViewById(R.id.login_button);

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


}
