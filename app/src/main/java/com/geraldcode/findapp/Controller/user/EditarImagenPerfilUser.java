package com.geraldcode.findapp.Controller.user;

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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geraldcode.findapp.Controller.admin.EditarImagenPerfil;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditarImagenPerfilUser extends AppCompatActivity {

    ImageView imagenPerfilActualizar;
    Button buttonElegirImagenDe, buttonActualizarImagen;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Dialog dialogElegirImagen;

    Uri imagenUri = null;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_imagen_perfil_user);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Seleccionar imagen");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imagenPerfilActualizar = findViewById(R.id.imagenPerfilActualizarUser);
        buttonElegirImagenDe = findViewById(R.id.buttonImagenDeUser);
        buttonActualizarImagen = findViewById(R.id.buttonActualizarImagenUser);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        dialogElegirImagen = new Dialog(EditarImagenPerfilUser.this);

        buttonElegirImagenDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ElegirImagenDe();
            }
        });

        buttonActualizarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagenUri == null) {
                    Toast.makeText(EditarImagenPerfilUser.this, "Inserte una nueva imagen", Toast.LENGTH_SHORT).show();
                } else {
                    SubirImagenStorage();
                }
            }
        });

        progressDialog = new ProgressDialog(EditarImagenPerfilUser.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        LecturaImagen();
    }

    private void LecturaImagen() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UsuariosCommons");
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Obtener el dato imagen
                String imagenPerfil = "" + snapshot.child("imagen_perfil").getValue();

                Glide.with(getApplicationContext())
                        .load(imagenPerfil)
                        .placeholder(R.drawable.imagen_perfil_usuario)
                        .into(imagenPerfilActualizar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SubirImagenStorage() {
        progressDialog.setMessage("Subiendo Imagen");
        progressDialog.show();
        String carpetaImagenes = "ImagenesPerfilUsuarios/";
        String nombreImagen = carpetaImagenes + firebaseUser.getUid();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(nombreImagen);
        storageReference.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String uirImagen = "" + uriTask.getResult();

                ActualizarImagenDb(uirImagen);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarImagenPerfilUser.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarImagenDb(String uirImagen) {
        progressDialog.setMessage("Actualizando la imagen");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        if (imagenUri != null) {
            hashMap.put("imagen_perfil", "" + uirImagen);
        }


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UsuariosCommons");
        databaseReference.child(firebaseUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(EditarImagenPerfilUser.this, "Imagen se ha actualizado con ??xito", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    private void ElegirImagenDe() {

        Button buttonElegirGaleria, buttonElegirCamara;

        dialogElegirImagen.setContentView(R.layout.cuadro_dialogo_elegir_imagen_user);

        buttonElegirGaleria = dialogElegirImagen.findViewById(R.id.buttonElegirGaleriaUser);
        buttonElegirCamara = dialogElegirImagen.findViewById(R.id.buttonElegirCamaraUser);

        buttonElegirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditarImagenPerfilUser.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
                if (ContextCompat.checkSelfPermission(EditarImagenPerfilUser.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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

    private void SeleccionarImagenGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaAcitvityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galeriaAcitvityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Obtener la uri de la imagen
                        Intent data = result.getData();
                        imagenUri = data.getData();

                        // Setear la imagen seleccionada en la ImagenView
                        imagenPerfilActualizar.setImageURI(imagenUri);
                    } else {
                        Toast.makeText(EditarImagenPerfilUser.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    /* Permiso para acceder a la galeria */
    private ActivityResultLauncher<String> SolicitudPermisoGaleria = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            SeleccionarImagenGaleria();
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    });

    private void SeleccionarImagenCamara() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Nueva imagen");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripci??n de imagen");
        imagenUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri);
        camaraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> camaraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagenPerfilActualizar.setImageURI(imagenUri);
                    } else {
                        Toast.makeText(EditarImagenPerfilUser.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    /* Permiso para acceder a la camara */
    private ActivityResultLauncher<String> SolicitudPermisoCamara = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            SeleccionarImagenCamara();
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}