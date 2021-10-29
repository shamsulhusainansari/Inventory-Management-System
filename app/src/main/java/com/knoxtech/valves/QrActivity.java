package com.knoxtech.valves;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.knoxtech.valves.front.FrontAdapter;
import com.knoxtech.valves.front.Note;

public class QrActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("QRs");
    private FrontAdapter front_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("bar_number", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        front_adapter = new FrontAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.qr_show_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(front_adapter);
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
}