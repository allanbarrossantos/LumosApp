package com.lumos.lumosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class InicialActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

//Declaração de variáveis
    private  TextoParaFala speaker;
    private Button btnRecProduto;
    private Button btnRecValidade;
    private Context context;
    private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);

            //Linkagem das variáveis com suas views do arquivo xml
            setContentView(R.layout.activity_inicial);
            context = getApplicationContext();
            btnRecProduto = findViewById(R.id.btnRecProduto_id);
            btnRecValidade = findViewById(R.id.btnRecValidate_id);
            speaker = new com.lumos.lumosapp.TextoParaFala();
              textToSpeech = new TextToSpeech(context,this);

              //Chamada do método speekText dando as instruções para utilização da tela inicial
            speaker.speekText(textToSpeech,"Bem Vindo ao Lumos App. Toque na parte superior da tela para identificar um produto. Ou toque na " +
                    "parte inferior para identificar a data de validade");


//Dependendo da opção escolhida é chamado a intent da activity correspondente
            btnRecProduto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ReconheceProdutoActivity.class);
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
        //a cada vez que a tela do aplicativo é iniciada ou reacessada as instruções para uso da tela são repetidas
        speaker.speekText(textToSpeech,"Toque na parte superior da tela para identificar um produto. Ou toque na " +
                "parte inferior para identificar a data de validade");
    }

    @Override
    public void onInit(int i) {
        //a cada vez que a tela do aplicativo é iniciada ou reacessada as instruções para uso da tela são repetidas
        speaker.speekText(textToSpeech,"Toque na parte superior da tela para identificar um produto. Ou toque na " +
                "parte inferior para identificar a data de validade");

    }
}
