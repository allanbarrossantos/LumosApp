package com.lumos.lumosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class InicialActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private  TextoParaFala speaker;
    private Button btnRecProduto;
    private Button btnRecValidade;
    private Context context;
    private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inicial);
            context = getApplicationContext();
            btnRecProduto = findViewById(R.id.btnRecProduto_id);
            btnRecValidade = findViewById(R.id.btnRecValidate_id);
            speaker = new com.lumos.lumosapp.TextoParaFala();
              textToSpeech = new TextToSpeech(context,this);

            speaker.speekText(textToSpeech,"Bem Vindo ao Lumos App. Toque na parte superior da tela para reconhecer um produto. Ou toque na " +
                    "parte inferior para reconhecer a data de validade");

            btnRecProduto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//

                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
            });

            btnRecValidade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ReconheceValidadeActivity.class);
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            Log.d("InicialError",""+e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        speaker.speekText(textToSpeech,"Toque na parte superior da tela para reconhecer um produto. Ou toque na " +
                "parte inferior para reconhecer a data de validade");
    }

    @Override
    public void onInit(int i) {
        speaker.speekText(textToSpeech,"Toque na parte superior da tela para reconhecer um produto. Ou toque na " +
                "parte inferior para reconhecer a data de validade");

    }
}
