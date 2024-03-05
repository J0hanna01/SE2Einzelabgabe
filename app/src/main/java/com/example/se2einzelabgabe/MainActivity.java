package com.example.se2einzelabgabe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


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



    }

     public class ClientTCPThread extends Thread {
        int port;
        String name;
        String nummer;
        TextView antwort = findViewById(R.id.Antwort);

        public ClientTCPThread(String name, int port, String matNr){
            this.name = name;
            this.port = port;
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
}


