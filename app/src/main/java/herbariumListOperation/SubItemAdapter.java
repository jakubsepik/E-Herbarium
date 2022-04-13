package herbariumListOperation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sk.spse.oursoft.android.e_herbarium.R;

public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {

    private List<SubItem> subItemList;

    SubItemAdapter(List<SubItem> subItemList) {
        this.subItemList = subItemList;
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
                Toast.makeText(view.getContext(), "You clicked " + subItem.getHerbName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subItemList.size();
    }

    class SubItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHerbName;
        ImageView imageViewHerbIcon;

        LinearLayout subLinearLayout;

        SubItemViewHolder(View itemView) {
            super(itemView);
            textViewHerbName = (TextView) itemView.findViewById(R.id.herbName);
            imageViewHerbIcon = (ImageView) itemView.findViewById(R.id.herbIcon);

            subLinearLayout = (LinearLayout) itemView.findViewById(R.id.subLinearLayout);
        }
    }
}