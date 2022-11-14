package com.geraldcode.findapp.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geraldcode.findapp.R;

public class ViewHolderObjeto extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderObjeto.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position); /* SE EJECUTA AL PRESIONAR EN EL ITEM */

        void onItemLongClick(View view, int position); /* SE EJECUTA AL MANTENER PRESIONADO EN EL ITEM */
    }

    public void setOnClickListener(ViewHolderObjeto.ClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public ViewHolderObjeto(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getBindingAdapterPosition());
                return false;
            }
        });
    }

    public void SetearDatos(Context context, String idObjeto, String fechaHoraActual, String titulo, String descripcion, String categoria, String lugarPerdida, String telefonoContacto, String fechaPerdida, String estado, String imagenObjeto) {

        // DECLARAR LAS VISTAS
        TextView idUsuarioObjetoItem, fechaHoraRegistroObjetoItem, tituloObjetoItem,
                descripcionObjetoItem, categoriaObjetoItem,
                lugarPerdidaObjetoItem, telefonoContactoOjetoItem,
                fechaPerdidaObjetoItem, estadoObjetoItem;
        ImageView imageObjetoItem, objetoPerdido, objetoEncontrado, objetoEntregado;

        // ESTABLECER LA CONEXIÓN CON EL ITEM
        objetoPerdido = mView.findViewById(R.id.objetoPerdido);
        objetoEncontrado = mView.findViewById(R.id.objetoEncontrado);
        objetoEntregado = mView.findViewById(R.id.objetoEntregado);
        idUsuarioObjetoItem = mView.findViewById(R.id.idObjetoItem);
        fechaHoraRegistroObjetoItem = mView.findViewById(R.id.fechaHoralActualItem);
        tituloObjetoItem = mView.findViewById(R.id.tituloObjetoItem);
        descripcionObjetoItem = mView.findViewById(R.id.descripcionObjetoItem);
        categoriaObjetoItem = mView.findViewById(R.id.categoriaObjetoItem);
        lugarPerdidaObjetoItem = mView.findViewById(R.id.lugarPerdidaObjetoItem);
        telefonoContactoOjetoItem = mView.findViewById(R.id.telefonoContactoObjetoItem);
        fechaPerdidaObjetoItem = mView.findViewById(R.id.fechaPerdidaObjetoItem);
        estadoObjetoItem = mView.findViewById(R.id.estadoObjetoItem);
        imageObjetoItem = mView.findViewById(R.id.imagenObjetoItem);

        // SETEAR LA INFORMACIÓN DENTRO DEL ITEM
        idUsuarioObjetoItem.setText(idObjeto);
        fechaHoraRegistroObjetoItem.setText(fechaHoraActual);
        tituloObjetoItem.setText(titulo);
        descripcionObjetoItem.setText(descripcion);
        telefonoContactoOjetoItem.setText(telefonoContacto);
        categoriaObjetoItem.setText(categoria);
        lugarPerdidaObjetoItem.setText(lugarPerdida);
        fechaPerdidaObjetoItem.setText(fechaPerdida);
        estadoObjetoItem.setText(estado);

        try {
            /* Si la imgen del objeto existe en la BD */
            Glide.with(context).load(imagenObjeto).placeholder(R.drawable.imagen_contacto).into(imageObjetoItem);
        } catch (Exception e) {
            /* Si la imagen del objeto NO exite en la BD */
            Glide.with(context).load(R.drawable.imagen_contacto).into(imageObjetoItem);
        }


        // GESTIONAMOS EL COLOR DEL ESTADO
        if (estado.equals("Encontrado")) {
            objetoEncontrado.setVisibility(View.VISIBLE);
        } else if (estado.equals("Entregado")) {
            objetoEntregado.setVisibility(View.VISIBLE);
        } else {
            objetoPerdido.setVisibility(View.VISIBLE);
        }

    }
}
