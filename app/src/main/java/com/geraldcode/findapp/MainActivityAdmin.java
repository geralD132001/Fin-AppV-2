package com.geraldcode.findapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.geraldcode.findapp.Controller.admin.Login;
import com.geraldcode.findapp.Controller.admin.MenuPrincipalAdmin;
import com.geraldcode.findapp.Controller.admin.Registro;
import com.geraldcode.findapp.Controller.user.LoginEstudiante;
import com.geraldcode.findapp.Controller.user.MenuPrincipalEstudiante;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityAdmin extends AppCompatActivity {

    Button btnLoginUser, btnLoginAdmin;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoginUser = findViewById(R.id.buttonLoginLoginUserAdmin);
        btnLoginAdmin = findViewById(R.id.buttonLoginAdminDos);

        firebaseAuth = FirebaseAuth.getInstance();

        btnLoginUser.setOnClickListener(v -> startActivity(new Intent(MainActivityAdmin.this, LoginEstudiante.class)));
        btnLoginAdmin.setOnClickListener(v -> startActivity(new Intent(MainActivityAdmin.this, Login.class)));
    }



}