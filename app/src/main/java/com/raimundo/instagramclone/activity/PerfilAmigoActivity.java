package com.raimundo.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.adapter.AdapterPostagens;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.UsuarioFirebase;
import com.raimundo.instagramclone.model.Postagem;
import com.raimundo.instagramclone.model.Usuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private CircleImageView imageViewPerfil;
    private TextView textViewPublicacoes, textViewSeguidores, textViewSeguindo;
    private Button buttonAcaoPerfil;
    private GridView gridViewPerfil;
    private AdapterPostagens adapterPostagens;
    private List<Postagem> postagens;

    private DatabaseReference referenceFirebase;
    private DatabaseReference referenceUsuario;
    private DatabaseReference referenceAmigos;
    private DatabaseReference referenceUsuarioLogado;
    private DatabaseReference referenceSeguidores;
    private DatabaseReference referencePostagensUsuario;
    private ValueEventListener valueEventListenerAmigos;

    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();
        referenceUsuario = referenceFirebase.child("usuarios");
        referenceSeguidores = referenceFirebase.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdUsuarioLogado();

        inicializarComponetes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");
            getSupportActionBar().setTitle(usuarioSelecionado.getNomeUsuario());

            referencePostagensUsuario = ConfiguracaoFirebase.getDatabaseReference()
                    .child("postagens").child(usuarioSelecionado.getIdUsuario());

            String fotoUsuario = usuarioSelecionado.getFotoUsuario();
            if (fotoUsuario != null){
                Uri uri = Uri.parse(fotoUsuario);
                Picasso.with(PerfilAmigoActivity.this).load(uri).into(imageViewPerfil);
            }
        }

        imageViewPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });
        inicializarImageLoader();
        carregarFotosPostagens();

        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Postagem postagem = postagens.get(position);
                Intent intent = new Intent(getApplicationContext(), VisualizarPostagemActivity.class);
                intent.putExtra("postagem", postagem);
                intent.putExtra("usuario", usuarioSelecionado);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigos();
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        referenceAmigos.removeEventListener(valueEventListenerAmigos);
    }

    private void inicializarComponetes(){
        imageViewPerfil = findViewById(R.id.imageViewPerfil);
        textViewPublicacoes = findViewById(R.id.textViewPublicacoes);
        textViewSeguidores = findViewById(R.id.textViewSeguidores);
        textViewSeguindo = findViewById(R.id.textViewSeguindo);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Carregando...");
        gridViewPerfil = findViewById(R.id.gridViewPerfil);
    }

    private void showImage() {
        final Dialog dialog = new Dialog(PerfilAmigoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialog);
        ImageView imageViewFoto = dialog.findViewById(R.id.imagemViewFoto);

        String fotoUsuario = usuarioSelecionado.getFotoUsuario();
        if (fotoUsuario != null){
            Uri uri = Uri.parse(fotoUsuario);
            Picasso.with(PerfilAmigoActivity.this).load(uri).into(imageViewFoto);
        }

        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void inicializarImageLoader(){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(configuration);

    }

    public void carregarFotosPostagens(){
        postagens = new ArrayList<>();

        referencePostagensUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 2 ;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Postagem postagem = dataSnapshot.getValue(Postagem.class);
                    postagens.add(postagem);
                    urlFotos.add(postagem.getFoto());
                }

                adapterPostagens = new AdapterPostagens(getApplicationContext(), R.layout.adapter_postagens, urlFotos);
                gridViewPerfil.setAdapter(adapterPostagens);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void recuperarDadosPerfilAmigos(){
        referenceAmigos = referenceUsuario.child(usuarioSelecionado.getIdUsuario());
        valueEventListenerAmigos = referenceAmigos.addValueEventListener(new ValueEventListener() {
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

    private void recuperarDadosUsuarioLogado(){

        referenceUsuarioLogado = referenceUsuario.child(idUsuarioLogado);
        referenceUsuarioLogado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usuarioLogado = dataSnapshot.getValue(Usuario.class);

                verificarSegueUsuarioAmigo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verificarSegueUsuarioAmigo(){
        DatabaseReference referenceSeguidor = referenceSeguidores
                .child(usuarioSelecionado.getIdUsuario())
                .child(idUsuarioLogado);
        referenceSeguidor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    habilitarBotaoSeguir(true);
                } else {
                    habilitarBotaoSeguir(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void habilitarBotaoSeguir(final Boolean segueUsuario){
        if (segueUsuario){
            buttonAcaoPerfil.setText("Seguindo");
        } else {
            buttonAcaoPerfil.setText("Seguir");

            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });
        }
    }

    private void salvarSeguidor(Usuario usuarioLogado, Usuario usuarioAmigo){

        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("nome", usuarioLogado.getNomeUsuario());
        dadosUsuarioLogado.put("foto", usuarioLogado.getFotoUsuario());

        DatabaseReference referenceSeguidor = referenceSeguidores
                .child(usuarioAmigo.getIdUsuario())
                .child(usuarioLogado.getIdUsuario());
        referenceSeguidor.setValue(dadosUsuarioLogado);

        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        int seguindo = usuarioLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);
        DatabaseReference usuarioSeguindo = referenceUsuario.child(usuarioLogado.getIdUsuario());
        usuarioSeguindo.updateChildren(dadosSeguindo);

        int seguidores = usuarioAmigo.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguindo.put("seguidores", seguidores);
        DatabaseReference usuarioSeguidores = referenceUsuario.child(usuarioAmigo.getIdUsuario());
        usuarioSeguidores.updateChildren(dadosSeguindo);
    }
}
