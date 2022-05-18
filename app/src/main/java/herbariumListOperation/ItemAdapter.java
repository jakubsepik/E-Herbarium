package herbariumListOperation;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;

import sk.spse.oursoft.android.e_herbarium.AddItemDialog;
import sk.spse.oursoft.android.e_herbarium.HerbariumViewActivity;
import sk.spse.oursoft.android.e_herbarium.R;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<Item> itemList;

    public static final int PICK_IMAGE = 1;

    Context context;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        Item item = itemList.get(i);
        itemViewHolder.tvItemTitle.setText(item.getItemTitle());

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                itemViewHolder.rvSubItem.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(item.getSubItemList().size());

        // Create sub item view adapter
        SubItemAdapter subItemAdapter = new SubItemAdapter(item.getSubItemList());

        itemViewHolder.rvSubItem.setLayoutManager(layoutManager);
        itemViewHolder.rvSubItem.setAdapter(subItemAdapter);
        itemViewHolder.rvSubItem.setRecycledViewPool(viewPool);

        itemViewHolder.optionsMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = view.getContext();
                showBottomSheetDialog(context, item, subItemAdapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemTitle;
        private RecyclerView rvSubItem;
        private ImageButton optionsMenuButton;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvItemTitle = (TextView) itemView.findViewById(R.id.tv_item_title);
            rvSubItem = (RecyclerView) itemView.findViewById(R.id.subRecyclerView);
            optionsMenuButton = (ImageButton) itemView.findViewById(R.id.optionsMenuButton);

        }
    }

    private void showBottomSheetDialog(Context context, Item item, SubItemAdapter subItemAdapter){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.options_menu_view);

        bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        ConstraintLayout addSubitem = bottomSheetDialog.findViewById(R.id.addSubitem);
        ConstraintLayout editGroup = bottomSheetDialog.findViewById(R.id.editGroup);
        ConstraintLayout deleteGroup = bottomSheetDialog.findViewById(R.id.deleteGroup);


        Objects.requireNonNull(addSubitem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();

                AddItemDialog newItemDialog = new AddItemDialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen, subItemAdapter, item, findItemPosition(item.getItemTitle(), itemList));

                newItemDialog.show();
            }
        });


        Objects.requireNonNull(editGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Edit item clicked", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });


        Objects.requireNonNull(deleteGroup).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

                SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_MULTI_PROCESS);
                boolean showDialog = sharedPreferences.getBoolean("showItemDeletionDialog", true);

                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

                int pos = findItemPosition(item.getItemTitle(), itemList);

                if (pos == -1){

                    Toast.makeText(view.getContext(), "This Item Doesn't Exist", Toast.LENGTH_SHORT).show();

                }else{

                    if (showDialog){

                        Dialog removeConfirmationDialog = new Dialog(view.getContext());
                        removeConfirmationDialog.setContentView(R.layout.confirm_subitem_removal_dialog);

                        ImageButton dismissDialogButton = (ImageButton) removeConfirmationDialog.findViewById(R.id.dismissDialogButton);

                        TextView removeQuestion = (TextView) removeConfirmationDialog.findViewById(R.id.removeQuestion);
                        removeQuestion.setText("Are you sure you want you want to \nremove " + item.getItemTitle() + " ?");

                        Button confirmRemovalButton = (Button) removeConfirmationDialog.findViewById(R.id.confirmRemovalButton);
                        Button cancelRemovalButton = (Button) removeConfirmationDialog.findViewById(R.id.cancelRemovalButton);

                        CheckBox dontAskAgainRemoval = (CheckBox) removeConfirmationDialog.findViewById(R.id.dontAskAgainRemoval);


                        dismissDialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeConfirmationDialog.dismiss();
                            }
                        });


                        confirmRemovalButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean showDialog = !dontAskAgainRemoval.isChecked();
                                editor.putBoolean("showItemDeletionDialog", showDialog);
                                editor.apply();

                                removeConfirmationDialog.dismiss();

                                removeItem(pos);

                            }
                        });

                        cancelRemovalButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeConfirmationDialog.dismiss();
                            }
                        });

                        removeConfirmationDialog.show();

                    }else{
                        removeItem(pos);
                    }

                }
            }
        });

        bottomSheetDialog.show();
    }



    public void removeItem(int pos){
        itemList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }


    public int findItemPosition(String itemTitle, List<Item> itemList){

        for (int i = 0; i < itemList.size(); i++){
            if (itemTitle.equals(itemList.get(i).getItemTitle())){
                return i;
            }
        }
        return -1;
    }


}