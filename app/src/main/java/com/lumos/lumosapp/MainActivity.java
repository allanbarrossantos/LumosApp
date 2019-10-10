    package com.lumos.lumosapp;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private CameraView cameraView;
    private Button btnDetect;
    private TextoParaFala speaker;
    private TextToSpeech textToSpeech;
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speaker = new TextoParaFala();
        Context context = getApplicationContext();
        textToSpeech = new TextToSpeech(context,this);
        try {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            cameraView = findViewById(R.id.camera_View);
            btnDetect = findViewById(R.id.button_Detect);
            //listViewPesquisa = findViewById(R.id.listViewItem_id);
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("Produtos");
            btnDetect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cameraView.start();
                    cameraView.captureImage();
                }
            });

            cameraView.addCameraKitListener(new CameraKitEventListener() {
                @Override
                public void onEvent(CameraKitEvent cameraKitEvent) {

                }

                @Override
                public void onError(CameraKitError cameraKitError) {

                }

                @Override
                public void onImage(CameraKitImage cameraKitImage) {
                    Bitmap bitmap = cameraKitImage.getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                    cameraView.stop();


                    runDetector(bitmap);
                }

                @Override
                public void onVideo(CameraKitVideo cameraKitVideo) {

                }
            });

        }catch (Exception e){

            Log.d("erroOncreate",""+e);
        }
    }

    private void runDetector(Bitmap bitmap) {
    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_EAN_13, FirebaseVisionBarcode.FORMAT_EAN_8,
                        FirebaseVisionBarcode.FORMAT_QR_CODE ).build();

        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                processResult(firebaseVisionBarcodes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {

    String value="";
    if(firebaseVisionBarcodes.size()==0){
        speaker.speekText(textToSpeech,"desculpe, não encontrei nenhum Código de barras. Toque na tela para tentar novamente");
    } else {
        for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {
            value = item.getRawValue();
            Toast.makeText(this, "" + value, Toast.LENGTH_SHORT).show();
        }


        encontraProdutoBanco(value);
    }
    }

    private void encontraProdutoBanco(String value) {

            Query query = databaseReference.orderByChild("barCode").equalTo(value);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Toast.makeText(MainActivity.this, "onDataChange", Toast.LENGTH_SHORT).show();
                  for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                        Produtos produtos = objSnapshot.getValue(Produtos.class);
                      speaker.speekText(textToSpeech,produtos.getDescricao());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "onCancelled", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onInit(int i) {

        speaker.speekText(textToSpeech,"Aponte a camera para onde você acredita estar o codigo de barras da mercadoria...." +
                "aguarde aproximadamente 2 segundos e toque na tela para que eu possa reconhecê-lo");



    }
}
