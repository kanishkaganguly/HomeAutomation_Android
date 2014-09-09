package com.kanishka.nightstalker.homeautomation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyActivity extends Activity implements View.OnClickListener {
    private static final int MY_PASSWORD_DIALOG_ID = 4;
    public Boolean connected = false;
    public static final String MyPREFERENCES = "MyPass";
    ImageButton fan, light, wifi, change;
    Button exit;
    final Context context = this;
    SharedPreferences pref;
    String savedPass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = pref.edit();
        if (pref.getInt("wifi", 0) == 1) {
            connectWifi connect = new connectWifi(MyActivity.this);
            connect.execute();
        }
        if (pref.getBoolean("pass", true) == true) {
            showDialog();
        }
        change = (ImageButton) findViewById(R.id.pass_change);
        fan = (ImageButton) findViewById(R.id.fan_btn);
        light = (ImageButton) findViewById(R.id.light_btn);
        wifi = (ImageButton) findViewById(R.id.wifi_btn);
        exit = (Button) findViewById(R.id.exit_btn);
        fan.setOnClickListener(this);
        light.setOnClickListener(this);
        wifi.setOnClickListener(this);
        exit.setOnClickListener(this);
        change.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int state_check = pref.getInt("wifi", 0);
        Boolean pass_check = pref.getBoolean("pass", true);
        MenuItem wifi_start = menu.findItem(R.id.wifi_onstart);
        MenuItem pass_start = menu.findItem(R.id.pass_onstart);

        if (state_check == 1) {
            wifi_start.setChecked(true);
        } else {
            wifi_start.setChecked(false);
        }

        if (pass_check == true) {
            pass_start.setChecked(true);
        } else {
            pass_start.setChecked(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        switch (item.getItemId()) {
            case R.id.menu_exit:
                findViewById(R.id.exit_btn).performClick();
                break;
            case R.id.menu_pass:
                findViewById(R.id.pass_change).performClick();
                break;
            case R.id.wifi_onstart:
                editor = pref.edit();
                if (item.isChecked()) {
                    item.setChecked(false);
                    editor.putInt("wifi", 0);
                } else {
                    item.setChecked(true);
                    editor.putInt("wifi", 1);
                }
                editor.commit();
                break;
            case R.id.pass_onstart:
                editor = pref.edit();
                if (item.isChecked()) {
                    item.setChecked(false);
                    editor.putBoolean("pass", false);
                } else {
                    item.setChecked(true);
                    editor.putBoolean("pass", true);
                }
                editor.commit();
                break;

        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pass_change:
                startActivity(new Intent(getApplicationContext(), Password.class));
                break;
            case R.id.fan_btn:
                if (connected == true) {
                    new abc().execute("FAN");
                    disableButton disable = new disableButton("FAN");
                    disable.execute();
                } else {
                    Context context = getApplicationContext();
                    CharSequence wifi_connect = "Connect to WiFi";
                    int duration = Toast.LENGTH_LONG;
                    Toast responseToast = Toast.makeText(context, wifi_connect, duration);
                    responseToast.show();
                }
                break;
            case R.id.light_btn:
                if (connected == true) {
                    new abc().execute("LIGHT");
                    disableButton disable = new disableButton("LIGHT");
                    disable.execute();
                } else {
                    Context context = getApplicationContext();
                    CharSequence wifi_connect = "Connect to WiFi";
                    int duration = Toast.LENGTH_LONG;
                    Toast responseToast = Toast.makeText(context, wifi_connect, duration);
                    responseToast.show();
                }
                break;
            case R.id.wifi_btn:
                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()) {
                    connected = true;
                    WifiInfo conn = wifi.getConnectionInfo();
                    Context context = getApplicationContext();
                    CharSequence wifi_on = "Already Connected to " + conn.getSSID();
                    int duration = Toast.LENGTH_LONG;
                    Toast responseToast = Toast.makeText(context, wifi_on, duration);
                    responseToast.show();
                } else if (wifi.isWifiEnabled() == false) {
                    connectWifi connect = new connectWifi(MyActivity.this);
                    connect.execute();
                }
                break;
            case R.id.exit_btn:
                WifiManager wifi_close = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifi_close.setWifiEnabled(false);
                System.exit(1);
                break;
            default:
                break;
        }
    }

    public void showDialog() {
        pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.prompts, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.userInput);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Default is qwerty");

        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String pass = input.getText().toString();
                        if (pref.contains("Password")) {
                            savedPass = pref.getString("Password", "");
                        } else {
                            savedPass = "qwerty";
                        }
                        if (!pass.equals(savedPass)) {
                            Toast responseToast = Toast.makeText(context, "WRONG PASSWORD.", Toast.LENGTH_LONG);
                            responseToast.show();
                            showDialog();
                        } else if (pass.equals(savedPass)) {
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                findViewById(R.id.exit_btn).performClick();
                            }
                        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public class disableButton extends AsyncTask<String, Void, Void> {
        public String buttonString = null;

        disableButton(String buttonData) {
            buttonString = buttonData;
        }

        @Override
        protected void onPreExecute() {
            if (buttonString.equals("FAN")) {
                findViewById(R.id.fan_btn).setEnabled(false);
            } else if (buttonString.equals("LIGHT")) {
                findViewById(R.id.light_btn).setEnabled(false);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            if (buttonString.equals("FAN")) {
                findViewById(R.id.fan_btn).setEnabled(true);
            } else if (buttonString.equals("LIGHT")) {
                findViewById(R.id.light_btn).setEnabled(true);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                Thread.sleep(1500);
            } catch (Exception e) {

            }
            return null;
        }
    }

    public class connectWifi extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        public connectWifi(MyActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting to WiFi. Please Wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            connected = true;
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo conn = wifi.getConnectionInfo();
            Context context = getApplicationContext();
            CharSequence wifi_on = "Connected to " + conn.getSSID();
            int duration = Toast.LENGTH_LONG;
            Toast responseToast = Toast.makeText(context, wifi_on, duration);
            responseToast.show();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            try {
                Thread.sleep(3000);
            } catch (Exception e) {

            }
            return null;
        }
    }

    public class abc extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://192.168.0.103/?data=" + strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.connect();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } catch (Exception e) {

            }
            return null;
        }
    }


}
