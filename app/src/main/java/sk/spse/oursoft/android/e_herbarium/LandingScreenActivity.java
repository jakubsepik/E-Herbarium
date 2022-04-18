package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sk.spse.oursoft.android.e_herbarium.database_objects.Group;
import sk.spse.oursoft.android.e_herbarium.database_objects.Plant;
import sk.spse.oursoft.android.e_herbarium.database_objects.User;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class LandingScreenActivity extends Activity {
    private Button open;
    private Button login;
    private TextView messageText;
    private Plant plant;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private static final String TAG = "MyActivity";
    private FirebaseAuth mAuth;
    private DatabaseTools databaseTools;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);

        messageText = (TextView) findViewById(R.id.message_text);

        //This is just to make a part of the text bold
        //No, there is literally no other way, I'm surprised as well
        String message = "You <b>Are</b> Signed In";
        messageText.setText(Html.fromHtml(message));

        login = (Button) findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        TextView login_status_text = (TextView) findViewById(R.id.login_status_text);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            login_status_text.setText(user.getEmail().split("@")[0]);
        } else {
            login_status_text.setText("no user signed in");
        }

        login = (Button) findViewById(R.id.login_button);
        addListenerOnButton();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_login();
            }
        });

        databaseTools = new DatabaseTools(getApplicationContext());
        //runs this method coz else it is one cycle behind
        databaseTools.getUserItems();


    }

    private void go_login() {
        startActivity(new Intent(LandingScreenActivity.this, LoginActivity.class));
    }

    public void addListenerOnButton() {
        final Context context = this;

        open = (Button) findViewById(R.id.open);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HerbariumViewActivity.class);
                startActivity(intent);
            }
        });
    }


    public void test_connection(View view) {
        /**/
        ArrayList<Group> values = new ArrayList<>();
        databaseTools.addItem("asdf", new Plant("1", "1", "1"));
        databaseTools.getUserItems();

    }
}
