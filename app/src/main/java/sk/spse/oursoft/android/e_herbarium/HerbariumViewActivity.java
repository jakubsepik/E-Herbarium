package sk.spse.oursoft.android.e_herbarium;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import herbariumListOperation.Item;
import herbariumListOperation.ItemAdapter;
import herbariumListOperation.SubItem;

import androidx.annotation.NonNull;

public class HerbariumViewActivity extends AppCompatActivity {
    ListView listView;

    public static Dialog dialogReference;

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

        //addItem(itemList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if(requestCode == 1 ){
           assert data != null;
           ((AddItemDialog) dialogReference).onImageSelect(data.getData());
       }else{
           Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
       }

    }

    public static void setCurrentDialog(Dialog d){
        dialogReference = d;
    }

    int[] icons = {R.drawable.listocek_symbolik, R.drawable.klasocek_symbolik, R.drawable.kricek_symbolik, R.drawable.stromcek_symbolik};

    Random rd = new Random();

    String[] herbNames = {"Mint", "Echinacea", "Thyme"};


    protected List<Item> buildItemList() {
        List<Item> itemList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Item item = new Item("Item " + i, buildSubItemList(i));
            itemList.add(item);
        }
        return itemList;
    }

    /*private void addItem(List<Item> itemList) {
        Item item = new Item("New Item", buildSubItemList());
        itemList.add(item);
    }*/

    private List<SubItem> buildSubItemList(int group) {
        List<SubItem> subItemList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String herbName = herbNames[rd.nextInt(herbNames.length)];
            int icon = icons[rd.nextInt(icons.length)];
            String herbId = "Group " + Integer.toString(group) + " Position " + Integer.toString(i);
            SubItem subItem = new SubItem(herbId,icon, herbName);
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
