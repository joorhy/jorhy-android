package org.libsdl.app;

/**
 * Created by cnjliu on 15-5-21.
 */
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class CategoryActivity extends Activity {
    private ListView treeListView;
    private String userID;
    private String departmentID;
    private ArrayList<TreeNode> treeNodes = new ArrayList<TreeNode>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        if (Configer.UseTemp()) {
            TempDataRequest tempDataRequest = new TempDataRequest();
            treeNodes = tempDataRequest.getTreeNodes();
        } else {
	     UserInfoRequest userInfoRequest = new UserInfoRequest();
	     String strUserInfoRequest = "http://222.214.218.237:8059/MobileService.asmx/GetUserByID?userID=" + userID;
         userInfoRequest.execute(strUserInfoRequest);

	     VehicleInfoRequest vehicleInfoRequest = new VehicleInfoRequest();
	     String strVehicleInfoRequest = "http://222.214.218.237:8059/MobileService.asmx/GetVehicleListByDeptID?deptID=" + departmentID + "&recursion=true";
	     vehicleInfoRequest.execute(strVehicleInfoRequest);

	     /*for (int i=0; i<treeNodes.size(); i++) {
	         ChannelInfoRequest channelInfoRequest = new ChannelInfoRequest();
                String strChannelInfoRequest = "http://222.214.218.237:8059/MobileService.asmx/GetVehicleListByDeptID?deptID=" + departmentID + "&recursion=true";
	     }*/
        }

        TreeViewAdapter adapter = new TreeViewAdapter(this, treeNodes);
        treeListView = (ListView) this.findViewById(R.id.tree_list);
        treeListView.setAdapter(adapter);

        Button btnPlay = (Button)findViewById(R.id.play_btn);
        btnPlay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this,SDLActivity.class);
                intent.putExtra("videoUrl", "222.214.218.237,6601,14189,0,14186,1");
                startActivity(intent);
            }
        });
    }

    class TreeNode {
        private int level;
        private boolean hasParent;
        private boolean hasChild;
        private boolean isDirectory;
        private boolean expanded;
        private String title;
        private ArrayList<TreeNode> childNodes = new ArrayList<TreeNode>();
        public TreeNode(String title) {
            this.title = title;
            hasParent = false;
            hasChild = false;
            isDirectory = false;
            this.expanded = false;
        }
        public String getTitle() {
            return title;
        }
        public boolean getHasParent(){
            return this.hasParent;
        }
        public void setHasParent(boolean hasParent) {
            this.hasParent = hasParent;
        }
        public boolean getHasChild(){
            return this.hasChild;
        }
        public void setHasChild(boolean hasChild) {
            this.hasChild = hasChild;
        }
        public int getLevel() {
            return this.level;
        }
        public void setLevel(int level) {
            this.level = level;
        }
        public ArrayList<TreeNode> getChildNodes() {
            return childNodes;
        }
        public boolean isExpanded() {
            return expanded;
        }
        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }
        public void setIsDirectory(boolean isDirectory){
            this.isDirectory = isDirectory;
        }
        public boolean getIsDirectory(){
            return this.isDirectory;
        }
        public void addChild(TreeNode childNode) {
            this.hasChild = true;
            this.childNodes.add(childNode);
        }
    }

    class TreeViewAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<TreeNode> treeNodes;

        public TreeViewAdapter(Context context, ArrayList<TreeNode> treeNodes) {
            super();
            this.context = context;
            this.treeNodes = treeNodes;
        }

        @Override
        public int getCount() {
            return treeNodes.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView text = new TextView(context);
            ImageView icon = new ImageView(context);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            final TreeNode node = treeNodes.get(position);

            linearLayout.setPadding(50 * node.getLevel(), 5, 5, 5);
            text.setPadding(20, 5, 5, 5);
            text.setText(node.getTitle());

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (node.getIsDirectory()) {
                        if (node.isExpanded()) {
                            node.setExpanded(false);
                            ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
                            for (int i = position + 1; i < treeNodes.size(); i++) {
                                if (node.getLevel() >= treeNodes.get(i).getLevel()) {
                                    break;
                                }
                                treeNodes.get(i).setExpanded(false);
                                //treeNodes.remove(treeNodes.get(i));
                                temp.add(treeNodes.get(i));
                            }
                            treeNodes.removeAll(temp);
                        } else {
                            node.setExpanded(true);
                            for (TreeNode childNode : node.getChildNodes()) {
                                childNode.setExpanded(false);
                                treeNodes.add(position + 1, childNode);
                            }
                        }
                        TreeViewAdapter.this.notifyDataSetChanged();
                    }
                }
            });

            if (node.getIsDirectory() && (node.isExpanded() == false)) {
                icon.setBackgroundResource(R.drawable.node_expand);
            } else if (node.getIsDirectory() && (node.isExpanded() == true)) {
                icon.setBackgroundResource(R.drawable.node_unexpand);
            } else if (node.getIsDirectory() == false) {
                icon.setVisibility(View.INVISIBLE);
            }

            linearLayout.addView(text);
            linearLayout.addView(icon);

            return linearLayout;
        }
    }

    class UserInfoRequest extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
	     try {
            JSONObject jsonObject = new JSONObject(result.toString()).getJSONObject("parent");
            departmentID = jsonObject.getString("DepartmentID");
	        
	     } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class VehicleInfoRequest extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
	     try {
		 JSONObject jsonObject = new JSONObject(result.toString()).getJSONObject("parent");
		 JSONArray vehicleArray = jsonObject.getJSONArray("arrayData");
		 for (int i = 0; i < vehicleArray.length(); i++) {  
		 	JSONObject item = vehicleArray.getJSONObject(i);
		 	String strTitle = item.getString("Name"); 
		 	TreeNode parentNode = new TreeNode(strTitle);
			parentNode.setHasParent(false);
                     parentNode.setIsDirectory(true);
                     parentNode.setLevel(0);
                     parentNode.setHasChild(true);
			for (int j = 0; j < 8; j++) {
                        TreeNode childNode = new TreeNode(Integer.toString(j));
                        childNode.setHasChild(true);
                        childNode.setIsDirectory(false);
                        childNode.setLevel(1);
                        childNode.setHasChild(false);
                        parentNode.addChild(childNode);
                    }
                    treeNodes.add(parentNode);
		 }
	     } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ChannelInfoRequest extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
	     try {
		 JSONObject jsonObject = new JSONObject(result.toString()).getJSONObject("parent");
		 JSONArray vehicleArray = jsonObject.getJSONArray("arrayData");
		 for (int i = 0; i < vehicleArray.length(); i++) {  
		 	JSONObject item = vehicleArray.getJSONObject(i);
		 	String strTitle = item.getString("Name"); 
		 	TreeNode parentNode = new TreeNode(strTitle);
			parentNode.setHasParent(false);
                     parentNode.setIsDirectory(true);
                     parentNode.setLevel(0);
                     parentNode.setHasChild(true);
		 }
	     } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
	
    class TempDataRequest extends AsyncTask<Object, Object, Object> {
        private ArrayList<TreeNode> treeNodes = new ArrayList<TreeNode>();

        public ArrayList<TreeNode> getTreeNodes() {
            if (Configer.UseTemp()) {
                for (int i = 1299880; i < 1299880 + 5; i++) {
                    TreeNode parentNode = new TreeNode(Integer.toString(i));
                    parentNode.setHasParent(false);
                    parentNode.setIsDirectory(true);
                    parentNode.setLevel(0);
                    parentNode.setHasChild(true);
                    for (int j = 0; j < 8; j++) {
                        TreeNode childNode = new TreeNode(Integer.toString(j));
                        childNode.setHasChild(true);
                        childNode.setIsDirectory(false);
                        childNode.setLevel(1);
                        childNode.setHasChild(false);
                        parentNode.addChild(childNode);
                    }
                    treeNodes.add(parentNode);
                }
            }
            return treeNodes;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
        }
    }
}
