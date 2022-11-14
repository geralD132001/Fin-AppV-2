package com.geraldcode.findapp.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geraldcode.findapp.R;


public class ViewHolder_Nota_Admin extends RecyclerView.ViewHolder {

    View mView;

    private ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position); /* SE EJECUTA AL PRESIONAR EN EL ITEM */

        void onItemLongClick(View view, int position); /* SE EJECUTA AL MANTENER PRESIONADO EN EL ITEM */
    }

    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolder_Nota_Admin(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getBindingAdapterPosition());
                return false;
            }
        });
    }

    public void SetearDatos(Context context, String id_nota, String uid_usuario, String correo_usuario, String fecha_hora_registro, String titulo, String descripcion, String fecha_nota, String estado) {

        // DECLARAR LAS VISTAS
        TextView idNotaItemAdmin, uidUsuarioNoteItemAdmin, correoUsuarioNoteItemAdmin, fechaHoraRegistroNoteItemAdmin,
                tituloNoteItemAdmin, descripcionNoteItemAdmin, fechaNoteItemAdmin, estadoNoteItemAdmin;

        ImageView TareaFinalizadaItem, TareaNoFinalizadaItem;

        TareaFinalizadaItem = mView.findViewById(R.id.tareaFinalizadaItemAdmin);
        TareaNoFinalizadaItem = mView.findViewById(R.id.tareaNoFinalizadaItemAdmin);


        // ESTABLECER LA CONEXIÓN CON EL ITEM
        idNotaItemAdmin = mView.findViewById(R.id.idNotaItemAdmin);
        uidUsuarioNoteItemAdmin = mView.findViewById(R.id.uidUsuarioNoteItemAdmin);
        correoUsuarioNoteItemAdmin = mView.findViewById(R.id.correoUsuarioNoteItemAdmin);
        fechaHoraRegistroNoteItemAdmin = mView.findViewById(R.id.fechaHoraRegistroNoteItemAdmin);
        tituloNoteItemAdmin = mView.findViewById(R.id.tituloNoteItemAdmin);
        descripcionNoteItemAdmin = mView.findViewById(R.id.descripcionNoteItemAdmin);
        fechaNoteItemAdmin = mView.findViewById(R.id.fechaNoteItemAdmin);
        estadoNoteItemAdmin = mView.findViewById(R.id.estadoNoteItemAdmin);

        // SETEAR LA INFORMACIÓN DENTRO DEL ITEM
        idNotaItemAdmin.setText(id_nota);
        uidUsuarioNoteItemAdmin.setText(uid_usuario);
        correoUsuarioNoteItemAdmin.setText(correo_usuario);
        fechaHoraRegistroNoteItemAdmin.setText(fecha_hora_registro);
        tituloNoteItemAdmin.setText(titulo);
        descripcionNoteItemAdmin.setText(descripcion);
        fechaNoteItemAdmin.setText(fecha_nota);
        estadoNoteItemAdmin.setText(estado);

        // GESTIONAMOS EL COLOR DEL ESTADO
        if(estado.equals("Finalizado")){
            TareaFinalizadaItem.setVisibility(View.VISIBLE);
        } else {
            TareaNoFinalizadaItem.setVisibility(View.VISIBLE);
        }
    }
}
