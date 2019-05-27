package com.wirda61.wirdakurnia.vote.product;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wirda61.wirdakurnia.vote.R;
import com.wirda61.wirdakurnia.vote.reviews.IdolModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class IdolAdapter extends RecyclerView.Adapter<IdolAdapter.IdolHolder> {
    public interface OnClickListenerAdapter {
        void onItemClicked(String idol);
    }

    private IdolAdapter.OnClickListenerAdapter onClickListenerAdapter;
    private List<IdolModel> listIdolModel;


    public IdolAdapter(List<IdolModel> listIdolModel) {
        this.listIdolModel = listIdolModel;
    }

    @NonNull
    @Override
    public IdolAdapter.IdolHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_idol, parent, false);
        return new IdolAdapter.IdolHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IdolAdapter.IdolHolder holder, int position) {
        final IdolModel model = listIdolModel.get(position);

        holder.totalStarRating.setRating(Float.parseFloat(String.valueOf(model.getTotalRating())));
        holder.tvIdolName.setText(model.getNameIdol());
        holder.tvAgensi.setText(model.getAgensi());
        holder.cvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListenerAdapter != null) {
                    onClickListenerAdapter.onItemClicked(new Gson().toJson(model));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listIdolModel.size();
    }

    public void setOnClickListenerAdapter(IdolAdapter.OnClickListenerAdapter onClickListenerAdapter) {
        this.onClickListenerAdapter = onClickListenerAdapter;
    }

    public class IdolHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_idol_name)
        TextView tvIdolName;
        @BindView(R.id.tv_agensi)
        TextView tvAgensi;
        @BindView(R.id.total_star_rating)
        MaterialRatingBar totalStarRating;
        @BindView(R.id.cv_item)
        CardView cvItem;

        public IdolHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
