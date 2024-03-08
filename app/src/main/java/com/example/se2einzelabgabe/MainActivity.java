package com.example.se2einzelabgabe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {
    private String serverName = "se2-submission.aau.at";
    private int serverPort = 20080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        Button abschicken = findViewById(R.id.buttonAbschicken);
        abschicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText matrikelnummer = findViewById(R.id.MatrikelnummerEingabe);
                String matNr = matrikelnummer.getText().toString();
                if (matNr.length() == 8){
                    ClientTCPThread client = new ClientTCPThread(matNr);
                    client.start();
                } else {
                    TextView antwort = findViewById(R.id.Antwort);
                    antwort.setText("Bitte gültige Matrikelnummer eingeben.");
                }

            }
        });

        Button berechnen = findViewById(R.id.buttonBerechnen);
        berechnen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText matrikelnummer = findViewById(R.id.MatrikelnummerEingabe);
                String matNr = matrikelnummer.getText().toString();
                if (matNr.length() == 8){
                    TextView antwort = findViewById(R.id.Antwort);
                    int num = Integer.parseInt(matNr);
                    AlternierendeQuersumme qs = new AlternierendeQuersumme(num);
                    String parity = "Die alternierende Quersumme Deiner Matrikelnummer ist: " + qs.quersummeGetParity();
                    antwort.setText(parity);
                } else {
                    TextView antwort = findViewById(R.id.Antwort);
                    antwort.setText("Bitte gültige Matrikelnummer eingeben.");
                }
            }
        });

    }

     public class ClientTCPThread extends Thread {
        String nummer;
        TextView antwort = findViewById(R.id.Antwort);

        public ClientTCPThread(String matNr){
            this.nummer = matNr;
        }


        @Override
        public void run() {

            try {
                Socket socket = new Socket(serverName, serverPort);
                PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                int num = Integer.parseInt(nummer);
                output.println(num);

                String message = input.readLine();

                input.close();
                output.close();
                socket.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        antwort.setText(message);
                    }
                });

            } catch (IOException io) {
                io.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

     }

    public class AlternierendeQuersumme {
        private int matrikelnummer;
        private int quersumme;

        public AlternierendeQuersumme(int matrikelnummer){
            this.matrikelnummer = matrikelnummer;
            this.quersumme = 0;
        }

        public void calculateQuersumme(){
            int counter = 0;

            while(matrikelnummer > 0){
                int lastDigit = matrikelnummer % 10;
                if (counter == 0) {
                    quersumme = lastDigit;
                } else if (counter % 2 == 0){
                    quersumme += lastDigit;
                } else{
                    quersumme -= lastDigit;
                }
                matrikelnummer = matrikelnummer / 10;
                counter++;
            }
        }

        public String quersummeGetParity(){
            calculateQuersumme();
            if (quersumme % 2 == 0){
                return "gerade";
            } else{
                return "ungerade";
            }
        }

    }
}


