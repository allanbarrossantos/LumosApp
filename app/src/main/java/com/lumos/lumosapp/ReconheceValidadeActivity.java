package com.lumos.lumosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.Date;
import java.util.List;

public class ReconheceValidadeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private CameraView cameraView;
    private Button btnDetect;
    private TextoParaFala speaker;
    private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconhece_validade);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        cameraView = findViewById(R.id.camera_View_date);
        btnDetect = findViewById(R.id.button_Detect_date);
        speaker = new TextoParaFala();
        textToSpeech = new TextToSpeech(ReconheceValidadeActivity.this,this);
        cameraView.start();
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
        try{
            Bitmap bitmap = cameraKitImage.getBitmap();
            bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
            cameraView.stop();


            runDetector(bitmap);
        }catch (Exception e){
            Log.d("errorOnImage", "" + e);
        }

    }

    @Override
    public void onVideo(CameraKitVideo cameraKitVideo) {

    }
});
    }

    private void runDetector(Bitmap bitmap) {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer textDetector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textDetector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processaTextoEncontrado(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                
            }
        });
    }

    public void processaTextoEncontrado(FirebaseVisionText firebaseVisionText) {
        try {
            List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
            if (blocks.size() == 0) {
                speaker.speekText(textToSpeech,"Desculpe nao encontrei nenhum texto válido");
            }
            String textoEncontrado="";
            for (int i = 0; i < blocks.size(); i++) {
                List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
                for (int j = 0; j < lines.size(); j++) {
                    List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                    for (int k = 0; k < elements.size(); k++) {

                        textoEncontrado = textoEncontrado+" "+ elements.get(k).getText();
                    }
                }
            }
            Log.i("textoEncontrado", "" + textoEncontrado);
            String resultString = limparString(textoEncontrado);
            //TesteMultiplasDatas(resultString);
            Log.i("textoEncontrado", "resultado" + resultString);
            String dataValidade = ProcessaDataValidade(resultString);

        }catch (Exception e){
            Log.d("ProcTextoEncontrado", "" + e);
        }
        }

    private void TesteMultiplasDatas(String resultString) {
        String dataFabricacao;
        String dataValidade;

        int contador = resultString.length();


        for(int i = 0;i<contador;i++){
            //usamos substring pra pegar um caractere, passando como parâmetro,
            //o primeiro caractere a ser pega, até a ultima.
            //fiz um if para verificar se o caractere é igual a " "
            if (resultString.substring(i,i+1).equals("/")){


                int posicao = i+1;
                System.out.println("Está na posição " + posicao);
                System.out.println(resultString.substring(0, posicao));
            }}


    }

    private String ProcessaDataValidade(String resultString) {
        String dia;
        String mes;
        String ano;

        Log.i("textoEncontrado", "processarei a data agora");
        String data="";

        String[] subString = resultString.split("/");

           for(int i=0;i<subString.length;i++){
               if(subString[i].equals("")){
                   subString[i] = subString[i+1];
                   subString[i+1] = subString[i+2];
                   subString[i+2] = subString[i+3];
               }

           }
        dia = subString[0].substring(subString[0].length()-2);
        mes = subString[1];

        if(subString[2].length()>2){
            ano = subString[2].substring(0,4);
            int anoInt = Integer.parseInt(ano);
            Date date = new Date();
            int anoatual = date.getYear();
            if((anoInt-anoatual)>30){
                ano = subString[2].substring(0,2);
                ano = "20"+ano;
            }
        } else{
            ano = subString[2].substring(0,2);
            ano = "20"+ano;
        }

        Log.i("textoEncontrado", "teste Ano " + ano);




        data = ano+"/"+mes+"/"+dia;


        Log.i("textoEncontrado", "dia " + dia);
        Log.i("textoEncontrado", "mes " + mes);
        Log.i("textoEncontrado", "Ano " + ano);
        Log.i("textoEncontrado", "data  " + data);

        int diaInt = Integer.parseInt(dia);
        int mesInt = Integer.parseInt(mes);
        if(diaInt>31 || mesInt>12){
            speaker.speekText(textToSpeech,"Desculpe, nao encontrei uma data válida");
        }else{ Toast.makeText(this, ""+data, Toast.LENGTH_SHORT).show();

            speaker.speekText(textToSpeech,"A data de validade é "+data);
            }
        return data;
    }

    private String limparString(String string) {
        String resultString;


        string = string.replace("a", "");
        string = string.replace("b", "");
        string = string.replace("c", "");
        string = string.replace("d", "");
        string = string.replace("e", "");
        string = string.replace("f", "");
        string = string.replace("g", "");
        string = string.replace("h", "");
        string = string.replace("i", "");
        string = string.replace("j", "");
        string = string.replace("k", "");
        string = string.replace("l", "");
        string = string.replace("m", "");
        string = string.replace("n", "");
        string = string.replace("o", "");
        string = string.replace("p", "");
        string = string.replace("q", "");
        string = string.replace("r", "");
        string = string.replace("s", "");
        string = string.replace("t", "");
        string = string.replace("u", "");
        string = string.replace("v", "");
        string = string.replace("x", "");
        string = string.replace("z", "");
        string = string.replace("w", "");
        string = string.replace("ç", "");
        string = string.replace("y", "");
        string = string.replace(":", "");

        string = string.replace("A", "");
        string = string.replace("B", "");
        string = string.replace("C", "");
        string = string.replace("D", "");
        string = string.replace("E", "");
        string = string.replace("F", "");
        string = string.replace("G", "");
        string = string.replace("H", "");
        string = string.replace("I", "");
        string = string.replace("J", "");
        string = string.replace("K", "");
        string = string.replace("L", "");
        string = string.replace("M", "");
        string = string.replace("N", "");
        string = string.replace("O", "");
        string = string.replace("P", "");
        string = string.replace("Q", "");
        string = string.replace("R", "");
        string = string.replace("S", "");
        string = string.replace("T", "");
        string = string.replace("U", "");
        string = string.replace("V", "");
        string = string.replace("X", "");
        string = string.replace("Z", "");
        string = string.replace("W", "");
        string = string.replace("Ç", "");
        string = string.replace("Y", "");

        string = string.replace("À", "");
        string = string.replace("Á", "");
        string = string.replace("Ã", "");
        string = string.replace("Â", "");

        string = string.replace("à", "");
        string = string.replace("á", "");
        string = string.replace("ã", "");
        string = string.replace("â", "");

        string = string.replace("È", "");
        string = string.replace("É", "");
        string = string.replace("Ê", "");

        string = string.replace("è", "");
        string = string.replace("é", "");
        string = string.replace("ê", "");

        string = string.replace("Ì", "");
        string = string.replace("Í", "");
        string = string.replace("Î", "");

        string = string.replace("ì", "");
        string = string.replace("í", "");
        string = string.replace("î", "");

        string = string.replace("Ò", "");
        string = string.replace("Ó", "");
        string = string.replace("Õ", "");
        string = string.replace("Ô", "");

        string = string.replace("ò", "");
        string = string.replace("ó", "");
        string = string.replace("õ", "");
        string = string.replace("ô", "");

        string = string.replace(" ", "");
        string = string.replace(",", "");
        string = string.replace(".", "/");


        resultString = string;
        return resultString;
    }

    @Override
    public void onInit(int i) {
        speaker.speekText(textToSpeech,"Aponte a camera para o local onde acredita estar a data de validade, e clique na tela para que eu possa identificar.");

    }
}
