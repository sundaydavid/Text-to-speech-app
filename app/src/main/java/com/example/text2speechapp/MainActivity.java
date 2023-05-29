package com.example.text2speechapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Text To Speech
    private TextToSpeech tts;
    private ImageButton ibSpeak;
    private EditText etInput;

    //Speech To Text
    protected static final int RESULT_SPEECH = 1;
    private ImageButton btnSpeak;
    private EditText tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInput = findViewById(R.id.etInput);
        ibSpeak = findViewById(R.id.ibSpeak);

        tvText = findViewById(R.id.tvInput);
        btnSpeak = findViewById(R.id.btnSpeak);

        initializeTextToSpeech();
        initializeSpeechToText();

    }

    private void initializeSpeechToText() {



        btnSpeak.setOnClickListener(v->{
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
            try {
                startActivityForResult(intent, RESULT_SPEECH);
                tvText.setText("");
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Your device doesn`t support speech to text", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(MainActivity.this, "The language is not supported.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = etInput.getText().toString().trim();
                int result = tts.speak(input,TextToSpeech.QUEUE_FLUSH,null);
                if (result == TextToSpeech.ERROR){
                    Toast.makeText(MainActivity.this, "Error in processing text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null){
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvText.setText(text.get(0));
                }
                break;
        }
    }
}