package com.knoxtech.valves;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.knoxtech.valves.back.BackAdapter;
import com.knoxtech.valves.back.backData;
import com.knoxtech.valves.front.FrontAdapter;
import com.knoxtech.valves.front.Note;

import java.util.Base64;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    private RelativeLayout frontLayout;
    private LinearLayout backLayout;
    private RelativeLayout.LayoutParams lp;
    boolean showBack = false;
    RecyclerView recyclerView, front_recycler;
    private FrontAdapter front_adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Valves");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.topAppBar);
        frontLayout = findViewById(R.id.front_layout);
        backLayout =findViewById(R.id.back_layout);
        front_recycler = findViewById(R.id.front_recyclerview);
        backData[] myListData = new backData[]{
                new backData("Auma Actuator"),
                new backData("Casting"),
                new backData("Forged Bar"),
                new backData("Positioner"),
                new backData("Rolled Bar"),
                new backData("SD Tork Actuator"),
        };

        recyclerView = (RecyclerView) findViewById(R.id.back_layout_recycler);
        BackAdapter adapter = new BackAdapter(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        toolbar.setNavigationOnClickListener(v -> dropLayout());

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.upload) {
                startActivity(new Intent(HomeActivity.this,UploadDocuments.class));
            }
            if (item.getItemId() == R.id.qrs) {
                startActivity(new Intent(HomeActivity.this,QrActivity.class));
            }
            return false;
        });

        setUpRecyclerView();

        findViewById(R.id.scanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
                intentIntegrator.setPrompt("Scan QR Easy");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(BarScanner.class);
                intentIntegrator.initiateScan();
            }
        });

        findViewById(R.id.addValves).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,AddValves.class));
            }
        });



    }

    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("bar_number", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        front_adapter = new FrontAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.front_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(front_adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);
        if (intentResult.getContents()!=null){

            String add_of_bar = intentResult.getContents();
            String first = add_of_bar.substring(0,2);

            final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.activity_more_details);
            LinearLayout linearLayout = dialog.findViewById(R.id.linear2);
            TextView title_name, size_no, cls_display,toc,moc,third, barcode_num;
            title_name = dialog.findViewById(R.id.heading_cast_more);
            size_no = dialog.findViewById(R.id.size_no_display);
            cls_display = dialog.findViewById(R.id.class_display);
            toc = dialog.findViewById(R.id.toc_displaya);
            moc = dialog.findViewById(R.id.moc_display);
            third = dialog.findViewById(R.id.third_textview);
            barcode_num = dialog.findViewById(R.id.barcode_number_display);
            ImageView barImage = dialog.findViewById(R.id.barcode_display);

            dialog.findViewById(R.id.more_cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            //dialog.show();
            if (first.equals("AA")){
                //Auma Actuator
                toc.setVisibility(View.INVISIBLE);

                db.collection("Valves")
                        .whereEqualTo("bar_number", intentResult.getContents())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                        byte[] bytes= Base64.getDecoder().decode(document.getString("bar_url"));
                                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        barImage.setImageBitmap(bitmap);
                                        title_name.setText(document.getString("name_of_material"));
                                        moc.setText(getString(R.string.work_number_display).concat(Objects.requireNonNull(document.getString("work_num"))));
                                        barcode_num.setText(document.getString("bar_number"));
                                        dialog.show();
                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }if (first.equals("CA")){
                //Casting
                linearLayout.setVisibility(View.VISIBLE);


                db.collection("Valves")
                        .whereEqualTo("bar_number", intentResult.getContents())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                        byte[] bytes= Base64.getDecoder().decode(document.getString("bar_url"));
                                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        barImage.setImageBitmap(bitmap);
                                        title_name.setText(document.getString("name_of_material"));

                                        size_no.setText(getString(R.string.size_num_display).concat(Objects.requireNonNull(document.getString("size"))));
                                        cls_display.setText(getString(R.string.cls_display).concat(Objects.requireNonNull(document.getString("cls_casting"))));
                                        toc.setText(getString(R.string.toc_display).concat(Objects.requireNonNull(document.getString("type"))));
                                        moc.setText(getString(R.string.moc_display).concat(Objects.requireNonNull(document.getString("material_of_const"))));

                                        barcode_num.setText(document.getString("bar_number"));
                                        dialog.show();
                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.show();
            }if (first.equals("FB")){
                //Forged Bar
                third.setVisibility(View.VISIBLE);
                db.collection("Valves")
                        .whereEqualTo("bar_number", intentResult.getContents())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                        byte[] bytes= Base64.getDecoder().decode(document.getString("bar_url"));
                                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        barImage.setImageBitmap(bitmap);
                                        title_name.setText(document.getString("name_of_material"));

                                        toc.setText(getString(R.string.dim).concat(Objects.requireNonNull(document.getString("outer_dim"))));
                                        moc.setText(getString(R.string.barId).concat(Objects.requireNonNull(document.getString("bar_id"))));
                                        third.setText(getString(R.string.moc_display).concat(Objects.requireNonNull(document.getString("material_of_const"))));

                                        barcode_num.setText(document.getString("bar_number"));
                                        dialog.show();
                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.show();
            }if (first.equals("PO")){
                third.setVisibility(View.VISIBLE);
                db.collection("Valves")
                        .whereEqualTo("bar_number", intentResult.getContents())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                        byte[] bytes= Base64.getDecoder().decode(document.getString("bar_url"));
                                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        barImage.setImageBitmap(bitmap);
                                        title_name.setText(document.getString("name_of_material"));

                                        toc.setText(getString(R.string.model_display).concat(Objects.requireNonNull(document.getString("model"))));
                                        moc.setText(getString(R.string.make_display).concat(Objects.requireNonNull(document.getString("make"))));
                                        third.setText(getString(R.string.invoice_display).concat(Objects.requireNonNull(document.getString("invoice"))));

                                        barcode_num.setText(document.getString("bar_number"));
                                        dialog.show();
                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                dialog.show();
            }if (first.equals("RB")){
                third.setVisibility(View.VISIBLE);
                db.collection("Valves")
                        .whereEqualTo("bar_number", intentResult.getContents())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                        byte[] bytes= Base64.getDecoder().decode(document.getString("bar_url"));
                                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        barImage.setImageBitmap(bitmap);
                                        title_name.setText(document.getString("name_of_material"));

                                        toc.setText(getString(R.string.dim).concat(Objects.requireNonNull(document.getString("outer_dim"))));
                                        moc.setText(getString(R.string.barId).concat(Objects.requireNonNull(document.getString("bar_id"))));
                                        third.setText(getString(R.string.moc_display).concat(Objects.requireNonNull(document.getString("material_of_const"))));

                                        barcode_num.setText(document.getString("bar_number"));
                                        dialog.show();
                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.show();
            }if (first.equals("SA")){
                toc.setVisibility(View.INVISIBLE);

                db.collection("Valves")
                        .whereEqualTo("bar_number", intentResult.getContents())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                        byte[] bytes= Base64.getDecoder().decode(document.getString("bar_url"));
                                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        barImage.setImageBitmap(bitmap);
                                        title_name.setText(document.getString("name_of_material"));

                                        moc.setText(getString(R.string.serial).concat(Objects.requireNonNull(document.getString("serial_num"))));
                                        barcode_num.setText(document.getString("bar_number"));
                                        dialog.show();
                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                dialog.show();
            }
            if (first.equals("QR")){
                Intent intent = new Intent(this,QrResult.class);
                Bundle bundle = new Bundle();
                bundle.putString("docId", add_of_bar);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        }else {
            Toast.makeText(HomeActivity.this, "OOPS... You did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        front_adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        front_adapter.stopListening();
    }

    private void dropLayout(){
        showBack = !showBack;
        toolbar.setNavigationIcon(ContextCompat.getDrawable(HomeActivity.this,showBack ? R.drawable.white_close_24 : R.drawable.ic_baseline_menu_24));

        lp=(RelativeLayout.LayoutParams) frontLayout.getLayoutParams();
        if (showBack){
            recyclerView.setVisibility(View.VISIBLE);
            ValueAnimator var = ValueAnimator.ofInt(backLayout.getHeight());
            var.setDuration(100);
            var.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    lp.setMargins(0,(Integer) animation.getAnimatedValue(),0,0);
                    frontLayout.setLayoutParams(lp);
                }
            });
            var.start();
        }else {
            recyclerView.setVisibility(View.GONE);
            lp.setMargins(0,0,0,0);
            frontLayout.setLayoutParams(lp);
            TranslateAnimation anim = new TranslateAnimation(
                    0,0,backLayout.getHeight(),0
            );
            anim.setStartOffset(0);
            anim.setDuration(200);
            frontLayout.setAnimation(anim);
        }
    }



}