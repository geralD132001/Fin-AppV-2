package com.geraldcode.findapp.Controller.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.geraldcode.findapp.Controller.user.AgregarNota;
import com.geraldcode.findapp.Model.Nota;
import com.geraldcode.findapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AgregarNotaAdmin extends AppCompatActivity {

    TextView uidAdminAddNote, correoAdminAddNote, fechaHoraActualAdminAddNote, fechaAdminAddNote, estadoAdminAddNote;
    EditText tituloAdminAddNote, descripcionAdminAddNote;
    Button btnCalendarioAdminAddNote;

    int dia, mes, anio;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    DatabaseReference BD_Firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nota_admin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // El Inicializar variables tiene que ir siempre antes de capturar los datos
        InicializarVariables();
        ObtenerDatos();
        ObtenerFechaHoraActual();

        btnCalendarioAdminAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendario = Calendar.getInstance();

                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AgregarNotaAdmin.this, new DatePickerDialog.OnDateSetListener() {
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
                        fechaAdminAddNote.setText(diaFormateado + "/" + mesFormateado + "/" + anioSeleccionado);
                    }
                }
                        , anio, mes, dia);
                datePickerDialog.show();
            }
        });
    }

    private void InicializarVariables() {
        uidAdminAddNote = findViewById(R.id.uidAdminAddNote);
        correoAdminAddNote = findViewById(R.id.correoAdminAddNote);
        fechaHoraActualAdminAddNote = findViewById(R.id.fechaHoraActualAdminAddNote);
        fechaAdminAddNote = findViewById(R.id.fechaAdminAddNote);
        estadoAdminAddNote = findViewById(R.id.estadoAdminAddNote);
        tituloAdminAddNote = findViewById(R.id.tituloAdminAddNote);
        descripcionAdminAddNote = findViewById(R.id.descripcionAdminAddNote);
        btnCalendarioAdminAddNote = findViewById(R.id.btnCalendarioAdminAddNote);

        BD_Firebase = FirebaseDatabase.getInstance().getReference("Usuarios");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void ObtenerDatos() {
        String uid_recuperado = getIntent().getStringExtra("Uid");
        String correo_recuperado = getIntent().getStringExtra("Correo");

        uidAdminAddNote.setText(uid_recuperado);
        correoAdminAddNote.setText(correo_recuperado);
    }

    private void ObtenerFechaHoraActual() {
        String fechaHoraRegistro = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        fechaHoraActualAdminAddNote.setText(fechaHoraRegistro);
    }

    private void AgregarNotaAdministrador() {

        // Obtener los datos
        String uidUsuario = uidAdminAddNote.getText().toString();
        String correoUsuario = correoAdminAddNote.getText().toString();
        String fechaHoraActual = fechaHoraActualAdminAddNote.getText().toString();
        String titulo = tituloAdminAddNote.getText().toString();
        String descripcion = descripcionAdminAddNote.getText().toString();
        String fecha = fechaAdminAddNote.getText().toString();
        String estado = estadoAdminAddNote.getText().toString();

        String id_nota = BD_Firebase.push().getKey();

        // Validar datos
        if (!uidUsuario.equals("")
                && !correoUsuario.equals("")
                && !fechaHoraActual.equals("")
                && !titulo.equals("")
                && !descripcion.equals("")
                && !fecha.equals("")
                && !estado.equals("")) {

            Nota nota = new Nota(id_nota, uidUsuario,
                    correoUsuario,
                    fechaHoraActual,
                    titulo, descripcion,
                    fecha,
                    estado);

            // Establecer el nombre de la BD
            String Nombre_BD = "Notas_Publicadas_Admin";

            assert id_nota != null;
            BD_Firebase.child(firebaseUser.getUid()).child(Nombre_BD).child(id_nota).setValue(nota);

            Toast.makeText(this, "Se ha agregado la nota exitosamente", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } else {
            Toast.makeText(this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar_nota_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.agendarNotaAdmin:
                AgregarNotaAdministrador();
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