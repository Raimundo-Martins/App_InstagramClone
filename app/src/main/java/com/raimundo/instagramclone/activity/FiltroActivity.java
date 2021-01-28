package com.raimundo.instagramclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.adapter.AdapterFiltro;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.RecyclerItemClickListener;
import com.raimundo.instagramclone.helper.UsuarioFirebase;
import com.raimundo.instagramclone.model.Postagem;
import com.raimundo.instagramclone.model.Usuario;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageViewFotoPostagem;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private TextInputEditText editTextDescricaoPostagem;

    private List<ThumbnailItem> listFiltros;
    private String idUsuarioLogado;
    private Usuario usuarioLogado;
    private AlertDialog dialog;

    private RecyclerView recyclerViewFiltros;
    private AdapterFiltro adapterFiltro;

    private DatabaseReference referenceUsuario;
    private DatabaseReference referenceUsuarioLogado;
    private DatabaseReference referenceFirebase;
    private DataSnapshot snapshotSeguidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        listFiltros = new ArrayList<>();
        referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();
        idUsuarioLogado = UsuarioFirebase.getIdUsuarioLogado();
        referenceUsuario = ConfiguracaoFirebase.getDatabaseReference().child("usuarios");

        imageViewFotoPostagem = findViewById(R.id.imageViewFotoPostagem);
        editTextDescricaoPostagem = findViewById(R.id.editTextDescricaoFoto);
        recyclerViewFiltros = findViewById(R.id.recyclerViewFiltros);

        recuperarDadosPostagem();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            byte[] dadosImagem = bundle.getByteArray("fotoPostagem");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageViewFotoPostagem.setImageBitmap(imagem);
            imagemFiltro = imagem.copy(imagem.getConfig(), true);

            adapterFiltro = new AdapterFiltro(listFiltros, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewFiltros.setLayoutManager(layoutManager);
            recyclerViewFiltros.setAdapter(adapterFiltro);

            recyclerViewFiltros.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), recyclerViewFiltros,
                            new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ThumbnailItem item = listFiltros.get(position);

                    imagemFiltro = imagem.copy(imagem.getConfig(), true);
                    Filter filtro = item.filter;
                    imageViewFotoPostagem.setImageBitmap(filtro.processFilter(imagemFiltro));
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            }));

            recuperarFiltros();
        }
    }

    private void dialogCarregando(String titulo){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();
    }

    private void recuperarDadosPostagem(){
        dialogCarregando("Carregando dados, aguarde!");

        referenceUsuarioLogado = referenceUsuario.child(idUsuarioLogado);
        referenceUsuarioLogado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioLogado = dataSnapshot.getValue(Usuario.class);

                DatabaseReference referenceSeguidores = referenceFirebase.child("seguidores")
                        .child(idUsuarioLogado);
                referenceSeguidores.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshotSeguidores = snapshot;
                        dialog.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recuperarFiltros(){
        ThumbnailsManager.clearThumbs();
        listFiltros.clear();

        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filtro : filtros){
            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterFiltro.notifyDataSetChanged();
    }

    private void publicarPostagem(){
        dialogCarregando("Salvando postagem.");

        final Postagem postagem = new Postagem();
        postagem.setIdUsuario(idUsuarioLogado);
        postagem.setDescricao(editTextDescricaoPostagem.getText().toString());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imagemFiltro.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] dadosImagem = outputStream.toByteArray();

        StorageReference reference = ConfiguracaoFirebase.getStorageReference();
        final StorageReference referenceImagem = reference.child("imagens").child("postagens").child(postagem.getId() + ".jpeg");

        UploadTask uploadTask = referenceImagem.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FiltroActivity.this, "Erro ao salvar imagem, tente novamente!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                referenceImagem.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri uri = Uri.parse(task.getResult().toString());
                        postagem.setFoto(uri.toString());

                        int totalPostagens = usuarioLogado.getPostagens() + 1;
                        usuarioLogado.setPostagens(totalPostagens);
                        usuarioLogado.atualizarPostagemUsuario();

                        if (postagem.salvarPostagem(snapshotSeguidores)){
                            Toast.makeText(FiltroActivity.this, "Sucesso ao salvar postagem!", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_salvar_postagem:
                publicarPostagem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
