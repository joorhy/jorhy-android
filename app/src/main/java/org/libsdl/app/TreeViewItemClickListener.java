package org.libsdl.app;

import java.util.ArrayList;  
  
import android.view.View;  
import android.widget.AdapterView;  
import android.widget.AdapterView.OnItemClickListener;  

public class TreeViewItemClickListener implements OnItemClickListener {  
    private TreeViewAdapter treeViewAdapter;  
      
    public TreeViewItemClickListener(TreeViewAdapter treeViewAdapter) {  
        this.treeViewAdapter = treeViewAdapter;  
    }  
      
    @Override  
    public void onItemClick(AdapterView<?> parent, View view, int position,  long id) {  
        Element element = (Element) treeViewAdapter.getItem(position);  
        ArrayList<Element> elements = treeViewAdapter.getElements();  
        ArrayList<Element> elementsData = treeViewAdapter.getElementsData();  
          
        if (!element.isHasChildren()) {  
            return;  
        }  
          
        if (element.isExpanded()) {  
            element.setExpanded(false);  
            ArrayList<Element> elementsToDel = new ArrayList<Element>();  
            for (int i = position + 1; i < elements.size(); i++) {  
                if (element.getLevel() >= elements.get(i).getLevel())  
                    break;  
                elementsToDel.add(elements.get(i));  
            }  
            elements.removeAll(elementsToDel);  
            treeViewAdapter.notifyDataSetChanged();  
        } else if (element.isOnline()){
            element.setExpanded(true);  
            int i = 1;
            for (Element e : elementsData) {  
                if (e.getParendId().equals(element.getId())) {
                    e.setExpanded(false);  
                    elements.add(position + i, e);  
                    i ++;  
                }  
            }  
            treeViewAdapter.notifyDataSetChanged();  
        }  
    }  
  
}  