package sk.spse.oursoft.android.e_herbarium;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sk.spse.oursoft.android.e_herbarium.misc.DatabaseTools;

//Activity for registering the user


public class RegisterActivity extends AppCompatActivity {
    private final int NETWORK_STATUS_NOT_CONNECTED = 0;
    private final int NETWORK_STATUS_CONNECTED = 1;
    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button btnRegister;
    private TextView textLogin;
    private DatabaseTools databaseTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseTools = new DatabaseTools(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        btnRegister = findViewById(R.id.register);
        textLogin = findViewById(R.id.text_login);

        //This is just to make a part of the text bold
        //No, there is literally no other way, I'm surprised as well
        String loginText = "Already Have An Account ? Login <b>Here</b>.";
        textLogin.setText(Html.fromHtml(loginText));


        //when button is slicker try to register user
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        //switches to the login activity
        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    //registers the user into firebase
    private void register() {

        //gets the values from the email and password fields
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        int networkStatus = databaseTools.isConnected();

        if (networkStatus == NETWORK_STATUS_NOT_CONNECTED) {
            Toast.makeText(RegisterActivity.this, "Internet connection error", Toast.LENGTH_SHORT).show();
        } else {
            if (user.isEmpty()) {
                email.setError("Email can not be empty");
            }
            if (pass.isEmpty()) {
                password.setError("Password can not be empty");
            } else {
                mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //writes the user registry in the database
                            FirebaseUser firebaseUser = databaseTools.getCurrentUser();

                            databaseTools.registerUser(firebaseUser);


                            //sends an intenst to the login so the fields are filled
                            Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            i.putExtra("email", user);
                            i.putExtra("password", pass);

                            startActivity(i);

                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }
    }
}
