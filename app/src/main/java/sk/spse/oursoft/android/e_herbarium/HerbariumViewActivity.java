package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import herbariumListOperation.Item;
import herbariumListOperation.ItemAdapter;
import herbariumListOperation.SubItem;

public class HerbariumViewActivity extends Activity {


    int icon = R.drawable.listocek_symbolik;

    private Random rd = new Random();

    String[] herbNames = {"Mint", "Echinacea", "Thyme"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herbarium_view);

        RecyclerView rvItem = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HerbariumViewActivity.this);
        ItemAdapter itemAdapter = new ItemAdapter(buildItemList());
        rvItem.setAdapter(itemAdapter);
        rvItem.setLayoutManager(layoutManager);
    }

    private List<Item> buildItemList() {
        List<Item> itemList = new ArrayList<>();
        for (int i=0; i<10; i++) {
            Item item = new Item("Item "+i, buildSubItemList());
            itemList.add(item);
        }
        return itemList;
    }

    private List<SubItem> buildSubItemList() {
        List<SubItem> subItemList = new ArrayList<>();
        for (int i=0; i<5; i++) {
            String herbName = herbNames[rd.nextInt(herbNames.length)];
            SubItem subItem = new SubItem(herbName, icon);
            subItemList.add(subItem);
        }
        return subItemList;
    }
}
