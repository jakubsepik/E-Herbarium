package sk.spse.oursoft.android.e_herbarium;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import herbariumListOperation.Item;
import herbariumListOperation.ItemAdapter;
import herbariumListOperation.SubItem;

import androidx.annotation.NonNull;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HerbariumViewActivity extends AppCompatActivity {
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herbarium_view);

        RecyclerView rvItem = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HerbariumViewActivity.this);
        List<Item> itemList = buildItemList();
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        rvItem.setAdapter(itemAdapter);
        rvItem.setLayoutManager(layoutManager);

        addItem(itemList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    int[] icons = {R.drawable.listocek_symbolik, R.drawable.klasocek_symbolik, R.drawable.kricek_symbolik, R.drawable.stromcek_symbolik};

    Random rd = new Random();

    String[] herbNames = {"Mint", "Echinacea", "Thyme"};


    protected List<Item> buildItemList() {
        List<Item> itemList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Item item = new Item("Item " + i, buildSubItemList());
            itemList.add(item);
        }
        return itemList;
    }

    private void addItem(List<Item> itemList) {
        Item item = new Item("New Item", buildSubItemList());
        itemList.add(item);
    }

    private List<SubItem> buildSubItemList() {
        List<SubItem> subItemList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String herbName = herbNames[rd.nextInt(herbNames.length)];
            int icon = icons[rd.nextInt(icons.length)];
            SubItem subItem = new SubItem(herbName, icon);
            subItemList.add(subItem);
        }
        return subItemList;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addItem() {

    }

}
