package com.raimundo.instagramclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.like.LikeButton;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.model.Comentario;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.MyViewHolder> {

    private List<Comentario> listComentarios;
    private Context context;

    public AdapterComentario(List<Comentario> listComentarios, Context context) {
        this.listComentarios = listComentarios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comentario, parent, false);

        return new AdapterComentario.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Comentario comentario = listComentarios.get(position);

        Picasso.with(context).load(comentario.getFotoUsuario()).into(holder.fotoUsuarioComentario);
        holder.nomeUsuarioComentario.setText(comentario.getNomeUsuario());
        holder.comentarioUsuario.setText(comentario.getComentario());
    }

    @Override
    public int getItemCount() {
        return listComentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView fotoUsuarioComentario;
        TextView nomeUsuarioComentario, comentarioUsuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoUsuarioComentario = itemView.findViewById(R.id.circleViewFotoUsuarioComentario);
            nomeUsuarioComentario = itemView.findViewById(R.id.textViewNomeUsuarioComentario);
            comentarioUsuario = itemView.findViewById(R.id.textViewComentarioUsuario);
        }
    }
}
