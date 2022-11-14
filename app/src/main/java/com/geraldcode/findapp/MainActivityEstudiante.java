package com.geraldcode.findapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.geraldcode.findapp.Controller.admin.Login;
import com.geraldcode.findapp.Controller.admin.Registro;
import com.geraldcode.findapp.Controller.user.LoginEstudiante;
import com.geraldcode.findapp.Controller.user.RegistroEstudiante;

public class MainActivityEstudiante extends AppCompatActivity {

    Button btnLoginUser, btnLoginAdmin, btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_estudiante);

        btnLoginUser = findViewById(R.id.buttonLoginUser);
        btnLoginAdmin = findViewById(R.id.buttonLoginAdmin);

        btnLoginUser.setOnClickListener(v -> startActivity(new Intent(MainActivityEstudiante.this, LoginEstudiante.class)));
        btnLoginAdmin.setOnClickListener(v -> startActivity(new Intent(MainActivityEstudiante.this, Login.class)));
    }
}