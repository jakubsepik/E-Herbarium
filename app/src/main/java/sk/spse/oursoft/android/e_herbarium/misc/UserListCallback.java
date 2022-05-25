package sk.spse.oursoft.android.e_herbarium.misc;

import java.util.ArrayList;

import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;

public interface UserListCallback {
    void onCallback(ArrayList<Item> value);
    void onTimeCallback(int time);
}