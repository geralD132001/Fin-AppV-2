package com.geraldcode.findapp.Controller.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geraldcode.findapp.Controller.user.NotasImportantes;
import com.geraldcode.findapp.Model.Nota;
import com.geraldcode.findapp.R;
import com.geraldcode.findapp.ViewHolder.ViewHolder_Nota_Importante;
import com.geraldcode.findapp.ViewHolder.ViewHolder_Nota_Importante_Admin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class NotasImportantesAdmin extends AppCompatActivity {

    RecyclerView RecyclerViewNotasImportantes;
    FirebaseDatabase firebaseDatabase;

    DatabaseReference Mis_Usuarios;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Importante_Admin> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> firebaseRecyclerOptions;

    LinearLayoutManager linearLayoutManager;

    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas_importantes_admin);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notas importantes");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerViewNotasImportantes = findViewById(R.id.recyclerViewNotasImportantesAdmin);
        RecyclerViewNotasImportantes.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        dialog = new Dialog(NotasImportantesAdmin.this);

        Mis_Usuarios = firebaseDatabase.getReference("Usuarios");


        ComprobarUsuario();
    }

    private void ComprobarUsuario(){
        if (user == null){
            Toast.makeText(NotasImportantesAdmin.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        }else {
            ListarNotasImportantes();
        }
    }

    private void ListarNotasImportantes() {
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(Mis_Usuarios.child(user.getUid()).child("Mis notas importantes").orderByChild("fecha_nota"), Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Importante_Admin>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota_Importante_Admin viewHolder_nota_importante, int position, @NotNull Nota nota) {
                viewHolder_nota_importante.SetearDatos(
                        getApplicationContext(),
                        nota.getId_nota(),
                        nota.getUid_usuario(),
                        nota.getCorreo_usuario(),
                        nota.getFecha_hora_actual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFecha_nota(),
                        nota.getEstado()
                );
            }


            @Override
            public ViewHolder_Nota_Importante_Admin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota_importante_admin,parent,false);
                ViewHolder_Nota_Importante_Admin viewHolder_nota_importante = new ViewHolder_Nota_Importante_Admin(view);
                viewHolder_nota_importante.setOnClickListener(new ViewHolder_Nota_Importante_Admin.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        String id_nota = getItem(position).getId_nota();

                        // Desclaramos las vistas
                        Button buttonEliminar, buttonEliminarNotaCancelar;

                        // Realizamos la conexión con el diseño
                        dialog.setContentView(R.layout.cuadro_dialogo_eliminar_nota_importante_admin);

                        // Inicializar las vistas
                        buttonEliminar = dialog.findViewById(R.id.eliminarNotaAdmin);
                        buttonEliminarNotaCancelar = dialog.findViewById(R.id.eliminarNotaCancelarAdmin);

                        buttonEliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Toast.makeText(Notas_Importantes.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                                EliminarNotaImportante(id_nota);
                                dialog.dismiss();
                            }
                        });

                        buttonEliminarNotaCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(NotasImportantesAdmin.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                return viewHolder_nota_importante;
            }
        };

        linearLayoutManager = new LinearLayoutManager(NotasImportantesAdmin.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        RecyclerViewNotasImportantes.setLayoutManager(linearLayoutManager);
        RecyclerViewNotasImportantes.setAdapter(firebaseRecyclerAdapter);

    }

    private void EliminarNotaImportante(String id_nota) {
        if (user == null) {
            Toast.makeText(NotasImportantesAdmin.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");
            reference.child(firebaseAuth.getUid()).child("Mis notas importantes").child(id_nota).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(NotasImportantesAdmin.this, "La nota ya no es importante", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NotasImportantesAdmin.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        if (firebaseRecyclerAdapter != null){
            firebaseRecyclerAdapter.startListening();
        }
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}