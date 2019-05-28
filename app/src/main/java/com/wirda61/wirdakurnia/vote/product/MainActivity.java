package com.wirda61.wirdakurnia.vote.product;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.wirda61.wirdakurnia.vote.LoginActivity;
import com.wirda61.wirdakurnia.vote.R;
import com.wirda61.wirdakurnia.vote.reviews.IdolModel;
import com.wirda61.wirdakurnia.vote.reviews.ReviewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements IdolAdapter.OnClickListenerAdapter {

    @BindView(R.id.rv_idol)
    RecyclerView rvIdol;
    private IdolAdapter adapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog ;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        ButterKnife.bind(this);

        initView();
        getIdol();
    }

    private void initView(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Load data idol ...");
        progressDialog.show();
    }

    private void getIdol(){
        CollectionReference collectionReference = firebaseFirestore.collection("idol");
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<IdolModel> list = new ArrayList<>();
                            for(DocumentSnapshot document : queryDocumentSnapshots){
                                IdolModel idolModel = document.toObject(IdolModel.class);
                                list.add(idolModel);
                            }
                            initListIdol(list);
                        }else{
                            Toast.makeText(MainActivity.this, "List is empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void initListIdol(List<IdolModel> idolModelList){
        adapter = new IdolAdapter(idolModelList);
        rvIdol.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListenerAdapter(this);
        rvIdol.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getIdol();
    }

    @Override
    public void onItemClicked(String idol) {
        ReviewActivity.start(this, idol);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.sign_out_menu:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                signOut();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }



}
