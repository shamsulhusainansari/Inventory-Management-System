package com.knoxtech.valves;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.knoxtech.valves.front.Note;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class AddValves extends AppCompatActivity {

    private TextInputEditText valveName, barIds;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String timeStamp;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_valves);
        PDFBoxResourceLoader.init(getApplicationContext());
        valveName = findViewById(R.id.editValveName);
        barIds = findViewById(R.id.edit_bar_id_bottom_sheet);
        progressBar = findViewById(R.id.progress);
        timeStamp = "QR"+TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        findViewById(R.id.btnGenerate).setOnClickListener(v -> {
            List<String> items = Arrays.asList(Objects.requireNonNull(Objects.requireNonNull(barIds.getText()).toString().split("\\s*,\\s*")));
            progressBar.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(valveName.getText()) || TextUtils.isEmpty(barIds.getText())){
                Toast.makeText(AddValves.this, "Please enter details", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }else {
                db.collection("Valves")
                        .whereIn("bar_number", items)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                Note note = queryDocumentSnapshot.toObject(Note.class);
                                note.setDocId(queryDocumentSnapshot.getId());
                                String documentId = note.getPdf_url();
                                String bar_number = note.getBar_number();
                                Map<String, Object> data2 = new HashMap<>();
                                data2.put("pdf_url", documentId);
                                data2.put("bar_number",bar_number);
                                db.collection("QRs").document(timeStamp).collection("files").add(data2)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                            Log.e("files", "Write success");
                                            progressBar.setVisibility(View.GONE);
                                        });
                            }
                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            try {
                                progressBar.setVisibility(View.VISIBLE);
                                BitMatrix bitMatrix = multiFormatWriter.encode(timeStamp, BarcodeFormat.QR_CODE,512,512);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                byte[] byteArray = byteArrayOutputStream .toByteArray();
                                String encodeImage = Base64.getEncoder().encodeToString(byteArray);
                                Map<String, Object> data3 = new HashMap<>();
                                data3.put("bar_number", timeStamp);
                                data3.put("name_of_material", Objects.requireNonNull(valveName.getText()).toString());
                                data3.put("bar_url", encodeImage);
                                db.collection("QRs").document(timeStamp).set(data3);
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(AddValves.this,HomeActivity.class));

                            } catch (WriterException e) {
                                Toast.makeText(v.getContext(), ""+e, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });


    }
}