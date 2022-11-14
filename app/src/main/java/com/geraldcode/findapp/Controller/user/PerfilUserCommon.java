package com.geraldcode.findapp.Controller.user;

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
import com.geraldcode.findapp.Controller.admin.EditarImagenPerfil;
import com.geraldcode.findapp.Controller.admin.MenuPrincipalAdmin;
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

public class PerfilUserCommon extends AppCompatActivity {

    ImageView imagenPerfilU, editarFecha, editarPerfilImagen, editarTelefono;
    TextView correoPerfil, uidPerfil, telefonoPerfil, fechaNaciminetoPerfil;
    EditText nombresPerfil, apellidosPerfil, edadPerfil, domicilioPerfil, universidadPerfil, facultadPerfil,carreraPerfil, tipoDocumentoPerfil, numeroTipoDeDocumentoPerfil;
    Button guardarData;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    DatabaseReference Usuarios;

    Dialog dialogEstablecerTelefono;

    int dia, mes, anio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_user_common);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Perfil de usuario");
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
                startActivity(new Intent(PerfilUserCommon.this, EditarImagenPerfilUser.class));
            }
        });

    }


    private void InicializarVariables() {
        imagenPerfilU = findViewById(R.id.imagen_perfil_usuario_common);
        correoPerfil = findViewById(R.id.correoPerfilUser);
        uidPerfil = findViewById(R.id.uidPerfilUser);
        nombresPerfil = findViewById(R.id.nombresPerfilUser);
        apellidosPerfil = findViewById(R.id.apellidosPerfilUser);
        edadPerfil = findViewById(R.id.edadPerfilUser);
        telefonoPerfil = findViewById(R.id.telefonoPerfilUser);
        domicilioPerfil = findViewById(R.id.domicilioPerfilUser);
        universidadPerfil = findViewById(R.id.universidadPerfilUser);
        tipoDocumentoPerfil = findViewById(R.id.tipoDocumentoUser);
        numeroTipoDeDocumentoPerfil = findViewById(R.id.numeroDocumentoUser);
        facultadPerfil = findViewById(R.id.facultadPerfilUser);
        carreraPerfil = findViewById(R.id.carreraPerfilUser);
        fechaNaciminetoPerfil = findViewById(R.id.fechaNacimientoPerfilUser);
        editarFecha = findViewById(R.id.editarFechaUser);
        editarTelefono = findViewById(R.id.editarTelefonoUser);
        editarPerfilImagen = findViewById(R.id.editarImagenUser);
        dialogEstablecerTelefono = new Dialog(PerfilUserCommon.this);

        guardarData = findViewById(R.id.guardarDatosUser);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Usuarios = FirebaseDatabase.getInstance().getReference("UsuariosCommons");
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
                    String facultad = "" + snapshot.child("facultad").getValue();
                    String carrera = "" + snapshot.child("carrera").getValue();
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
                    facultadPerfil.setText(facultad);
                    carreraPerfil.setText(carrera);
                    tipoDocumentoPerfil.setText(tipoDocumento);
                    numeroTipoDeDocumentoPerfil.setText(numeroDocumento);
                    fechaNaciminetoPerfil.setText(fechaDeNacimiento);
                    CargarImagen(imagenPerfil);
                } else {
                    Toast.makeText(PerfilUserCommon.this, "Esperando Datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PerfilUserCommon.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CargarImagen(String imagenPerfil) {
        try {
            /* Cuando la imagen ha sido traida exitosamente desde Firebase*/
            Glide.with(getApplicationContext()).load(imagenPerfil).placeholder(R.drawable.imagen_perfil_usuario).into(imagenPerfilU);
        } catch (Exception e) {
            /* Si la imagen no fue traida con éxito */
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
                    Toast.makeText(PerfilUserCommon.this, "Ingrese un número telefónico", Toast.LENGTH_SHORT).show();
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(PerfilUserCommon.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anioSeleccionado, int mesSeleccionado, int diaSeleccionado) {
                String diaFormateado, mesFormateado;

                // Obtener el día
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
        String aFacultad = facultadPerfil.getText().toString();
        String aCarrera = carreraPerfil.getText().toString();
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
        datosActualizar.put("facultad", aFacultad);
        datosActualizar.put("carrera", aCarrera);
        datosActualizar.put("tipo_documento", aTipoDocumento);
        datosActualizar.put("numero_documento", aNumeroTipoDocumento);
        datosActualizar.put("fecha_de_nacimiento", aFechaNacimiento);

        Usuarios.child(firebaseUser.getUid()).updateChildren(datosActualizar).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PerfilUserCommon.this, "Actualizado Correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PerfilUserCommon.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ComprobarInicioSesion() {
        if (firebaseUser != null) {
            LecturaDeDatos();
        } else {
            startActivity(new Intent(PerfilUserCommon.this, MenuPrincipalEstudiante.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar_password, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.actualizarpassUser){
            startActivity(new Intent(PerfilUserCommon.this, ActualizarPassUsuario.class));
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