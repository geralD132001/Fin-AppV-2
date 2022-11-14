package com.geraldcode.findapp.Controller.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geraldcode.findapp.Controller.user.ActualizarNota;
import com.geraldcode.findapp.Controller.user.DetalleNota;
import com.geraldcode.findapp.Controller.user.ListarNotas;
import com.geraldcode.findapp.Model.Nota;
import com.geraldcode.findapp.R;
import com.geraldcode.findapp.ViewHolder.ViewHolder_Nota;
import com.geraldcode.findapp.ViewHolder.ViewHolder_Nota_Admin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ListarNotasAdmin extends AppCompatActivity {

    RecyclerView recyclerViewNotas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BD_Usuarios;
    LinearLayoutManager linearLayoutManager;

    FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Admin> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> options;

    Dialog dialog, dialogFiltrar;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_nota_admin);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mis notas");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerViewNotas = findViewById(R.id.recyclerviewNotasAdmin);
        recyclerViewNotas.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        dialogFiltrar = new Dialog(ListarNotasAdmin.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        BD_Usuarios = firebaseDatabase.getReference("Usuarios");


        dialog = new Dialog(ListarNotasAdmin.this);

        EstadoFiltro();
    }

    private void ListarTodasLasNotas() {

        // CONSULTA
        Query query = BD_Usuarios.child(firebaseUser.getUid()).child("Notas_Publicadas_Admin").orderByChild("fecha_nota");
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Admin>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota_Admin viewHolder_nota, int position, @NonNull Nota nota) {
                viewHolder_nota.SetearDatos(
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

            @NonNull
            @Override
            public ViewHolder_Nota_Admin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota_admin, parent, false);
                ViewHolder_Nota_Admin viewHolder_nota = new ViewHolder_Nota_Admin(view);
                viewHolder_nota.setOnClickListener(new ViewHolder_Nota_Admin.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha__registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        // Enviamos los datos a la siguiente actividad
                        Intent intent = new Intent(ListarNotasAdmin.this, DetalleNotaAdmin.class);
                        intent.putExtra("id_nota", id_nota);
                        intent.putExtra("uid_usuario", uid_usuario);
                        intent.putExtra("correo_usuario", correo_usuario);
                        intent.putExtra("fecha_registro", fecha__registro);
                        intent.putExtra("titulo", titulo);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("fecha_nota", fecha_nota);
                        intent.putExtra("estado", estado);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        // Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha__registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        // Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        // Realizar la conexion con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones_list_notas_user);

                        // Inicializar las vistas
                        CD_Eliminar = dialog.findViewById(R.id.buttonEliminarNotaUser);
                        CD_Actualizar = dialog.findViewById(R.id.buttonActualizarNotaUser);

                        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EliminarNota(id_nota);
                                dialog.dismiss();
                            }
                        });

                        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(ListarNotasAdmin.this, ActualizarNotaAdmin.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo_usuario", correo_usuario);
                                intent.putExtra("fecha_registro", fecha__registro);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });


                        dialog.show();
                    }
                });
                return viewHolder_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(ListarNotasAdmin.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerViewNotas.setLayoutManager(linearLayoutManager);
        recyclerViewNotas.setAdapter(firebaseRecyclerAdapter);
    }

    private void ListarTodasFinalizadas() {

        // CONSULTA
        String estadoNota = "Finalizado";
        Query query = BD_Usuarios.child(firebaseUser.getUid()).child("Notas_Publicadas_Admin").orderByChild("estado").equalTo(estadoNota);
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Admin>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota_Admin viewHolder_nota, int position, @NonNull Nota nota) {
                viewHolder_nota.SetearDatos(
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

            @NonNull
            @Override
            public ViewHolder_Nota_Admin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota_admin, parent, false);
                ViewHolder_Nota_Admin viewHolder_nota = new ViewHolder_Nota_Admin(view);
                viewHolder_nota.setOnClickListener(new ViewHolder_Nota_Admin.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha__registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        // Enviamos los datos a la siguiente actividad
                        Intent intent = new Intent(ListarNotasAdmin.this, DetalleNotaAdmin.class);
                        intent.putExtra("id_nota", id_nota);
                        intent.putExtra("uid_usuario", uid_usuario);
                        intent.putExtra("correo_usuario", correo_usuario);
                        intent.putExtra("fecha_registro", fecha__registro);
                        intent.putExtra("titulo", titulo);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("fecha_nota", fecha_nota);
                        intent.putExtra("estado", estado);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        // Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha__registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        // Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        // Realizar la conexion con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones_list_notas_user);

                        // Inicializar las vistas
                        CD_Eliminar = dialog.findViewById(R.id.buttonEliminarNotaUser);
                        CD_Actualizar = dialog.findViewById(R.id.buttonActualizarNotaUser);

                        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EliminarNota(id_nota);
                                dialog.dismiss();
                            }
                        });

                        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(ListarNotasAdmin.this, ActualizarNotaAdmin.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo_usuario", correo_usuario);
                                intent.putExtra("fecha_registro", fecha__registro);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });


                        dialog.show();
                    }
                });
                return viewHolder_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(ListarNotasAdmin.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerViewNotas.setLayoutManager(linearLayoutManager);
        recyclerViewNotas.setAdapter(firebaseRecyclerAdapter);
    }

    private void ListarTodasNoFinalizadas() {

        // CONSULTA
        String estadoNota = "No finalizado";
        Query query = BD_Usuarios.child(firebaseUser.getUid()).child("Notas_Publicadas_Admin").orderByChild("estado").equalTo(estadoNota);
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota_Admin>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota_Admin viewHolder_nota, int position, @NonNull Nota nota) {
                viewHolder_nota.SetearDatos(
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

            @NonNull
            @Override
            public ViewHolder_Nota_Admin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota_admin, parent, false);
                ViewHolder_Nota_Admin viewHolder_nota = new ViewHolder_Nota_Admin(view);
                viewHolder_nota.setOnClickListener(new ViewHolder_Nota_Admin.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha__registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        // Enviamos los datos a la siguiente actividad
                        Intent intent = new Intent(ListarNotasAdmin.this, DetalleNotaAdmin.class);
                        intent.putExtra("id_nota", id_nota);
                        intent.putExtra("uid_usuario", uid_usuario);
                        intent.putExtra("correo_usuario", correo_usuario);
                        intent.putExtra("fecha_registro", fecha__registro);
                        intent.putExtra("titulo", titulo);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("fecha_nota", fecha_nota);
                        intent.putExtra("estado", estado);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        // Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha__registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        // Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        // Realizar la conexion con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones_list_notas_user);

                        // Inicializar las vistas
                        CD_Eliminar = dialog.findViewById(R.id.buttonEliminarNotaUser);
                        CD_Actualizar = dialog.findViewById(R.id.buttonActualizarNotaUser);

                        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EliminarNota(id_nota);
                                dialog.dismiss();
                            }
                        });

                        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(ListarNotasAdmin.this, ActualizarNotaAdmin.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo_usuario", correo_usuario);
                                intent.putExtra("fecha_registro", fecha__registro);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });


                        dialog.show();
                    }
                });
                return viewHolder_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(ListarNotasAdmin.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerViewNotas.setLayoutManager(linearLayoutManager);
        recyclerViewNotas.setAdapter(firebaseRecyclerAdapter);
    }

    private void EliminarNota(String id_nota) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ListarNotasAdmin.this);
        builder.setTitle("Eliminar nota");
        builder.setMessage("¿Desea eliminar la nota?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ELIMINAR NOTA EN DB
                Query query = BD_Usuarios.child(firebaseUser.getUid()).child("Notas_Publicadas_Admin").orderByChild("id_nota").equalTo(id_nota);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(ListarNotasAdmin.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ListarNotasAdmin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ListarNotasAdmin.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    private void VaciarRegistroDeNotas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListarNotasAdmin.this);
        builder.setTitle("Vaciar todos los registros");
        builder.setMessage("¿Estás seguro(a) de eliminar todas las notas");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Elminación de todas las notas
                Query query = BD_Usuarios.child(firebaseUser.getUid()).child("Notas_Publicadas_Admin");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(ListarNotasAdmin.this, "Todas las notas se han eliminado correctamente", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ListarNotasAdmin.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notas_user_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.vaciarTodasLasNotasMenuAdmin) {
            VaciarRegistroDeNotas();
        }
        if (item.getItemId() == R.id.filtrarNotasMenuAdmin) {
            FiltrarNotas();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    private void FiltrarNotas() {
        Button buttonTodasLasNotas, buttonNotasFinalizadas, buttonNotasNoFinalizadas;

        dialogFiltrar.setContentView(R.layout.cuadro_dialogo_filtrar_notas_admin);

        buttonTodasLasNotas = dialogFiltrar.findViewById(R.id.todasNotasAdmin);
        buttonNotasFinalizadas = dialogFiltrar.findViewById(R.id.notasFinalizadasAdmin);
        buttonNotasNoFinalizadas = dialogFiltrar.findViewById(R.id.notasNoFinalizadasAdmin);

        buttonTodasLasNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Todas");
                editor.apply();
                recreate();
                Toast.makeText(ListarNotasAdmin.this, "Todas las notas", Toast.LENGTH_SHORT).show();
                dialogFiltrar.dismiss();
            }
        });

        buttonNotasFinalizadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Finalizados");
                editor.apply();
                recreate();
                Toast.makeText(ListarNotasAdmin.this, "Notas finzalizadas", Toast.LENGTH_SHORT).show();
                dialogFiltrar.dismiss();
            }
        });

        buttonNotasNoFinalizadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "No finalizados");
                editor.apply();
                recreate();
                Toast.makeText(ListarNotasAdmin.this, "Notas no finalizadas", Toast.LENGTH_SHORT).show();
                dialogFiltrar.dismiss();
            }
        });

        dialogFiltrar.show();
    }

    private void EstadoFiltro() {
        sharedPreferences = ListarNotasAdmin.this.getSharedPreferences("Notas", MODE_PRIVATE);

        String estadoFiltro = sharedPreferences.getString("Listar", "Todas");

        switch (estadoFiltro) {
            case "Todas":
                ListarTodasLasNotas();
                break;
            case "Finalizados":
                ListarTodasFinalizadas();
                break;
            case "No finalizados":
                ListarTodasNoFinalizadas();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}