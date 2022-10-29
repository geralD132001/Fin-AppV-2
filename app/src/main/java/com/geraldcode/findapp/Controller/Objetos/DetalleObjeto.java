package com.geraldcode.findapp.Controller.Objetos;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geraldcode.findapp.R;

public class DetalleObjeto extends AppCompatActivity {

    ImageView imagenObjetoDetalle;
    TextView idObjetoDetalle, uidObjetoDetalle, correoObjetoDetalle, fechaRegistroObjetoDetalle, tituloObjetoDetalle,
            descripcionObjetoDetalle, categoriaObjetoDetalle, lugarPerdidaObjetoDetalle, fechaPerdidaObjetoDetalle,
            telefonoContactoObjetoDetalle, estadoObjetoDetalle;

    /* String donde almacenaremos los datos del contacto seleccionado */
    String idObjeto, uidUsuarioObjeto, correoUsuarioObjeto, fechaRegistroObjeto, tituloObjeto, descripcionObjeto,
            categoriaObjeto, lugarPerdidaObjeto, fechaPerdidaObjeto, contactoObjeto, estadoObjeto;

    Button llamarContacto, enviarMensajeContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_objeto);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle Objeto");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        InicializarVariables();
        RecuperarDatosObjeto();
        SetearDatosDelObjeto();
        ObtenerImgenObjeto();

        llamarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DetalleObjeto.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    LlamarContacto();
                } else {
                    solicitudPermisoLLamada.launch(Manifest.permission.CALL_PHONE);
                }
            }
        });

        enviarMensajeContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DetalleObjeto.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    EnviarMensaje();
                } else {
                    solicitudPermisoMensaje.launch(Manifest.permission.CALL_PHONE);
                }
            }
        });
    }

    private void LlamarContacto() {
        String telefono = telefonoContactoObjetoDetalle.getText().toString();
        if (!telefono.equals("")) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + telefono));
            startActivity(intent);
        } else {
            Toast.makeText(this, "El contacto no cuenta con número telefónico ", Toast.LENGTH_SHORT).show();
        }
    }

    private void EnviarMensaje() {
        String telefono = telefonoContactoObjetoDetalle.getText().toString();
        if (!telefono.equals("")) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto: " + telefono));
            intent.putExtra("sms_body", "");
            startActivity(intent);
        } else {
            Toast.makeText(this, "El contacto no cuenta con número telefónico", Toast.LENGTH_SHORT).show();
        }
    }

    private void InicializarVariables() {
        imagenObjetoDetalle = findViewById(R.id.imagenContactoDetalle);
        idObjetoDetalle = findViewById(R.id.idObjetoDetalle);
        uidObjetoDetalle = findViewById(R.id.uidUsuarioObjetoDetalle);
        fechaRegistroObjetoDetalle = findViewById(R.id.fechaRegistroObjetoDetalle);
        correoObjetoDetalle = findViewById(R.id.correoUsuarioObjetoDetalle);
        tituloObjetoDetalle = findViewById(R.id.tituloObjetoDetalle);
        descripcionObjetoDetalle = findViewById(R.id.descripcionObjetoDetalle);
        categoriaObjetoDetalle = findViewById(R.id.categoriaObjetoDetalle);
        lugarPerdidaObjetoDetalle = findViewById(R.id.lugarPerdidaObjetoDetalle);
        telefonoContactoObjetoDetalle = findViewById(R.id.telefonoContactoDetalle);
        fechaPerdidaObjetoDetalle = findViewById(R.id.fechaPerdidaObjetoDetalle);
        estadoObjetoDetalle = findViewById(R.id.estadoObjetoDetalle);

        llamarContacto = findViewById(R.id.llamarContactoObjetoDetalle);
        enviarMensajeContacto = findViewById(R.id.mensajeObjetoContactoDetalle);
    }

    private void RecuperarDatosObjeto() {
        Bundle bundle = getIntent().getExtras();

        idObjeto = bundle.getString("idObjeto");
        uidUsuarioObjeto = bundle.getString("uidUsuarioObjeto");
        correoUsuarioObjeto = bundle.getString("correoUsuarioObjeto");
        fechaRegistroObjeto = bundle.getString("fechaRegistro");
        tituloObjeto = bundle.getString("tituloObjeto");
        descripcionObjeto = bundle.getString("descripcionObjeto");
        categoriaObjeto = bundle.getString("categoriaObjeto");
        lugarPerdidaObjeto = bundle.getString("lugarPerdidaObjeto");
        contactoObjeto = bundle.getString("telefonoContactoObjeto");
        fechaPerdidaObjeto = bundle.getString("fechaPerdidaObjeto");
        estadoObjeto = bundle.getString("estadoObjeto");
    }

    private void SetearDatosDelObjeto() {
        idObjetoDetalle.setText(idObjeto);
        uidObjetoDetalle.setText(uidUsuarioObjeto);
        correoObjetoDetalle.setText(correoUsuarioObjeto);
        fechaRegistroObjetoDetalle.setText(fechaRegistroObjeto);
        tituloObjetoDetalle.setText(tituloObjeto);
        descripcionObjetoDetalle.setText(descripcionObjeto);
        categoriaObjetoDetalle.setText(categoriaObjeto);
        lugarPerdidaObjetoDetalle.setText(lugarPerdidaObjeto);
        telefonoContactoObjetoDetalle.setText(contactoObjeto);
        fechaPerdidaObjetoDetalle.setText(fechaPerdidaObjeto);
        estadoObjetoDetalle.setText(estadoObjeto);
    }

    private void ObtenerImgenObjeto() {
        String imagenObjeto = getIntent().getStringExtra("imagenObjeto");

        try {
            Glide.with(getApplicationContext()).load(imagenObjeto).placeholder(R.drawable.imagen_contacto).into(imagenObjetoDetalle);
        } catch (Exception e) {
            Toast.makeText(this, "Esperando imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private ActivityResultLauncher<String> solicitudPermisoLLamada = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            LlamarContacto();
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    });

    private ActivityResultLauncher<String> solicitudPermisoMensaje = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            EnviarMensaje();
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