package org.libsdl.app;

/**
 * Created by cnjliu on 15-5-21.
 */

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;

public class CategoryActivity extends Activity {
    public static final String HOST = "http://222.214.218.237:8059/MobileService.asmx/";
    public static String strCookies;
    private ListView treeListView;
    private ArrayList<Element> elements;
    private ArrayList<Element> elementsData;
    private String strUserID;

    private VideoInfo videoInfoA = new VideoInfo();
    private VideoInfo videoInfoB = new VideoInfo();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        if (Configer.UseTemp()) {
            init();
        } else {
            strUserID = getIntent().getStringExtra("UserID");
            CategoryActivity.strCookies = getIntent().getStringExtra("Cookies");
            String strRequestURL = HOST + "GetUserByID?userID=" + strUserID;
            UserInfoRequest userInfoRequest = new UserInfoRequest();
            userInfoRequest.execute(strRequestURL);
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListView treeview = (ListView) findViewById(R.id.tree_list);
        TreeViewAdapter treeViewAdapter = new TreeViewAdapter(elements, elementsData, inflater);
        TreeViewItemClickListener treeViewItemClickListener = new TreeViewItemClickListener(treeViewAdapter);
        treeview.setAdapter(treeViewAdapter);
        treeview.setOnItemClickListener(treeViewItemClickListener);

        Button btnPlay = (Button) findViewById(R.id.play_btn);
        btnPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, SDLActivity.class);
                String strVideoURL = "222.214.218.237,6601";
                if (videoInfoA.strVehID != null && videoInfoA.strChaID != null) {
                    strVideoURL += "," + videoInfoA.strVehID + "," + videoInfoA.strChaID;
                }
                if (videoInfoB.strVehID != null && videoInfoB.strChaID != null) {
                    strVideoURL += "," + videoInfoB.strVehID + "," + videoInfoB.strChaID;
                }

                intent.putExtra("videoUrl", strVideoURL);
                startActivity(intent);
            }
        });
    }

    private void init() {
        elements = new ArrayList<Element>();
        elementsData = new ArrayList<Element>();
        Element e1 = new Element("1", Element.TOP_LEVEL, 0, Element.NO_PARENT, true, false);
        Element e2 = new Element("2", Element.TOP_LEVEL + 1, 1, e1.getId(), true, false);
        Element e3 = new Element("3", Element.TOP_LEVEL + 2, 2, e2.getId(), true, false);
        Element e4 = new Element("4", Element.TOP_LEVEL + 3, 3, e3.getId(), false, false);

        Element e5 = new Element("5", Element.TOP_LEVEL + 1, 4, e1.getId(), true, false);
        Element e6 = new Element("6", Element.TOP_LEVEL + 2, 5, e5.getId(), true, false);
        Element e7 = new Element("7", Element.TOP_LEVEL + 3, 6, e6.getId(), false, false);

        Element e8 = new Element("8", Element.TOP_LEVEL + 1, 7, e1.getId(), false, false);

        Element e9 = new Element("9", Element.TOP_LEVEL, 8, Element.NO_PARENT, true, false);
        Element e10 = new Element("10", Element.TOP_LEVEL + 1, 9, e9.getId(), true, false);
        Element e11 = new Element("11", Element.TOP_LEVEL + 2, 10, e10.getId(), true, false);
        Element e12 = new Element("12", Element.TOP_LEVEL + 3, 11, e11.getId(), true, false);
        Element e13 = new Element("13", Element.TOP_LEVEL + 4, 12, e12.getId(), false, false);

        elements.add(e1);
        elements.add(e9);

        elementsData.add(e1);
        elementsData.add(e2);
        elementsData.add(e3);
        elementsData.add(e4);
        elementsData.add(e5);
        elementsData.add(e6);
        elementsData.add(e7);
        elementsData.add(e8);
        elementsData.add(e9);
        elementsData.add(e10);
        elementsData.add(e11);
        elementsData.add(e12);
        elementsData.add(e13);
    }

    static public String InvokeService(String strURL) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Cookie", CategoryActivity.strCookies);
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
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

    class VideoInfo {
        public String strVehID;
        public String strChaID;
    }
}
    class UserInfoRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            Log.i("", "onPreExecute() called");
        }

        @Override
        protected String doInBackground(String... params) {
            return CategoryActivity.InvokeService(params[0]);
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
                    String strDeptntID = jsonObject.getString("DepartmentID");
                    String strDeptName = jsonObject.getString("DepartmentName");
                    //TreeNode deptNode = new TreeNode(strDeptName);
                    //CategoryActivity.AddDepartment(strDeptntID, parentNode)

                    String strRequestURL = CategoryActivity.HOST + "GetVehicleListByDeptID?deptID="
                            + strDeptntID + "&recursion=false";
                    VehicleInfoRequest vehicleInfoRequest = new VehicleInfoRequest();
                    vehicleInfoRequest.execute(strRequestURL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class VehicleInfoRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return CategoryActivity.InvokeService(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray vehicleArray = new JSONArray(result);
                for (int i = 0; i < vehicleArray.length(); i++) {
                    JSONObject item = (JSONObject) vehicleArray.opt(i);
                    String strVehName = item.getString("Name");
                    String strDeptID = item.getString("DepartmentID");
                    String strVehID = item.getString("ID");
                    //vehMap.put(strVehID, strDeptID);
                    //TreeNode vehNode = new TreeNode(strVehName);
                    //CategoryActivity.AddVehicle(strDeptID, strVehID, vehNode)

                    String strRequestURL = CategoryActivity.HOST + "GetChannelByVehicleID?vehicleID=" + strVehID;
                    ChannelInfoRequest channelInfoRequst = new ChannelInfoRequest();
                    channelInfoRequst.execute(strRequestURL);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ChannelInfoRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return CategoryActivity.InvokeService(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray channelArray = new JSONArray(result);
                for (int i = 0; i < channelArray.length(); i++) {
                    JSONObject item = (JSONObject) channelArray.opt(i);
                    String strChaName = item.getString("Name");
                    String strChaID = item.getString("Number");
                    String strVehID = item.getString("VehicleID");
                    //String strDeptID = vehMap.get(strVehID);
                    //TreeNode chaNode = new TreeNode(strChaName);
                    //CategoryActivity.AddVehicle(strDeptID, strVehID, chaNode)
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
