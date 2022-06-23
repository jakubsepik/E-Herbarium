package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;

import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;
import sk.spse.oursoft.android.e_herbarium.misc.DatabaseTools;
import sk.spse.oursoft.android.e_herbarium.misc.UserListCallback;


public class LandingScreenActivity extends Activity {
    private static final String TAG = "MyActivity";
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int RESULT_LOAD_IMAGE = 2;
    private final int CAMERA_REQUEST = 1888;
    public File photoFile;
    String currentPhotoPath;
    private Button open;
    private Button login;
    private TextView messageText;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private DatabaseTools databaseTools;
    private ArrayList<Item> items;
    private Uri uri;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);

        messageText = findViewById(R.id.login_status_text);

        //This is just to make a part of the text bold
        //No, there is literally no other way, I'm surprised as well
        String message = "You <b>Are Not</b> Signed In";
        messageText.setText(Html.fromHtml(message));

        login = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        TextView login_status_text = findViewById(R.id.login_status_text);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            login_status_text.setText(user.getEmail().split("@")[0]);
        } else {
            login_status_text.setText("no user signed in");
        }

        login = findViewById(R.id.login_button);
        addListenerOnButton();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_login();
            }
        });

        //I have to initalize the callback here or else every time i move to the herbarium view activity the callback gets called
        databaseTools = new DatabaseTools(getApplicationContext());

        //runs this method coz else it is one cycle behind
        items = new ArrayList<>();

        //I run it here to clean up the database when the user logs in and

        databaseTools.getUserItems(new UserListCallback() {
            @Override
            public void onDataCallback(ArrayList<Item> value) {
                System.out.println("This callback was called");
                //finally use the database items here
                //od the stuff here
                FirebaseUser user = databaseTools.getCurrentUser();
                if (user != null) {
                    String userName = user.getUid();
                    //Log.d("EH",user);
                    long timestamp = DatabaseTools.timestamp;
                    ListLogic.begin(databaseTools.getItems(), getApplicationContext(), userName, timestamp);
                    int tmp = ListLogic.getList().size() - 1;

                    String CallingActivity = getIntent().getStringExtra("Activity");
                    if(CallingActivity != null){
                        if(CallingActivity.equals("LoginActivity")){
                            System.out.println("THIS IS RUns");
                            databaseTools.synchronizeDatabaseToInternalImageStorage();
                        }
                    }else{
                        // happened to delete images also works for import
//                        databaseTools.synchronizeInternalImageStorageToDatabase();
                    }
                    System.out.println("THE DEFAULT URI IS " + databaseTools.getDefaultURI());
                    databaseTools.initializeNetworkCallback();
                }
            }

            @Override
            public void onImageCallback(Uri uri) {
            }
        });



    }


    private void go_login() {
        startActivity(new Intent(LandingScreenActivity.this, LoginActivity.class));
    }

    public void addListenerOnButton() {
        final Context context = this;

        open = findViewById(R.id.open);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HerbariumViewActivity.class);
                startActivity(intent);
            }
        });
    }

}

