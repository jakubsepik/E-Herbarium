package sk.spse.oursoft.android.e_herbarium;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import herbariumListOperation.Item;
import herbariumListOperation.ItemAdapter;
import sk.spse.oursoft.android.e_herbarium.DatabaseTools;

import androidx.annotation.NonNull;

import herbariumListOperation.SubItem;
import herbariumListOperation.SubItemAdapter;

public class AddItemDialog extends Dialog {
    
    private String herbName;
    private String herbDescription;
    private Uri imageURI;

    private SubItem subItem = new SubItem();

    private ImageView insertImage;

    private final int[] iconList = {R.drawable.listocek_symbolik, R.drawable.kricek_symbolik, R.drawable.klasocek_symbolik, R.drawable.stromcek_symbolik};

    private boolean leavesPicked = false;
    private boolean bushPicked = false;
    private boolean earPicked = false;
    private boolean treePicked = false;
    private boolean continueWithoutIcon = false;

    
    public AddItemDialog(@NonNull Context context, int theme_Black_NoTitleBar_Fullscreen, SubItemAdapter subItemAdapter, Item item) {
        super(context, theme_Black_NoTitleBar_Fullscreen);

        DatabaseTools databaseTools = new DatabaseTools(this.getContext());

        HerbariumViewActivity.setCurrentDialog(this);

        this.setContentView(R.layout.add_subitem_view);

        insertImage = this.findViewById(R.id.insertImage);
        ImageButton dismissButton = this.findViewById(R.id.dismissButton);
        EditText herbNameInput = this.findViewById(R.id.itemNameInput);
        EditText itemDescriptionInput = this.findViewById(R.id.itemDescriptionInput);
        Button addItemButton = this.findViewById(R.id.addItemButton);


        insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ((Activity) context).startActivityForResult(intent, 1);
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

                if (herbName.length() == 0){
                    Toast.makeText(context, "Please input a valid herb name.", Toast.LENGTH_SHORT).show();
                }else{

                    subItem.setHerbName(herbName);
                    subItem.setHerbDescription(herbDescription);

                    if (imageURI != null){
                        subItem.setImageUri(imageURI);
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

                    leavesIcon.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForColorStateLists")
                        @Override
                        public void onClick(View view) {

                            if (leavesPicked){
                                leavesPicked = false;
                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonUnclickable));
                                showIcon.setImageResource(0);
                                leavesIcon.setBackgroundResource(0);
                            }else{
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

                    withoutPickingCheck.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForColorStateLists")
                        @Override
                        public void onClick(View view) {

                            if (withoutPickingCheck.isChecked()){
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
                            }else{
                                continueWithoutIcon = false;
                                addIconButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonUnclickable));
                            }

                        }
                    });

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

                                }else{
                                    subItem.setIcon(iconList[3]);

                                }
                                //add the name of the Item
                                subItem.setHerbId(databaseTools.getSubItemID(item));
                                addIconDialog.dismiss();
                                AddItemDialog.this.dismiss();
                                subItemAdapter.addSubItem(subItem);
                                databaseTools.addEditItem(item,subItem);
                                ListLogic.addOne(subItem, 1);
                            }
                        }
                    });

                    addIconDialog.show();
                }

            }
        });
    }

    public void onImageSelect(Uri imageURI){
        this.imageURI = imageURI;
        insertImage.setImageURI(imageURI);
    }

}
