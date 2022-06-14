package sk.spse.oursoft.android.e_herbarium.herbariumListOperation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sk.spse.oursoft.android.e_herbarium.EditItemDialog;
import sk.spse.oursoft.android.e_herbarium.ListLogic;
import sk.spse.oursoft.android.e_herbarium.R;

public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {

    private List<SubItem> subItemList;
    private String itemTitle;

    Context context;

    SubItemAdapter(List<SubItem> subItemList, String itemTitle) {
        this.subItemList = subItemList;
        this.itemTitle = itemTitle;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_subitem, viewGroup, false);
        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemViewHolder subItemViewHolder, int i) {
        SubItem subItem = subItemList.get(i);
        subItemViewHolder.textViewHerbName.setText(subItem.getHerbName());
        subItemViewHolder.imageViewHerbIcon.setImageResource(subItem.getIcon());

        subItemViewHolder.subLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog itemDialog = new Dialog(view.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                itemDialog.setContentView(R.layout.dialog_view);

                TextView name = (TextView) itemDialog.findViewById(R.id.name);
                name.setText(subItem.getHerbName());

                TextView description = (TextView) itemDialog.findViewById(R.id.description);
                description.setText(subItem.getHerbDescription());
                description.setMovementMethod(new ScrollingMovementMethod());

                ImageView image = (ImageView) itemDialog.findViewById(R.id.insertImage);

                if (subItem.getImageUri() == null){
                    image.setImageResource(subItem.getIcon());
                }else{
                    image.setImageURI(Uri.parse(subItem.getImageUri()));
                }

                ImageButton dismissButton = (ImageButton) itemDialog.findViewById(R.id.dismissButton);
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemDialog.dismiss();
                    }
                });

                Button editButton = (Button) itemDialog.findViewById(R.id.editButton);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditItemDialog editItemDialog = new EditItemDialog(view.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen, subItem, subItemList);

                        editItemDialog.show();

                        itemDialog.dismiss();
                    }
                });


                itemDialog.show();
            }
        });

        subItemViewHolder.removeSubItem.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                context = view.getContext();
                removeItemMethod(context, subItem, view, itemTitle);

            }
        });
    }

    class SubItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHerbName;
        ImageView imageViewHerbIcon;
        ImageButton removeSubItem;

        LinearLayout subLinearLayout;

        SubItemViewHolder(View itemView) {
            super(itemView);
            textViewHerbName = (TextView) itemView.findViewById(R.id.herbName);
            imageViewHerbIcon = (ImageView) itemView.findViewById(R.id.herbIcon);
            removeSubItem = (ImageButton) itemView.findViewById(R.id.removeSubitem);

            subLinearLayout = (LinearLayout) itemView.findViewById(R.id.subLinearLayout);
        }
    }


    public void removeItem(int pos, String itemTitle){
        ListLogic.deleteOne(pos, itemTitle);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }

    public void addSubItem(){
        notifyItemInserted(subItemList.size()-1);
    }

    public int findItemPosition(String herbName, List<SubItem> subItemList){
        for (int i = 0; i < subItemList.size(); i++){
            if (herbName.equals(subItemList.get(i).getHerbName())){
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return subItemList.size();
    }

    public List<SubItem> getSubItemList(){
        return subItemList;
    }

    public void removeItemMethod(Context context, SubItem subItem, View view, String itemTitle){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_MULTI_PROCESS);
        boolean showDialog = sharedPreferences.getBoolean("showSubitemDeletionDialog", true);

        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

        int pos = findItemPosition(subItem.getHerbName(), subItemList);

        if (pos == -1){

            Toast.makeText(view.getContext(), "This Item Doesn't Exist", Toast.LENGTH_SHORT).show();

        }else{

            if (showDialog){

                Dialog removeConfirmationDialog = new Dialog(view.getContext());
                removeConfirmationDialog.setContentView(R.layout.confirm_subitem_removal_dialog);

                ImageButton dismissDialogButton = (ImageButton) removeConfirmationDialog.findViewById(R.id.dismissDialogButton);

                TextView removeQuestion = (TextView) removeConfirmationDialog.findViewById(R.id.removeQuestion);
                removeQuestion.setText("Are you sure you want you want to \nremove " + subItem.getHerbName() + " ?");

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
                    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                    @Override
                    public void onClick(View view) {
                        boolean showDialog = !dontAskAgainRemoval.isChecked();
                        editor.putBoolean("showSubitemDeletionDialog", showDialog);
                        editor.apply();

                        removeConfirmationDialog.dismiss();

                        removeItem(pos, itemTitle);

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
                removeItem(pos, itemTitle);
            }

        }

    }
}
