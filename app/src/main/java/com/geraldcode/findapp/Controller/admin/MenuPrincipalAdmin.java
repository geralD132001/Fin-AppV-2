package com.geraldcode.findapp.Controller.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.bumptech.glide.Glide;
import com.geraldcode.findapp.Controller.user.AgregarNota;
import com.geraldcode.findapp.Controller.user.ConfiguracionUser;
import com.geraldcode.findapp.Controller.user.ListObjectsUser;
import com.geraldcode.findapp.Controller.user.ListarNotas;
import com.geraldcode.findapp.Controller.user.MenuPrincipalEstudiante;
import com.geraldcode.findapp.Controller.user.NotasImportantes;
import com.geraldcode.findapp.MainActivityAdmin;
import com.geraldcode.findapp.MainActivityEstudiante;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuPrincipalAdmin extends AppCompatActivity {

    Button cerrarSesion, agregarObjeto, listarObjetos, configuracionAcount, perfilAdmin, agregarNotaAdmin, listarNotasAdmin, notasImportantesAdmin;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ImageView imagenAdmin;


    Button estadoDeCuentaActual;
    TextView nombresPrincipal, correoPrincipal, uidPrincipal, idObjeto;

    LinearLayoutCompat linearNombres, linearCorreo, linearVerificacion;

    ProgressBar progressBarData;
    ProgressDialog progressDialog;

    DatabaseReference usuarios, objetos;

    Dialog dialogCuentaVerificada, dialogInformacion, dialogFecha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
        objetos = FirebaseDatabase.getInstance().getReference("Objetos");


        nombresPrincipal = findViewById(R.id.nombresPrincipal);
        correoPrincipal = findViewById(R.id.correoPrincipal);
        agregarObjeto = findViewById(R.id.buttonAgregarObjeto);
        listarObjetos = findViewById(R.id.buttonObjetos);
        perfilAdmin = findViewById(R.id.buttonPerfilMenuAdmin);
        configuracionAcount = findViewById(R.id.buttonConfiguracionAdmin);
        estadoDeCuentaActual = findViewById(R.id.estadoCuentaPrincipal);
        cerrarSesion = findViewById(R.id.buttonCerrarSesionAdmin);
        uidPrincipal = findViewById(R.id.uidPrincipal);
        progressBarData = findViewById(R.id.progressBarDatos);
        agregarNotaAdmin = findViewById(R.id.agregarNotaMenuAdmin);
        listarNotasAdmin = findViewById(R.id.listarNotasMenuAdmin);
        notasImportantesAdmin = findViewById(R.id.importantesMenuAdmin);
        imagenAdmin = findViewById(R.id.imagenPerfilAdmin);
        dialogCuentaVerificada = new Dialog(this);
        dialogInformacion = new Dialog(this);
        dialogFecha = new Dialog(MenuPrincipalAdmin.this);

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

        agregarNotaAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uid_usuario = uidPrincipal.getText().toString();
                String correo_usuario = correoPrincipal.getText().toString();

                Intent intent = new Intent(MenuPrincipalAdmin.this, AgregarNotaAdmin.class);
                intent.putExtra("Uid", uid_usuario);
                intent.putExtra("Correo", correo_usuario);
                startActivity(intent);
            }
        });


        listarNotasAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalAdmin.this, ListarNotasAdmin.class));
                Toast.makeText(MenuPrincipalAdmin.this, "Listar Notas", Toast.LENGTH_SHORT).show();
            }
        });



        agregarObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalAdmin.this, AgregarObjeto.class);
                startActivity(intent);
            }
        });

        listarObjetos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalAdmin.this, ListarObjetos.class));
                Toast.makeText(MenuPrincipalAdmin.this, "Listar Objetos", Toast.LENGTH_SHORT).show();
            }
        });

        configuracionAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uidUsuario = uidPrincipal.getText().toString();
                Intent intent = new Intent(MenuPrincipalAdmin.this, Configuracion.class);
                intent.putExtra("Uid", uidUsuario);
                startActivity(intent);
            }
        });

        perfilAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalAdmin.this, PerfilUsuario.class));
            }
        });


        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });

        notasImportantesAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipalAdmin.this, NotasImportantesAdmin.class));
                Toast.makeText(MenuPrincipalAdmin.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MenuPrincipalAdmin.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MenuPrincipalAdmin.this, "Instrucciones enviadas, revise su bandeja", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Falló el envio
                Toast.makeText(MenuPrincipalAdmin.this, "Falló devido a: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void VisualizarFecha(){
        TextView fechaHoy;
        Button buttonCerrar;

        dialogFecha.setContentView(R.layout.cuadro_dialog_fecha_admin);

        fechaHoy = dialogFecha.findViewById(R.id.fechaHoyAdmin);
        buttonCerrar = dialogFecha.findViewById(R.id.btnCerrarCuadradiAdmin);

        /* Obtener fecha actual */
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d 'de' MMMM 'del' yyyy"); // 03 de noviembre del 2022
        String fecha = simpleDateFormat.format(date);
        fechaHoy.setText(fecha);

        buttonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFecha.dismiss();
            }
        });

        dialogFecha.show();
        dialogFecha.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        ComprobarInicioDeSesion();
        super.onStart();
    }

    private void ComprobarInicioDeSesion() {
        if (firebaseUser != null) {
            Toast.makeText(MenuPrincipalAdmin.this, "Estas en el modo administrador", Toast.LENGTH_SHORT).show();
            CargaDeDatos();
        } else {
            startActivity(new Intent(MenuPrincipalAdmin.this, MainActivityEstudiante.class));
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
                    String imagen = "" + snapshot.child("imagen_perfil").getValue();

                    // Setear los datos de los respectivos textView
                    uidPrincipal.setText(uid);
                    nombresPrincipal.setText(nombres);
                    correoPrincipal.setText(correo);

                    // Habilitar los botonoes del menú

                    agregarObjeto.setEnabled(true);
                    listarObjetos.setEnabled(true);
                    configuracionAcount.setEnabled(true);
                    agregarNotaAdmin.setEnabled(true);
                    listarNotasAdmin.setEnabled(true);
                    notasImportantesAdmin.setEnabled(true);
                    perfilAdmin.setEnabled(true);
                    cerrarSesion.setEnabled(true);

                    ObtenerImagen(imagen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ObtenerImagen(String imagen) {
        try {
            // Si la imagen no se ha traido con éxito
            Glide.with(getApplicationContext()).load(imagen).placeholder(R.drawable.imagen_usuario_admin).into(imagenAdmin);
        }catch (Exception e){
            /// Si la iamgen no fue traido con éxito
            Glide.with(getApplicationContext()).load(R.drawable.imagen_usuario_admin).into(imagenAdmin);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_principal_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.cerrarSesionItemAdmin){
            SalirAplicacion();
        }

        if(item.getItemId() == R.id.calendarMenuItemAdmin){
            VisualizarFecha();
        }

        if (item.getItemId() == R.id.acerdaDeItemAdmin) {
            Informacion();
        }


        return super.onOptionsItemSelected(item);
    }


    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipalAdmin.this, MainActivityAdmin.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
    }


}