package com.geraldcode.findapp.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geraldcode.findapp.Controller.ActualizarPassword.ActualizarPassUsuario;
import com.geraldcode.findapp.Controller.Configuration.Configuracion;
import com.geraldcode.findapp.Controller.Objetos.AgregarObjeto;
import com.geraldcode.findapp.Controller.Objetos.ListarObjetos;
import com.geraldcode.findapp.Controller.Perfil.PerfilUsuario;
import com.geraldcode.findapp.MainActivity;
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

public class MenuPrincipal extends AppCompatActivity {

    Button cerrarSesion, agregarObjeto, listarObjetos, configuracionContrasenia, perfilUsuario, acercaDe;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Button estadoDeCuentaActual;
    TextView nombresPrincipal, correoPrincipal, uidPrincipal;

    LinearLayoutCompat linearNombres, linearCorreo, linearVerificacion;

    ProgressBar progressBarData;
    ProgressDialog progressDialog;

    DatabaseReference usuarios;

    Dialog dialogCuentaVerificada, dialogInformacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        nombresPrincipal = findViewById(R.id.nombresPrincipal);
        correoPrincipal = findViewById(R.id.correoPrincipal);
        agregarObjeto = findViewById(R.id.buttonAgregarObjeto);
        listarObjetos = findViewById(R.id.buttonObjetos);
        configuracionContrasenia = findViewById(R.id.buttonConfiguracion);
        perfilUsuario = findViewById(R.id.buttonPerfilUsuario);
        estadoDeCuentaActual = findViewById(R.id.estadoCuentaPrincipal);
        acercaDe = findViewById(R.id.buttonAcercaDe);
        cerrarSesion = findViewById(R.id.buttonCerrarSesion);
        uidPrincipal = findViewById(R.id.uidPrincipal);
        progressBarData = findViewById(R.id.progressBarDatos);

        dialogCuentaVerificada = new Dialog(this);
        dialogInformacion = new Dialog(this);

        linearNombres = findViewById(R.id.linearNombres);
        linearCorreo = findViewById(R.id.linearCorreo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor...");
        progressDialog.setCanceledOnTouchOutside(false);

        linearVerificacion = findViewById(R.id.linearVerificacion);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        estadoDeCuentaActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser.isEmailVerified()) {
                    // Si la cuenta esta verificada
                    // Toast.makeText(MenuPrincipal.this, "Cuenta ya verificada", Toast.LENGTH_SHORT).show();
                    AnimacionCuentaVerificada();
                } else {
                    // Si la cuenta no esta verificada
                    VerificarCuentaCorreo();
                }
            }
        });


        agregarObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uidUsuario = uidPrincipal.getText().toString();
                String correoUsuario = correoPrincipal.getText().toString();

                Intent intent = new Intent(MenuPrincipal.this, AgregarObjeto.class);
                intent.putExtra("Uid", uidUsuario);
                intent.putExtra("Correo", correoUsuario);
                startActivity(intent);
            }
        });

        listarObjetos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, ListarObjetos.class));
                Toast.makeText(MenuPrincipal.this, "Listar Objetos", Toast.LENGTH_SHORT).show();
            }
        });

        configuracionContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, ActualizarPassUsuario.class));
            }
        });

        perfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, PerfilUsuario.class));
            }
        });

        acercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Informacion();
            }
        });


        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });
    }

    private void VerificarCuentaCorreo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("verificar cuenta")
                .setMessage("¿Estás seguro(a) de enviar instrucciones de verificación a su correo electrónico?"
                        + firebaseUser.getEmail()).setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EnviarCorreoAVerificacion();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MenuPrincipal.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void EnviarCorreoAVerificacion() {

        progressDialog.setMessage("Enviando instrucciones de verificación a su correo electrónico");
        progressDialog.show();

        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Envio fue exitoso
                progressDialog.dismiss();
                Toast.makeText(MenuPrincipal.this, "Instrucciones enviadas, revise su bandeja", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Falló el envio
                Toast.makeText(MenuPrincipal.this, "Falló devido a: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void VerificarEstadoDeCuenta() {
        String verificado = "Verificado";
        String noVerificado = "No Verificado";

        if (firebaseUser.isEmailVerified()) {
            estadoDeCuentaActual.setText(verificado);
            estadoDeCuentaActual.setBackgroundColor(Color.rgb(41, 128, 185));
        } else {
            estadoDeCuentaActual.setText(noVerificado);
            estadoDeCuentaActual.setBackgroundColor(Color.rgb(231, 76, 60));
        }
    }


    private void AnimacionCuentaVerificada() {
        Button entendidoVerificado;
        dialogCuentaVerificada.setContentView(R.layout.dialogo_cuenta_verificada);
        entendidoVerificado = dialogCuentaVerificada.findViewById(R.id.entendidoVerificado);
        entendidoVerificado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCuentaVerificada.dismiss();
            }
        });

        dialogCuentaVerificada.show();
        dialogCuentaVerificada.setCanceledOnTouchOutside(false);
    }

    private void Informacion() {
        Button entendidoInfo;
        dialogInformacion.setContentView(R.layout.cuadro_dialogo_informacion);
        entendidoInfo = dialogInformacion.findViewById(R.id.entendidoInfo);

        entendidoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInformacion.dismiss();
            }
        });
        dialogInformacion.show();
        dialogInformacion.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        ComprobarInicioDeSesion();
        super.onStart();
    }

    private void ComprobarInicioDeSesion() {
        if (firebaseUser != null) {
            CargaDeDatos();
        } else {
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            finish();
        }
    }


    private void CargaDeDatos() {

        VerificarEstadoDeCuenta();

        usuarios.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Si el usuario existe en la base de datos
                if (snapshot.exists()) {

                    // El progressbar se oculta
                    progressBarData.setVisibility(View.GONE);


                    linearNombres.setVisibility(View.VISIBLE);
                    linearCorreo.setVisibility(View.VISIBLE);
                    linearVerificacion.setVisibility(View.VISIBLE);

                    // Obtener los datos
                    String uid = "" + snapshot.child("uid").getValue();
                    String nombres = "" + snapshot.child("nombres").getValue();
                    String correo = "" + snapshot.child("correo").getValue();

                    // Setear los datos de los respectivos textView
                    uidPrincipal.setText(uid);
                    nombresPrincipal.setText(nombres);
                    correoPrincipal.setText(correo);

                    // Habilitar los botonoes del menú

                    agregarObjeto.setEnabled(true);
                    listarObjetos.setEnabled(true);
                    configuracionContrasenia.setEnabled(true);
                    perfilUsuario.setEnabled(true);
                    acercaDe.setEnabled(true);
                    cerrarSesion.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
    }


}