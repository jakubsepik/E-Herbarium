package sk.spse.oursoft.android.e_herbarium;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import herbariumListOperation.Item;
import herbariumListOperation.ItemAdapter;
import herbariumListOperation.SubItem;

import androidx.annotation.NonNull;

public class HerbariumViewActivity extends AppCompatActivity {
    ListView listView;

    public static Dialog dialogReference;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herbarium_view);

        ListLogic.begin(null,getApplicationContext());

        RecyclerView rvItem = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HerbariumViewActivity.this);
        List<Item> itemList = ListLogic.getList();
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        rvItem.setAdapter(itemAdapter);
        rvItem.setLayoutManager(layoutManager);

        ImageButton hamburgerMenu = (ImageButton) findViewById(R.id.hamburgerMenu);

        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(HerbariumViewActivity.this, hamburgerMenu);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Add Group")){
                            Dialog addGroupDialog = new Dialog(view.getContext());
                            addGroupDialog.setContentView(R.layout.add_group_view);

                            ImageButton dismissButton = addGroupDialog.findViewById(R.id.dismissAddGroup);
                            dismissButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addGroupDialog.dismiss();
                                }
                            });

                            EditText nameInput = addGroupDialog.findViewById(R.id.groupName);

                            Button addGroupButton = addGroupDialog.findViewById(R.id.addGroupButton);
                            addGroupButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(nameInput.getText().toString().equals("") || nameInput.getText().toString().length() == 0){
                                        Toast.makeText(view.getContext(), "You have to enter a name!", Toast.LENGTH_SHORT).show();

                                    }else if (groupExists(nameInput.getText().toString(), itemList)){
                                        Toast.makeText(view.getContext(), "The group's name has to be unique!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        List<SubItem> subItemList = new ArrayList<SubItem>();
                                        Item item = new Item(nameInput.getText().toString(), subItemList);

                                        ListLogic.addCategory(item);
                                        itemAdapter.notifyItemInserted(ListLogic.getList().size()-1);

                                        addGroupDialog.dismiss();
                                    }
                                }
                            });

                            addGroupDialog.show();
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    protected void onPause() {
        //ListLogic.saveAll(getApplicationContext());
        super.onPause();
    }

    //    Selecting image from the gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if(requestCode == 1 ){

           if(data == null){
               Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
           }else{
               ((AddItemDialog) dialogReference).onImageSelect(data.getData());
           }
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

    private boolean groupExists(String name, List<Item> itemList){
        for (Item item:itemList){
            if(item.getItemTitle().equals(name)){
                return true;
            }
        }
        return false;
    }


}
