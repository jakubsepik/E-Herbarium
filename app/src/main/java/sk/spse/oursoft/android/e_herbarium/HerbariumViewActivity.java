package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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


public class HerbariumViewActivity extends AppCompatActivity {
    public static Dialog dialogReference;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int RESULT_LOAD_IMAGE = 2;
    private final int REQUEST_IMPORT_FILE = 3;
    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    private final String[] invalidCharacters = {".", "@", "$", "%", "&", "/", "<", ">", "?", "|", "{", "}", "[", "]"};
    public String TAG = "HerbariumViewActivity";

    public ItemAdapter itemAdapter;
    public String currentPhotoPath;
    public DatabaseTools databaseTools;
    ListView listView;
    int[] icons = {R.drawable.listocek_symbolik, R.drawable.klasocek_symbolik, R.drawable.kricek_symbolik, R.drawable.stromcek_symbolik};
    Random rd = new Random();
    String[] herbNames = {"Mint", "Echinacea", "Thyme"};


    public static void setCurrentDialog(Dialog d) {
        dialogReference = d;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herbarium_view);
        databaseTools = new DatabaseTools(getApplicationContext());

        RecyclerView rvItem = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HerbariumViewActivity.this);


        List<Item> itemList = ListLogic.getList();
        itemAdapter = new ItemAdapter(itemList);
        rvItem.setAdapter(itemAdapter);
        rvItem.setLayoutManager(layoutManager);
        itemAdapter.notifyItemInserted(ListLogic.getList().size() - 1);
        ImageButton hamburgerMenu = (ImageButton) findViewById(R.id.hamburgerMenu);

//        databaseTools.initializeNetworkCallback();

        //tento callback daj to Landing screen Activity
        //nech sa to loadne pred tym ako zapnes toto
        //nemalo by byt
        //jake meno ?


        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = databaseTools.getCurrentUser();
                if (user != null) {
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


                                        } else if (stringContainsInvalidCharacters(nameInput.getText().toString())) {

                                            Toast.makeText(view.getContext(), "Characters " + Arrays.toString(invalidCharacters) + " aren't allowed!", Toast.LENGTH_SHORT).show();

                                        } else {
                                            List<SubItem> subItemList = new ArrayList<SubItem>();
                                            Item item = new Item(nameInput.getText().toString(), subItemList);

                                            databaseTools.addItemToDatabase(item);
                                            ListLogic.addCategory(item);
                                            itemAdapter.notifyItemInserted(ListLogic.getList().size() - 1);

                                            addGroupDialog.dismiss();
                                        }
                                    }
                                });

                                addGroupDialog.show();
                            } else if (menuItem.getTitle().equals("Import")) {
                                FirebaseUser user = databaseTools.getCurrentUser();
                                if (user != null) {
                                    String userName = user.getUid();
                                    Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                    chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                                    chooseFile.setType("*/*");
                                    startActivityForResult(chooseFile, REQUEST_IMPORT_FILE);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Not signed In", Toast.LENGTH_SHORT).show();
                                }
                            } else if (menuItem.getTitle().equals("Export")) {
                                FirebaseUser user = databaseTools.getCurrentUser();
                                if (user != null) {
                                    try {
                                        String userName = user.getUid();
                                        ListLogic.exportHerbarium(userName);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Not signed In", Toast.LENGTH_SHORT).show();
                                }
                            }

                            return true;
                        }

                    });

                    popupMenu.show();

                } else {
                    Toast.makeText(getApplicationContext(), "user not signed in", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    protected void onPause() {
        try {
            ListLogic.saveAll();
        } catch (java.lang.RuntimeException e) {
            Log.e("Pause error", "error because there is no data in the herbarium");
        } catch (Exception e) {
            Log.e("View Activity pause", e.getMessage());
        }
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //this is urn when a camera picture is taken
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                Log.i("Camera", "Camera data is null" + data.getData());
                Toast.makeText(this, "No Image taken", Toast.LENGTH_SHORT).show();
            } else {

                try {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");


                    //so I know what dialog to set the given uri
                    if (dialogReference.getClass().toString().equals("class sk.spse.oursoft.android.e_herbarium.EditItemDialog")) {
                        Uri SourceImageUri = ((EditItemDialog) dialogReference).getImageUri();

                        //the path is so the edit one gets overwritten
                        File imageFile = storeImage(imageBitmap, REQUEST_IMAGE_CAPTURE, SourceImageUri.getPath());
                        Uri imageUri = Uri.fromFile(imageFile);
                        if (imageFile != null) {
                            System.out.println("EDIT ITEM DIALOG");
                            ((EditItemDialog) dialogReference).setImageURI(imageUri);

                        } else {
                            Log.e("Gallery", "failed to save image");
                            Toast.makeText(this, "Couldn't save image", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        //if the imaeg path is null it stored in a generated place
                        File imageFile = storeImage(imageBitmap, RESULT_LOAD_IMAGE, null);
                        if (imageFile != null) {
                            Uri imageUri = Uri.fromFile(imageFile);

                            ((AddItemDialog) dialogReference).setImageURI(imageUri);
                        } else {
                            Log.e("Gallery", "failed to save image");
                            Toast.makeText(this, "Couldn't save image", Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    Log.e("Camera", "Failed to load image from camera" + Arrays.toString(e.getStackTrace()));
                    Toast.makeText(this, "Couldn't set image", Toast.LENGTH_SHORT).show();
                }

            }

            //this is run when an image is selected form the gallery
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                Log.i("Gallery", "gallery data is null" + data.getData());
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    Uri newImageUri = data.getData();
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), newImageUri);

                    //processes the image and either overwrites the pevious one or writes a new one
                    if (dialogReference.getClass().toString().equals("class sk.spse.oursoft.android.e_herbarium.EditItemDialog")) {
                        Uri SourceImageUri = ((EditItemDialog) dialogReference).getImageUri();

                        File imageFile = storeImage(imageBitmap, RESULT_LOAD_IMAGE, SourceImageUri.getPath());
                        Uri imageUri = Uri.fromFile(imageFile);
                        if (imageFile != null) {
                            System.out.println("EDIT ITEM DIALOG");
                            ((EditItemDialog) dialogReference).setImageURI(imageUri);

                        } else {
                            Log.e("Gallery", "failed to save image");
                            Toast.makeText(this, "Couldn't save image", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        //if the imaeg path is null it stored in a generated place
                        File imageFile = storeImage(imageBitmap, RESULT_LOAD_IMAGE, null);
                        if (imageFile != null) {
                            Uri imageUri = Uri.fromFile(imageFile);

                            ((AddItemDialog) dialogReference).setImageURI(imageUri);
                        } else {
                            Log.e("Gallery", "failed to save image");
                            Toast.makeText(this, "Couldn't save image", Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    Log.e("Gallery", "failed to load image from gallery" + Arrays.toString(e.getStackTrace()));
                    Toast.makeText(this, "Couldn't set image", Toast.LENGTH_SHORT).show();
                }
            }
            //THis is run when  an imort is requested
        } else if (requestCode == REQUEST_IMPORT_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                //upload the files to hte JSON
                //synchronize the internal database with hte firebase one
                //download the images from firebase that were gotten from the file
                if (data.getData() != null) {
                    Uri content_describer = data.getData();
                    byte[] byteData = getBytes(content_describer);
                    if (byteData != null) {
                        System.out.println("THE DATA IS " + new String(byteData, StandardCharsets.UTF_8));
                        try {
                            ListLogic.importHerbarium(byteData, itemAdapter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "couldn't load the file from the given location move the file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Couldn't load file from given locatino", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Didn't file successfully ", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
        }
        }





    byte[] getBytes(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = this.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
            Toast.makeText(this, "getBytes error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }


    //    Selecting image from the gallery
    private File storeImage(Bitmap image, int requestCode, String imagePath) {

        //if there is a predetermined path use that
        File pictureFile = null;
        if (imagePath == null) {
            //gets a file path
            pictureFile = getOutputMediaFile();
        } else {
            pictureFile = new File(imagePath);
        }
        if (pictureFile == null) {
            Log.d("Store image", "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {

//            image = getResizedBitmap(image, 650, 800);
//
//            if (image == null) {
//                Log.e("Resize image", "Failed to resize image");
//                return null;
//            }
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
            String herbId = "Group " + group + " Position " + i;

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

}


