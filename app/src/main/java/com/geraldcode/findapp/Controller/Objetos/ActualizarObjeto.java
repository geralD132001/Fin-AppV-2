package com.geraldcode.findapp.Controller.Objetos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geraldcode.findapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.util.Calendar;
import java.util.HashMap;

public class ActualizarObjeto extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView idObjetoUpdate, uidUsuarioObjetoUpdate, correoUsuarioObjetoUpdate,
            fechaRegistroObjetoUpdate, telefonoContactoObjetoUpdate,
            fechaPerdidaObjetoUpdate, estadoObjetoUpdate,
            estadoNuevoObjetoUpdate;

    EditText titutoObjetoUpdate, descripcionObjetoUpdate, categoriaObjetoUpdate, lugarPerdidaObjetoUpdate;

    ImageView imagenObjetoUpdate, updateImagenObjeto, imagenContactoTelefonoUpdate, objetoPerdido, objetoEncontrado, objetoEntregado;

    Button buttonUpdateData, buttonCalendarioUpdate;

    Spinner spinnerEstadoObjeto;

    Dialog dialogEstablecerTelefono;

    String idObjetoR, uidUsuarioObjetoR, correoUsuarioObjetoR, fechaRegistoObjetoR,
            tituloObjetoR, descripcionObjetoR, categoriaObjetoR, lugarPerdidaObjetoR,
            fechaPerdidaObjetoR, telefonoContactoObjetoR, estadoObjetoR;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Uri imagenUri = null;

    ProgressDialog progressDialog;

    int dia, mes, anio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_objeto);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Actualizar Objeto");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        IniciliarVistas();
        RecuperarDatos();
        SetearDatosRecuperados();
        ComprobarEstadoObjeto();
        SpinnerEstadoObjeto();
        ObtenerImagenObjeto();

        buttonCalendarioUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeleccionarFecha();
            }
        });

        imagenContactoTelefonoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EstablecerTelefonoContacto();
            }
        });

        buttonUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarInformacionObjeto();
            }
        });

        updateImagenObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ActualizarObjeto.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    SeleccionarImagenGaleria();
                } else {
                    solicitarPermisoGaleria.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        progressDialog = new ProgressDialog(ActualizarObjeto.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    private void SeleccionarImagenGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galeriaActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                imagenUri = data.getData();
                imagenObjetoUpdate.setImageURI(imagenUri);
                SubirImagenStorage();
            } else {
                Toast.makeText(ActualizarObjeto.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private void SubirImagenStorage() {
        progressDialog.setMessage("Subiendo imagen");
        progressDialog.show();
        String idObjeto = getIntent().getStringExtra("idObjeto");

        String carpetaImagenesObjetos = "ImagenesObjetos/";
        String nombreImagen = carpetaImagenesObjetos + idObjeto;

        StorageReference reference = FirebaseStorage.getInstance().getReference(nombreImagen);
        reference.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String uriImagen = "" + uriTask.getResult();
                ActualizarImagenContactoDb(uriImagen);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActualizarObjeto.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarImagenContactoDb(String uriImagen) {
        progressDialog.setMessage("Actualizando la imagen");
        progressDialog.show();

        String idObjeto = getIntent().getStringExtra("idObjeto");
        HashMap<String, Object> hashMap = new HashMap<>();

        if (imagenUri != null) {
            hashMap.put("imagenObjeto", "" + uriImagen);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(firebaseUser.getUid()).child("Objetos").child(idObjeto).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(ActualizarObjeto.this, "Imagen Actualizada con éxito", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ActualizarObjeto.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarInformacionObjeto() {
        String tituloUpdate = titutoObjetoUpdate.getText().toString().trim();
        String descricionUpdate = descripcionObjetoUpdate.getText().toString().trim();
        String categoriaUpdate = categoriaObjetoUpdate.getText().toString().trim();
        String lugarPerdidaUpdate = lugarPerdidaObjetoUpdate.getText().toString().trim();
        String fechaPerdidaUpdate = fechaPerdidaObjetoUpdate.getText().toString().trim();
        String telefonoContactoUpdate = telefonoContactoObjetoUpdate.getText().toString().trim();
        String estadoUpdate = estadoNuevoObjetoUpdate.getText().toString().trim();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios");

        Query query = databaseReference.child(firebaseUser.getUid()).child("Objetos").orderByChild("idObjeto").equalTo(idObjetoR);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().child("titulo").setValue(tituloUpdate);
                    ds.getRef().child("descripcion").setValue(descricionUpdate);
                    ds.getRef().child("categoria").setValue(categoriaUpdate);
                    ds.getRef().child("lugarPerdida").setValue(lugarPerdidaUpdate);
                    ds.getRef().child("fechaPerdida").setValue(fechaPerdidaUpdate);
                    ds.getRef().child("telefonoContacto").setValue(telefonoContactoUpdate);
                    ds.getRef().child("estado").setValue(estadoUpdate);
                }

                Toast.makeText(ActualizarObjeto.this, "Información actualizada", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void EstablecerTelefonoContacto() {
        CountryCodePicker ccp;
        EditText establecerTelefono;
        Button buttonAceptarTelefono;

        dialogEstablecerTelefono.setContentView(R.layout.cuadro_dialogo_establecer_telefono);

        ccp = dialogEstablecerTelefono.findViewById(R.id.ccp);
        establecerTelefono = dialogEstablecerTelefono.findViewById(R.id.establecerTelefono);
        buttonAceptarTelefono = dialogEstablecerTelefono.findViewById(R.id.buttonAceptarTelefono);

        buttonAceptarTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigoPais = ccp.getSelectedCountryCodeWithPlus();
                String telefono = establecerTelefono.getText().toString();
                String codigoPaisTelefono = codigoPais + telefono; //+51956605043

                if (!telefono.equals("")) {
                    telefonoContactoObjetoUpdate.setText(codigoPaisTelefono);
                    dialogEstablecerTelefono.dismiss();
                } else {
                    Toast.makeText(ActualizarObjeto.this, "Ingrese un número telefónico", Toast.LENGTH_SHORT).show();
                    dialogEstablecerTelefono.dismiss();
                }
            }
        });

        dialogEstablecerTelefono.show();
        dialogEstablecerTelefono.setCanceledOnTouchOutside(true);
    }

    private void SeleccionarFecha() {
        final Calendar calendario = Calendar.getInstance();

        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        anio = calendario.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ActualizarObjeto.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int AnioSeleccionado, int MesSeleccionado, int DiaSeleccionado) {

                String diaFormateado, mesFormateado;

                //OBTENER DIA
                if (DiaSeleccionado < 10) {
                    diaFormateado = "0" + String.valueOf(DiaSeleccionado);
                    // Antes: 9/11/2022 -  Ahora 09/11/2022
                } else {
                    diaFormateado = String.valueOf(DiaSeleccionado);
                    //Ejemplo 13/08/2022
                }

                //OBTENER EL MES
                int Mes = MesSeleccionado + 1;

                if (Mes < 10) {
                    mesFormateado = "0" + String.valueOf(Mes);
                    // Antes: 09/8/2022 -  Ahora 09/08/2022
                } else {
                    mesFormateado = String.valueOf(Mes);
                    //Ejemplo 13/10/2022 - 13/11/2022 - 13/12/2022

                }

                //Setear fecha en TextView
                fechaPerdidaObjetoUpdate.setText(diaFormateado + "/" + mesFormateado + "/" + AnioSeleccionado);

            }
        }
                , anio, mes, dia);
        datePickerDialog.show();
    }

    // Inicializar vistas
    private void IniciliarVistas() {
        idObjetoUpdate = findViewById(R.id.idUpdateObjeto);
        uidUsuarioObjetoUpdate = findViewById(R.id.uidUsuarioUpdateObjeto);
        correoUsuarioObjetoUpdate = findViewById(R.id.correoUsuarioUpdateObjeto);
        fechaRegistroObjetoUpdate = findViewById(R.id.fechaHoraActualUpdateObjeto);
        titutoObjetoUpdate = findViewById(R.id.tituloUpdateObjeto);
        descripcionObjetoUpdate = findViewById(R.id.descripcionUpdateObjeto);
        categoriaObjetoUpdate = findViewById(R.id.categoriaUpdateObjeto);
        telefonoContactoObjetoUpdate = findViewById(R.id.telefonoUpdateObjeto);
        lugarPerdidaObjetoUpdate = findViewById(R.id.lugarPerdidaUpdateObjeto);
        fechaPerdidaObjetoUpdate = findViewById(R.id.fechaPerdidaUpdateObjeto);
        estadoObjetoUpdate = findViewById(R.id.estadoUpdateObjeto);
        imagenObjetoUpdate = findViewById(R.id.imageObjetoUpdate);
        updateImagenObjeto = findViewById(R.id.updateImagenObjeto);
        imagenContactoTelefonoUpdate = findViewById(R.id.editarTelefonoContactoUpdateObjeto);
        buttonCalendarioUpdate = findViewById(R.id.btnCalendarioUpdateObjeto);
        buttonUpdateData = findViewById(R.id.buttonUpdateObjetobuttonUpdateObjeto);
        objetoPerdido = findViewById(R.id.objetoPerdidoUpdate);
        objetoEncontrado = findViewById(R.id.objetoEncontradoUpdate);
        objetoEntregado = findViewById(R.id.objetoEntregadoUpdate);
        spinnerEstadoObjeto = findViewById(R.id.spinnerEstadoObjeto);
        estadoNuevoObjetoUpdate = findViewById(R.id.estadoNewUpdate);

        dialogEstablecerTelefono = new Dialog(ActualizarObjeto.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    // Recuperar los datos
    private void RecuperarDatos() {
        Bundle intent = getIntent().getExtras();

        idObjetoR = intent.getString("idObjeto");
        uidUsuarioObjetoR = intent.getString("uidUsuarioObjeto");
        fechaRegistoObjetoR = intent.getString("fechaRegistro");
        correoUsuarioObjetoR = intent.getString("correoUsuarioObjeto");
        tituloObjetoR = intent.getString("tituloObjeto");
        descripcionObjetoR = intent.getString("descripcionObjeto");
        categoriaObjetoR = intent.getString("categoriaObjeto");
        lugarPerdidaObjetoR = intent.getString("lugarPerdidaObjeto");
        telefonoContactoObjetoR = intent.getString("telefonoContactoObjeto");
        fechaPerdidaObjetoR = intent.getString("fechaPerdidaObjeto");
        estadoObjetoR = intent.getString("estadoObjeto");
    }


    private void ObtenerImagenObjeto() {
        String imagen = getIntent().getStringExtra("imagenObjeto");

        try {
            Glide.with(getApplicationContext()).load(imagen).placeholder(R.drawable.imagen_contacto).into(imagenObjetoUpdate);

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void SetearDatosRecuperados() {
        idObjetoUpdate.setText(idObjetoR);
        uidUsuarioObjetoUpdate.setText(uidUsuarioObjetoR);
        correoUsuarioObjetoUpdate.setText(correoUsuarioObjetoR);
        fechaRegistroObjetoUpdate.setText(fechaRegistoObjetoR);
        titutoObjetoUpdate.setText(tituloObjetoR);
        descripcionObjetoUpdate.setText(descripcionObjetoR);
        categoriaObjetoUpdate.setText(categoriaObjetoR);
        lugarPerdidaObjetoUpdate.setText(lugarPerdidaObjetoR);
        telefonoContactoObjetoUpdate.setText(telefonoContactoObjetoR);
        fechaPerdidaObjetoUpdate.setText(fechaPerdidaObjetoR);
        estadoObjetoUpdate.setText(estadoObjetoR);
    }

    private void ComprobarEstadoObjeto() {
        String estadoOjeto = estadoObjetoUpdate.getText().toString();

        if (estadoOjeto.equals("Perdido")) {
            objetoPerdido.setVisibility(View.VISIBLE);
        }

        if (estadoOjeto.equals("Encontrado")) {
            objetoEncontrado.setVisibility(View.VISIBLE);
        }

        if (estadoOjeto.equals("Entregado")) {
            objetoEntregado.setVisibility(View.VISIBLE);
        }
    }

    private void SpinnerEstadoObjeto() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.estadosObjetos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerEstadoObjeto.setAdapter(adapter);
        spinnerEstadoObjeto.setOnItemSelectedListener(this);
    }

    private ActivityResultLauncher<String> solicitarPermisoGaleria = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    SeleccionarImagenGaleria();
                } else {
                    Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
                }
            }
    );


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String estadoActual = estadoObjetoUpdate.getText().toString();

        String posicion1 = parent.getItemAtPosition(1).toString();

        String estadoSeleccionado = parent.getItemAtPosition(position).toString();
        estadoNuevoObjetoUpdate.setText(estadoSeleccionado);

        if (estadoActual.equals("Encontrado")) {
            estadoNuevoObjetoUpdate.setText(posicion1);
        }
        if (estadoActual.equals("Entregado")) {
            estadoNuevoObjetoUpdate.setText(posicion1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actualizatObjetoMenu:
                ActualizarInformacionObjeto();
                // Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}