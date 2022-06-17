package sk.spse.oursoft.android.e_herbarium.misc;

import android.net.Uri;

import java.util.ArrayList;

import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;

public interface UserListCallback {
    void onDataCallback(ArrayList<Item> value);
    void onImageCallback(Uri uri);
}