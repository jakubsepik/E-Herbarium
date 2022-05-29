package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.ItemAdapter;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItem;
import sk.spse.oursoft.android.e_herbarium.misc.DatabaseTools;
import sk.spse.oursoft.android.e_herbarium.misc.UserListCallback;

import android.graphics.Matrix;


public class HerbariumViewActivity extends AppCompatActivity {
    ListView listView;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int RESULT_LOAD_IMAGE = 2;

    public static Dialog dialogReference;
    public String TAG = "HerbariumViewActivity";

    public String currentPhotoPath;
    public DatabaseTools databaseTools;

    private final String[] invalidCharacters = {".", "@", "$", "%", "&", "/", "<", ">", "?", "|", "{", "}", "[", "]"};


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        databaseTools = new DatabaseTools(getApplicationContext(), this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.herbarium_view);
        DatabaseTools databaseTools = new DatabaseTools(getApplicationContext(), this);




        databaseTools.initializeNetworkCallback();


        RecyclerView rvItem = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HerbariumViewActivity.this);

        final List<Item>[] itemList = new List[]{ListLogic.getList()};
        final ItemAdapter[] itemAdapter = {new ItemAdapter(itemList[0])};
        ImageButton hamburgerMenu = (ImageButton) findViewById(R.id.hamburgerMenu);


        databaseTools.getUserItems(new UserListCallback() {
            @Override
            public void onCallback(ArrayList<Item> value) {
                //finally use the database items here
                //od the stuff here
                String user =databaseTools.getCurrentUser().getEmail().split("\\.")[0];
                //Log.d("EH",user);
                ListLogic.begin(databaseTools.getItems(), getApplicationContext(),user);
                int tmp = ListLogic.getList().size()-1;
                itemList[0] = ListLogic.getList();
                itemAdapter[0] = new ItemAdapter(itemList[0]);
                rvItem.setAdapter(itemAdapter[0]);
                rvItem.setLayoutManager(layoutManager);
                itemAdapter[0].notifyItemInserted(ListLogic.getList().size()-1);
            }

            @Override
            public void onTimeCallback(int time) {

            }

        });

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

                                    } else if (groupExists(nameInput.getText().toString(), itemList[0])) {
                                        Toast.makeText(view.getContext(), "The group's name has to be unique!", Toast.LENGTH_SHORT).show();


                                    } else if (stringContainsInvalidCharacters(nameInput.getText().toString())) {

                                        Toast.makeText(view.getContext(), "Characters " + Arrays.toString(invalidCharacters) + " aren't allowed!", Toast.LENGTH_SHORT).show();

                                    } else {
                                        List<SubItem> subItemList = new ArrayList<SubItem>();
                                        Item item = new Item(nameInput.getText().toString(), subItemList);

                                        ListLogic.addCategory(item);

                                        itemAdapter[0].notifyItemInserted(ListLogic.getList().size()-1);

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
        ListLogic.saveAll();
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                Log.i("Camera", "Camera data is null" + String.valueOf(data.getData()));
                Toast.makeText(this, "No Image taken", Toast.LENGTH_SHORT).show();
            } else {

                try {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    String ItemName = (String) extras.get("ItemName");
                    File imageFile = storeImage(imageBitmap, REQUEST_IMAGE_CAPTURE);

                    if (imageFile != null) {
                        Uri imageUri = Uri.fromFile(imageFile);
                        ((AddItemDialog) dialogReference).setImageURI(imageUri);

//                        databaseTools.saveImage(imageUri,ItemName);

                        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.e("Camera", "failed to save image");
                        Toast.makeText(this, "Couldn't save image", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    Log.e("Camera", "Failed to load image from camera" + Arrays.toString(e.getStackTrace()));
                    Toast.makeText(this, "Couldn't set image", Toast.LENGTH_SHORT).show();
                }

            }

        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                Log.i("Gallery", "gallery data is null" + String.valueOf(data.getData()));
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Bundle extras = data.getExtras();
                    String ItemName = (String) extras.get("ItemName");
                    Uri imageURI = data.getData();
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);


                    File imageFile = storeImage(imageBitmap, RESULT_LOAD_IMAGE);
                    if (imageFile != null) {
                        Uri imageUri = Uri.fromFile(imageFile);
                        ((AddItemDialog) dialogReference).setImageURI(imageUri);

                        //saves the image and the image ref in the firebase storage
//                        databaseTools.saveImage(imageUri,ItemName);

                        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.e("Gallery", "failed to save image");
                        Toast.makeText(this, "Couldn't save image", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    Log.e("Gallery", "failed to load image from gallery" + Arrays.toString(e.getStackTrace()));
                    Toast.makeText(this, "Couldn't set image", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
        }
    }


    //    Selecting image from the gallery
    private File storeImage(Bitmap image, int requestCode) {

        //gets a file path
        File pictureFile = getOutputMediaFile();

        if (pictureFile == null) {
            Log.d("Store image", "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {

            image = getResizedBitmap(image, 650, 800);

            if (image == null) {
                Log.e("Resize image", "Failed to resize image");
                return null;
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE) {

                FileOutputStream fos = new FileOutputStream(pictureFile);
                long startTime = System.nanoTime();

                image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Log.i("Time to compress", String.valueOf((System.nanoTime() - startTime)));
                fos.close();


            } else if (requestCode == RESULT_LOAD_IMAGE) {

                FileOutputStream fos = new FileOutputStream(pictureFile);
                long startTime = System.nanoTime();

                image.compress(Bitmap.CompressFormat.JPEG, 80, fos);

                Log.i("Time to compress", String.valueOf((System.nanoTime() - startTime)));
                fos.close();

            }
        } catch (FileNotFoundException e) {
            Log.d("herbarium View Activity", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("herbarium View Activity", "Error accessing file: " + e.getMessage());
        }
        return pictureFile;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        try {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();

            return resizedBitmap;
        } catch (Exception e) {
            Log.e("Resize image", "Problem resizing the image " + Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    private File getOutputMediaFile() {

        //creates temp file
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss.SSS").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".png";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File mediaFile;
        mediaFile = new File(storageDir + File.separator + imageFileName);
        return mediaFile;
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

            Uri uri = (new Uri.Builder())
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(this.getResources().getResourcePackageName(R.drawable.tree_placeholder))
                    .appendPath(this.getResources().getResourceTypeName(R.drawable.tree_placeholder))
                    .appendPath(this.getResources().getResourceEntryName(R.drawable.tree_placeholder))
                    .build();


            SubItem subItem = new SubItem(herbId, herbName, icon, uri.toString());


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

    protected boolean stringContainsInvalidCharacters(String string) {
        for (String character : invalidCharacters) {
            if (string.contains(character)) {
                return true;
            }
        }

        return false;
    }

    public void testActivituButton(View view) {
        databaseTools.synchronizeInternalStorageToDatabase();
    }
}
