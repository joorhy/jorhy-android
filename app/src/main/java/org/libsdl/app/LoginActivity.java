package org.libsdl.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
                    EditText txtUser=(EditText) findViewById(R.id.login_user_input);
		      EditText txtPassword=(EditText) findViewById(R.id.login_password_input);
		      String httpRequest = "http://222.214.218.237:8059/MobileService.asmx/Login?Account=" + txtUser.getText().toString() + "&pwd=" + txtPassword.getText().toString();
                    new LoginRequest().execute(httpRequest) ;
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