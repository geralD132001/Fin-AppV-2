package com.geraldcode.findapp.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geraldcode.findapp.R;


public class ViewHolder_Nota_Importante_Admin extends RecyclerView.ViewHolder {

    View mView;

    private ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); /*SE EJECUTA AL PRESIONAR EN EL ITEM*/
        void onItemLongClick(View view, int position); /*SE EJECUTA AL MANTENER PRESIONADO EN EL ITEM*/
    }

    public void setOnClickListener(ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolder_Nota_Importante_Admin(@NonNull View itemView) {
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

    public void SetearDatos(Context context, String id_nota , String uid_usuario, String correo_usuario,
                            String fecha_hora_registro, String titulo, String descripcion, String fecha_nota,
                            String estado){

        //DECLARAR LAS VISTAS
        TextView idNotaItemAdminImportante, uidUsuarioNoteItemAdminImportante,
                correoUsuarioNoteItemAdminImportante, fechaHoraRegistroNoteItemAdminImportante,
                tituloNoteItemAdminImportante, descripcionNoteItemAdminImportante,
                fechaNoteItemAdminImportante, estadoNoteItemAdminImportante;

        ImageView TareaFinalizadaItemImportante, TareaNoFinalizadaItemImportante;

        //ESTABLECER LA CONEXIÓN CON EL ITEM
        idNotaItemAdminImportante = mView.findViewById(R.id.idNotaItemAdminImportante);
        uidUsuarioNoteItemAdminImportante = mView.findViewById(R.id.uidUsuarioNoteItemAdminImportante);
        correoUsuarioNoteItemAdminImportante = mView.findViewById(R.id.correoUsuarioNoteItemAdminImportante);
        fechaHoraRegistroNoteItemAdminImportante = mView.findViewById(R.id.fechaHoraRegistroNoteItemAdminImportante);
        tituloNoteItemAdminImportante = mView.findViewById(R.id.tituloNoteItemAdminImportante);
        descripcionNoteItemAdminImportante = mView.findViewById(R.id.descripcionNoteItemAdminImportante);
        fechaNoteItemAdminImportante = mView.findViewById(R.id.fechaNoteItemAdminImportante);
        estadoNoteItemAdminImportante = mView.findViewById(R.id.estadoNoteItemAdminImportante);

        TareaFinalizadaItemImportante = mView.findViewById(R.id.tareaFinalizadaItemAdminImportante);
        TareaNoFinalizadaItemImportante = mView.findViewById(R.id.tareaNoFinalizadaItemAdminImportante);

        //SETEAR LA INFORMACIÓN DENTRO DEL ITEM
        idNotaItemAdminImportante.setText(id_nota);
        uidUsuarioNoteItemAdminImportante.setText(uid_usuario);
        correoUsuarioNoteItemAdminImportante.setText(correo_usuario);
        fechaHoraRegistroNoteItemAdminImportante.setText(fecha_hora_registro);
        tituloNoteItemAdminImportante.setText(titulo);
        descripcionNoteItemAdminImportante.setText(descripcion);
        fechaNoteItemAdminImportante.setText(fecha_nota);
        estadoNoteItemAdminImportante.setText(estado);

        //GESTIONAMOS EL COLOR DEL ESTADO
        if (estado.equals("Finalizado")){
            TareaFinalizadaItemImportante.setVisibility(View.VISIBLE);
        }else {
            TareaNoFinalizadaItemImportante.setVisibility(View.VISIBLE);
        }
    }

}
