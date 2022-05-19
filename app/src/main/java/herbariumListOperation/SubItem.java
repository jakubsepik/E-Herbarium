package herbariumListOperation;

import android.net.Uri;

public class SubItem {
    private String herbId;
    private String herbName;
    private String herbDescription;
    private int icon;
    private Uri imageUri;

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public SubItem(String herbId, int icon) {
        this.herbId = herbId;
        this.icon = icon;
    }
    public SubItem(){}

    public SubItem(String herbId,String herbName,int icon) {
        this.herbId = herbId;
        this.herbDescription = "";
        this.herbName = herbName;
        this.icon = icon;
    }
    public SubItem(String herbId,String herbName,String herbDescription, int icon) {
        this.herbId = herbId;
        this.herbDescription = herbDescription;
        this.herbName = herbName;
        this.icon = icon;
    }
    public SubItem(String herbId,String herbName,int icon,Uri imageUri){
        this.herbId = herbId;
        this.herbDescription = "";
        this.herbName = herbName;
        this.icon = icon;
        this.imageUri = imageUri;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getHerbName() {
        return herbName;
    }

    public void setHerbName(String herbName) {
        this.herbName = herbName;
    }

    public String getHerbId() {
        return herbId;
    }

    public void setHerbId(String herbId) {
        this.herbId = herbId;
    }

    public String getHerbDescription() {
        return herbDescription;
    }

    public void setHerbDescription(String herbDescription) {
        this.herbDescription = herbDescription;
    }
}