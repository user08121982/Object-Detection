package com.avin.intelliscan.texttrac;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.avin.intelliscan.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageView clear, getImage, copy;
    private EditText recgText;
    private Uri imageUri;
    private Map<String, TextRecognizer> textRecognizers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clear = findViewById(R.id.clearText);
        getImage = findViewById(R.id.camera);
        copy = findViewById(R.id.copyText);
        recgText = findViewById(R.id.recgText);

        // Initialize text recognizers for all languages
        TextRecognizer latinRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        TextRecognizer devanagariRecognizer = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
//        TextRecognizer chineseRecognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
//        TextRecognizer japaneseRecognizer = TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
//        TextRecognizer koreanRecognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

        // Store text recognizers in a map
        textRecognizers = new HashMap<>();
        textRecognizers.put("Latin", latinRecognizer);
        textRecognizers.put("Devanagari", devanagariRecognizer);
//        textRecognizers.put("Chinese", chineseRecognizer);
//        textRecognizers.put("Japanese", japaneseRecognizer);
//        textRecognizers.put("Korean", koreanRecognizer);

        // Set click listeners
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = recgText.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(MainActivity.this, "There is no text to copy", Toast.LENGTH_SHORT).show();
                } else {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data", text);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "Text copied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = recgText.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No text to clear", Toast.LENGTH_SHORT).show();
                } else {
                    recgText.setText("");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            recognizeText();
        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizeText() {
        if (imageUri != null) {
            try {
                InputImage inputImage = InputImage.fromFilePath(MainActivity.this, imageUri);

                final boolean[] textRecognized = {false};
                // Use the appropriate text recognizer based on the language detected
                for (Map.Entry<String, TextRecognizer> entry : textRecognizers.entrySet()) {
                    TextRecognizer recognizer = entry.getValue();
                    Task<Text> result = recognizer.process(inputImage)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text text) {
                                    String recognizedText = text.getText();
                                    if (!recognizedText.isEmpty() && !textRecognized[0]) {
                                        recgText.setText(recognizedText);
                                        Toast.makeText(MainActivity.this, "Text extracted", Toast.LENGTH_SHORT).show();
                                        textRecognized[0] = true; // Set the flag to true if text is recognized
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Text recognition failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                    if (textRecognized[0]) {
                        break; // Break out of the loop if text is recognized
                    }
                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
