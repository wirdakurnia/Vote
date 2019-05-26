package com.wirda61.wirdakurnia.vote.reviews;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.wirda61.wirdakurnia.vote.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewActivity extends AppCompatActivity {

    public static String EXTRA_PRODUCT_MODEL = "EXTRA_PRODUCT_MODEL";

    @BindView(R.id.tv_total_number_rating)
    TextView tvTotalNumberRating;
    @BindView(R.id.total_star_rating)
    MaterialRatingBar totalStarRating;
    @BindView(R.id.tv_total_pemberi_bintang)
    TextView tvTotalPemberiBintang;
    @BindView(R.id.ll_percentage_5)
    LinearLayout llPercentage5;
    @BindView(R.id.constrain_layout_5)
    ConstraintLayout constrainLayout5;
    @BindView(R.id.ll_percentage_4)
    LinearLayout llPercentage4;
    @BindView(R.id.constrain_layout_4)
    ConstraintLayout constrainLayout4;
    @BindView(R.id.ll_percentage_3)
    LinearLayout llPercentage3;
    @BindView(R.id.constrain_layout_3)
    ConstraintLayout constrainLayout3;
    @BindView(R.id.ll_percentage_2)
    LinearLayout llPercentage2;
    @BindView(R.id.constrain_layout_2)
    ConstraintLayout constrainLayout2;
    @BindView(R.id.ll_percentage_1)
    LinearLayout llPercentage1;
    @BindView(R.id.constrain_layout_1)
    ConstraintLayout constrainLayout1;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.rv_review)
    RecyclerView rvReview;

    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private IdolModel idolModelGlobal;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ReviewAdapter adapter;
    private Button btn_rate;

    public static void start(Context context, String idolModel) {
        Intent starter = new Intent(context, ReviewActivity.class);
        starter.putExtra(EXTRA_PRODUCT_MODEL, idolModel);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ButterKnife.bind(this);
        initView();

        btn_rate = findViewById(R.id.btn_rate);
        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogReview();
            }
        });
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        idolModelGlobal = new Gson().fromJson(getIntent().getStringExtra(EXTRA_PRODUCT_MODEL), IdolModel.class);

        //this method used to get the width of view
        progressDialog.setMessage("Count Width Of View");
        progressDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                setRatingByColor(idolModelGlobal);
                getAllReview(idolModelGlobal.getIdIdol());
            }
        }, 3000);
    }

    /**
     * Insert data review to collection of idol
     * @param review
     */
    private void insertDataReview(final ReviewModel review) {
        ReviewModel reviewModel = new ReviewModel(review.getName(), review.getReview(), review.getTimeStamp(), review.getTotalStarGiven());
        CollectionReference collectionReference = firebaseFirestore.collection("idol");
        DocumentReference documentReference = collectionReference.document(idolModelGlobal.getIdIdol());
        documentReference.collection("review")
                .add(reviewModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference1) {
                        progressDialog.dismiss();
                        //after success, then update the rating in idol
                        ReviewActivity.this.updateRating(review, idolModelGlobal);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ReviewActivity.this, "Failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * this method used to update rating of idol
     *
     * @param reviewModel
     * @param idolModel
     */
    private void updateRating(ReviewModel reviewModel, final IdolModel idolModel) {
        final IdolModel rate = new IdolModel();
        rate.setIdIdol(idolModel.getIdIdol());
        rate.setNameIdol(idolModel.getNameIdol());

        //update stars
        double totalStars;
        int totalVoters = 0;
        if (reviewModel.getTotalStarGiven() == 1.0) {
            totalStars = 1.0 + (double) idolModel.getStar1();
            rate.setStar1((int) totalStars);
            rate.setStar2(idolModel.getStar2());
            rate.setStar3(idolModel.getStar3());
            rate.setStar4(idolModel.getStar4());
            rate.setStar5(idolModel.getStar5());

            totalVoters = (int) (totalStars + idolModel.getStar2() + idolModel.getStar3() + idolModel.getStar4() + idolModel.getStar5());
            if (idolModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 2.0) {
            totalStars = 1.0 + (double) idolModel.getStar2();
            rate.setStar1(idolModel.getStar1());
            rate.setStar2((int) totalStars);
            rate.setStar3(idolModel.getStar3());
            rate.setStar4(idolModel.getStar4());
            rate.setStar5(idolModel.getStar5());

            totalVoters = (int) (totalStars + idolModel.getStar1() + idolModel.getStar3() + idolModel.getStar4() + idolModel.getStar5());
            if (idolModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 3.0) {
            totalStars = 1.0 + (double) idolModel.getStar3();
            rate.setStar1(idolModel.getStar1());
            rate.setStar2(idolModel.getStar2());
            rate.setStar3((int) totalStars);
            rate.setStar4(idolModel.getStar4());
            rate.setStar5(idolModel.getStar5());

            totalVoters = (int) (totalStars + idolModel.getStar1() + idolModel.getStar2() + idolModel.getStar4() + idolModel.getStar5());
            if (idolModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 4.0) {
            totalStars = 1.0 + (double) idolModel.getStar4();
            rate.setStar1(idolModel.getStar1());
            rate.setStar2(idolModel.getStar2());
            rate.setStar3(idolModel.getStar3());
            rate.setStar4((int) totalStars);
            rate.setStar5(idolModel.getStar5());

            totalVoters = (int) (totalStars + idolModel.getStar1() + idolModel.getStar2() + idolModel.getStar3() + idolModel.getStar5());
            if (idolModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 5.0) {
            totalStars = 1.0 + (double) idolModel.getStar5();
            rate.setStar1(idolModel.getStar1());
            rate.setStar2(idolModel.getStar2());
            rate.setStar3(idolModel.getStar3());
            rate.setStar4(idolModel.getStar4());
            rate.setStar5((int) totalStars);

            totalVoters = (int) (totalStars + idolModel.getStar1() + idolModel.getStar2() + idolModel.getStar3() + idolModel.getStar4());
            if (idolModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        }

        //update rate
        int totalStar1 = rate.getStar1() * 1;
        int totalStar2 = rate.getStar2() * 2;
        int totalStar3 = rate.getStar3() * 3;
        int totalStar4 = rate.getStar4() * 4;
        int totalStar5 = rate.getStar5() * 5;

        double sumOfStars = totalStar1 + totalStar2 + totalStar3 + totalStar4 + totalStar5;
        double totalRating = sumOfStars / (double) totalVoters;
        DecimalFormat format = new DecimalFormat(".#");
        rate.setTotalRating(Double.parseDouble(String.valueOf(totalRating)));

        CollectionReference collectionReference = firebaseFirestore.collection("idol");
        collectionReference.document(idolModel.getIdIdol())
                .set(rate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(ReviewActivity.this, "Successfully update Rating", Toast.LENGTH_SHORT).show();
                        idolModelGlobal = rate;
                        ReviewActivity.this.setRatingByColor(rate);
                        ReviewActivity.this.getAllReview(idolModel.getIdIdol());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ReviewActivity.this, "Failed Update Rating : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This method used to display rating by colors
     *
     * @param idolModel
     */
    private void setRatingByColor(IdolModel idolModel) {
        int widthView = constrainLayout1.getWidth();
        int totalAllVoters = idolModel.getTotalVoters();
        int totalRateStar1 = idolModel.getStar1();
        int totalRateStar2 = idolModel.getStar2();
        int totalRateStar3 = idolModel.getStar3();
        int totalRateStar4 = idolModel.getStar4();
        int totalRateStar5 = idolModel.getStar5();

        //convert to double
        double votersInDouble = (double) totalAllVoters;


        //RATING STAR 1
        double star1 = (double) totalRateStar1;
        double sum1 = (star1 / votersInDouble);
        int rating1 = (int) (sum1 * widthView);
        ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(rating1, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.setMargins(0, 5, 0, 5);
        llPercentage1.setBackgroundColor(Color.parseColor("#ff6f31"));
        llPercentage1.setLayoutParams(layoutParams1);

        //RATING STAR 2
        double star2 = (double) totalRateStar2;
        double sum2 = (star2 / votersInDouble);
        int rating2 = (int) (sum2 * widthView);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(rating2, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams2.setMargins(0, 5, 0, 5);
        llPercentage2.setBackgroundColor(Color.parseColor("#ff9f02"));
        llPercentage2.setLayoutParams(layoutParams2);

        //RATING STAR 3
        double star3 = (double) totalRateStar3;
        double sum3 = (star3 / votersInDouble);
        int rating3 = (int) (sum3 * widthView);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(rating3, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams3.setMargins(0, 5, 0, 5);
        llPercentage3.setBackgroundColor(Color.parseColor("#ffcf02"));
        llPercentage3.setLayoutParams(layoutParams3);

        //RATING STAR 4
        double star4 = (double) totalRateStar4;
        double sum4 = (star4 / votersInDouble);
        int rating4 = (int) (sum4 * widthView);
        ConstraintLayout.LayoutParams layoutParams4 = new ConstraintLayout.LayoutParams(rating4, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams4.setMargins(0, 5, 0, 5);
        llPercentage4.setBackgroundColor(Color.parseColor("#9ace6a"));
        llPercentage4.setLayoutParams(layoutParams4);

        //RATING STAR 5
        double star5 = (double) totalRateStar5;
        double sum5 = (star5 / votersInDouble);
        int rating5 = (int) (sum5 * widthView);
        ConstraintLayout.LayoutParams layoutParams5 = new ConstraintLayout.LayoutParams(rating5, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams5.setMargins(0, 5, 0, 5);
        llPercentage5.setBackgroundColor(Color.parseColor("#57bb8a"));
        llPercentage5.setLayoutParams(layoutParams5);

        // menampilkan rating berdasarkan angka
        int totalBintangSatu = totalRateStar1 * 1;
        int totalBintangDua = totalRateStar2 * 2;
        int totalBintangTiga = totalRateStar3 * 3;
        int totalBintangEmpat = totalRateStar4 * 5;
        int totalBintangLima = totalRateStar5 * 5;

        double sumBintang = totalBintangSatu +
                totalBintangDua +
                totalBintangTiga +
                totalBintangEmpat +
                totalBintangLima;

        double rating = (sumBintang / votersInDouble);
        DecimalFormat format = new DecimalFormat(".#");

        tvTotalNumberRating.setText(String.valueOf(format.format(rating)));

        totalStarRating.setRating(Float.parseFloat(String.valueOf(rating)));
        tvTotalPemberiBintang.setText(String.valueOf(totalAllVoters) + " total");


    }

    /**
     * this method used to open dialog input review
     */
    private void openDialogReview() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review);

        final EditText etReview = dialog.findViewById(R.id.et_review);
        final EditText etName = dialog.findViewById(R.id.et_name);
        final MaterialRatingBar rate = dialog.findViewById(R.id.rate_star);
        Button btnKirimUlasan = dialog.findViewById(R.id.btn_send_review);

        btnKirimUlasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (TextUtils.isEmpty(etReview.getText().toString())) {
                    etReview.setError("Required field");
                } else {
                    progressDialog.setMessage("Please wait ...");
                    progressDialog.show();

                    ReviewModel reviewModel = new ReviewModel();
                    reviewModel.setName(etName.getText().toString());
                    reviewModel.setReview(etReview.getText().toString());
                    reviewModel.setTotalStarGiven(Math.round(rate.getRating()));
                    ReviewActivity.this.insertDataReview(reviewModel);
                }
            }
        });

        dialog.show();
    }

    /**
     * the method used to get all reviews in firebase firestore
     * @param idIdol
     */
    private void getAllReview(String idIdol) {
        progressBar.setVisibility(View.VISIBLE);
        rvReview.setVisibility(View.GONE);
        CollectionReference collectionReference = firebaseFirestore.collection("idol");
        DocumentReference documentReference = collectionReference.document(idIdol);
        documentReference.collection("review")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        rvReview.setVisibility(View.VISIBLE);
                        if (task.getResult().isEmpty()) {
                        } else if (task.isSuccessful()) {
                            List<ReviewModel> listReview = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                ReviewModel reviewModel = new ReviewModel();
                                try {
                                    reviewModel.setName(documentSnapshot.get("name").toString());
                                    reviewModel.setReview(documentSnapshot.get("review").toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                listReview.add(reviewModel);
                                ReviewActivity.this.initListReview(listReview);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    //this method used to populating the recyclerview with data of reviews
    private void initListReview(List<ReviewModel> reviewModels) {
        adapter = new ReviewAdapter(reviewModels);
        rvReview.setLayoutManager(new LinearLayoutManager(this));
        rvReview.setAdapter(adapter);
    }
}
