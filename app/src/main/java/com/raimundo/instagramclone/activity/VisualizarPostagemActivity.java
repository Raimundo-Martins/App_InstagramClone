package com.raimundo.instagramclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.model.Postagem;
import com.raimundo.instagramclone.model.Usuario;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {

    private CircleImageView circleImageViewPerfilPostagem;
    private TextView textViewPerfilPostagem, textViewCurtidasPostagem, textViewDescricaoPostagem, textViewComentariosPostagem;
    private ImageView imageViewFotoPostagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Postagem");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Postagem postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuario");

            Uri uri = Uri.parse(usuario.getFotoUsuario());
            Picasso.with(VisualizarPostagemActivity.this).load(uri).into(circleImageViewPerfilPostagem);
            textViewPerfilPostagem.setText(usuario.getNomeUsuario());

            Uri uriPostagem = Uri.parse(postagem.getFoto());
            Picasso.with(VisualizarPostagemActivity.this).load(uriPostagem).into(imageViewFotoPostagem);
            textViewDescricaoPostagem.setText(postagem.getDescricao());
        }
    }

    private void inicializarComponentes(){
        circleImageViewPerfilPostagem = findViewById(R.id.circleImageViewPerfilPostagem);
        textViewPerfilPostagem = findViewById(R.id.textViewPerfilPostagem);
        textViewCurtidasPostagem = findViewById(R.id.textViewCurtidasPostagem);
        textViewDescricaoPostagem = findViewById(R.id.textViewDescricaoPostagem);
        //textViewComentariosPostagem = findViewById(R.id.textViewComentariosPostagem);
        imageViewFotoPostagem = findViewById(R.id.imageViewPostagem);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}