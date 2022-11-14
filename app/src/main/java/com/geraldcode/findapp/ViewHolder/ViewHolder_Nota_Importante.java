package com.geraldcode.findapp.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geraldcode.findapp.R;


public class ViewHolder_Nota_Importante extends RecyclerView.ViewHolder {

    View mView;

    private ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); /*SE EJECUTA AL PRESIONAR EN EL ITEM*/
        void onItemLongClick(View view, int position); /*SE EJECUTA AL MANTENER PRESIONADO EN EL ITEM*/
    }

    public void setOnClickListener(ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolder_Nota_Importante(@NonNull View itemView) {
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
        TextView idNotaItemUserImportante, uidUsuarioNoteItemUserImportante,
                correoUsuarioNoteItemUserImportante, fechaHoraRegistroNoteItemUserImportante,
                tituloNoteItemUserImportante, descripcionNoteItemUserImportante,
                fechaNoteItemUserImportante, estadoNoteItemUserImportante;

        ImageView TareaFinalizadaItemImportante, TareaNoFinalizadaItemImportante;

        //ESTABLECER LA CONEXIÓN CON EL ITEM
        idNotaItemUserImportante = mView.findViewById(R.id.idNotaItemUserImportante);
        uidUsuarioNoteItemUserImportante = mView.findViewById(R.id.uidUsuarioNoteItemUserImportante);
        correoUsuarioNoteItemUserImportante = mView.findViewById(R.id.correoUsuarioNoteItemUserImportante);
        fechaHoraRegistroNoteItemUserImportante = mView.findViewById(R.id.fechaHoraRegistroNoteItemUserImportante);
        tituloNoteItemUserImportante = mView.findViewById(R.id.tituloNoteItemUserImportante);
        descripcionNoteItemUserImportante = mView.findViewById(R.id.descripcionNoteItemUserImportante);
        fechaNoteItemUserImportante = mView.findViewById(R.id.fechaNoteItemUserImportante);
        estadoNoteItemUserImportante = mView.findViewById(R.id.estadoNoteItemUserImportante);

        TareaFinalizadaItemImportante = mView.findViewById(R.id.tareaFinalizadaItemUserImportante);
        TareaNoFinalizadaItemImportante = mView.findViewById(R.id.tareaNoFinalizadaItemUserImportante);

        //SETEAR LA INFORMACIÓN DENTRO DEL ITEM
        idNotaItemUserImportante.setText(id_nota);
        uidUsuarioNoteItemUserImportante.setText(uid_usuario);
        correoUsuarioNoteItemUserImportante.setText(correo_usuario);
        fechaHoraRegistroNoteItemUserImportante.setText(fecha_hora_registro);
        tituloNoteItemUserImportante.setText(titulo);
        descripcionNoteItemUserImportante.setText(descripcion);
        fechaNoteItemUserImportante.setText(fecha_nota);
        estadoNoteItemUserImportante.setText(estado);

        //GESTIONAMOS EL COLOR DEL ESTADO
        if (estado.equals("Finalizado")){
            TareaFinalizadaItemImportante.setVisibility(View.VISIBLE);
        }else {
            TareaNoFinalizadaItemImportante.setVisibility(View.VISIBLE);
        }
    }

}
