package sk.spse.oursoft.android.e_herbarium;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import sk.spse.oursoft.android.e_herbarium.misc.DatabaseTools;
//activity to login the user

public class LoginActivity extends AppCompatActivity {
    private final int NETWORK_STATUS_NOT_CONNECTED = 0;
    private final int NETWORK_STATUS_CONNECTED = 1;
    private FirebaseAuth mAuth;
    private EditText email, password;
    private DatabaseTools databaseTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        databaseTools = new DatabaseTools(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);

        //This is just to make a part of the text bold
        //No, there is literally no other way, I'm surprised as well
        //String registerText = "Not Registered Yet ? Register <b>Here</b>.";
        //textRegister.setText(Html.fromHtml(registerText));

        Button btnLogin = findViewById(R.id.login);
        TextView textRegister = findViewById(R.id.text_register);

        //the bundle is sent from the register activity so you don't need to write your info again
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email.setText(extras.getString("email"));
            password.setText(extras.getString("password"));
        }

        //when the button is pressed try to login
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
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        //pop up window for reset password
        //uses the reset_password xml
        TextView resetPassword = findViewById(R.id.reset_password);
        Dialog passwordResetDialog = new Dialog(this);

        resetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                passwordResetDialog.setContentView(R.layout.reset_password);


                EditText resetArea = passwordResetDialog.findViewById(R.id.resetArea);
                Button resetPasswordButton = passwordResetDialog.findViewById(R.id.resetPasswordButton);
                ImageView exit_reset = passwordResetDialog.findViewById(R.id.reset_exit);

                exit_reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        passwordResetDialog.dismiss();
                    }
                });

                resetPasswordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int networkStatus = databaseTools.isConnected();

                        if (resetArea.getText().toString().isEmpty()) {
                            resetArea.setError("email can not be empty");
                        } else if (networkStatus == NETWORK_STATUS_NOT_CONNECTED) {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                            resetArea.setError(null);
                        } else {

                            mAuth.sendPasswordResetEmail(resetArea.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Password reset email send successfully", Toast.LENGTH_SHORT).show();
                                        passwordResetDialog.dismiss();

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        resetArea.setError("problem with email");

                                    }
                                }
                            });
                        }
                    }
                });
                passwordResetDialog.show();

            }

        });
    }


    //the login method
    public void login() {

        //gets values from the email and password fields
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        int networkStatus = databaseTools.isConnected();
        if (networkStatus == NETWORK_STATUS_NOT_CONNECTED) {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            email.setError(null);
            password.setError(null);
        } else {
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
                            //When the user logs in you get the values from the firebase storage
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, LandingScreenActivity.class);
                            i.putExtra("Activity", "LoginActivity");
                            startActivity(i);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}