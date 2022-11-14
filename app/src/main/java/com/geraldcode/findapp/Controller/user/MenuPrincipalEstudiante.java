package com.geraldcode.findapp.Controller.user;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geraldcode.findapp.Controller.admin.MenuPrincipalAdmin;
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

public class MenuPrincipalEstudiante extends AppCompatActivity {

    Button cerrarSesionUser, listarObjetosUser, perfilUsuarioUser, agregarNotasUser, listarNotasUser, notasImportantesUser;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Button estadoDeCuentaActualUser;
    TextView nombresPrincipalUser, correoPrincipalUser, uidPrincipalUser;

    LinearLayoutCompat linearNombresUser, linearCorreoUser, linearVerificacionUser;

    ProgressBar progressBarDataUser;
    ProgressDialog progressDialogUser;

    DatabaseReference usuariosCommons;

    Dialog dialogCuentaVerificadaUser, dialogInformacionUser, dialogFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal_estudiante);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        usuariosCommons = FirebaseDatabase.getInstance().getReference("UsuariosCommons");

        nombresPrincipalUser = findViewById(R.id.nombresPrincipalUser);
        correoPrincipalUser = findViewById(R.id.correoPrincipalUser);
        listarObjetosUser = findViewById(R.id.buttonObjetosUser);
        perfilUsuarioUser = findViewById(R.id.buttonPerfilUsuarioUser);
        estadoDeCuentaActualUser = findViewById(R.id.estadoCuentaPrincipalUser);
        cerrarSesionUser = findViewById(R.id.buttonCerrarSesionUser);
        uidPrincipalUser = findViewById(R.id.uidPrincipalUser);
        progressBarDataUser = findViewById(R.id.progressBarDatosUser);
        listarNotasUser = findViewById(R.id.listarNotasUser);
        notasImportantesUser = findViewById(R.id.importantesUser);
        agregarNotasUser = findViewById(R.id.agregarNotasUser);
        dialogCuentaVerificadaUser = new Dialog(this);
        dialogInformacionUser = new Dialog(this);
        dialogFecha = new Dialog(MenuPrincipalEstudiante.this);

        linearNombresUser = findViewById(R.id.linearNombresUser);
        linearCorreoUser = findViewById(R.id.linearCorreoUser);

        progressDialogUser = new ProgressDialog(this);
        progressDialogUser.setTitle("Espere por favor...");
        progressDialogUser.setCanceledOnTouchOutside(false);

        linearVerificacionUser = findViewById(R.id.linearVerificacionUser);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        estadoDeCuentaActualUser.setOnClickListener(new View.OnClickListener() {
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


        agregarNotasUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uid_usuario = uidPrincipalUser.getText().toString();
                String correo_usuario = correoPrincipalUser.getText().toString();

                Intent intent = new Intent(MenuPrincipalEstudiante.this, AgregarNota.class);
                intent.putExtra("Uid", uid_usuario);
                intent.putExtra("Correo", correo_usuario);
                startActivity(intent);
            }
        });


        listarNotasUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalEstudiante.this, ListarNotas.class));
                Toast.makeText(MenuPrincipalEstudiante.this, "Listar Notas", Toast.LENGTH_SHORT).show();
            }
        });

        listarObjetosUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalEstudiante.this, ListObjectsUser.class));
                Toast.makeText(MenuPrincipalEstudiante.this, "Listar Objetos", Toast.LENGTH_SHORT).show();
            }
        });


        notasImportantesUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipalEstudiante.this, NotasImportantes.class));
                Toast.makeText(MenuPrincipalEstudiante.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
            }
        });

        perfilUsuarioUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalEstudiante.this, PerfilUserCommon.class));
            }
        });



        cerrarSesionUser.setOnClickListener(new View.OnClickListener() {
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
                        Toast.makeText(MenuPrincipalEstudiante.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void EnviarCorreoAVerificacion() {

        progressDialogUser.setMessage("Enviando instrucciones de verificación a su correo electrónico");
        progressDialogUser.show();

        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Envio fue exitoso
                progressDialogUser.dismiss();
                Toast.makeText(MenuPrincipalEstudiante.this, "Instrucciones enviadas, revise su bandeja", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Falló el envio
                Toast.makeText(MenuPrincipalEstudiante.this, "Falló devido a: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void VerificarEstadoDeCuenta() {
        String verificado = "Verificado";
        String noVerificado = "No Verificado";

        if (firebaseUser.isEmailVerified()) {
            estadoDeCuentaActualUser.setText(verificado);
            estadoDeCuentaActualUser.setBackgroundColor(Color.rgb(41, 128, 185));
        } else {
            estadoDeCuentaActualUser.setText(noVerificado);
            estadoDeCuentaActualUser.setBackgroundColor(Color.rgb(231, 76, 60));
        }
    }

    private void AnimacionCuentaVerificada() {
        Button entendidoVerificado;
        dialogCuentaVerificadaUser.setContentView(R.layout.dialogo_cuenta_verificada);
        entendidoVerificado = dialogCuentaVerificadaUser.findViewById(R.id.entendidoVerificado);
        entendidoVerificado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCuentaVerificadaUser.dismiss();
            }
        });

        dialogCuentaVerificadaUser.show();
        dialogCuentaVerificadaUser.setCanceledOnTouchOutside(false);
    }

    private void Informacion() {
        Button entendidoInfo;
        dialogInformacionUser.setContentView(R.layout.cuadro_dialogo_informacion);
        entendidoInfo = dialogInformacionUser.findViewById(R.id.entendidoInfo);

        entendidoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInformacionUser.dismiss();
            }
        });
        dialogInformacionUser.show();
        dialogInformacionUser.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        ComprobarInicioDeSesion();
        super.onStart();
    }

    private void ComprobarInicioDeSesion() {
        if (firebaseUser != null) {
            Toast.makeText(MenuPrincipalEstudiante.this, "Estas en el modo usuario común", Toast.LENGTH_SHORT).show();
            CargaDeDatos();
        } else {
            startActivity(new Intent(MenuPrincipalEstudiante.this, MainActivityEstudiante.class));
            finish();
        }
    }


    private void CargaDeDatos() {

        VerificarEstadoDeCuenta();

        usuariosCommons.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Si el usuario existe en la base de datos
                if (snapshot.exists()) {

                    // El progressbar se oculta
                    progressBarDataUser.setVisibility(View.GONE);


                    linearNombresUser.setVisibility(View.VISIBLE);
                    linearCorreoUser.setVisibility(View.VISIBLE);
                    linearVerificacionUser.setVisibility(View.VISIBLE);

                    // Obtener los datos
                    String uid = "" + snapshot.child("uid").getValue();
                    String nombres = "" + snapshot.child("nombres").getValue();
                    String correo = "" + snapshot.child("correo").getValue();

                    // Setear los datos de los respectivos textView
                    uidPrincipalUser.setText(uid);
                    nombresPrincipalUser.setText(nombres);
                    correoPrincipalUser.setText(correo);

                    // Habilitar los botonoes del menú

                    listarObjetosUser.setEnabled(true);
                    perfilUsuarioUser.setEnabled(true);
                    cerrarSesionUser.setEnabled(true);
                    agregarNotasUser.setEnabled(true);
                    listarNotasUser.setEnabled(true);
                    notasImportantesUser.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.configuracionContrasenia){
            String uidUsuario = uidPrincipalUser.getText().toString();
            Intent intent = new Intent(MenuPrincipalEstudiante.this, ConfiguracionUser.class);
            intent.putExtra("Uid", uidUsuario);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.calendarMenuItemUser){
            VisualizarFecha();
        }

        if (item.getItemId() == R.id.acerdaDeItem) {
            Informacion();
        }


        return super.onOptionsItemSelected(item);
    }

    private void VisualizarFecha(){
        TextView fechaHoy;
        Button buttonCerrar;

        dialogFecha.setContentView(R.layout.cuadro_dialog_fecha_user);

        fechaHoy = dialogFecha.findViewById(R.id.fechaHoyUser);
        buttonCerrar = dialogFecha.findViewById(R.id.btnCerrarCuadradiUser);

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


    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipalEstudiante.this, MainActivityAdmin.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
    }

}