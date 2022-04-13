package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LandingScreenActivity extends Activity {
    private Button open;
    private Button login;
    private TextView messageText;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);
        addListenerOnButton();

        messageText = (TextView) findViewById(R.id.message_text);

        //This is just to make a part of the text bold
        //No, there is literally no other way, I'm surprised as well
        String message = "You <b>Are</b> Signed In";
        messageText.setText(Html.fromHtml(message));

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

        open = (Button) findViewById(R.id.open);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HerbariumViewActivity.class);
                startActivity(intent);
            }
        });
    }


}
