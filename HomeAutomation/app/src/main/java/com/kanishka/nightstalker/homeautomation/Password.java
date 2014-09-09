package com.kanishka.nightstalker.homeautomation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Password extends Activity implements View.OnClickListener {
    public static final String MyPREFERENCES = "MyPass";
    Button passBtn, cancelBtn;
    EditText passValue;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        passValue = (EditText) findViewById(R.id.passInput);
        passBtn = (Button) findViewById(R.id.set_pass);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        passBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_pass:
                editor.putString("Password", passValue.getText().toString());
                editor.commit();
                Context context = getApplicationContext();
                CharSequence saved = "Saved New Password";
                int duration = Toast.LENGTH_LONG;
                Toast responseToast = Toast.makeText(context, saved, duration);
                responseToast.show();
                finish();
                break;
            case R.id.cancel_btn:
                finish();
                break;
            default:
                break;
        }
    }
}
