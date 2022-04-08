package sk.spse.oursoft.android.e_herbarium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email, password;
    private Misc misc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        misc = new Misc();

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);

        Button btnLogin = findViewById(R.id.login);
        TextView textRegister = findViewById(R.id.text_register);
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
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        TextView resetPassword = (TextView) findViewById(R.id.reset_password);
        Dialog passwordResetDialog = new Dialog(this);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordResetDialog.setContentView(R.layout.reset_password);

                EditText resetArea = (EditText) passwordResetDialog.findViewById(R.id.resetArea);
                Button resetPasswordButton = (Button) passwordResetDialog.findViewById(R.id.resetPasswordButton);
                ImageView exit_reset = (ImageView) passwordResetDialog.findViewById(R.id.reset_exit);

                exit_reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        passwordResetDialog.dismiss();
                    }
                });

                resetPasswordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getApplicationContext(),Boolean.toString(misc.isConnected(getApplicationContext())),Toast.LENGTH_SHORT).show();

                        if (resetArea.getText().toString().isEmpty()) {
                            resetArea.setError("email can not be empty");
                        } else if (!misc.isConnected(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                            resetArea.setError(null);
                        } else {

                            mAuth.sendPasswordResetEmail(resetArea.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "Password reset email send successfully", Toast.LENGTH_SHORT).show();
                                    passwordResetDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error sending reset password mail", Toast.LENGTH_SHORT).show();
                                    resetArea.setError("problem with email");
                                }
                            });
                        }
                    }
                });
                passwordResetDialog.show();
            }
        });
    }

    private void login() {
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (!misc.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            email.setError(null);
            password.setError(null);
        }else {
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


}