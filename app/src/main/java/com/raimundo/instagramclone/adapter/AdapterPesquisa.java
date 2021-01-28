package com.raimundo.instagramclone.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.model.Usuario;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder> {

    private List<Usuario> usuarioList;
    private Context context;

    public AdapterPesquisa(List<Usuario> usuarioList, Context context) {
        this.usuarioList = usuarioList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_pesquisa_usuario, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Usuario usuario = usuarioList.get(i);
        if (usuario.getFotoUsuario() != null){
            Uri uri = Uri.parse(usuario.getFotoUsuario());
            Picasso.with(context).load(uri).into(myViewHolder.imageViewFoto);
        } else {
            myViewHolder.imageViewFoto.setImageResource(R.drawable.avatar);
        }
        myViewHolder.textViewNome.setText(usuario.getNomeUsuario());
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imageViewFoto;
        TextView textViewNome;

        public MyViewHolder(View view){
            super(view);

            imageViewFoto = view.findViewById(R.id.imageViewFotoPesquisa);
            textViewNome = view.findViewById(R.id.textViewNomePesquisa);
        }
    }
}
