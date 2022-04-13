package sk.spse.oursoft.android.e_herbarium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button btnLogin;
    private TextView textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.login);
        textRegister = findViewById(R.id.text_register);

        //This is just to make a part of the text bold
        //No, there is literally no other way, I'm surprised as well
        String registerText = "Not Registered Yet ? Register <b>Here</b>.";
        textRegister.setText(Html.fromHtml(registerText));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email.setText(extras.getString("email"));
            password.setText(extras.getString("password"));
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                //i.putExtra("email",email.getText().toString().trim());
                //i.putExtra("password",password.getText().toString().trim());
                startActivity(i);
                finish();
            }
        });
    }

    private void login() {
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        if (user.isEmpty()) {
            email.setError("Email can not be empty");
        }
        if (pass.isEmpty()) {
            password.setError("Password can not be empty");
        } else {
            mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, LandingScreenActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}