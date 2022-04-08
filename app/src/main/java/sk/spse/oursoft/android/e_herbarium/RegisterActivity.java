package sk.spse.oursoft.android.e_herbarium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button btnRegister;
    private TextView textLogin;
    private Misc misc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        misc = new Misc();

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        btnRegister = findViewById(R.id.register);
        textLogin = findViewById(R.id.text_login);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email.setText(extras.getString("email"));
            password.setText(extras.getString("password"));
        }


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void register() {
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(misc.isConnected(getApplicationContext())) {
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
        }else{
            Toast.makeText(RegisterActivity.this, "Internet connection error", Toast.LENGTH_SHORT).show();
        }
    }

}