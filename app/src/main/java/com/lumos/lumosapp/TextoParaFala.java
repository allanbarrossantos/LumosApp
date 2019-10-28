package com.lumos.lumosapp;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TextoParaFala implements TextToSpeech.OnInitListener {


    @Override
    public void onInit(int i) {

    }

    public void speekText(TextToSpeech textToSpeech,String msg ){

        textToSpeech.setLanguage(Locale.getDefault());
        textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH,null);

    }
}
