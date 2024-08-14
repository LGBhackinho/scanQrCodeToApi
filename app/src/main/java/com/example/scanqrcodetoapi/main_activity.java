package com.example.scanqrcodetoapi;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class main_activity extends AppCompatActivity {

    private static final String API_URL = "https://webhook.site/e82faf52-cd48-4eae-b055-3c3596903a3c"; // Remplacez cette URL par l'URL de votre API
    private String scannedData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Button scanButton = findViewById(R.id.scan_button);
        Button sendButton = findViewById(R.id.send_button);

        scanButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Placez le code QR dans le cadre");
            integrator.setBeepEnabled(true);
            integrator.setOrientationLocked(true);
            integrator.initiateScan();
        });

        sendButton.setOnClickListener(v -> {
            if (!scannedData.isEmpty()) {
                sendDataToApi(scannedData);
            } else {
                Toast.makeText(this, "Aucune donnée à envoyer. Veuillez scanner d'abord un code QR.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String qrCodeData = result.getContents();
            if (qrCodeData != null) {
                scannedData = qrCodeData;
                Toast.makeText(this, "QR Code: " + qrCodeData, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scan annulé", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendDataToApi(String data) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(data, MediaType.parse("text/plain"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(this, "Données envoyées avec succès!", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Échec de l'envoi des données!", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Erreur de connexion!", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
