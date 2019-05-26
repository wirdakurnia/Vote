package com.wirda61.wirdakurnia.vote.reviews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wirda61.wirdakurnia.vote.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewModel> listReview;

    public ReviewAdapter(List<ReviewModel> listReview) {
        this.listReview = listReview;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel model = listReview.get(position);
        holder.tvDescReview.setText(model.getReview());
        holder.tvNamaPasien.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return listReview.size();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_nama_pasien)
        TextView tvNamaPasien;
        @BindView(R.id.tv_desc_review)
        TextView tvDescReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
