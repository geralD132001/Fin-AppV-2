package com.geraldcode.findapp.Controller.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geraldcode.findapp.Controller.admin.Login;
import com.geraldcode.findapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ActualizarPassUsuario extends AppCompatActivity {

    TextView actualPass;
    EditText editTextActualPass, editTextNuevoPass, editTextRepetirNuevoPass;
    Button buttonActualizarPass;

    DatabaseReference BD_Usuarios;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_pass_usuario);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Cambiar contraseña");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        InicializarVariables();
        LecturaDeData();

        buttonActualizarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passActual = editTextActualPass.getText().toString();
                String nuevoPass = editTextNuevoPass.getText().toString();
                String repetirNuevoPass = editTextRepetirNuevoPass.getText().toString();

                if (passActual.equals("")) {
                    editTextActualPass.setError("Llene el campo");
                } else if (nuevoPass.equals("")) {
                    editTextNuevoPass.setError("Llene el campo");
                } else if (repetirNuevoPass.equals("")) {
                    editTextRepetirNuevoPass.setError("Llene el campo");
                } else if (!nuevoPass.equals(repetirNuevoPass)) {
                    Toast.makeText(ActualizarPassUsuario.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                } else if (nuevoPass.length() < 6) {
                    editTextNuevoPass.setError("Debe ser igual o mayor de 6 caracteres");
                } else {
                    ActualizarPassword(passActual, nuevoPass);
                }
            }
        });
    }

    private void ActualizarPassword(String pass_actual, String nuevo_pass) {
        progressDialog.show();
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor");

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), pass_actual);
        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseUser.updatePassword(nuevo_pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        String nuevoPass = editTextNuevoPass.getText().toString().trim();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("password", nuevoPass);

                        BD_Usuarios = FirebaseDatabase.getInstance().getReference("UsuariosCommons");
                        BD_Usuarios.child(firebaseUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ActualizarPassUsuario.this, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();

                                Intent intent = new Intent(ActualizarPassUsuario.this, LoginEstudiante.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ActualizarPassUsuario.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ActualizarPassUsuario.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ActualizarPassUsuario.this, "La contraseña actual no es la correcta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InicializarVariables() {
        actualPass = findViewById(R.id.actualPass);
        editTextActualPass = findViewById(R.id.etActualPass);
        editTextNuevoPass = findViewById(R.id.etNuevoPass);
        editTextRepetirNuevoPass = findViewById(R.id.etRepetirNuevoPass);

        buttonActualizarPass = findViewById(R.id.buttonActualizarPass);

        progressDialog = new ProgressDialog(ActualizarPassUsuario.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void LecturaDeData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UsuariosCommons");
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String password = "" + snapshot.child("password").getValue();
                actualPass.setText(password);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}