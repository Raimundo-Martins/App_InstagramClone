package com.raimundo.instagramclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.activity.ComentariosActivity;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.UsuarioFirebase;
import com.raimundo.instagramclone.model.Feed;
import com.raimundo.instagramclone.model.PostagemCurtida;
import com.raimundo.instagramclone.model.Usuario;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {

    private List<Feed> feedList;
    private Context context;

    public AdapterFeed(List<Feed> feedList, Context context) {
        this.feedList = feedList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);

        return new AdapterFeed.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Feed feed = feedList.get(position);
        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        Uri uriFotoUsuario = Uri.parse(feed.getFotoUsuario());
        Uri uriFotoPostagem = Uri.parse(feed.getFotoPostagem());
        Picasso.with(context).load(uriFotoUsuario).into(holder.fotoPerfil);
        Picasso.with(context).load(uriFotoPostagem).into(holder.fotoPostagem);
        holder.nome.setText(feed.getNomeUsuario());
        holder.descricao.setText(feed.getDescricao());

        holder.visualizarComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComentariosActivity.class);
                intent.putExtra("idPostagem", feed.getId());
                context.startActivity(intent);
            }
        });

        DatabaseReference referenceCurtidas = ConfiguracaoFirebase.getDatabaseReference().child("postagens-curtidas")
                .child(feed.getId());
        referenceCurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalCurtidas = 0;
                if (snapshot.hasChild("totalCurtidas")){
                    PostagemCurtida postagemCurtida = snapshot.getValue(PostagemCurtida.class);
                    totalCurtidas = postagemCurtida.getTotalCurtidas();
                }
                if (snapshot.hasChild(usuarioLogado.getIdUsuario())){
                    holder.likeButtonFeed.setLiked(true);
                } else {
                    holder.likeButtonFeed.setLiked(false);
                }

                final PostagemCurtida curtida = new PostagemCurtida();
                curtida.setFeed(feed);
                curtida.setUsuario(usuarioLogado);
                curtida.setTotalCurtidas(totalCurtidas);

                holder.likeButtonFeed.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        curtida.salvar();
                        holder.totalCurtidas.setText(curtida.getTotalCurtidas() + " curtidas");
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.removerCurtidas();
                        holder.totalCurtidas.setText(curtida.getTotalCurtidas() + " curtidas");
                    }
                });
                holder.totalCurtidas.setText(curtida.getTotalCurtidas() + " curtidas");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView fotoPerfil;
        TextView nome, descricao, totalCurtidas;
        ImageView fotoPostagem, visualizarComentarios;
        LikeButton likeButtonFeed;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPerfil = itemView.findViewById(R.id.circleImageViewPerfilPostagem);
            nome = itemView.findViewById(R.id.textViewPerfilPostagem);
            descricao = itemView.findViewById(R.id.textViewDescricaoPostagem);
            visualizarComentarios = itemView.findViewById(R.id.imageViewComentario);
            totalCurtidas = itemView.findViewById(R.id.textViewCurtidasPostagem);
            fotoPostagem = itemView.findViewById(R.id.imageViewPostagem);
            likeButtonFeed = itemView.findViewById(R.id.likeButtonFeed);
        }
    }
}
