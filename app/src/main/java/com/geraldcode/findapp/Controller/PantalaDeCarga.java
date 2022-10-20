package com.geraldcode.findapp.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.geraldcode.findapp.MainActivity;
import com.geraldcode.findapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PantalaDeCarga extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantala_de_carga);

        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();

        int tiempo = 4700;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // startActivity(new Intent(PantalaDeCarga.this, MainActivity.class));
                // finish();
                VerificarUsuario();
            }
        }, tiempo);
    }

    private void VerificarUsuario() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser == null){
            startActivity(new Intent(PantalaDeCarga.this, MainActivity.class));
        } else {
            startActivity(new Intent(PantalaDeCarga.this, MenuPrincipal.class));
        }
    }
}