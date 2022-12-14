package com.geraldcode.findapp.Controller.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geraldcode.findapp.Controller.user.ActualizarPassUsuario;
import com.geraldcode.findapp.Controller.user.PerfilUserCommon;
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
import com.hbb20.CountryCodePicker;

import java.util.Calendar;
import java.util.HashMap;

public class PerfilUsuario extends AppCompatActivity {

    ImageView imagenPerfilU, editarFecha, editarPerfilImagen, editarTelefono;
    TextView correoPerfil, uidPerfil, telefonoPerfil, fechaNaciminetoPerfil;
    EditText nombresPerfil, apellidosPerfil, edadPerfil, domicilioPerfil, universidadPerfil, cargoPerfil, tipoDocumentoPerfil, numeroTipoDeDocumentoPerfil;
    Button guardarData;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    DatabaseReference Usuarios;

    Dialog dialogEstablecerTelefono;

    int dia, mes, anio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Perfil de admin");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        InicializarVariables();

        editarTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EstablecerTelefonoUsuario();
            }
        });

        editarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirCalendario();
            }
        });

        guardarData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarDatos();
            }
        });

        editarPerfilImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PerfilUsuario.this, EditarImagenPerfil.class));
            }
        });

    }


    private void InicializarVariables() {
        imagenPerfilU = findViewById(R.id.imagen_perfil_usuario);
        correoPerfil = findViewById(R.id.correoPerfil);
        uidPerfil = findViewById(R.id.uidPerfil);
        nombresPerfil = findViewById(R.id.nombresPerfil);
        apellidosPerfil = findViewById(R.id.apellidosPerfil);
        edadPerfil = findViewById(R.id.edadPerfil);
        telefonoPerfil = findViewById(R.id.telefonoPerfil);
        domicilioPerfil = findViewById(R.id.domicilioPerfil);
        universidadPerfil = findViewById(R.id.universidadPerfil);
        tipoDocumentoPerfil = findViewById(R.id.tipoDocumento);
        numeroTipoDeDocumentoPerfil = findViewById(R.id.numeroDocumento);
        cargoPerfil = findViewById(R.id.cargoPerfil);
        fechaNaciminetoPerfil = findViewById(R.id.fechaNacimientoPerfil);
        editarFecha = findViewById(R.id.editarFecha);
        editarTelefono = findViewById(R.id.editarTelefono);
        editarPerfilImagen = findViewById(R.id.editarImagen);
        dialogEstablecerTelefono = new Dialog(PerfilUsuario.this);

        guardarData = findViewById(R.id.guardarDatos);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    private void LecturaDeDatos() {
        Usuarios.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtener sus datos
                    String uid = "" + snapshot.child("uid").getValue();
                    String nombres = "" + snapshot.child("nombres").getValue();
                    String apellidos = "" + snapshot.child("apellidos").getValue();
                    String correo = "" + snapshot.child("correo").getValue();
                    String edad = "" + snapshot.child("edad").getValue();
                    String telefono = "" + snapshot.child("telefono").getValue();
                    String domicilio = "" + snapshot.child("domicilio").getValue();
                    String universidad = "" + snapshot.child("universidad").getValue();
                    String cargo = "" + snapshot.child("cargo").getValue();
                    String tipoDocumento = "" + snapshot.child("tipo_documento").getValue();
                    String numeroDocumento = "" + snapshot.child("numero_documento").getValue();
                    String fechaDeNacimiento = "" + snapshot.child("fecha_de_nacimiento").getValue();
                    String imagenPerfil = "" + snapshot.child("imagen_perfil").getValue();

                    // Seteo los datos
                    uidPerfil.setText(uid);
                    nombresPerfil.setText(nombres);
                    apellidosPerfil.setText(apellidos);
                    correoPerfil.setText(correo);
                    edadPerfil.setText(edad);
                    telefonoPerfil.setText(telefono);
                    domicilioPerfil.setText(domicilio);
                    universidadPerfil.setText(universidad);
                    cargoPerfil.setText(cargo);
                    tipoDocumentoPerfil.setText(tipoDocumento);
                    numeroTipoDeDocumentoPerfil.setText(numeroDocumento);
                    fechaNaciminetoPerfil.setText(fechaDeNacimiento);
                    CargarImagen(imagenPerfil);
                } else {
                    Toast.makeText(PerfilUsuario.this, "Esperando Datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PerfilUsuario.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CargarImagen(String imagenPerfil) {
        try {
            /* Cuando la imagen ha sido traida exitosamente desde Firebase*/
            Glide.with(getApplicationContext()).load(imagenPerfil).placeholder(R.drawable.imagen_perfil_usuario).into(imagenPerfilU);
        } catch (Exception e) {
            /* Si la imagen no fue traida con ??xito */
            Glide.with(getApplicationContext()).load(R.drawable.imagen_perfil_usuario).into(imagenPerfilU);
        }
    }

    private void EstablecerTelefonoUsuario() {
        CountryCodePicker countryCodePicker;
        EditText establecerTelefono;
        Button buttonAceptarTelefono;

        dialogEstablecerTelefono.setContentView(R.layout.cuadro_dialogo_establecer_telefono);

        countryCodePicker = dialogEstablecerTelefono.findViewById(R.id.ccp);
        establecerTelefono = dialogEstablecerTelefono.findViewById(R.id.establecerTelefono);
        buttonAceptarTelefono = dialogEstablecerTelefono.findViewById(R.id.buttonAceptarTelefono);

        buttonAceptarTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigoPais = countryCodePicker.getSelectedCountryCodeWithPlus();
                String telefono = establecerTelefono.getText().toString();
                String codigoPaisTelefono = codigoPais + telefono; // -51939038017

                if (!telefono.equals("")) {
                    telefonoPerfil.setText(codigoPaisTelefono);
                    dialogEstablecerTelefono.dismiss();
                } else {
                    Toast.makeText(PerfilUsuario.this, "Ingrese un n??mero telef??nico", Toast.LENGTH_SHORT).show();
                    dialogEstablecerTelefono.dismiss();
                }
            }
        });

        dialogEstablecerTelefono.show();
        dialogEstablecerTelefono.setCanceledOnTouchOutside(true);
    }

    private void AbrirCalendario() {
        final Calendar calendario = Calendar.getInstance();

        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        anio = calendario.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(PerfilUsuario.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anioSeleccionado, int mesSeleccionado, int diaSeleccionado) {
                String diaFormateado, mesFormateado;

                // Obtener el d??a
                if (diaSeleccionado < 10) {
                    diaFormateado = "0" + String.valueOf(diaSeleccionado);
                    // Antes: 9/11/2022 - Ahora 09/11/2022
                } else {
                    diaFormateado = String.valueOf(diaSeleccionado);
                    // Ejemplo 13/08/2022
                }

                // Obtener el mes
                int mes = mesSeleccionado + 1;

                if (mes < 10) {
                    mesFormateado = "0" + String.valueOf(mes);
                    // Antes : 09/8/2022 - Ahora 09/08/2022
                } else {
                    mesFormateado = String.valueOf(mes);
                    // Ejemplo 13/10/2022 - 13/11/2022 - 13/12/2022
                }

                // Setear fecha en TextView
                fechaNaciminetoPerfil.setText(diaFormateado + "/" + mesFormateado + "/" + anioSeleccionado);

            }
        }, anio, mes, dia);
        datePickerDialog.show();
    }

    private void ActualizarDatos() {
        String aNombres = nombresPerfil.getText().toString().trim();
        String aApellidos = apellidosPerfil.getText().toString().trim().trim();
        String aEdad = edadPerfil.getText().toString().trim().trim();
        String aTelefono = telefonoPerfil.getText().toString().trim().trim();
        String aDomicilio = domicilioPerfil.getText().toString();
        String aUniversidad = universidadPerfil.getText().toString();
        String aCargo = cargoPerfil.getText().toString();
        String aTipoDocumento = tipoDocumentoPerfil.getText().toString();
        String aNumeroTipoDocumento = numeroTipoDeDocumentoPerfil.getText().toString();
        String aFechaNacimiento = fechaNaciminetoPerfil.getText().toString();

        HashMap<String, Object> datosActualizar = new HashMap<>();

        datosActualizar.put("nombres", aNombres);
        datosActualizar.put("apellidos", aApellidos);
        datosActualizar.put("edad", aEdad);
        datosActualizar.put("telefono", aTelefono);
        datosActualizar.put("domicilio", aDomicilio);
        datosActualizar.put("universidad", aUniversidad);
        datosActualizar.put("cargo", aCargo);
        datosActualizar.put("tipo_documento", aTipoDocumento);
        datosActualizar.put("numero_documento", aNumeroTipoDocumento);
        datosActualizar.put("fecha_de_nacimiento", aFechaNacimiento);

        Usuarios.child(firebaseUser.getUid()).updateChildren(datosActualizar).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PerfilUsuario.this, "Actualizado Correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PerfilUsuario.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ComprobarInicioSesion() {
        if (firebaseUser != null) {
            LecturaDeDatos();
        } else {
            startActivity(new Intent(PerfilUsuario.this, MenuPrincipalAdmin.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar_password_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.actualizarpassAdmin){
            startActivity(new Intent(PerfilUsuario.this, ActualizarPassAdmin.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        ComprobarInicioSesion();
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}