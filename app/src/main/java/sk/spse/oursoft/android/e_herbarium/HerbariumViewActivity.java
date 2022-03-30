package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class HerbariumViewActivity extends Activity {

    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herbarium_view);
    }
}
