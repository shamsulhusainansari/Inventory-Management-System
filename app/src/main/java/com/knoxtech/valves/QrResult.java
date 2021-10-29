package com.knoxtech.valves;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.knoxtech.valves.front.FrontAdapter;
import com.knoxtech.valves.front.Note;
import com.knoxtech.valves.qr_adapter.QrAdapter;

import java.util.Base64;

public class QrResult extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private QrAdapter qr_adapter;
    MaterialToolbar toolbar;
    String docId, QR_CODE;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_result);
        Bundle bundle = getIntent().getExtras();
        docId= bundle.getString("docId");
        toolbar = findViewById(R.id.topAppBarQr);
        DocumentReference docRef = db.collection("QRs").document(docId);

//
        Source source = Source.CACHE;

//  CA1635435619
        docRef.get(source).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                toolbar.setTitle(document.getString("name_of_material"));
                QR_CODE = document.getString("bar_url");
                Log.d("QrResult", "Cached document data: " + document.getData());
            } else {
                Log.d("QrResult: ", "Cached get failed: ", task.getException());
            }
        });


        setUpRecyclerView();
        findViewById(R.id.QR_Share).setOnClickListener(v -> {
            byte[] bytes= Base64.getDecoder().decode(QR_CODE);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            String bitmapPath = MediaStore.Images.Media.insertImage(v.getContext().getContentResolver(), bitmap,"", null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            sendIntent.setType("image/png");
            v.getContext().startActivity(sendIntent);
        });
    }

    private void setUpRecyclerView() {
        Log.e("DOC_ID",docId);
        CollectionReference notebookRef = db.collection("QRs").document(docId).collection("files");
        Query query = notebookRef.orderBy("bar_number", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        qr_adapter = new QrAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.qr_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(qr_adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        qr_adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        qr_adapter.stopListening();
    }
}