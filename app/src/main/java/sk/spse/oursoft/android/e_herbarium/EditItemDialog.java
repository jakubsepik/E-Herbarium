package sk.spse.oursoft.android.e_herbarium;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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

import java.util.List;

import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItem;

public class EditItemDialog extends Dialog {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int RESULT_LOAD_IMAGE = 2;

    public EditItemDialog(@NonNull Context context, int theme_Black_NoTitleBar_Fullscreen, SubItem subItem, List<SubItem> subItemList) {
        super(context, theme_Black_NoTitleBar_Fullscreen);

        HerbariumViewActivity.setCurrentDialog(this);

        this.setContentView(R.layout.edit_subitem_view);

        ImageView editImage = (ImageView) findViewById(R.id.editImage);
        ImageButton editDismissButton = (ImageButton) findViewById(R.id.editDismissButton);
        EditText editNameInput = (EditText) findViewById(R.id.editNameInput);
        EditText editDescriptionInput = (EditText) findViewById(R.id.editDescriptionInput);
        Button editItemButton = (Button) findViewById(R.id.editItemButton);

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
                            ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

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
                            ((Activity) context).startActivityForResult(galleryChoose, RESULT_LOAD_IMAGE);

                        } catch (Exception e) {
                            Toast.makeText(context, "error opening gallery", Toast.LENGTH_SHORT).show();
                            Log.e("Gallery", "error occured while opening the gallery" + e.getStackTrace());
                        }

                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();

//
            }
        });

    }
}
