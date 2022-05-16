package sk.spse.oursoft.android.e_herbarium;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;

import herbariumListOperation.Item;
import herbariumListOperation.ItemAdapter;
import herbariumListOperation.SubItem;

import androidx.annotation.NonNull;

public class HerbariumViewActivity extends AppCompatActivity {
    ListView listView;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int RESULT_LOAD_IMAGE = 2;

    public static Dialog dialogReference;

    public String currentPhotoPath;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herbarium_view);

        ListLogic.begin(null, getApplicationContext());

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
                        if (menuItem.getTitle().equals("Add Group")) {
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
                                    if (nameInput.getText().toString().equals("") || nameInput.getText().toString().length() == 0) {
                                        Toast.makeText(view.getContext(), "You have to enter a name!", Toast.LENGTH_SHORT).show();

                                    } else if (groupExists(nameInput.getText().toString(), itemList)) {
                                        Toast.makeText(view.getContext(), "The group's name has to be unique!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        List<SubItem> subItemList = new ArrayList<SubItem>();
                                        Item item = new Item(nameInput.getText().toString(), subItemList);

                                        itemList.add(item);
                                        itemAdapter.notifyItemInserted(itemList.size() - 1);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            if (data == null) {
                Log.i("information", String.valueOf(data.getData()));

                Toast.makeText(this, "No Image taken", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, (CharSequence) data.getExtras().get("data"), Toast.LENGTH_SHORT).show();

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri uri = getImageUri(this.getApplicationContext(),photo);
                ((AddItemDialog) dialogReference).setSubItemImage(uri);
            }


        }

     else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            if (data == null) {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            } else {

                ((AddItemDialog) dialogReference).setSubItemImage(data.getData());
            }
        } else {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
        }


    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);

    }

    public static void setCurrentDialog(Dialog d) {
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
            SubItem subItem = new SubItem(herbId, icon, herbName);
            subItemList.add(subItem);
        }
        return subItemList;
    }

    private boolean groupExists(String name, List<Item> itemList) {
        for (Item item : itemList) {
            if (item.getItemTitle().equals(name)) {
                return true;
            }
        }
        return false;
    }



}
