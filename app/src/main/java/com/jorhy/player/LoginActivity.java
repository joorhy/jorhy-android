package com.jorhy.player;

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
                    new LoginRequest().execute("http://");
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
                String strResult = jsonObject.getString("");
                if (strResult == "") {
                    Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
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