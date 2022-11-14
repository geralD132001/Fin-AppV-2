package com.geraldcode.findapp.Controller.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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

import com.geraldcode.findapp.Controller.user.ActualizarNota;
import com.geraldcode.findapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ActualizarNotaAdmin extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    TextView idNotaUpdate, uidUsuarioNotaUpdate, correoUsuarioNotaUpdate, fechaRegistroNotaUpdate, fechaPerdidaNotaUpdate, estadoNotaUpdate, estadoNuevoNotaUpdate;
    EditText tituloNotaUpdate, descripcionNotaUpdate;
    Button btnCalendarNotaUpdate;

    Spinner spinnerStadoNotaUpdate;

    ImageView tareaFinalizadaNotaUpdate,tareaNoFinalizadaNotaUpdate;

    String idNotaRecuperadoUpdate, uidUsuarioRecuperadoUpdate, correoUsuarioRecuperadoUpdate,
            fechaRegistroRecuperadoUpdate, tituloRecuperadoUpdate, descripcionRecuperadoUpdate, fechaPerdidaRecuperadoUpdate, estadoRecuperadoUpdate;

    int dia, mes, anio;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizat_nota_admin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Actualizar Nota");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        InicializarVistas();
        RecuperarDatos();
        SetearDatos();
        ComprobarEstadoNota();
        SpinnerEstado();

        btnCalendarNotaUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeleccionarFecha();
            }
        });
    }

    // Inicializar vistas
    private void InicializarVistas() {
        idNotaUpdate = findViewById(R.id.idNotaUpdateAdmin);
        uidUsuarioNotaUpdate = findViewById(R.id.uidNotaUpdateAdmin);
        correoUsuarioNotaUpdate = findViewById(R.id.correoNotaUpdateAdmin);
        fechaRegistroNotaUpdate = findViewById(R.id.fechaRegistroNotaUpdateAdmin);
        fechaPerdidaNotaUpdate = findViewById(R.id.fechaPerdidaNotaUpdateAdmin);
        estadoNotaUpdate = findViewById(R.id.estadoNotaUpdateAdmin);
        tituloNotaUpdate = findViewById(R.id.tituloNotaUpdateAdmin);
        descripcionNotaUpdate = findViewById(R.id.descripcionNotaUpdateAdmin);
        btnCalendarNotaUpdate = findViewById(R.id.btnCalendarioNotaUpdateAdmin);
        tareaFinalizadaNotaUpdate = findViewById(R.id.tareaFinalizadaUpdateAdmin);
        tareaNoFinalizadaNotaUpdate = findViewById(R.id.tareaNoFinalizadaUpdateAdmin);
        spinnerStadoNotaUpdate = findViewById(R.id.spinnerEstadoNotaUpdateAdmin);
        estadoNuevoNotaUpdate = findViewById(R.id.estadoNuevoNotaUpdateAdmin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    // Recuperar los datos
    private void RecuperarDatos() {
        Bundle intent = getIntent().getExtras();

        idNotaRecuperadoUpdate = intent.getString("id_nota");
        uidUsuarioRecuperadoUpdate = intent.getString("uid_usuario");
        correoUsuarioRecuperadoUpdate = intent.getString("correo_usuario");
        fechaRegistroRecuperadoUpdate = intent.getString("fecha_registro");
        tituloRecuperadoUpdate = intent.getString("titulo");
        descripcionRecuperadoUpdate = intent.getString("descripcion");
        fechaPerdidaRecuperadoUpdate = intent.getString("fecha_nota");
        estadoRecuperadoUpdate = intent.getString("estado");

    }

    // Setear datos
    private void SetearDatos() {
        idNotaUpdate.setText(idNotaRecuperadoUpdate);
        uidUsuarioNotaUpdate.setText(uidUsuarioRecuperadoUpdate);
        correoUsuarioNotaUpdate.setText(correoUsuarioRecuperadoUpdate);
        fechaRegistroNotaUpdate.setText(fechaRegistroRecuperadoUpdate);
        tituloNotaUpdate.setText(tituloRecuperadoUpdate);
        descripcionNotaUpdate.setText(descripcionRecuperadoUpdate);
        fechaPerdidaNotaUpdate.setText(fechaPerdidaRecuperadoUpdate);
        estadoNotaUpdate.setText(estadoRecuperadoUpdate);
    }

    private void ComprobarEstadoNota(){
        String estado_nota = estadoNotaUpdate.getText().toString();

        if(estado_nota.equals("No finalizado")){
            tareaNoFinalizadaNotaUpdate.setVisibility(View.VISIBLE);
        }
        if(estado_nota.equals("Finalizado")){
            tareaFinalizadaNotaUpdate.setVisibility(View.VISIBLE);
        }
    }

    private void SeleccionarFecha(){
        final Calendar calendario = Calendar.getInstance();

        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        anio = calendario.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ActualizarNotaAdmin.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int AnioSeleccionado, int MesSeleccionado, int DiaSeleccionado) {

                String diaFormateado, mesFormateado;

                //OBTENER DIA
                if (DiaSeleccionado < 10){
                    diaFormateado = "0"+String.valueOf(DiaSeleccionado);
                    // Antes: 9/11/2022 -  Ahora 09/11/2022
                }else {
                    diaFormateado = String.valueOf(DiaSeleccionado);
                    //Ejemplo 13/08/2022
                }

                //OBTENER EL MES
                int Mes = MesSeleccionado + 1;

                if (Mes < 10){
                    mesFormateado = "0"+String.valueOf(Mes);
                    // Antes: 09/8/2022 -  Ahora 09/08/2022
                }else {
                    mesFormateado = String.valueOf(Mes);
                    //Ejemplo 13/10/2022 - 13/11/2022 - 13/12/2022

                }

                //Setear fecha en TextView
                fechaPerdidaNotaUpdate.setText(diaFormateado + "/" + mesFormateado + "/"+ AnioSeleccionado);

            }
        }
                ,anio,mes,dia);
        datePickerDialog.show();
    }

    private void SpinnerEstado(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.estadosNotaAdminUpdate, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerStadoNotaUpdate.setAdapter(adapter);
        spinnerStadoNotaUpdate.setOnItemSelectedListener(this);
    }

    private void ActualizarNotaDb(){
        String tituloActualizar = tituloNotaUpdate.getText().toString();
        String descripcionActualizar = descripcionNotaUpdate.getText().toString();
        String fechaActualizar = fechaPerdidaNotaUpdate.getText().toString();
        String estadoActualizar = estadoNuevoNotaUpdate.getText().toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios");

        //Consulta
        Query query = databaseReference.child(firebaseUser.getUid()).child("Notas_Publicadas_Admin").orderByChild("id_nota").equalTo(idNotaRecuperadoUpdate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().child("titulo").setValue(tituloActualizar);
                    ds.getRef().child("descripcion").setValue(descripcionActualizar);
                    ds.getRef().child("fecha_nota").setValue(fechaActualizar);
                    ds.getRef().child("estado").setValue(estadoActualizar);
                }

                Toast.makeText(ActualizarNotaAdmin.this, "Nota actualizada con éxito", Toast.LENGTH_SHORT).show();
                onBackPressed();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String estado_actual = estadoNotaUpdate.getText().toString();

        String poscion_1 = parent.getItemAtPosition(1).toString();

        String estado_seleccionado = parent.getItemAtPosition(position).toString();
        estadoNuevoNotaUpdate.setText(estado_seleccionado);

        if(estado_actual.equals("Finalizado")){
            estadoNuevoNotaUpdate.setText(poscion_1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar_nota_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actualizarNotaMenuUser:
                ActualizarNotaDb();
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