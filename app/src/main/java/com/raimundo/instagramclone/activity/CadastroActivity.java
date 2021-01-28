package com.raimundo.instagramclone.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.UsuarioFirebase;
import com.raimundo.instagramclone.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button buttonCadastrar;
    private ProgressBar progressBarCadastro;

    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        iniciarComponetes();

        progressBarCadastro.setVisibility(View.GONE);
        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if (!textoNome.isEmpty()) {
                    if (!textoEmail.isEmpty()) {
                        if (!textoSenha.isEmpty()) {
                            usuario = new Usuario();
                            usuario.setNomeUsuario(textoNome);
                            usuario.setEmailUsuario(textoEmail);
                            usuario.setSenhaUsuario(textoSenha);
                            cadastrar(usuario);
                        } else {
                            Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadastroActivity.this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void iniciarComponetes() {
        campoNome = findViewById(R.id.editTextCadastroNome);
        campoEmail = findViewById(R.id.editTextCadastroEmail);
        campoSenha = findViewById(R.id.editTextCadastroSenha);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        progressBarCadastro = findViewById(R.id.progressBarCadastro);

        campoNome.requestFocus();
    }

    public void cadastrar(final Usuario usuario){
        progressBarCadastro.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmailUsuario(), usuario.getSenhaUsuario())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    try {
                        progressBarCadastro.setVisibility(View.GONE);

                        String idUsuario = task.getResult().getUser().getUid();
                        usuario.setIdUsuario(idUsuario);
                        usuario.setFotoUsuario("");
                        usuario.salvarUsuario();

                        UsuarioFirebase.atualizarNomeUsuario(usuario.getNomeUsuario());

                        Toast.makeText(CadastroActivity.this, "Cadastro com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    progressBarCadastro.setVisibility(View.GONE);

                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "Por favor, digite um e-mail válido!";
                    } catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "Esta conta já foi cadastrada!";
                    } catch (Exception e) {
                        erroExcecao = "Ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
