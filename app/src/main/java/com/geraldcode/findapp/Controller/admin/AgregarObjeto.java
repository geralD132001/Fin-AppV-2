package com.geraldcode.findapp.Controller.admin;

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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geraldcode.findapp.Controller.user.EditarImagenPerfilUser;
import com.geraldcode.findapp.Controller.user.PerfilUserCommon;
import com.geraldcode.findapp.Model.Objeto;
import com.geraldcode.findapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AgregarObjeto extends AppCompatActivity {

    TextView fechaHoralActual, fechaPerdidaAddObjeto, estadoAddObjeto, telefonoContactoAddObjeto, idObjetos;
    EditText tituloAddObjeto, descripcionAddObjeto, categoriaAddObjeto, lugarPerdidaAddObjeto;
    Button buttonCalendarioAddObjeto, buttonGuardarObjeto;

    ImageView editarTelefonoContactoAddObjeto, imagenObjetoPerdido;

    Dialog dialog, dialogEstablecerTelefono, dialogElegirImagen;

    ProgressDialog progressDialog;

    String rutaAlmacenamiento = "Objetos_Subida";

    Uri rutaArchivoUri = null;

    int dia, mes, anio;

    StorageReference mStorageReference;
    DatabaseReference BD_Firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_objeto);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // El inicializar variables tiene que ir siempre antes de capturar los datos
        InicializarVariables();
        ObtenerFechaHoraActual();


        buttonCalendarioAddObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendario = Calendar.getInstance();

                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AgregarObjeto.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int anioSeleccionado, int mesSeleccionado, int diaSeleccionado) {

                        String diaFormateado, mesFormateado;

                        // OBTENER DÍA
                        if (diaSeleccionado < 10) {
                            diaFormateado = "0" + String.valueOf(diaSeleccionado);
                        } else {
                            diaFormateado = String.valueOf(diaSeleccionado);
                        }

                        // OBTENER EL MES
                        int Mes = mesSeleccionado + 1;

                        if (Mes < 10) {
                            mesFormateado = "0" + String.valueOf(Mes);
                        } else {
                            mesFormateado = String.valueOf(Mes);
                        }

                        // Setear Fecha en TextView
                        fechaPerdidaAddObjeto.setText(diaFormateado + "/" + mesFormateado + "/" + anioSeleccionado);
                    }
                }
                        , anio, mes, dia);
                datePickerDialog.show();
            }
        });

        editarTelefonoContactoAddObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EstablecerTelefonoContactoObjeto();
            }
        });

        imagenObjetoPerdido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ElegirImagenDe();
            }
        });

        buttonGuardarObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubirImagen();
            }
        });
    }

    private void ElegirImagenDe() {

        Button buttonElegirGaleria, buttonElegirCamara;

        dialogElegirImagen.setContentView(R.layout.cuadro_dialogo_elegir_imagen_objeto);

        buttonElegirGaleria = dialogElegirImagen.findViewById(R.id.buttonElegirGaleriaObjeto);
        buttonElegirCamara = dialogElegirImagen.findViewById(R.id.buttonElegirCamaraObjeto);

        buttonElegirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AgregarObjeto.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    SeleccionarImagenGaleria();
                    dialogElegirImagen.dismiss();
                } else {
                    SolicitudPermisoGaleria.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    dialogElegirImagen.dismiss();
                }
            }
        });

        buttonElegirCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AgregarObjeto.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    SeleccionarImagenCamara();
                    dialogElegirImagen.dismiss();
                } else {
                    SolicitudPermisoCamara.launch(Manifest.permission.CAMERA);
                    dialogElegirImagen.dismiss();
                }
            }
        });

        dialogElegirImagen.show();
        dialogElegirImagen.setCanceledOnTouchOutside(true);
    }

    private void SeleccionarImagenCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva imagen");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripción de imagen");
        rutaArchivoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, rutaArchivoUri);
        camaraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> camaraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagenObjetoPerdido.setImageURI(rutaArchivoUri);
                    } else {
                        Toast.makeText(AgregarObjeto.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    /*PERMISO PARA ACCEDER A LA CAMARA*/
    private ActivityResultLauncher<String> SolicitudPermisoCamara =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    SeleccionarImagenCamara();
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            });

    private ActivityResultLauncher<String> SolicitudPermisoGaleria = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            SeleccionarImagenGaleria();
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    });


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
                rutaArchivoUri = data.getData();
                imagenObjetoPerdido.setImageURI(rutaArchivoUri);
            } else {
                Toast.makeText(AgregarObjeto.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        }
    });


    private void EstablecerTelefonoContactoObjeto() {
        CountryCodePicker ccp;
        EditText Establecer_Telefono;
        Button Btn_Aceptar_Telefono;

        dialogEstablecerTelefono.setContentView(R.layout.cuadro_dialogo_establecer_telefono);

        ccp = dialogEstablecerTelefono.findViewById(R.id.ccp);
        Establecer_Telefono = dialogEstablecerTelefono.findViewById(R.id.establecerTelefono);
        Btn_Aceptar_Telefono = dialogEstablecerTelefono.findViewById(R.id.buttonAceptarTelefono);

        Btn_Aceptar_Telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigoPais = ccp.getSelectedCountryCodeWithPlus();
                String telefono = Establecer_Telefono.getText().toString();
                String codigoPaisTelefono = codigoPais + telefono; //+51956605043

                if (!telefono.equals("")) {
                    telefonoContactoAddObjeto.setText(codigoPaisTelefono);
                    dialogEstablecerTelefono.dismiss();
                } else {
                    Toast.makeText(AgregarObjeto.this, "Ingrese un número telefónico", Toast.LENGTH_SHORT).show();
                    dialogEstablecerTelefono.dismiss();
                }
            }
        });

        dialogEstablecerTelefono.show();
        dialogEstablecerTelefono.setCanceledOnTouchOutside(true);
    }

    private void InicializarVariables() {
        fechaHoralActual = findViewById(R.id.fechaHoraActualAddObjeto);
        fechaPerdidaAddObjeto = findViewById(R.id.fechaAddObjeto);
        estadoAddObjeto = findViewById(R.id.estadoAddObjeto);
        tituloAddObjeto = findViewById(R.id.tituloAddObjeto);
        descripcionAddObjeto = findViewById(R.id.descripcionAddObjeto);

        telefonoContactoAddObjeto = findViewById(R.id.telefonoAddObjeto);
        imagenObjetoPerdido = findViewById(R.id.imageObjetoAdd);
        editarTelefonoContactoAddObjeto = findViewById(R.id.editarTelefonoContactoAddObjeto);
        categoriaAddObjeto = findViewById(R.id.categoriaAddObjeto);
        lugarPerdidaAddObjeto = findViewById(R.id.lugarPerdidaAddObjeto);
        buttonGuardarObjeto = findViewById(R.id.buttonAddObjeto);
        buttonCalendarioAddObjeto = findViewById(R.id.btnCalendarioAddObjeto);
        progressDialog = new ProgressDialog(AgregarObjeto.this);

        dialogElegirImagen = new Dialog(AgregarObjeto.this);
        dialogEstablecerTelefono = new Dialog(AgregarObjeto.this);
        idObjetos = findViewById(R.id.idObjetos);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        BD_Firebase = FirebaseDatabase.getInstance().getReference("Objetos");

        dialog = new Dialog(AgregarObjeto.this);


    }


    private void ObtenerFechaHoraActual() {
        String fechaHoraRegistro = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        fechaHoralActual.setText(fechaHoraRegistro);
    }

    private void SubirImagen() {

        String fechaHoraActual = fechaHoralActual.getText().toString();
        String tituloObjeto = tituloAddObjeto.getText().toString();
        String descripcionObjeto = descripcionAddObjeto.getText().toString();
        String categoriaObjeto = categoriaAddObjeto.getText().toString();
        String lugarDePerdida = lugarPerdidaAddObjeto.getText().toString();
        String contactoPerdida = telefonoContactoAddObjeto.getText().toString();
        String fechaPerdida = fechaPerdidaAddObjeto.getText().toString();
        String estadoObjeto = estadoAddObjeto.getText().toString();

        String idObjeto = BD_Firebase.push().getKey();

        //Validar que el nombre y la imagen no sean nulos
        if (fechaHoraActual.equals("") || tituloObjeto.equals("") || descripcionObjeto.equals("")
                || categoriaObjeto.equals("")
                || lugarDePerdida.equals("")
                || contactoPerdida.equals("")
                || fechaPerdida.equals("")
                || estadoObjeto.equals("") || rutaArchivoUri == null) {
            ValidarRegistroObjeto();
        } else {
            progressDialog.setTitle("Espere por favor");
            progressDialog.setMessage("Guardando información OBJETO ...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            StorageReference storageReference2 = mStorageReference.child(rutaAlmacenamiento + System.currentTimeMillis() + "." + ObtenerExtensionDelArchivo(rutaArchivoUri));
            //20210204_1234.PNG
            storageReference2.putFile(rutaArchivoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;

                            Uri downloadURI = uriTask.getResult();

                            Objeto objeto = new Objeto(idObjeto, fechaHoraActual, tituloObjeto, descripcionObjeto, categoriaObjeto, lugarDePerdida, contactoPerdida, fechaPerdida, estadoObjeto, downloadURI.toString());

                            assert idObjeto != null;

                            BD_Firebase.child(idObjeto).setValue(objeto);

                            progressDialog.dismiss();
                            Toast.makeText(AgregarObjeto.this, "Subido Exitosamente", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(AgregarObjeto.this, ListarObjetos.class));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AgregarObjeto.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setTitle("Publicando");
                            progressDialog.setCancelable(false);
                        }
                    });

        }
    }

    private String ObtenerExtensionDelArchivo(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    /* private void AgregarObjetoBD() {
        String fechaHoraActual = fechaHoralActual.getText().toString();
        String tituloObjeto = tituloAddObjeto.getText().toString();
        String descripcionObjeto = descripcionAddObjeto.getText().toString();
        String categoriaObjeto = categoriaAddObjeto.getText().toString();
        String lugarDePerdida = lugarPerdidaAddObjeto.getText().toString();
        String contactoPerdida = telefonoContactoAddObjeto.getText().toString();
        String fechaPerdida = fechaPerdidaAddObjeto.getText().toString();
        String estadoObjeto = estadoAddObjeto.getText().toString();

        String idObjeto = BD_Firebase.push().getKey();

        if (!fechaHoraActual.equals("")
                && !tituloObjeto.equals("")
                && !descripcionObjeto.equals("")
                && !categoriaObjeto.equals("")
                && !lugarDePerdida.equals("")
                && !contactoPerdida.equals("")
                && !fechaPerdida.equals("")
                && !estadoObjeto.equals("")) {

            Objeto objeto = new Objeto(idObjeto,
                    fechaHoraActual,
                    tituloObjeto,
                    descripcionObjeto,
                    categoriaObjeto,
                    lugarDePerdida,
                    contactoPerdida,
                    fechaPerdida,
                    estadoObjeto,
                    "");

            assert idObjeto != null;
            BD_Firebase.child(idObjeto).setValue(objeto);

            Toast.makeText(this, "Se ha agregado el objeto de manera exitosamente", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } else {
            ValidarRegistroObjeto();
        }

    } */

    private void ValidarRegistroObjeto() {

        Button buttonValidarRegistroObjeto;

        dialog.setContentView(R.layout.cuadro_dialogo_validar_registro_c);
        buttonValidarRegistroObjeto = dialog.findViewById(R.id.buttonValidarRegistroObjeto);

        buttonValidarRegistroObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar_objeto2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.agregarObjetoMenu2:
                SubirImagen();
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