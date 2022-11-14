package com.geraldcode.findapp.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geraldcode.findapp.R;


public class ViewHolder_Nota extends RecyclerView.ViewHolder {

    View mView;

    private ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position); /* SE EJECUTA AL PRESIONAR EN EL ITEM */

        void onItemLongClick(View view, int position); /* SE EJECUTA AL MANTENER PRESIONADO EN EL ITEM */
    }

    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolder_Nota(@NonNull View itemView) {
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
        TextView idNotaItemUser, uidUsuarioNoteItemUser, correoUsuarioNoteItemUser, fechaHoraRegistroNoteItemUser, tituloNoteItemUser, descripcionNoteItemUser, fechaNoteItemUser, estadoNoteItemUser;

        ImageView TareaFinalizadaItem, TareaNoFinalizadaItem;

        TareaFinalizadaItem = mView.findViewById(R.id.tareaFinalizadaItemUser);
        TareaNoFinalizadaItem = mView.findViewById(R.id.tareaNoFinalizadaItemUser);


        // ESTABLECER LA CONEXIÓN CON EL ITEM
        idNotaItemUser = mView.findViewById(R.id.idNotaItemUser);
        uidUsuarioNoteItemUser = mView.findViewById(R.id.uidUsuarioNoteItemUser);
        correoUsuarioNoteItemUser = mView.findViewById(R.id.correoUsuarioNoteItemUser);
        fechaHoraRegistroNoteItemUser = mView.findViewById(R.id.fechaHoraRegistroNoteItemUser);
        tituloNoteItemUser = mView.findViewById(R.id.tituloNoteItemUser);
        descripcionNoteItemUser = mView.findViewById(R.id.descripcionNoteItemUser);
        fechaNoteItemUser = mView.findViewById(R.id.fechaNoteItemUser);
        estadoNoteItemUser = mView.findViewById(R.id.estadoNoteItemUser);

        // SETEAR LA INFORMACIÓN DENTRO DEL ITEM
        idNotaItemUser.setText(id_nota);
        uidUsuarioNoteItemUser.setText(uid_usuario);
        correoUsuarioNoteItemUser.setText(correo_usuario);
        fechaHoraRegistroNoteItemUser.setText(fecha_hora_registro);
        tituloNoteItemUser.setText(titulo);
        descripcionNoteItemUser.setText(descripcion);
        fechaNoteItemUser.setText(fecha_nota);
        estadoNoteItemUser.setText(estado);

        // GESTIONAMOS EL COLOR DEL ESTADO
        if(estado.equals("Finalizado")){
            TareaFinalizadaItem.setVisibility(View.VISIBLE);
        } else {
            TareaNoFinalizadaItem.setVisibility(View.VISIBLE);
        }
    }
}
