package com.geraldcode.findapp.Controller.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geraldcode.findapp.Controller.admin.Login;
import com.geraldcode.findapp.Controller.admin.MenuPrincipalAdmin;
import com.geraldcode.findapp.Controller.admin.Registro;
import com.geraldcode.findapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistroEstudiante extends AppCompatActivity {

    EditText nombreEt, correoEt, contraseniaEt, confirmarContraseniaEt;
    Button registrarUsuario;
    TextView tengoUnaCuentaTextView;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    String nombre = "", correo = "", password = "", confirmarPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_estudiante);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Registro Estudiante");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        nombreEt = findViewById(R.id.nombreEditTextUser);
        correoEt = findViewById(R.id.correoEditTextUser);
        contraseniaEt = findViewById(R.id.contraseniaEditTextUser);
        confirmarContraseniaEt = findViewById(R.id.confirmarContraseniaEditTextUser);
        registrarUsuario = findViewById(R.id.buttonRegistrarUsuarioUser);
        tengoUnaCuentaTextView = findViewById(R.id.tengoUnaCuentaTXTUser);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(RegistroEstudiante.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        registrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarDatos();
            }
        });

        tengoUnaCuentaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroEstudiante.this, LoginEstudiante.class));
            }
        });
    }


    private void ValidarDatos() {
        nombre = nombreEt.getText().toString();
        correo = correoEt.getText().toString();
        password = contraseniaEt.getText().toString();
        confirmarPassword = confirmarContraseniaEt.getText().toString();


        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "Ingrese nombre", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese correo", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingresar Contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmarPassword)) {
            Toast.makeText(this, "Confirme contraseña", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coiciden", Toast.LENGTH_SHORT).show();
        } else {
            CrearCuenta();
        }
    }

    private void CrearCuenta() {
        progressDialog.setMessage("Creando su cuenta...");
        progressDialog.show();

        // Crear usuario en FIREBASE
        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        GuardarInformacion();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistroEstudiante.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void GuardarInformacion() {
        progressDialog.setMessage("Guardando su información");
        progressDialog.dismiss();

        // Obtener la identificación del usuario actual
        String uid = firebaseAuth.getUid();

        HashMap<String, String> Data = new HashMap<>();

        // Datos del usuario
        Data.put("uid", uid);
        Data.put("correo", correo);
        Data.put("nombres", nombre);
        Data.put("password", password);

        Data.put("apellidos", "");
        Data.put("edad", "");
        Data.put("telefono", "");
        Data.put("domicilio", "");
        Data.put("tipo_documento", "");
        Data.put("numero_documento", "");
        Data.put("universidad", "");
        Data.put("facultad", "");
        Data.put("carrera", "");
        Data.put("ciclo", "");
        Data.put("fecha_de_nacimiento", "");
        Data.put("imagen_perfil", "");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UsuariosCommons");
        databaseReference.child(uid).setValue(Data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(RegistroEstudiante.this, "Cuenta creada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistroEstudiante.this, MenuPrincipalEstudiante.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegistroEstudiante.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}