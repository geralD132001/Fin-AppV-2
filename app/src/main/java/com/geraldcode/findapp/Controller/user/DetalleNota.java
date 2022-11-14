package com.geraldcode.findapp.Controller.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geraldcode.findapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DetalleNota extends AppCompatActivity {

    TextView idNotaDetalleUser, uidNotaDetalleUser, correoUsuarioDetalleUser, tituloDetalleUser,
            descripcionDetalleUser, fechaRegistroDetalleUser, fechaNotaDetalleUser, estadoDetalleUser;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Button buttonImportanteUser;

    String idNotaRecuperadoUser, uidUsuarioRecuperadoUser, correoUsuarioRecuperadoUser,
            fechaRegistroRecuperadoUser, tituloRecuperadoUser, descripcionRecuperadoUser,
            fechaRecuperadoUser, estadoRecuperadoUser;

    boolean comprobarNotaImportanteUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_nota);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle de nota");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        InicializarVistas();
        RecuperarDatos();
        SetearDatosRecuperados();
        VerificarNotaImportante();

        buttonImportanteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comprobarNotaImportanteUser) {
                    EliminarNotaImportante();
                } else {
                    AgregarNotasImportantes();
                }
            }
        });
    }

    private void InicializarVistas() {
        idNotaDetalleUser = findViewById(R.id.idNotaDetalleUser);
        uidNotaDetalleUser = findViewById(R.id.uidUsuarioDetalleUser);
        correoUsuarioDetalleUser = findViewById(R.id.correoUsuarioDetalleUser);
        tituloDetalleUser = findViewById(R.id.tituloDetalleUser);
        descripcionDetalleUser = findViewById(R.id.descripcionDetalleUser);
        fechaRegistroDetalleUser = findViewById(R.id.fechaRegistroDetalleUser);
        fechaNotaDetalleUser = findViewById(R.id.fechaNotaDetalleUser);
        estadoDetalleUser = findViewById(R.id.estadoDetalleUser);
        buttonImportanteUser = findViewById(R.id.buttonImportanteUser);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void RecuperarDatos() {
        Bundle intent = getIntent().getExtras();

        idNotaRecuperadoUser = intent.getString("id_nota");
        uidUsuarioRecuperadoUser = intent.getString("uid_usuario");
        correoUsuarioRecuperadoUser = intent.getString("correo_usuario");
        fechaRegistroRecuperadoUser = intent.getString("fecha_registro");
        tituloRecuperadoUser = intent.getString("titulo");
        descripcionRecuperadoUser = intent.getString("descripcion");
        fechaRecuperadoUser = intent.getString("fecha_nota");
        estadoRecuperadoUser = intent.getString("estado");


    }

    private void SetearDatosRecuperados() {
        idNotaDetalleUser.setText(idNotaRecuperadoUser);
        uidNotaDetalleUser.setText(uidUsuarioRecuperadoUser);
        correoUsuarioDetalleUser.setText(correoUsuarioRecuperadoUser);
        fechaRegistroDetalleUser.setText(fechaRegistroRecuperadoUser);
        tituloDetalleUser.setText(tituloRecuperadoUser);
        descripcionDetalleUser.setText(descripcionRecuperadoUser);
        fechaNotaDetalleUser.setText(fechaRecuperadoUser);
        estadoDetalleUser.setText(estadoRecuperadoUser);
    }

    private void AgregarNotasImportantes() {
        if (firebaseUser == null) {
            // Si el usuario es nulo
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        } else {
            // Obtenemos los datos de la nota de la actividad anterior
            Bundle intent = getIntent().getExtras();

            idNotaRecuperadoUser = intent.getString("id_nota");
            uidUsuarioRecuperadoUser = intent.getString("uid_usuario");
            correoUsuarioRecuperadoUser = intent.getString("correo_usuario");
            fechaRegistroRecuperadoUser = intent.getString("fecha_registro");
            tituloRecuperadoUser = intent.getString("titulo");
            descripcionRecuperadoUser = intent.getString("descripcion");
            fechaRecuperadoUser = intent.getString("fecha_nota");
            estadoRecuperadoUser = intent.getString("estado");

            HashMap<String, String> Nota_Importante = new HashMap<>();
            Nota_Importante.put("id_nota", idNotaRecuperadoUser);
            Nota_Importante.put("uid_usuario", uidUsuarioRecuperadoUser);
            Nota_Importante.put("correo_usuario", correoUsuarioRecuperadoUser);
            Nota_Importante.put("fecha_hora_actual", fechaRegistroRecuperadoUser);
            Nota_Importante.put("titulo", tituloRecuperadoUser);
            Nota_Importante.put("descripcion", descripcionRecuperadoUser);
            Nota_Importante.put("fecha_nota", fechaRecuperadoUser);
            Nota_Importante.put("estado", estadoRecuperadoUser);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UsuariosCommons");
            reference
                    .child(firebaseAuth.getUid())
                    .child("Mis notas importantes")
                    .child(idNotaRecuperadoUser)
                    .setValue(Nota_Importante)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetalleNota.this, "Se ha añadido a notas importantes", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetalleNota.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void EliminarNotaImportante() {
        if (firebaseUser == null) {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        } else {
            Bundle intent = getIntent().getExtras();

            idNotaRecuperadoUser = intent.getString("id_nota");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UsuariosCommons");
            reference.child(firebaseAuth.getUid()).child("Mis notas importantes").child(idNotaRecuperadoUser).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(DetalleNota.this, "La nota ya no es importante", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetalleNota.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void VerificarNotaImportante() {
        if (firebaseUser == null) {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        } else {
            Bundle intent = getIntent().getExtras();

            idNotaRecuperadoUser = intent.getString("id_nota");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UsuariosCommons");
            reference
                    .child(firebaseAuth.getUid())
                    .child("Mis notas importantes")
                    .child(idNotaRecuperadoUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            comprobarNotaImportanteUser = snapshot.exists();
                            if (comprobarNotaImportanteUser) {
                                String importante = "Importante";
                                buttonImportanteUser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icono_nota_importante_user, 0, 0);
                                buttonImportanteUser.setText(importante);
                            } else {
                                String no_importante = "No importante";
                                buttonImportanteUser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icono_nota_no_importante, 0, 0);
                                buttonImportanteUser.setText(no_importante);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}