package org.libsdl.app;

import java.util.ArrayList;
  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;  
 
public class TreeViewAdapter extends BaseAdapter {  
    private ArrayList<Element> elementsData;  
    private ArrayList<Element> elements;  
    private LayoutInflater inflater;  
    private int indentionBase;  
      
    public TreeViewAdapter(ArrayList<Element> elements, ArrayList<Element> elementsData, LayoutInflater inflater) {  
        this.elements = elements;  
        this.elementsData = elementsData;  
        this.inflater = inflater;  
        indentionBase = 40;
    }  
      
    public ArrayList<Element> getElements() {  
        return elements;  
    }  
      
    public ArrayList<Element> getElementsData() {  
        return elementsData;  
    }  
      
    @Override  
    public int getCount() {  
        return elements.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return elements.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder holder = null;
        if (convertView == null) {  
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.treeview_item, null);
            holder.disclosureImg = (ImageView) convertView.findViewById(R.id.disclosureImg);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.item_cb);
            holder.contentText = (TextView) convertView.findViewById(R.id.contentText);  
            convertView.setTag(holder);  
        } else {  
            holder = (ViewHolder) convertView.getTag();  
        }
        final Element element = elements.get(position);
        int level = element.getLevel();
        holder.disclosureImg.setPadding(
                indentionBase * (level),
                holder.disclosureImg.getPaddingTop(),
                holder.disclosureImg.getPaddingRight(),
                holder.disclosureImg.getPaddingBottom());

        holder.contentText.setText(element.getContentText());
        if (element.isHasChildren() && !element.isExpanded()) {
            if (element.isOnline()) {
                holder.disclosureImg.setImageResource(R.drawable.close);
            } else {
                holder.disclosureImg.setImageResource(R.drawable.offline);
            }
            holder.disclosureImg.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.INVISIBLE);
        } else if (element.isHasChildren() && element.isExpanded()) {
            if (element.isOnline()) {
                holder.disclosureImg.setImageResource(R.drawable.open);
            } else {
                holder.disclosureImg.setImageResource(R.drawable.offline);
            }
            holder.disclosureImg.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.INVISIBLE);
        } else if (!element.isHasChildren()) {
            holder.disclosureImg.setImageResource(R.drawable.camera);
            holder.disclosureImg.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.VISIBLE);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    if (!CategoryActivity.AddVideoInfo(element.getParendId(), element.getId())) {
                        buttonView.toggle();
                    }
                }else{
                    if (!CategoryActivity.DelVideoInfo(element.getParendId(), element.getId())) {
                        buttonView.toggle();
                    }
                }
            }
        });
        return convertView;
    }  
      
    static class ViewHolder{
        ImageView disclosureImg;
        CheckBox checkBox;
        TextView contentText;
    }
}