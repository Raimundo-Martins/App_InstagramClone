package com.raimundo.instagramclone.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.Permissao;
import com.raimundo.instagramclone.helper.UsuarioFirebase;
import com.raimundo.instagramclone.model.Usuario;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private CircleImageView imageViewFotoPerfil;
    private TextView textViewEditarFoto;
    private EditText editTextAlterarNome, editTextAlteraEmail;
    private Button buttonAlterarPerfil;
    private Usuario usuarioLogado;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageReference = ConfiguracaoFirebase.getStorageReference();
        idUsuarioLogado = UsuarioFirebase.getIdUsuarioLogado();
        Permissao.validarPermissoes(permissoes, this, 1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);

        inicializarComponentes();

        FirebaseUser firebaseUser = UsuarioFirebase.getUsuarioAtual();
        Uri uri = firebaseUser.getPhotoUrl();
        if (uri != null){
            Picasso.with(EditarPerfilActivity.this).load(uri).into(imageViewFotoPerfil);
        } else {
            imageViewFotoPerfil.setImageResource(R.drawable.avatar);
        }

        editTextAlterarNome.setText(firebaseUser.getDisplayName().toUpperCase());
        editTextAlteraEmail.setText(firebaseUser.getEmail());

        buttonAlterarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtualizado = editTextAlterarNome.getText().toString();

                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);
                usuarioLogado.setNomeUsuario(nomeAtualizado);
                usuarioLogado.atualizarUsuario();

                Toast.makeText(EditarPerfilActivity.this, "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        textViewEditarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }
                if (imagem != null){
                    imageViewFotoPerfil.setImageBitmap(imagem);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                    byte[] dadosImagem = outputStream.toByteArray();

                    final StorageReference referenceImagem = storageReference.child("imagens").child("perfil")
                            .child(idUsuarioLogado + ".jpeg");
                    UploadTask uploadTask = referenceImagem.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this, "Erro ao fazer uploud da imagem!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            referenceImagem.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri uri = Uri.parse(task.getResult().toString());
                                    atualizarFotoUsuario(uri);
                                }
                            });

                            Toast.makeText(EditarPerfilActivity.this, "Sucesso ao fazer uploud da imagem!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(Uri uri){
        UsuarioFirebase.atualizarFotoUsuario(uri);
        usuarioLogado.setFotoUsuario(uri.toString());
        usuarioLogado.atualizarUsuario();

        Toast.makeText(EditarPerfilActivity.this, "Sua foto foi atualizada com sucesso!", Toast.LENGTH_SHORT).show();
    }

    public void inicializarComponentes(){
        imageViewFotoPerfil = findViewById(R.id.imageViewFotoEditarPerfil);
        textViewEditarFoto = findViewById(R.id.textViewEditarFoto);
        editTextAlterarNome = findViewById(R.id.editTextAlterarNome);
        editTextAlteraEmail = findViewById(R.id.editTextAlterarEmail);
        buttonAlterarPerfil = findViewById(R.id.buttonAlterarPerfil);
        editTextAlteraEmail.setFocusable(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResult : grantResults){
            if (permissaoResult == PackageManager.PERMISSION_DENIED){
                alertPermissao();
            }
        }
    }

    public void alertPermissao(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Permissões Negadas!");
        alert.setMessage("Para utilizar o app é necessário aceitar as permissões!");
        alert.setCancelable(false);
        alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
