package com.raimundo.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.adapter.AdapterComentario;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.UsuarioFirebase;
import com.raimundo.instagramclone.model.Comentario;
import com.raimundo.instagramclone.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editTextComentario;

    private RecyclerView recyclerViewComentarios;
    private List<Comentario> listComentarios = new ArrayList<>();

    private AdapterComentario adapterComentario;

    private String idPostagem;
    private Usuario usuario;

    private DatabaseReference referenceFirebase;
    private DatabaseReference referenceComentarios;
    private ValueEventListener valueEventListenerComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        editTextComentario = findViewById(R.id.editTextComentario);
        recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios);

        referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);

        adapterComentario = new AdapterComentario(listComentarios, getApplicationContext());
        recyclerViewComentarios.setHasFixedSize(true);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComentarios.setAdapter(adapterComentario);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            idPostagem = bundle.getString("idPostagem");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        referenceComentarios.removeEventListener(valueEventListenerComentarios);
    }

    private void recuperarComentarios(){

        referenceComentarios = referenceFirebase.child("comentarios").child(idPostagem);
        valueEventListenerComentarios = referenceComentarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComentarios.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    listComentarios.add(dataSnapshot.getValue(Comentario.class));
                }
                adapterComentario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void salvarComentario(View view){
        String textoComentario = editTextComentario.getText().toString();

        if (editTextComentario != null && !editTextComentario.equals("")){
            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setIdUsuario(usuario.getIdUsuario());
            comentario.setNomeUsuario(usuario.getNomeUsuario());
            comentario.setFotoUsuario(usuario.getFotoUsuario());
            comentario.setComentario(textoComentario);

            if (comentario.salvarComentario()){
                Toast.makeText(ComentariosActivity.this, "Comentário salvo!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ComentariosActivity.this, "Insira um comentário!", Toast.LENGTH_SHORT).show();
        }
        editTextComentario.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}