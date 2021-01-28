package com.raimundo.instagramclone.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.activity.EditarPerfilActivity;
import com.raimundo.instagramclone.adapter.AdapterPostagens;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.UsuarioFirebase;
import com.raimundo.instagramclone.model.Postagem;
import com.raimundo.instagramclone.model.Usuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    private ProgressBar progressBarPerfil;
    private CircleImageView imageViewPerfil;
    private TextView textViewPublicacoes, textViewSeguidores, textViewSeguindo;
    private Button buttonAcaoPerfil;
    private GridView gridViewPerfil;
    private AdapterPostagens adapterPostagens;

    private Usuario usuarioLogado;

    private DatabaseReference referenceFirebase;
    private DatabaseReference referenceUsuario;
    private DatabaseReference referenceUsuarioLogado;
    private DatabaseReference referencePostagensUsuario;
    private ValueEventListener valueEventListenerPerfil;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();
        referenceUsuario = referenceFirebase.child("usuarios");
        referencePostagensUsuario = ConfiguracaoFirebase.getDatabaseReference()
                .child("postagens").child(usuarioLogado.getIdUsuario());

        inicializarComponetes(view);

        imageViewPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });

        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
            startActivity(intent);
            }
        });

        inicializarImageLoader();
        carregarFotosPostagens();

        return view;
    }

    private void showImage() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialog);
        ImageView imageViewFoto = dialog.findViewById(R.id.imagemViewFoto);

        FirebaseUser firebaseUser = UsuarioFirebase.getUsuarioAtual();
        Uri uri = firebaseUser.getPhotoUrl();
        if (uri != null) {
            Picasso.with(getActivity()).load(uri).into(imageViewFoto);
        } else {
            imageViewFoto.setImageResource(R.drawable.avatar);
        }

        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarDadosUsuarioLogado();
        carregarFotoUsuario();
    }

    @Override
    public void onStop() {
        super.onStop();
        referenceUsuarioLogado.removeEventListener(valueEventListenerPerfil);
    }

    private void inicializarComponetes(View view){
        progressBarPerfil = view.findViewById(R.id.progressBarPerfil);
        imageViewPerfil = view.findViewById(R.id.imageViewPerfil);
        textViewPublicacoes = view.findViewById(R.id.textViewPublicacoes);
        textViewSeguidores = view.findViewById(R.id.textViewSeguidores);
        textViewSeguindo = view.findViewById(R.id.textViewSeguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
    }

    private void carregarFotoUsuario(){
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        String foto = usuarioLogado.getFotoUsuario();
        if (foto != null) {
            Uri uri = Uri.parse(foto);
            Picasso.with(getActivity()).load(uri).into(imageViewPerfil);
        } else {
            imageViewPerfil.setImageResource(R.drawable.avatar);
        }
    }

    private void recuperarDadosUsuarioLogado(){
        referenceUsuarioLogado = referenceUsuario.child(usuarioLogado.getIdUsuario());
        valueEventListenerPerfil = referenceUsuarioLogado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                String postagens = String.valueOf(usuario.getPostagens());
                String seguidores = String.valueOf(usuario.getSeguidores());
                String seguindo = String.valueOf(usuario.getSeguindo());

                textViewPublicacoes.setText(postagens);
                textViewSeguidores.setText(seguidores);
                textViewSeguindo.setText(seguindo);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void inicializarImageLoader(){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    public void carregarFotosPostagens(){
        referencePostagensUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Postagem postagem = dataSnapshot.getValue(Postagem.class);
                    urlFotos.add(postagem.getFoto());
                    System.out.println(dataSnapshot);
                }

                adapterPostagens = new AdapterPostagens(getActivity(), R.layout.adapter_postagens, urlFotos);
                gridViewPerfil.setAdapter(adapterPostagens);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
