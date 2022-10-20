package com.geraldcode.findapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.geraldcode.findapp.Controller.Login;
import com.geraldcode.findapp.Controller.Registro;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnLogin = findViewById(R.id.buttonLogin);
        btnRegistro = findViewById(R.id.buttonRegistro);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Login.class)));


        btnRegistro.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Registro.class)));
    }
}