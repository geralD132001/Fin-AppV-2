package com.geraldcode.findapp.Controller.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geraldcode.findapp.Controller.user.ConfiguracionUser;
import com.geraldcode.findapp.MainActivityEstudiante;
import com.geraldcode.findapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Configuracion extends AppCompatActivity {

    TextView uidEliminar, eliminarCuenta;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Dialog dialogAutenticacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Configuración Estudiante");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        InicializarVariables();
        ObtenerUid();


        eliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EliminarUsuarioAutenticacion();
            }
        });
    }

    private void InicializarVariables() {
        uidEliminar = findViewById(R.id.uidEliminarAdmin);
        eliminarCuenta = findViewById(R.id.eliminarCuentaAdmin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Usuarios");

        dialogAutenticacion = new Dialog(Configuracion.this);
    }


    private void ObtenerUid() {
        String uid = getIntent().getStringExtra("Uid");
        uidEliminar.setText(uid);
    }

    private void EliminarUsuarioAutenticacion() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Configuracion.this);
        alertDialog.setTitle("¿Estás seguro(a)?");
        alertDialog.setMessage("Su cuenta se eliminará permanentemente");
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            EliminarUsuarioBD();
                            Intent intent = new Intent(Configuracion.this, MainActivityEstudiante.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(Configuracion.this, "Se ha eliminado su cuenta con éxito", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(Configuracion.this, "Ha ocurrido un problema", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(Configuracion.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Autenticacion();
                    }
                });
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Configuracion.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.create().show();
    }


    private void EliminarUsuarioBD(){
        String uid_eliminar = uidEliminar.getText().toString();
        Query query = databaseReference.child(uid_eliminar);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(Configuracion.this, "Se ha eliminado su cuenta", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void Autenticacion() {
        Button buttonEntendido, buttonCerrarSesion;

        dialogAutenticacion.setContentView(R.layout.cuadro_de_dialogo_autenticacion_admin);

        buttonEntendido = dialogAutenticacion.findViewById(R.id.buttonEntendidoAuthAdmin);
        buttonCerrarSesion = dialogAutenticacion.findViewById(R.id.buttonCerarSesionAuthAdmin);

        buttonEntendido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAutenticacion.dismiss();
            }
        });

        buttonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CerrarSesion();
                dialogAutenticacion.dismiss();
            }
        });

        dialogAutenticacion.show();
        dialogAutenticacion.setCanceledOnTouchOutside(false);
    }

    private void CerrarSesion() {
        firebaseAuth.signOut();
        startActivity(new Intent(Configuracion.this, MainActivityEstudiante.class));
        finish();
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}