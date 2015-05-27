package bci.cerebro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by suryo on 27/5/15.
 */
public class HomeActivity extends ActionBarActivity implements View.OnClickListener{

    Button btnRecorder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        assignVariables();
        }


    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.goToRecorder:
                Intent intent_record = new Intent(this, RecorderActivity.class);
                // calling an activity using <intent-filter> action name
                //  Intent inent = new Intent("com.hmkcode.android.ANOTHER_ACTIVITY");
                startActivity(intent_record);
                break;
        }
    }

    void assignVariables() {
        btnRecorder = (Button) findViewById(R.id.goToRecorder);
        btnRecorder.setOnClickListener(this);
    }
}
