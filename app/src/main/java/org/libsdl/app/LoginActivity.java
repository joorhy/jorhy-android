package org.libsdl.app;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.security.MessageDigest;

import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;

public class LoginActivity extends Activity {
    private static final String HOST = "http://222.214.218.237:8059/MobileService.asmx/";
    private LoginRequest loginRequest;
    private String httpRequest;
    private String strCookies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn = (Button)findViewById(R.id.sign_in_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Configer.UseTemp()) {
                    Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                    startActivity(intent);
                } else {
                    EditText txtUser = (EditText) findViewById(R.id.username_edit);
                    EditText txtPassword = (EditText) findViewById(R.id.password_edit);
                    String strPassword = txtPassword.getText().toString();
                    byte[] hash;
                    try {
                        hash = MessageDigest.getInstance("MD5").digest(strPassword.getBytes());
                        StringBuilder hex = new StringBuilder(hash.length * 2);
                        for (byte b : hash) {
                            if ((b & 0xFF) < 0x10)
                                hex.append("0");
                            hex.append(Integer.toHexString(b & 0xFF));
                        }
                        httpRequest = HOST + "Login?Account=" + txtUser.getText().toString()
                                + "&pwd=" + hex.toString();
                        loginRequest = new LoginRequest();
                        loginRequest.execute(httpRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class LoginRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            Button btn = (Button)findViewById(R.id.sign_in_button);
            btn.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    String cookie = urlConnection.getHeaderField("set-cookie");
                    if(cookie!=null && cookie.length()>0){
                        strCookies = cookie;
                    }

                    InputStream input = urlConnection.getInputStream();
                    if (input != null) {
                        XmlPullParser parser = Xml.newPullParser();
                        parser.setInput(input, "UTF-8");
                        int eventType = parser.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    if (parser.getName().equals("string")) {
                                        eventType = parser.next();
                                        String result = parser.getText();
                                        Log.i("", result);
                                        return result;
                                    }
                                    break;
                                case XmlPullParser.END_TAG:
                                    break;
                            }
                            eventType = parser.next();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i("", "onProgressUpdate(Progress... progresses) called");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONTokener jsonParser = new JSONTokener(result);
                    JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
                    String strStatus = jsonObject.getString("State");
                    if (strStatus.equals("0")) {
                        String strUserID = jsonObject.getString("UserID");
                        Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                        intent.putExtra("UserID", strUserID);
                        intent.putExtra("Cookies", strCookies);
                        startActivity(intent);
                    } else {
                        strStatus = "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}