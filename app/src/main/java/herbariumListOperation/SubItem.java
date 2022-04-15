package herbariumListOperation;

import android.net.Uri;

public class SubItem {
    private String herbId;
    private String herbName;
    private String herbDescription;
    private int icon;

    public SubItem(String herbName, int icon) {
        this.herbName = herbName;
        this.icon = icon;
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