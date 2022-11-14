package com.geraldcode.findapp.Controller.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.geraldcode.findapp.Controller.user.DetalleNota;
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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class DetalleNotaAdmin extends AppCompatActivity {

    TextView idNotaDetalleAdmin, uidNotaDetalleAdmin, correoUsuarioDetalleAdmin, tituloDetalleAdmin,
            descripcionDetalleAdmin, fechaRegistroDetalleAdmin, fechaNotaDetalleAdmin, estadoDetalleAdmin;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Button buttonImportanteAdmin;

    String idNotaRecuperadoAdmin, uidUsuarioRecuperadoAdmin, correoUsuarioRecuperadoAdmin,
            fechaRegistroRecuperadoAdmin, tituloRecuperadoAdmin, descripcionRecuperadoAdmin,
            fechaRecuperadoAdmin, estadoRecuperadoAdmin;

    boolean comprobarNotaImportanteAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_nota_admin);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle de nota");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        InicializarVistas();
        RecuperarDatos();
        SetearDatosRecuperados();
        VerificarNotaImportante();

        buttonImportanteAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comprobarNotaImportanteAdmin) {
                    EliminarNotaImportante();
                } else {
                    AgregarNotasImportantes();
                }
            }
        });
    }

    private void InicializarVistas() {
        idNotaDetalleAdmin = findViewById(R.id.idNotaDetalleAdmin);
        uidNotaDetalleAdmin = findViewById(R.id.uidUsuarioDetalleAdmin);
        correoUsuarioDetalleAdmin = findViewById(R.id.correoUsuarioDetalleAdmin);
        tituloDetalleAdmin = findViewById(R.id.tituloDetalleAdmin);
        descripcionDetalleAdmin = findViewById(R.id.descripcionDetalleAdmin);
        fechaRegistroDetalleAdmin = findViewById(R.id.fechaRegistroDetalleAdmin);
        fechaNotaDetalleAdmin = findViewById(R.id.fechaNotaDetalleAdmin);
        estadoDetalleAdmin = findViewById(R.id.estadoDetalleAdmin);
        buttonImportanteAdmin = findViewById(R.id.buttonImportanteAdmin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void RecuperarDatos() {
        Bundle intent = getIntent().getExtras();

        idNotaRecuperadoAdmin = intent.getString("id_nota");
        uidUsuarioRecuperadoAdmin = intent.getString("uid_usuario");
        correoUsuarioRecuperadoAdmin = intent.getString("correo_usuario");
        fechaRegistroRecuperadoAdmin = intent.getString("fecha_registro");
        tituloRecuperadoAdmin = intent.getString("titulo");
        descripcionRecuperadoAdmin = intent.getString("descripcion");
        fechaRecuperadoAdmin = intent.getString("fecha_nota");
        estadoRecuperadoAdmin = intent.getString("estado");
    }

    private void SetearDatosRecuperados() {
        idNotaDetalleAdmin.setText(idNotaRecuperadoAdmin);
        uidNotaDetalleAdmin.setText(uidUsuarioRecuperadoAdmin);
        correoUsuarioDetalleAdmin.setText(correoUsuarioRecuperadoAdmin);
        fechaRegistroDetalleAdmin.setText(fechaRegistroRecuperadoAdmin);
        tituloDetalleAdmin.setText(tituloRecuperadoAdmin);
        descripcionDetalleAdmin.setText(descripcionRecuperadoAdmin);
        fechaNotaDetalleAdmin.setText(fechaRecuperadoAdmin);
        estadoDetalleAdmin.setText(estadoRecuperadoAdmin);
    }

    private void AgregarNotasImportantes() {
        if (firebaseUser == null) {
            // Si el usuario es nulo
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        } else {
            // Obtenemos los datos de la nota de la actividad anterior
            Bundle intent = getIntent().getExtras();

            idNotaRecuperadoAdmin = intent.getString("id_nota");
            uidUsuarioRecuperadoAdmin = intent.getString("uid_usuario");
            correoUsuarioRecuperadoAdmin = intent.getString("correo_usuario");
            fechaRegistroRecuperadoAdmin = intent.getString("fecha_registro");
            tituloRecuperadoAdmin = intent.getString("titulo");
            descripcionRecuperadoAdmin = intent.getString("descripcion");
            fechaRecuperadoAdmin = intent.getString("fecha_nota");
            estadoRecuperadoAdmin = intent.getString("estado");

            HashMap<String, String> Nota_Importante = new HashMap<>();
            Nota_Importante.put("id_nota", idNotaRecuperadoAdmin);
            Nota_Importante.put("uid_usuario", uidUsuarioRecuperadoAdmin);
            Nota_Importante.put("correo_usuario", correoUsuarioRecuperadoAdmin);
            Nota_Importante.put("fecha_hora_actual", fechaRegistroRecuperadoAdmin);
            Nota_Importante.put("titulo", tituloRecuperadoAdmin);
            Nota_Importante.put("descripcion", descripcionRecuperadoAdmin);
            Nota_Importante.put("fecha_nota", fechaRecuperadoAdmin);
            Nota_Importante.put("estado", estadoRecuperadoAdmin);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
            reference
                    .child(firebaseAuth.getUid())
                    .child("Mis notas importantes")
                    .child(idNotaRecuperadoAdmin)
                    .setValue(Nota_Importante)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetalleNotaAdmin.this, "Se ha añadido a notas importantes", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetalleNotaAdmin.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void EliminarNotaImportante() {
        if (firebaseUser == null) {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        } else {
            Bundle intent = getIntent().getExtras();

            idNotaRecuperadoAdmin = intent.getString("id_nota");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
            reference.child(firebaseAuth.getUid()).child("Mis notas importantes").child(idNotaRecuperadoAdmin).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(DetalleNotaAdmin.this, "La nota ya no es importante", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetalleNotaAdmin.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void VerificarNotaImportante() {
        if (firebaseUser == null) {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        } else {
            Bundle intent = getIntent().getExtras();

            idNotaRecuperadoAdmin = intent.getString("id_nota");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
            reference
                    .child(firebaseAuth.getUid())
                    .child("Mis notas importantes")
                    .child(idNotaRecuperadoAdmin).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            comprobarNotaImportanteAdmin = snapshot.exists();
                            if (comprobarNotaImportanteAdmin) {
                                String importante = "Importante";
                                buttonImportanteAdmin.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icono_nota_importante_user, 0, 0);
                                buttonImportanteAdmin.setText(importante);
                            } else {
                                String no_importante = "No importante";
                                buttonImportanteAdmin.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icono_nota_no_importante, 0, 0);
                                buttonImportanteAdmin.setText(no_importante);
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