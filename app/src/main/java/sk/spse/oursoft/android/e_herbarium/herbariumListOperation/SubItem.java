package sk.spse.oursoft.android.e_herbarium.herbariumListOperation;

import androidx.annotation.NonNull;

public class SubItem {
    private String herbId;
    private String herbName;
    private String herbDescription;
    private int icon;
    private String imageUri;


    public SubItem(String herbId, int icon) {
        this.herbId = herbId;
        this.icon = icon;
    }

    public SubItem() {
    }

    public SubItem(String herbId, String herbName, int icon) {
        this.herbId = herbId;
        this.herbDescription = "";
        this.herbName = herbName;
        this.icon = icon;
    }

    public SubItem(String herbId, String herbName, String herbDescription, int icon) {
        this.herbId = herbId;
        this.herbDescription = herbDescription;
        this.herbName = herbName;
        this.icon = icon;
    }

    public SubItem(String herbId, String herbName, int icon, String imageUri) {
        this.herbId = herbId;
        this.herbDescription = "";
        this.herbName = herbName;
        this.icon = icon;
        this.imageUri = imageUri;
    }

    public SubItem(String herbId, String herbName, String herbDescription, int icon, String imageUri) {
        this.herbId = herbId;
        this.herbDescription = herbDescription;
        this.herbName = herbName;
        this.icon = icon;
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" +
                "      \"id\": \"" + this.herbId + "\"," +
                "      \"name\": \"" + this.herbName + "\"," +
                "      \"description\": \"" + this.herbDescription + "\"," +
                "      \"image\": \"" + this.imageUri + "\"," +
                "      \"icon\": \"" + this.icon + "\"" +
                "    }";
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}