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
        
        Button send = findViewById(R.id.buttonAbschicken);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText studentNumber = findViewById(R.id.MatrikelnummerEingabe);
                String matNr = studentNumber.getText().toString();
                if (matNr.length() == 8){
                    ClientTCPThread client = new ClientTCPThread(matNr);
                    client.start();
                } else {
                    TextView response = findViewById(R.id.Antwort);
                    response.setText("Bitte gültige Matrikelnummer eingeben.");
                }

            }
        });

        Button calculate = findViewById(R.id.buttonBerechnen);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText studentNumber = findViewById(R.id.MatrikelnummerEingabe);
                String matNr = studentNumber.getText().toString();
                if (matNr.length() == 8){
                    TextView response = findViewById(R.id.Antwort);
                    int num = Integer.parseInt(matNr);
                    AlternierendeQuersumme qs = new AlternierendeQuersumme(num);
                    String parity = "Die alternierende Quersumme Deiner Matrikelnummer ist: " + qs.quersummeGetParity();
                    response.setText(parity);
                } else {
                    TextView response = findViewById(R.id.Antwort);
                    response.setText("Bitte gültige Matrikelnummer eingeben.");
                }
            }
        });

    }

     public class ClientTCPThread extends Thread {
        String studentNumber;
        TextView response = findViewById(R.id.Antwort);

        public ClientTCPThread(String matNr){
            this.studentNumber = matNr;
        }


        @Override
        public void run() {

            try {
                Socket socket = new Socket(serverName, serverPort);
                PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                int numOut = Integer.parseInt(studentNumber);
                output.println(numOut);

                String message = input.readLine();

                input.close();
                output.close();
                socket.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        response.setText(message);
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
        private int studentNumber;
        private int crossSum;

        public AlternierendeQuersumme(int studentNumber){
            this.studentNumber = studentNumber;
            this.crossSum = 0;
        }

        public void calculateQuersumme(){
            int counter = 0;

            while(studentNumber > 0){
                int lastDigit = studentNumber % 10;
                if (counter == 0) {
                    crossSum = lastDigit;
                } else if (counter % 2 == 0){
                    crossSum += lastDigit;
                } else{
                    crossSum -= lastDigit;
                }
                studentNumber = studentNumber / 10;
                counter++;
            }
        }

        public String quersummeGetParity(){
            calculateQuersumme();
            if (crossSum % 2 == 0){
                return "gerade";
            } else{
                return "ungerade";
            }
        }

    }
}


