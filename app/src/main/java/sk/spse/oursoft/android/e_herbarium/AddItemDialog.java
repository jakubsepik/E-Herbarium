package sk.spse.oursoft.android.e_herbarium;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.Item;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Arrays;


import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItem;
import sk.spse.oursoft.android.e_herbarium.herbariumListOperation.SubItemAdapter;
import sk.spse.oursoft.android.e_herbarium.misc.DatabaseTools;

public class AddItemDialog extends Dialog {

    private String herbName;
    private String herbDescription;

    private SubItem subItem;

    private ImageView insertImage;

    private final int[] iconList = {R.drawable.listocek_symbolik, R.drawable.kricek_symbolik, R.drawable.klasocek_symbolik, R.drawable.stromcek_symbolik};

    private boolean leavesPicked = false;
    private boolean bushPicked = false;
    private boolean earPicked = false;
    private boolean treePicked = false;
    private boolean continueWithoutIcon = false;

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int RESULT_LOAD_IMAGE = 2;
    private Uri imageURI;


    public AddItemDialog(@NonNull Context context, int theme_Black_NoTitleBar_Fullscreen, SubItemAdapter subItemAdapter, Item item, int index) {
        super(context, theme_Black_NoTitleBar_Fullscreen);

        subItem = new SubItem();

        DatabaseTools databaseTools = new DatabaseTools(this.getContext(),this.getOwnerActivity());

        HerbariumViewActivity.setCurrentDialog(this);

        this.setContentView(R.layout.add_subitem_view);

        insertImage = this.findViewById(R.id.insertImage);
        ImageButton dismissButton = this.findViewById(R.id.dismissButton);
        EditText herbNameInput = this.findViewById(R.id.itemNameInput);
        EditText itemDescriptionInput = this.findViewById(R.id.itemDescriptionInput);
        Button addItemButton = this.findViewById(R.id.addItemButton);

//        Intent that manages the image picking from the gallery
        insertImage.setOnClickListener(new View.OnClickListener() {
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

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemDialog.this.dismiss();
            }
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                herbName = herbNameInput.getText().toString();
                herbDescription = itemDescriptionInput.getText().toString();

                if (herbName.length() == 0) {
                    Toast.makeText(context, "Please input a valid herb name.", Toast.LENGTH_SHORT).show();

                }else if (subItemAdapter.findItemPosition(herbName, subItemAdapter.getSubItemList()) != -1){

                    Toast.makeText(context, "Please input a unique herb name", Toast.LENGTH_SHORT).show();

                }else{

                    subItem.setHerbName(herbName);
                    subItem.setHerbDescription(herbDescription);

                    if (imageURI == null) {
                        //sets the URI to the placeholder tree
                        Uri uri = (new Uri.Builder())
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(context.getResources().getResourcePackageName(R.drawable.tree_placeholder))
                                .appendPath(context.getResources().getResourceTypeName(R.drawable.tree_placeholder))
                                .appendPath(context.getResources().getResourceEntryName(R.drawable.tree_placeholder))
                                .build();

                        Toast.makeText(context, String.valueOf(uri), Toast.LENGTH_SHORT).show();

                        try {
                            setImageURI(uri);

                        } catch (Exception e) {
                            Log.e("set Image", "tried to set default image " + Arrays.toString(e.getStackTrace()));
                        }
                    }

                    Dialog addIconDialog = new Dialog(context);

                    int icon = 0;

                    addIconDialog.setContentView(R.layout.icon_select_view);

                    ImageButton dismissDialog = addIconDialog.findViewById(R.id.dismissDialog);
                    ImageView leavesIcon = addIconDialog.findViewById(R.id.leavesIcon);
                    ImageView bushIcon = addIconDialog.findViewById(R.id.bushIcon);
                    ImageView earIcon = addIconDialog.findViewById(R.id.earIcon);
                    ImageView treeIcon = addIconDialog.findViewById(R.id.treeIcon);
                    ImageView showIcon = addIconDialog.findViewById(R.id.showIcon);
                    TextView showName = addIconDialog.findViewById(R.id.showName);
                    CheckBox withoutPickingCheck = addIconDialog.findViewById(R.id.withoutPickingIconCheckbox);
                    Button cancelButton = addIconDialog.findViewById(R.id.cancelIconButton);
                    Button addIconButton = addIconDialog.findViewById(R.id.addIconButton);


                    showName.setText(herbName);
//                    Cancelling buttons
                    dismissDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addIconDialog.dismiss();
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addIconDialog.dismiss();
                        }
                    });
//                    What happens when you choose the leaves icon
                    leavesIcon.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForColorStateLists")
                        @Override
                        public void onClick(View view) {

                            if (leavesPicked) {
                                leavesPicked = false;
                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonUnclickable));
                                showIcon.setImageResource(0);
                                leavesIcon.setBackgroundResource(0);
                            } else {
                                leavesPicked = true;
                                bushPicked = false;
                                earPicked = false;
                                treePicked = false;
                                showIcon.setImageResource(iconList[0]);
                                leavesIcon.setBackgroundResource(R.drawable.border_for_widgets);

                                bushIcon.setBackgroundResource(0);
                                earIcon.setBackgroundResource(0);
                                treeIcon.setBackgroundResource(0);

                                withoutPickingCheck.setChecked(false);
                                continueWithoutIcon = false;

                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonClickable));
                            }

                        }
                    });
//                  What happens when you choose the bush icon
                    bushIcon.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForColorStateLists")
                        @Override
                        public void onClick(View view) {
                            if (bushPicked) {
                                bushPicked = false;
                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonUnclickable));
                                showIcon.setImageResource(0);
                                bushIcon.setBackgroundResource(0);
                            } else {
                                bushPicked = true;
                                leavesPicked = false;
                                earPicked = false;
                                treePicked = false;
                                showIcon.setImageResource(iconList[1]);
                                bushIcon.setBackgroundResource(R.drawable.border_for_widgets);

                                leavesIcon.setBackgroundResource(0);
                                earIcon.setBackgroundResource(0);
                                treeIcon.setBackgroundResource(0);

                                withoutPickingCheck.setChecked(false);
                                continueWithoutIcon = false;

                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonClickable));
                            }
                        }
                    });
//                    What happens when you choose the ear icon
                    earIcon.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForColorStateLists")
                        @Override
                        public void onClick(View view) {
                            if (earPicked) {
                                earPicked = false;
                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonUnclickable));
                                showIcon.setImageResource(0);
                                earIcon.setBackgroundResource(0);
                            } else {
                                earPicked = true;
                                leavesPicked = false;
                                bushPicked = false;
                                treePicked = false;
                                showIcon.setImageResource(iconList[2]);
                                earIcon.setBackgroundResource(R.drawable.border_for_widgets);

                                leavesIcon.setBackgroundResource(0);
                                bushIcon.setBackgroundResource(0);
                                treeIcon.setBackgroundResource(0);

                                withoutPickingCheck.setChecked(false);
                                continueWithoutIcon = false;

                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonClickable));
                            }
                        }
                    });
//                  What happens when you choose the tree icon
                    treeIcon.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForColorStateLists")
                        @Override
                        public void onClick(View view) {
                            if (treePicked) {
                                treePicked = false;
                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonUnclickable));
                                showIcon.setImageResource(0);
                                treeIcon.setBackgroundResource(0);
                            } else {
                                treePicked = true;
                                leavesPicked = false;
                                bushPicked = false;
                                earPicked = false;
                                showIcon.setImageResource(iconList[3]);
                                treeIcon.setBackgroundResource(R.drawable.border_for_widgets);

                                leavesIcon.setBackgroundResource(0);
                                bushIcon.setBackgroundResource(0);
                                earIcon.setBackgroundResource(0);

                                withoutPickingCheck.setChecked(false);
                                continueWithoutIcon = false;

                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonClickable));
                            }
                        }
                    });
//                  What happens when you don't choose an icon, but choose the "Continue without icon" checkbox
                    withoutPickingCheck.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForColorStateLists")
                        @Override
                        public void onClick(View view) {

                            if (withoutPickingCheck.isChecked()) {
                                continueWithoutIcon = true;

                                leavesPicked = false;
                                bushPicked = false;
                                earPicked = false;
                                treePicked = false;

                                showIcon.setImageResource(0);

                                leavesIcon.setBackgroundResource(0);
                                bushIcon.setBackgroundResource(0);
                                earIcon.setBackgroundResource(0);
                                treeIcon.setBackgroundResource(0);

                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonClickable));
                            } else {
                                continueWithoutIcon = false;
                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonUnclickable));
                            }

                        }
                    });

//                    Add item button and adding the icon to the subitem
                    addIconButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!continueWithoutIcon && !leavesPicked && !bushPicked && !earPicked && !treePicked) {
                                Toast.makeText(context, "You have to pick something to continue!", Toast.LENGTH_SHORT).show();

                            } else {
                                if (continueWithoutIcon) {
                                    subItem.setIcon(0);

                                } else if (leavesPicked) {
                                    subItem.setIcon(iconList[0]);

                                } else if (bushPicked) {
                                    subItem.setIcon(iconList[1]);

                                } else if (earPicked) {
                                    subItem.setIcon(iconList[2]);

                                } else {
                                    subItem.setIcon(iconList[3]);

                                }
                                //add the name of the Item
                                String herbId = databaseTools.getSubItemID(item);
                                subItem.setHerbId(herbId);

                                addIconDialog.dismiss();
                                AddItemDialog.this.dismiss();

                                ListLogic.addOne(subItem, index);
                                subItemAdapter.addSubItem();

                                databaseTools.addEditSubItem(item,subItem);

                            }
                        }
                    });

                    addIconDialog.show();
                }

            }
        });
    }


    //    Saving selected image and setting it to the imageView
    public void setImageURI(Uri pictureURI) {

        this.imageURI = pictureURI;

        insertImage.setImageURI(null);
        Toast.makeText(this.getContext(), imageURI.toString(), Toast.LENGTH_SHORT).show();

        Log.e("IMAGEURI",pictureURI.toString());

        insertImage.setImageURI(imageURI);

        subItem.setImageUri(imageURI.toString());


    }


}
