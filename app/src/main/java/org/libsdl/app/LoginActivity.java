package org.libsdl.app;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn = (Button)findViewById(R.id.sign_in_button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Configer.UseTemp()) {
                    Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                    startActivity(intent);
                } else {
                    new Thread (new Runnable () {
                        @Override
                        public void run() {
                            EditText txtUser = (EditText) findViewById(R.id.username_edit);
                            EditText txtPassword = (EditText) findViewById(R.id.password_edit);
                            String httpRequest = "http://222.214.218.237:8059/MobileService.asmx/Login?Account=" + txtUser.getText().toString() + "&pwd=" + txtPassword.getText().toString();
                            //new LoginRequest().execute(httpRequest) ;
                            HttpURLConnection urlConnection = null;
                            try {
                                URL url = new URL(httpRequest);
                                urlConnection = (HttpURLConnection) url.openConnection();
                                urlConnection.setRequestMethod("GET");
                                int responseCode = urlConnection.getResponseCode();
                                if (responseCode == 200) {
                                    InputStream input = urlConnection.getInputStream();
                                    if (input != null) {
                                        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
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
                                                    }
                                                    break;
                                                case XmlPullParser.END_TAG:
                                                    break;
                                            }
                                            eventType = parser.next();
                                        }
                                        //拿到流后处理
                                        /*StringBuffer out = new StringBuffer();
                                        byte[] b = new byte[4096];
                                        for (int n; (n  = input.read(b)) != -1;) {
                                            out.append(new String(b,0,n));
                                        }
                                        String result = out.toString();
                                        JSONObject jsonObject = new JSONObject(result).getJSONObject("parent");
                                        String strResult = jsonObject.getString("State");
                                        Log.i("", result);*/
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                urlConnection.disconnect();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    class LoginRequest extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            try {
                    String s = result.toString();
                    JSONObject jsonObject = new JSONObject(result.toString()).getJSONObject("parent");
                    String strResult = jsonObject.getString("State");
                    if (strResult == "0") {
                        String strUserID = jsonObject.getString("UserID");
                        Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                        intent.putExtra("UserID", strUserID);
                        startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            Button btn = (Button)findViewById(R.id.sign_in_button);
            btn.setEnabled(false);
        }
    }
}