package com.geraldcode.findapp.Controller.Objetos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
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

import com.geraldcode.findapp.Model.Objeto;
import com.geraldcode.findapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AgregarObjeto extends AppCompatActivity {

    TextView uidUsuarioAddObjeto, correoUsuarioAddObjeto, fechaHoralActual, fechaPerdidaAddObjeto, estadoAddObjeto, telefonoContactoAddObjeto;
    EditText tituloAddObjeto, descripcionAddObjeto, categoriaAddObjeto, lugarPerdidaAddObjeto;
    Button buttonCalendarioAddObjeto, buttonGuardarObjeto;
    ImageView editarTelefonoContactoAddObjeto;

    Dialog dialog, dialogEstablecerTelefono;

    int dia, mes, anio;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

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
        ObtenerDatos();
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

        buttonGuardarObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarObjetoBD();
            }
        });

    }

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
        uidUsuarioAddObjeto = findViewById(R.id.uidUsuarioAddObjeto);
        correoUsuarioAddObjeto = findViewById(R.id.correoUsuarioAddObjeto);
        fechaHoralActual = findViewById(R.id.fechaHoraActualAddObjeto);
        fechaPerdidaAddObjeto = findViewById(R.id.fechaAddObjeto);
        estadoAddObjeto = findViewById(R.id.estadoAddObjeto);
        tituloAddObjeto = findViewById(R.id.tituloAddObjeto);
        descripcionAddObjeto = findViewById(R.id.descripcionAddObjeto);
        telefonoContactoAddObjeto = findViewById(R.id.telefonoAddObjeto);
        editarTelefonoContactoAddObjeto = findViewById(R.id.editarTelefonoContactoAddObjeto);
        categoriaAddObjeto = findViewById(R.id.categoriaAddObjeto);
        lugarPerdidaAddObjeto = findViewById(R.id.lugarPerdidaAddObjeto);
        buttonGuardarObjeto = findViewById(R.id.buttonAddObjeto);
        buttonCalendarioAddObjeto = findViewById(R.id.btnCalendarioAddObjeto);

        dialogEstablecerTelefono = new Dialog(AgregarObjeto.this);
        BD_Firebase = FirebaseDatabase.getInstance().getReference("Usuarios");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        dialog = new Dialog(AgregarObjeto.this);


    }

    private void ObtenerDatos() {
        String uidRecuperado = getIntent().getStringExtra("Uid");
        String correoRecuperado = getIntent().getStringExtra("Correo");

        uidUsuarioAddObjeto.setText(uidRecuperado);
        correoUsuarioAddObjeto.setText(correoRecuperado);
    }

    private void ObtenerFechaHoraActual() {
        String fechaHoraRegistro = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        fechaHoralActual.setText(fechaHoraRegistro);
    }

    private void AgregarObjetoBD() {

        // Obtener los datos
        String uidUsuario = uidUsuarioAddObjeto.getText().toString();
        String correoUsuario = correoUsuarioAddObjeto.getText().toString();
        String fechaHoraActual = fechaHoralActual.getText().toString();
        String tituloObjeto = tituloAddObjeto.getText().toString();
        String descripcionObjeto = descripcionAddObjeto.getText().toString();
        String categoriaObjeto = categoriaAddObjeto.getText().toString();
        String lugarDePerdida = lugarPerdidaAddObjeto.getText().toString();
        String contactoPerdida = telefonoContactoAddObjeto.getText().toString();
        String fechaPerdida = fechaPerdidaAddObjeto.getText().toString();
        String estadoObjeto = estadoAddObjeto.getText().toString();

        String idObjeto = BD_Firebase.push().getKey();

        if (!uidUsuario.equals("")
                && !correoUsuario.equals("")
                && !fechaHoraActual.equals("")
                && !tituloObjeto.equals("")
                && !descripcionObjeto.equals("")
                && !categoriaObjeto.equals("")
                && !lugarDePerdida.equals("")
                && !contactoPerdida.equals("")
                && !fechaPerdida.equals("")
                && !estadoObjeto.equals("")) {

            Objeto objeto = new Objeto(idObjeto, uidUsuario,
                    correoUsuario,
                    fechaHoraActual,
                    tituloObjeto,
                    descripcionObjeto,
                    categoriaObjeto,
                    lugarDePerdida,
                    contactoPerdida,
                    fechaPerdida,
                    estadoObjeto,
                    "");

            // Establecer el nombre de la BD
            String nombreBaseDeDatos = "Objetos";

            assert idObjeto != null;
            BD_Firebase.child(firebaseUser.getUid()).child(nombreBaseDeDatos).child(idObjeto).setValue(objeto);

            Toast.makeText(this, "Se ha agregado el objeto de manera exitosamente", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } else {
            ValidarRegistroObjeto();
        }

    }

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
                AgregarObjetoBD();
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