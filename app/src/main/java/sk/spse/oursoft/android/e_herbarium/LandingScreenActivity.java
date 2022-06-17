package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItem;
import sk.spse.oursoft.android.e_herbarium.misc.DatabaseTools;

import sk.spse.oursoft.android.e_herbarium.misc.DatabaseTools;
import sk.spse.oursoft.android.e_herbarium.misc.UserListCallback;

import java.io.File;
import java.util.ArrayList;


public class LandingScreenActivity extends Activity {
    private Button open;
    private Button login;
    private TextView messageText;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private static final String TAG = "MyActivity";
    private FirebaseAuth mAuth;
    private DatabaseTools databaseTools;
    private ArrayList<Item> items;

    String currentPhotoPath;
    public File photoFile;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int RESULT_LOAD_IMAGE = 2;
    private Uri uri;
    private final int CAMERA_REQUEST = 1888;


    @RequiresApi(api = Build.VERSION_CODES.O)
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

        //I have to initalize the callback here or else every time i move to the herbarium view activity the callback gets called
        databaseTools = new DatabaseTools(getApplicationContext());
        databaseTools.initializeNetworkCallback();

        //runs this method coz else it is one cycle behind
        items = new ArrayList<>();
        //ListLogic.begin(items, getApplicationContext());

//        databaseTools.synchronizeDatabaseImages();

        databaseTools.getUserItems(new UserListCallback() {
            @Override
            public void onDataCallback(ArrayList<Item> value) {

                //finally use the database items here
                //od the stuff here
                System.out.println(databaseTools.getItems());

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

        open = (Button) findViewById(R.id.open);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HerbariumViewActivity.class);
                startActivity(intent);
            }
        });
    }


    //these are testing methods that I will move to my own branch

    public Item item = new Item("pines");
    public SubItem sub = new SubItem("11", R.drawable.listocek_symbolik);


    public void test_connection(View view) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();
        /**/
//        databaseTools.addEditSubItem(item,sub);
//        databaseTools.getUserItems(items);
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }


    public void testing_button(View view) {
        SubItem sub0 = new SubItem("78","tree","this is the tree descprition", R.drawable.ic_delete_group_icon);
        SubItem sub1 = new SubItem("45","tree", R.drawable.ic_delete_group_icon,"imageUri");
        SubItem sub2 = new SubItem("45","tree", R.drawable.ic_delete_group_icon);


        databaseTools.addEditSubItem(item, sub2);
        databaseTools.getUserItems(new UserListCallback() {
            @Override
            public void onDataCallback(ArrayList<Item> value) {

                //finally use the database items here
                //od the stuff here
                System.out.println(databaseTools.getItems());
                for (Item subitem : databaseTools.getItems()) {
                    for (SubItem sub : subitem.getSubItemList()) {
                        System.out.println(sub + " a");
                    }
                }

            }

            @Override
            public void onImageCallback(Uri uri) {

            }


        });

    }

}

