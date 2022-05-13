package herbariumListOperation;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String itemTitle;
    private List<SubItem> subItemList;

    public Item(String itemTitle, List<SubItem> subItemList) {
        this.itemTitle = itemTitle;
        this.subItemList = subItemList;
    }

    public Item(String itemTitle) {
        this.itemTitle = itemTitle;
        this.subItemList = new ArrayList<>();
    }
    public void addSubItem(SubItem subItem){
        subItemList.add(subItem);
    }


    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    @Override
    public String toString() {
        StringBuilder output= new StringBuilder();
        for(int i=0;i<subItemList.size();i++){
            output.insert(0, subItemList.get(i).toString() + ",");
        }
        return this.itemTitle+":["+output.toString()+"]";
    }

    public List<SubItem> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(List<SubItem> subItemList) {
        this.subItemList = subItemList;
    }
}
