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
import android.content.pm.ActivityInfo;
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
    public static ArrayList<Element> elements;
    public static ArrayList<Element> elementsData;
    public static TreeViewAdapter treeViewAdapter;
    public static VideoInfo videoInfoA = null;
    public static VideoInfo videoInfoB = null;
    public static int videoCount = 0;

    private String strUserID;
    private ListView treeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        videoInfoA = null;
        videoInfoB = null;
        if (Configer.UseTemp()) {
            init();
        } else {
            elements = new ArrayList<Element>();
            elementsData = new ArrayList<Element>();

            strUserID = getIntent().getStringExtra("UserID");
            CategoryActivity.strCookies = getIntent().getStringExtra("Cookies");
            String strRequestURL = HOST + "GetUserByID?userID=" + strUserID;
            UserInfoRequest userInfoRequest = new UserInfoRequest();
            userInfoRequest.execute(strRequestURL);
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListView treeview = (ListView) findViewById(R.id.tree_list);
        treeViewAdapter = new TreeViewAdapter(elements, elementsData, inflater);
        TreeViewItemClickListener treeViewItemClickListener = new TreeViewItemClickListener(treeViewAdapter);
        treeview.setAdapter(treeViewAdapter);
        treeview.setOnItemClickListener(treeViewItemClickListener);

        Button btnPlay = (Button) findViewById(R.id.play_btn);
        btnPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoInfoA != null) {
                    videoCount++;
                    String strRequestURL = HOST + "GetVehicleByID?ID=" + videoInfoA.strVehID;
                    DeviceInfoRequest deviceInfoRequest = new DeviceInfoRequest();
                    deviceInfoRequest.execute(strRequestURL);
                }

                if (videoInfoB != null) {
                    videoCount++;
                    String strRequestURL = HOST + "GetVehicleByID?ID=" + videoInfoB.strVehID;
                    DeviceInfoRequest deviceInfoRequest = new DeviceInfoRequest();
                    deviceInfoRequest.execute(strRequestURL);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoCount = 0;
        if (videoInfoA != null) {
            videoInfoA.strDevID = null;
        }

        if (videoInfoB != null) {
            videoInfoB.strDevID = null;
        }

        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void init() {
        elements = new ArrayList<Element>();
        elementsData = new ArrayList<Element>();
        Element e1 = new Element("1", Element.TOP_LEVEL, "0", Element.NO_PARENT, true, false);
        /*Element e2 = new Element("2", Element.TOP_LEVEL + 1, 1, e1.getId(), true, false);
        Element e3 = new Element("3", Element.TOP_LEVEL + 2, 2, e2.getId(), true, false);
        Element e4 = new Element("4", Element.TOP_LEVEL + 3, 3, e3.getId(), false, false);

        Element e5 = new Element("5", Element.TOP_LEVEL + 1, 4, e1.getId(), true, false);
        Element e6 = new Element("6", Element.TOP_LEVEL + 2, 5, e5.getId(), true, false);
        Element e7 = new Element("7", Element.TOP_LEVEL + 3, 6, e6.getId(), false, false);

        Element e8 = new Element("8", Element.TOP_LEVEL + 1, 7, e1.getId(), false, false);*/

        Element e9 = new Element("9", Element.TOP_LEVEL, "8", Element.NO_PARENT, true, false);
        /*Element e10 = new Element("10", Element.TOP_LEVEL + 1, 9, e9.getId(), true, false);
        Element e11 = new Element("11", Element.TOP_LEVEL + 2, 10, e10.getId(), true, false);
        Element e12 = new Element("12", Element.TOP_LEVEL + 3, 11, e11.getId(), true, false);
        Element e13 = new Element("13", Element.TOP_LEVEL + 4, 12, e12.getId(), false, false);*/

        elements.add(e1);
        elements.add(e9);

        /*elementsData.add(e1);
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
        elementsData.add(e13);*/
    }

    static public boolean AddVideoInfo(String strVehID, String strChaID) {
        if (videoInfoA == null) {
            videoInfoA = new VideoInfo();
            videoInfoA.strVehID = strVehID;
            videoInfoA.strChaID = strChaID;
        } else if (videoInfoB == null) {
            videoInfoB = new VideoInfo();
            videoInfoB.strVehID = strVehID;
            videoInfoB.strChaID = strChaID;
        } else {
            return false;
        }
        return true;
    }

    static public boolean DelVideoInfo(String strVehID, String strChaID) {
        if (videoInfoA != null
                && videoInfoA.strVehID.equals(strVehID)
                && videoInfoA.strChaID.equals(strChaID)) {
            videoInfoA = null;
            return true;
        }

        if (videoInfoB != null
                && videoInfoB.strVehID.equals(strVehID)
                && videoInfoB.strChaID.equals(strChaID)) {
            videoInfoB = null;
            return true;
        }
        return false;
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

    static class VideoInfo {
        public String strVehID = null;
        public String strChaID = null;
        public String strDevID = null;
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
                    Element e1 = new Element(strDeptName, Element.TOP_LEVEL, strDeptntID, Element.NO_PARENT, true, false);
                    CategoryActivity.elements.add(e1);
                    String strRequestURL = CategoryActivity.HOST + "GetVehicleListByDeptID?deptID="
                            + strDeptntID + "&recursion=false";
                    VehicleInfoRequest vehicleInfoRequest = new VehicleInfoRequest();
                    vehicleInfoRequest.execute(strRequestURL);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CategoryActivity.treeViewAdapter.notifyDataSetChanged();
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
                    String strState = item.getString("State");
                    Element e2 = new Element(strVehName, Element.TOP_LEVEL + 1,
                            strVehID, strDeptID, true, false);
                    e2.setOnline(strState.equals("1"));
                    CategoryActivity.elementsData.add(e2);

                    String strRequestURL = CategoryActivity.HOST + "GetChannelByVehicleID?vehicleID=" + strVehID;
                    ChannelInfoRequest channelInfoRequst = new ChannelInfoRequest();
                    channelInfoRequst.execute(strRequestURL);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                CategoryActivity.treeViewAdapter.notifyDataSetChanged();
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
                    Element e3 = new Element(strChaName, Element.TOP_LEVEL + 2,
                            strChaID, strVehID, false, false);
                    CategoryActivity.elementsData.add(e3);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CategoryActivity.treeViewAdapter.notifyDataSetChanged();
            }
        }
    }

    class DeviceInfoRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return CategoryActivity.InvokeService(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONTokener jsonParser = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
                String strID = jsonObject.getString("ID");
                String strDevID = jsonObject.getString("DevID");
                if (CategoryActivity.videoInfoA.strVehID.equals(strID) && CategoryActivity.videoInfoA.strDevID == null) {
                    CategoryActivity.videoInfoA.strDevID = strDevID;
                    CategoryActivity.videoCount--;
                }
                else if (CategoryActivity.videoInfoB.strVehID.equals(strID) && CategoryActivity.videoInfoB.strDevID == null) {
                    CategoryActivity.videoInfoB.strDevID = strDevID;
                    CategoryActivity.videoCount--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CategoryActivity.treeViewAdapter.notifyDataSetChanged();
            } finally {
                if (CategoryActivity.videoCount == 0) {
                    Intent intent = new Intent(CategoryActivity.this, SDLActivity.class);
                    String strVideoURL = "222.214.218.237,6601";
                    if (CategoryActivity.videoInfoA.strDevID != null && CategoryActivity.videoInfoA.strChaID != null) {
                        strVideoURL += "," + CategoryActivity.videoInfoA.strDevID + "," + CategoryActivity.videoInfoA.strChaID;
                    }
                    if (CategoryActivity.videoInfoB.strDevID != null && CategoryActivity.videoInfoB.strChaID != null) {
                        strVideoURL += "," + CategoryActivity.videoInfoB.strDevID + "," + CategoryActivity.videoInfoB.strChaID;
                    }

                    intent.putExtra("videoUrl", strVideoURL);
                    startActivity(intent);
                }
            }
        }
    }
}

