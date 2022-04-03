package herbariumListOperation;

import android.net.Uri;

public class SubItem {
    private String herbName;
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

    public void setSubItemTitle(String herbName) {
        this.herbName = herbName;
    }

}