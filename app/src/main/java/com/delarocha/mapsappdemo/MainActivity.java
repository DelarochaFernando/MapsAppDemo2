package com.delarocha.mapsappdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText editCalle, editColonia, editCodigoPostal;
    private TextView textCalle, textColonia, textCodigoPostal;
    private Spinner spinnerColonia;
    private Button btnBuscar;
    private String URL_WS = "https://api-codigos-postales.herokuapp.com/v2/codigo_postal/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editCalle = (EditText)findViewById(R.id.editCalle);
        editColonia = (EditText)findViewById(R.id.editColonia);
        textCalle = (TextView)findViewById(R.id.textCalle);
        textColonia = (TextView)findViewById(R.id.textColonia);
        btnBuscar = (Button)findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
