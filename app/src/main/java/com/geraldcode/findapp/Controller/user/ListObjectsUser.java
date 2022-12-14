package com.geraldcode.findapp.Controller.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.geraldcode.findapp.Model.Objeto;
import com.geraldcode.findapp.R;
import com.geraldcode.findapp.ViewHolder.ViewHolderObjetoUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ListObjectsUser extends AppCompatActivity {

    RecyclerView recyclerViewObjetos;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BD_Objetos;
    LinearLayoutManager linearLayoutManager;

    FirebaseRecyclerAdapter<Objeto, ViewHolderObjetoUser> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Objeto> firebaseRecyclerOptions;

    Dialog dialog, dialogFilter;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_objects_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Objetos");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerViewObjetos = findViewById(R.id.recyclerViewObjetosUser);
        recyclerViewObjetos.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        dialogFilter = new Dialog(ListObjectsUser.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        BD_Objetos = firebaseDatabase.getReference("Objetos");

        dialog = new Dialog(ListObjectsUser.this);

        FiltroEstadoDeObjeto();
    }

    private void ListarTodosLosObjetos() {

        // CONSULTA
        Query query = BD_Objetos.orderByChild("titulo");
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Objeto>().setQuery(query, Objeto.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Objeto, ViewHolderObjetoUser>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderObjetoUser viewHolderObjeto, int position, @NonNull Objeto objeto) {
                viewHolderObjeto.SetearDatos(
                        getApplicationContext(),
                        objeto.getIdObjeto(),
                        objeto.getFechaHoraActual(),
                        objeto.getTitulo(),
                        objeto.getDescripcion(),
                        objeto.getCategoria(),
                        objeto.getLugarPerdida(),
                        objeto.getTelefonoContacto(),
                        objeto.getFechaPerdida(),
                        objeto.getEstado(),
                        objeto.getImagenObjeto()
                );
            }

            @NonNull
            @Override
            public ViewHolderObjetoUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objeto_user, parent, false);
                ViewHolderObjetoUser viewHolderObjetoUser = new ViewHolderObjetoUser(view);
                viewHolderObjetoUser.setOnClickListener(new ViewHolderObjetoUser.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Obteniendo los datos del objeto seleccionado
                        String idObjeto = getItem(position).getIdObjeto();
                        String fechaRegistro = getItem(position).getFechaHoraActual();
                        String tituloObjeto = getItem(position).getTitulo();
                        String descripcionObjeto = getItem(position).getDescripcion();
                        String categoriaObjeto = getItem(position).getCategoria();
                        String lugarPerdidaObjeto = getItem(position).getLugarPerdida();
                        String telefonoContactoObjeto = getItem(position).getTelefonoContacto();
                        String fechaPerdidaObjeto = getItem(position).getFechaPerdida();
                        String estadoObjeto = getItem(position).getEstado();
                        String imagenObjeto = getItem(position).getImagenObjeto();

                        // Enviar los datos a la siguiente actividad
                        Intent intent = new Intent(ListObjectsUser.this, DetalleObjetoUser.class);
                        intent.putExtra("idObjeto", idObjeto);
                        intent.putExtra("fechaRegistro", fechaRegistro);
                        intent.putExtra("tituloObjeto", tituloObjeto);
                        intent.putExtra("descripcionObjeto", descripcionObjeto);
                        intent.putExtra("categoriaObjeto", categoriaObjeto);
                        intent.putExtra("lugarPerdidaObjeto", lugarPerdidaObjeto);
                        intent.putExtra("telefonoContactoObjeto", telefonoContactoObjeto);
                        intent.putExtra("fechaPerdidaObjeto", fechaPerdidaObjeto);
                        intent.putExtra("estadoObjeto", estadoObjeto);
                        intent.putExtra("imagenObjeto", imagenObjeto);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return viewHolderObjetoUser;
            }
        };

        recyclerViewObjetos.setLayoutManager(new GridLayoutManager(ListObjectsUser.this, 2));
        firebaseRecyclerAdapter.startListening();
        recyclerViewObjetos.setAdapter(firebaseRecyclerAdapter);
    }

    private void ListarObjetosPerdidos() {

        // CONSULTA
        String estadoObjeto = "Perdido";
        Query query = BD_Objetos.orderByChild("estado").equalTo(estadoObjeto);
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Objeto>().setQuery(query, Objeto.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Objeto, ViewHolderObjetoUser>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderObjetoUser viewHolderObjeto, int position, @NonNull Objeto objeto) {
                viewHolderObjeto.SetearDatos(
                        getApplicationContext(),
                        objeto.getIdObjeto(),
                        objeto.getFechaHoraActual(),
                        objeto.getTitulo(),
                        objeto.getDescripcion(),
                        objeto.getCategoria(),
                        objeto.getLugarPerdida(),
                        objeto.getTelefonoContacto(),
                        objeto.getFechaPerdida(),
                        objeto.getEstado(),
                        objeto.getImagenObjeto()
                );
            }

            @NonNull
            @Override
            public ViewHolderObjetoUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objeto_user, parent, false);
                ViewHolderObjetoUser viewHolderObjetoUser = new ViewHolderObjetoUser(view);
                viewHolderObjetoUser.setOnClickListener(new ViewHolderObjetoUser.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Obteniendo los datos del objeto seleccionado
                        String idObjeto = getItem(position).getIdObjeto();
                        String fechaRegistro = getItem(position).getFechaHoraActual();
                        String tituloObjeto = getItem(position).getTitulo();
                        String descripcionObjeto = getItem(position).getDescripcion();
                        String categoriaObjeto = getItem(position).getCategoria();
                        String lugarPerdidaObjeto = getItem(position).getLugarPerdida();
                        String telefonoContactoObjeto = getItem(position).getTelefonoContacto();
                        String fechaPerdidaObjeto = getItem(position).getFechaPerdida();
                        String estadoObjeto = getItem(position).getEstado();
                        String imagenObjeto = getItem(position).getImagenObjeto();

                        // Enviar los datos a la siguiente actividad
                        Intent intent = new Intent(ListObjectsUser.this, DetalleObjetoUser.class);
                        intent.putExtra("idObjeto", idObjeto);
                        intent.putExtra("fechaRegistro", fechaRegistro);
                        intent.putExtra("tituloObjeto", tituloObjeto);
                        intent.putExtra("descripcionObjeto", descripcionObjeto);
                        intent.putExtra("categoriaObjeto", categoriaObjeto);
                        intent.putExtra("lugarPerdidaObjeto", lugarPerdidaObjeto);
                        intent.putExtra("telefonoContactoObjeto", telefonoContactoObjeto);
                        intent.putExtra("fechaPerdidaObjeto", fechaPerdidaObjeto);
                        intent.putExtra("estadoObjeto", estadoObjeto);
                        intent.putExtra("imagenObjeto", imagenObjeto);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {


                    }
                });
                return viewHolderObjetoUser;
            }
        };

        recyclerViewObjetos.setLayoutManager(new GridLayoutManager(ListObjectsUser.this, 2));
        firebaseRecyclerAdapter.startListening();
        recyclerViewObjetos.setAdapter(firebaseRecyclerAdapter);
    }

    private void ListarObjetosEncontrados() {
        // CONSULTA
        String estadoObjeto = "Encontrado";
        Query query = BD_Objetos.orderByChild("estado").equalTo(estadoObjeto);
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Objeto>().setQuery(query, Objeto.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Objeto, ViewHolderObjetoUser>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderObjetoUser viewHolderObjeto, int position, @NonNull Objeto objeto) {
                viewHolderObjeto.SetearDatos(
                        getApplicationContext(),
                        objeto.getIdObjeto(),
                        objeto.getFechaHoraActual(),
                        objeto.getTitulo(),
                        objeto.getDescripcion(),
                        objeto.getCategoria(),
                        objeto.getLugarPerdida(),
                        objeto.getTelefonoContacto(),
                        objeto.getFechaPerdida(),
                        objeto.getEstado(),
                        objeto.getImagenObjeto()
                );
            }

            @NonNull
            @Override
            public ViewHolderObjetoUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objeto_user, parent, false);
                ViewHolderObjetoUser viewHolderObjeto = new ViewHolderObjetoUser(view);
                viewHolderObjeto.setOnClickListener(new ViewHolderObjetoUser.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Obteniendo los datos del objeto seleccionado
                        String idObjeto = getItem(position).getIdObjeto();
                        String fechaRegistro = getItem(position).getFechaHoraActual();
                        String tituloObjeto = getItem(position).getTitulo();
                        String descripcionObjeto = getItem(position).getDescripcion();
                        String categoriaObjeto = getItem(position).getCategoria();
                        String lugarPerdidaObjeto = getItem(position).getLugarPerdida();
                        String telefonoContactoObjeto = getItem(position).getTelefonoContacto();
                        String fechaPerdidaObjeto = getItem(position).getFechaPerdida();
                        String estadoObjeto = getItem(position).getEstado();
                        String imagenObjeto = getItem(position).getImagenObjeto();

                        // Enviar los datos a la siguiente actividad
                        Intent intent = new Intent(ListObjectsUser.this, DetalleObjetoUser.class);
                        intent.putExtra("idObjeto", idObjeto);
                        intent.putExtra("fechaRegistro", fechaRegistro);
                        intent.putExtra("tituloObjeto", tituloObjeto);
                        intent.putExtra("descripcionObjeto", descripcionObjeto);
                        intent.putExtra("categoriaObjeto", categoriaObjeto);
                        intent.putExtra("lugarPerdidaObjeto", lugarPerdidaObjeto);
                        intent.putExtra("telefonoContactoObjeto", telefonoContactoObjeto);
                        intent.putExtra("fechaPerdidaObjeto", fechaPerdidaObjeto);
                        intent.putExtra("estadoObjeto", estadoObjeto);
                        intent.putExtra("imagenObjeto", imagenObjeto);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return viewHolderObjeto;
            }
        };

        recyclerViewObjetos.setLayoutManager(new GridLayoutManager(ListObjectsUser.this, 2));
        firebaseRecyclerAdapter.startListening();
        recyclerViewObjetos.setAdapter(firebaseRecyclerAdapter);
    }

    private void ListarObjetosEntregados() {
        // CONSULTA
        String estadoObjeto = "Entregado";
        Query query = BD_Objetos.orderByChild("estado").equalTo(estadoObjeto);
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Objeto>().setQuery(query, Objeto.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Objeto, ViewHolderObjetoUser>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderObjetoUser viewHolderObjeto, int position, @NonNull Objeto objeto) {
                viewHolderObjeto.SetearDatos(
                        getApplicationContext(),
                        objeto.getIdObjeto(),
                        objeto.getFechaHoraActual(),
                        objeto.getTitulo(),
                        objeto.getDescripcion(),
                        objeto.getCategoria(),
                        objeto.getLugarPerdida(),
                        objeto.getTelefonoContacto(),
                        objeto.getFechaPerdida(),
                        objeto.getEstado(),
                        objeto.getImagenObjeto()
                );
            }

            @NonNull
            @Override
            public ViewHolderObjetoUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objeto_user, parent, false);
                ViewHolderObjetoUser viewHolderObjetoUser = new ViewHolderObjetoUser(view);
                viewHolderObjetoUser.setOnClickListener(new ViewHolderObjetoUser.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Obteniendo los datos del objeto seleccionado
                        String idObjeto = getItem(position).getIdObjeto();
                        String fechaRegistro = getItem(position).getFechaHoraActual();
                        String tituloObjeto = getItem(position).getTitulo();
                        String descripcionObjeto = getItem(position).getDescripcion();
                        String categoriaObjeto = getItem(position).getCategoria();
                        String lugarPerdidaObjeto = getItem(position).getLugarPerdida();
                        String telefonoContactoObjeto = getItem(position).getTelefonoContacto();
                        String fechaPerdidaObjeto = getItem(position).getFechaPerdida();
                        String estadoObjeto = getItem(position).getEstado();
                        String imagenObjeto = getItem(position).getImagenObjeto();

                        // Enviar los datos a la siguiente actividad
                        Intent intent = new Intent(ListObjectsUser.this, DetalleObjetoUser.class);
                        intent.putExtra("idObjeto", idObjeto);
                        intent.putExtra("fechaRegistro", fechaRegistro);
                        intent.putExtra("tituloObjeto", tituloObjeto);
                        intent.putExtra("descripcionObjeto", descripcionObjeto);
                        intent.putExtra("categoriaObjeto", categoriaObjeto);
                        intent.putExtra("lugarPerdidaObjeto", lugarPerdidaObjeto);
                        intent.putExtra("telefonoContactoObjeto", telefonoContactoObjeto);
                        intent.putExtra("fechaPerdidaObjeto", fechaPerdidaObjeto);
                        intent.putExtra("estadoObjeto", estadoObjeto);
                        intent.putExtra("imagenObjeto", imagenObjeto);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return viewHolderObjetoUser;
            }
        };

        linearLayoutManager = new LinearLayoutManager(ListObjectsUser.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerViewObjetos.setLayoutManager(linearLayoutManager);
        recyclerViewObjetos.setAdapter(firebaseRecyclerAdapter);
    }

    private void BuscarObjetos(String tituloObjeto) {
        Query query = BD_Objetos.orderByChild("titulo").startAt(tituloObjeto).endAt(tituloObjeto + "\uf8ff");
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Objeto>().setQuery(query, Objeto.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Objeto, ViewHolderObjetoUser>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderObjetoUser viewHolderObjeto, int position, @NonNull Objeto objeto) {
                viewHolderObjeto.SetearDatos(
                        getApplicationContext(),
                        objeto.getIdObjeto(),
                        objeto.getFechaHoraActual(),
                        objeto.getTitulo(),
                        objeto.getDescripcion(),
                        objeto.getCategoria(),
                        objeto.getLugarPerdida(),
                        objeto.getTelefonoContacto(),
                        objeto.getFechaPerdida(),
                        objeto.getEstado(),
                        objeto.getImagenObjeto()
                );
            }

            @NonNull
            @Override
            public ViewHolderObjetoUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objeto_user, parent, false);
                ViewHolderObjetoUser viewHolderObjetoUser = new ViewHolderObjetoUser(view);
                viewHolderObjetoUser.setOnClickListener(new ViewHolderObjetoUser.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Obteniendo los datos del objeto seleccionado
                        String idObjeto = getItem(position).getIdObjeto();
                        String fechaRegistro = getItem(position).getFechaHoraActual();
                        String tituloObjeto = getItem(position).getTitulo();
                        String descripcionObjeto = getItem(position).getDescripcion();
                        String categoriaObjeto = getItem(position).getCategoria();
                        String lugarPerdidaObjeto = getItem(position).getLugarPerdida();
                        String telefonoContactoObjeto = getItem(position).getTelefonoContacto();
                        String fechaPerdidaObjeto = getItem(position).getFechaPerdida();
                        String estadoObjeto = getItem(position).getEstado();
                        String imagenObjeto = getItem(position).getImagenObjeto();

                        // Enviar los datos a la siguiente actividad
                        Intent intent = new Intent(ListObjectsUser.this, DetalleObjetoUser.class);
                        intent.putExtra("idObjeto", idObjeto);
                        intent.putExtra("fechaRegistro", fechaRegistro);
                        intent.putExtra("tituloObjeto", tituloObjeto);
                        intent.putExtra("descripcionObjeto", descripcionObjeto);
                        intent.putExtra("categoriaObjeto", categoriaObjeto);
                        intent.putExtra("lugarPerdidaObjeto", lugarPerdidaObjeto);
                        intent.putExtra("telefonoContactoObjeto", telefonoContactoObjeto);
                        intent.putExtra("fechaPerdidaObjeto", fechaPerdidaObjeto);
                        intent.putExtra("estadoObjeto", estadoObjeto);
                        intent.putExtra("imagenObjeto", imagenObjeto);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return viewHolderObjetoUser;
            }
        };

        recyclerViewObjetos.setLayoutManager(new GridLayoutManager(ListObjectsUser.this, 2));
        firebaseRecyclerAdapter.startListening();
        recyclerViewObjetos.setAdapter(firebaseRecyclerAdapter);
    }

    private void EliminarObjeto(String idObjeto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListObjectsUser.this);
        builder.setTitle("Eliminar");
        builder.setMessage("??Desea eliminar este objeto?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Query query = BD_Objetos.orderByChild("idObjeto").equalTo(idObjeto);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(ListObjectsUser.this, "Contacto Eliminado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ListObjectsUser.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ListObjectsUser.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    private void VaciarRegistroDeObjetos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListObjectsUser.this);
        builder.setTitle("Vaciar todos las objetos");
        builder.setMessage("??Est??s seguro(a) de eliminar todos los objetos");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Query query = BD_Objetos;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(ListObjectsUser.this, "Todos los objetos se han eliminado correctamente", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ListObjectsUser.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_objetos_user, menu);
        MenuItem menuItem = menu.findItem(R.id.buscarObjetosMenuUser);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                BuscarObjetos(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                BuscarObjetos(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.filtrarObjetosMenuUser) {
            FiltrarObjetos();
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

    private void FiltrarObjetos() {
        Button buttonTodosLosObjetos, buttonObjetosPerdidos, buttonObjetosEncontrados, buttonObjetosEntregados;

        dialogFilter.setContentView(R.layout.cuadro_dialogo_filtrar_objetos);

        buttonTodosLosObjetos = dialogFilter.findViewById(R.id.todosLosObjetos);
        buttonObjetosPerdidos = dialogFilter.findViewById(R.id.objetosPerdidos);
        buttonObjetosEncontrados = dialogFilter.findViewById(R.id.objetosEcontrados);
        buttonObjetosEntregados = dialogFilter.findViewById(R.id.objetosEntregados);

        buttonTodosLosObjetos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Todos");
                editor.apply();
                recreate();
                Toast.makeText(ListObjectsUser.this, "Todas los objetos", Toast.LENGTH_SHORT).show();
                dialogFilter.dismiss();
            }
        });

        buttonObjetosPerdidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Perdidos");
                editor.apply();
                recreate();
                Toast.makeText(ListObjectsUser.this, "Objetos perdidos", Toast.LENGTH_SHORT).show();
                dialogFilter.dismiss();
            }
        });

        buttonObjetosEncontrados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Encontrados");
                editor.apply();
                recreate();
                Toast.makeText(ListObjectsUser.this, "Objetos encontrados", Toast.LENGTH_SHORT).show();
                dialogFilter.dismiss();
            }
        });

        buttonObjetosEntregados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Entregados");
                editor.apply();
                recreate();
                Toast.makeText(ListObjectsUser.this, "Objetos entregados", Toast.LENGTH_SHORT).show();
                dialogFilter.dismiss();
            }
        });

        dialogFilter.show();
    }

    private void FiltroEstadoDeObjeto() {
        sharedPreferences = ListObjectsUser.this.getSharedPreferences("Objetos", MODE_PRIVATE);

        String estadoFiltro = sharedPreferences.getString("Listar", "Todos");

        switch (estadoFiltro) {
            case "Todos":
                ListarTodosLosObjetos();
                break;
            case "Perdidos":
                ListarObjetosPerdidos();
                break;
            case "Encontrados":
                ListarObjetosEncontrados();
                break;
            case "Entregados":
                ListarObjetosEntregados();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}