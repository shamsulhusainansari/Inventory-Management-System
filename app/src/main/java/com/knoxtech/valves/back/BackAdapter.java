package com.knoxtech.valves.back;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.knoxtech.valves.R;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BackAdapter extends RecyclerView.Adapter<BackAdapter.ViewHolder>{
    private final backData[] listdata;

    private String timeStamp;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String encrypt_bar;

    public BackAdapter(backData[] listdata) {
        this.listdata = listdata;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.back_recycler_shape, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull BackAdapter.ViewHolder holder, int position) {


        int pos = position;
        final backData myListData = listdata[position];
        holder.textView.setText(listdata[position].getMaterial_name());
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(view.getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.custom_dialog);
                TextView heading = dialog.findViewById(R.id.heading_cast);
                TextInputLayout txtInpHeatNo,txtInpSize,txtInpCls, txtInpToc,txtInpMoc;
                TextInputEditText editInpHeatNo,editInpSize,editInpCls, editInpToc,editInpMoc;
                txtInpHeatNo = dialog.findViewById(R.id.txtInpHeadCast);
                txtInpSize = dialog.findViewById(R.id.txtInpSize);
                txtInpCls = dialog.findViewById(R.id.txtInpClass);
                txtInpToc = dialog.findViewById(R.id.txtInpTypeofCast);
                txtInpMoc = dialog.findViewById(R.id.txtInpMaterialofConst);
                editInpHeatNo = dialog.findViewById(R.id.editTextHeatInp);
                editInpSize = dialog.findViewById(R.id.editTextSize);
                editInpCls = dialog.findViewById(R.id.editTextClass);
                editInpToc = dialog.findViewById(R.id.editTextTypeofCast);
                editInpMoc = dialog.findViewById(R.id.editTextMaterialofConst);
                LinearLayout customLinear = dialog.findViewById(R.id.customLinear);
                dialog.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());
                switch (pos){
                    case 0:
                        heading.setText(R.string.AA);
                        txtInpToc.setVisibility(View.GONE);
                        customLinear.setVisibility(View.GONE);
                        txtInpMoc.setVisibility(View.GONE);
                        txtInpHeatNo.setHint(R.string.wrk_num);
                        dialog.show();


                        dialog.findViewById(R.id.btnSubmit).setOnClickListener(v -> {

                            if (TextUtils.isEmpty(editInpHeatNo.getText())){
                                Toast.makeText(v.getContext(), "Please enter details!!!", Toast.LENGTH_SHORT).show();
                            }else {
                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                try {
                                    BitMatrix bitMatrix = multiFormatWriter.encode("AA"+timeStamp, BarcodeFormat.CODE_128,400,170);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                                    String encodeImage = Base64.getEncoder().encodeToString(byteArray);
                                    Log.e("Base64",encodeImage);
                                    encrypt_bar = encodeImage;


                                    Map<String, Object> data = new HashMap<>();
                                    data.put("bar_number", "AA"+timeStamp);
                                    data.put("bar_url", encrypt_bar);
                                    data.put("name_of_material","Auma Actuator");
                                    data.put("work_num",Objects.requireNonNull(editInpHeatNo.getText()).toString());
                                    db.collection("Valves").document("AA"+timeStamp).set(data)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(view.getContext(), "error :"+e, Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            });
                                } catch (WriterException e) {
                                    Toast.makeText(view.getContext(), ""+e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case 1:
                        heading.setText(R.string.CA);
                        dialog.show();

                        dialog.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
                            if (TextUtils.isEmpty(editInpHeatNo.getText()) || TextUtils.isEmpty(editInpMoc.getText()) ||TextUtils.isEmpty(editInpSize.getText()) ||TextUtils.isEmpty(editInpToc.getText()) || TextUtils.isEmpty(editInpCls.getText())){
                                Toast.makeText(v.getContext(), "Please enter details!!!", Toast.LENGTH_SHORT).show();
                            }else {
                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                try {
                                    BitMatrix bitMatrix = multiFormatWriter.encode("CA"+timeStamp, BarcodeFormat.CODE_128,400,170);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                                    String encodeImage = Base64.getEncoder().encodeToString(byteArray);
                                    //Toast.makeText(view.getContext(), ""+ encodeImage, Toast.LENGTH_SHORT).show();
                                    Log.e("Base64",encodeImage);
                                    encrypt_bar = encodeImage;


                                    Map<String, Object> data = new HashMap<>();
                                    data.put("bar_number", "CA"+timeStamp);
                                    data.put("heatNo", Objects.requireNonNull(editInpHeatNo.getText()).toString());
                                    data.put("bar_url", encrypt_bar);
                                    data.put("material_of_const", Objects.requireNonNull(editInpMoc.getText()).toString());
                                    data.put("name_of_material","Casting");
                                    data.put("size", Objects.requireNonNull(editInpSize.getText()).toString());
                                    data.put("type", Objects.requireNonNull(editInpToc.getText()).toString());
                                    data.put("cls_casting", Objects.requireNonNull(editInpCls.getText()).toString());
                                    db.collection("Valves").document("CA"+timeStamp).set(data)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(view.getContext(), "error :"+e, Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });
                                } catch (WriterException e) {
                                    Toast.makeText(view.getContext(), ""+e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;

                    case 2:
                        heading.setText(R.string.FB);
                        customLinear.setVisibility(View.GONE);
                        txtInpHeatNo.setHint(R.string.outerDim);
                        txtInpToc.setHint(R.string.bar_id);
                        dialog.show();

                        dialog.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
                            if (TextUtils.isEmpty(editInpHeatNo.getText()) || TextUtils.isEmpty(editInpToc.getText()) ||TextUtils.isEmpty(editInpMoc.getText())){
                                Toast.makeText(v.getContext(), "Please enter details!!!", Toast.LENGTH_SHORT).show();
                            }else {
                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                try {
                                    BitMatrix bitMatrix = multiFormatWriter.encode("FB"+timeStamp, BarcodeFormat.CODE_128,400,170);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                                    String encodeImage = Base64.getEncoder().encodeToString(byteArray);
                                    Log.e("Base64",encodeImage);
                                    encrypt_bar = encodeImage;


                                    Map<String, Object> data = new HashMap<>();
                                    data.put("bar_number", "FB"+timeStamp);
                                    data.put("outer_dim", Objects.requireNonNull(editInpHeatNo.getText()).toString());
                                    data.put("bar_url", encrypt_bar);
                                    data.put("material_of_const", Objects.requireNonNull(editInpMoc.getText()).toString());
                                    data.put("name_of_material","Forged Bar");
                                    data.put("bar_id", Objects.requireNonNull(editInpToc.getText()).toString());
                                    db.collection("Valves").document("FB"+timeStamp).set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(view.getContext(), "error :"+e, Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            });
                                } catch (WriterException e) {
                                    Toast.makeText(view.getContext(), ""+e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;
                    case 3:
                        heading.setText(R.string.PO);

                        customLinear.setVisibility(View.GONE);
                        txtInpHeatNo.setHint(R.string.model);
                        txtInpToc.setHint(R.string.make);
                        txtInpMoc.setHint(R.string.invoice);
                        dialog.show();

                        dialog.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
                            if (TextUtils.isEmpty(editInpHeatNo.getText()) || TextUtils.isEmpty(editInpToc.getText()) ||TextUtils.isEmpty(editInpMoc.getText())){
                                Toast.makeText(v.getContext(), "Please enter details!!!", Toast.LENGTH_SHORT).show();
                            }else {
                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                try {
                                    BitMatrix bitMatrix = multiFormatWriter.encode("PO"+timeStamp, BarcodeFormat.CODE_128,400,170);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                                    String encodeImage = Base64.getEncoder().encodeToString(byteArray);
                                    //Toast.makeText(view.getContext(), ""+ encodeImage, Toast.LENGTH_SHORT).show();
                                    Log.e("Base64",encodeImage);
                                    encrypt_bar = encodeImage;


                                    Map<String, Object> data = new HashMap<>();
                                    data.put("bar_number", "PO"+timeStamp);
                                    data.put("model", Objects.requireNonNull(editInpHeatNo.getText()).toString());
                                    data.put("bar_url", encrypt_bar);
                                    data.put("invoice", Objects.requireNonNull(editInpMoc.getText()).toString());
                                    data.put("name_of_material","Positioner");
                                    data.put("make", Objects.requireNonNull(editInpToc.getText()).toString());
                                    db.collection("Valves").document("PO"+timeStamp).set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(view.getContext(), "error :"+e, Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });
                                } catch (WriterException e) {
                                    Toast.makeText(view.getContext(), ""+e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case 4:
                        heading.setText(R.string.RB);
                        customLinear.setVisibility(View.GONE);
                        txtInpHeatNo.setHint(R.string.outerDim);
                        txtInpToc.setHint(R.string.bar_id);

                        dialog.show();

                        dialog.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
                            if (TextUtils.isEmpty(editInpHeatNo.getText()) || TextUtils.isEmpty(editInpToc.getText()) ||TextUtils.isEmpty(editInpMoc.getText())){
                                Toast.makeText(v.getContext(), "Please enter details!!!", Toast.LENGTH_SHORT).show();
                            }else {
                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                try {
                                    BitMatrix bitMatrix = multiFormatWriter.encode("RB"+timeStamp, BarcodeFormat.CODE_128,400,170);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                                    String encodeImage = Base64.getEncoder().encodeToString(byteArray);
                                    //Toast.makeText(view.getContext(), ""+ encodeImage, Toast.LENGTH_SHORT).show();
                                    Log.e("Base64",encodeImage);
                                    encrypt_bar = encodeImage;


                                    Map<String, Object> data = new HashMap<>();
                                    data.put("bar_number", "RB"+timeStamp);
                                    data.put("outer_dim", Objects.requireNonNull(editInpHeatNo.getText()).toString());
                                    data.put("bar_url", encrypt_bar);
                                    data.put("material_of_const", Objects.requireNonNull(editInpMoc.getText()).toString());
                                    data.put("name_of_material","Rolled Bar");
                                    data.put("bar_id", Objects.requireNonNull(editInpToc.getText()).toString());
                                    db.collection("Valves").document("RB"+timeStamp).set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(view.getContext(), "error :"+e, Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });
                                } catch (WriterException e) {
                                    Toast.makeText(view.getContext(), ""+e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case 5:
                        heading.setText(R.string.SA);

                        txtInpToc.setVisibility(View.GONE);
                        customLinear.setVisibility(View.GONE);
                        txtInpMoc.setVisibility(View.GONE);
                        txtInpHeatNo.setHint(R.string.serialNum);

                        dialog.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
                            if (TextUtils.isEmpty(editInpHeatNo.getText())){
                                Toast.makeText(v.getContext(), "Please enter details!!!", Toast.LENGTH_SHORT).show();
                            }else {
                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                try {
                                    BitMatrix bitMatrix = multiFormatWriter.encode("SA"+timeStamp, BarcodeFormat.CODE_128,400,170);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                                    String encodeImage = Base64.getEncoder().encodeToString(byteArray);
                                    //Toast.makeText(view.getContext(), ""+ encodeImage, Toast.LENGTH_SHORT).show();
                                    Log.e("Base64",encodeImage);
                                    encrypt_bar = encodeImage;


                                    Map<String, Object> data = new HashMap<>();
                                    data.put("bar_number", "SA"+timeStamp);
                                    data.put("bar_url", encrypt_bar);
                                    data.put("name_of_material","SD Tork Actuator");
                                    data.put("serial_num",Objects.requireNonNull(editInpHeatNo.getText()).toString());
                                    db.collection("Valves").document("SA"+timeStamp).set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(view.getContext(), "error :"+e, Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });
                                } catch (WriterException e) {
                                    Toast.makeText(view.getContext(), ""+e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.show();
                        break;
                    default:
                        Toast.makeText(view.getContext(), "null", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public LinearLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.materials_name);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}