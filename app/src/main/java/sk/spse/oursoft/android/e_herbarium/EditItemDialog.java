package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItem;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItemAdapter;
import sk.spse.oursoft.android.e_herbarium.misc.DatabaseTools;

public class EditItemDialog extends Dialog {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int RESULT_LOAD_IMAGE = 2;
    private final String[] invalidCharacters = {".", "@", "$", "%", "&", "/", "<", ">", "?", "|", "{", "}", "[", "]"};
    public Uri imageURI;
    public ImageView editImage;

    public EditItemDialog(@NonNull Context context, int theme_Black_NoTitleBar_Fullscreen, SubItem subItem, List<SubItem> subItemList, SubItemAdapter subItemAdapter, Item item) {
        super(context, theme_Black_NoTitleBar_Fullscreen);

        HerbariumViewActivity.setCurrentDialog(this);

        this.setContentView(R.layout.edit_subitem_view);

        editImage = (ImageView) findViewById(R.id.editImage);
        ImageButton editDismissButton = (ImageButton) findViewById(R.id.editDismissButton);
        EditText editNameInput = (EditText) findViewById(R.id.editNameInput);
        EditText editDescriptionInput = (EditText) findViewById(R.id.editDescriptionInput);
        Button editItemButton = (Button) findViewById(R.id.editSubItemButton);

        imageURI = Uri.parse(subItem.getImageUri());

        editImage.setImageURI(Uri.parse(subItem.getImageUri()));

        editNameInput.setText(subItem.getHerbName());
        editDescriptionInput.setText(subItem.getHerbDescription());

        editDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditItemDialog.this.dismiss();
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //bottom sheet dialog containing gallery or camera options
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.camera_choose);

                LinearLayout chooseCameraLayout = bottomSheetDialog.findViewById(R.id.chooseCamera);
                LinearLayout chooseGalleryLayout = bottomSheetDialog.findViewById(R.id.chooseGallery);

                //camera intent
                chooseCameraLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            Activity activity = (Activity) ((ContextWrapper) context).getBaseContext();
                            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, "error opening camera", Toast.LENGTH_SHORT).show();
                            Log.e("Camera", "error occured while taking the image" + e.getStackTrace());
                        }

                        bottomSheetDialog.dismiss();

                    }
                });

                //gallery intent
                chooseGalleryLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent galleryChoose = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        try {
                            Activity activity = (Activity) ((ContextWrapper) context).getBaseContext();
                            activity.startActivityForResult(galleryChoose, RESULT_LOAD_IMAGE);

                        } catch (Exception e) {
                            Toast.makeText(context, "error opening gallery", Toast.LENGTH_SHORT).show();
                            Log.e("Gallery", "error occured while opening the gallery" + e.getMessage());
                        }

                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();

            }
        });

        editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editNameInput.getText().toString().equals("") || editNameInput.getText().toString().length() == 0) {
                    Toast.makeText(view.getContext(), "You have to enter a name!", Toast.LENGTH_SHORT).show();

                } else if (stringContainsInvalidCharacters(editNameInput.getText().toString())) {
                    Toast.makeText(context, "Characters " + Arrays.toString(invalidCharacters) + " aren't allowed!", Toast.LENGTH_SHORT).show();

                } else {

                    DatabaseTools databaseTools = new DatabaseTools(context);

                    int subItemPosition = findSubItemPosition(subItem.getHerbName(), subItemList);

                    SubItem editedSubItem = new SubItem();

                    editedSubItem.setHerbName(editNameInput.getText().toString());
                    editedSubItem.setHerbDescription(editDescriptionInput.getText().toString());
                    editedSubItem.setIcon(subItem.getIcon());
                    editedSubItem.setHerbId(subItem.getHerbId());

                    System.out.println("THE SUB ITEM URI " + subItem.getImageUri());

                    if (imageURI == null) {
                        editedSubItem.setImageUri(subItem.getImageUri());
                    } else {
                        editedSubItem.setImageUri(imageURI.toString());
                    }


                    FirebaseUser user = databaseTools.getCurrentUser();
                    if (user != null) {
                        try {
                            String UserName = user.getUid();

                            databaseTools.addEditSubItem(item, editedSubItem);
                            System.out.println("ITEM POSITION " + subItemPosition);
                            ListLogic.editOne(item.getItemTitle(), subItemPosition, editedSubItem);
                            subItemAdapter.notifyItemChanged(subItemPosition);

                            EditItemDialog.this.dismiss();

                            Toast.makeText(view.getContext(), "Item has been edited", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, "Not signed In", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    protected boolean stringContainsInvalidCharacters(String string) {
        for (String character : invalidCharacters) {
            if (string.contains(character)) {
                return true;
            }
        }

        return false;
    }

    public int findSubItemPosition(String subItemTitle, List<SubItem> subItemList) {

        for (int i = 0; i < subItemList.size(); i++) {
            if (subItemTitle.equals(subItemList.get(i).getHerbName())) {
                return i;
            }
        }
        return -1;
    }

    public void setImageURI(Uri pictureURI) {

        this.imageURI = pictureURI;
        Toast.makeText(this.getContext(), imageURI.toString(), Toast.LENGTH_SHORT).show();
        Log.e("IMAGEURI", pictureURI.toString());
        editImage.setImageURI(imageURI);


    }
    public Uri getImageUri() {
        return this.imageURI;
    }

}
