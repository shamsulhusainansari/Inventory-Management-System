package com.knoxtech.valves.qr_adapter;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.knoxtech.valves.R;
import com.knoxtech.valves.front.Note;

public class QrAdapter extends FirestoreRecyclerAdapter<Note, QrAdapter.QrHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public QrAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull QrHolder holder, int position, @NonNull Note model) {

        String url = model.getPdf_url();
        holder.bar_name.setText(model.getBar_number());
        holder.itemView.setOnClickListener(v -> {
            if (TextUtils.isEmpty(url) || url.startsWith("url")){
                Toast.makeText(v.getContext(), "Ignore this view", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url),"application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public QrHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_pdf,
                    parent, false);

        return new QrHolder(v);

    }


    static class QrHolder extends RecyclerView.ViewHolder {

        ImageView imgPdf;
        TextView bar_name;
        public QrHolder(View itemView) {
            super(itemView);
            imgPdf = itemView.findViewById(R.id.Qr_pdf);
            bar_name = itemView.findViewById(R.id.bar_Name);
        }
    }
}