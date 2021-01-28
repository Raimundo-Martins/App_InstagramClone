package com.raimundo.instagramclone.activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmailLogin, campoSenhaLogin;
    private Button buttonLogin;
    private ProgressBar progressBarLogin;

    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();
        iniciarComponetes();

        progressBarLogin.setVisibility(View.GONE);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoEmail = campoEmailLogin.getText().toString();
                String textoSenha = campoSenhaLogin.getText().toString();

                if (!textoEmail.isEmpty()) {
                    if (!textoSenha.isEmpty()) {
                        usuario = new Usuario();
                        usuario.setEmailUsuario(textoEmail);
                        usuario.setSenhaUsuario(textoSenha);
                        validarLogin(usuario);

                    } else {
                        Toast.makeText(LoginActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        if (autenticacao.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void validarLogin(Usuario usuario){
        progressBarLogin.setVisibility(View.VISIBLE);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmailUsuario(), usuario.getSenhaUsuario())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBarLogin.setVisibility(View.VISIBLE);

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Erro ao fazer login! ", Toast.LENGTH_SHORT).show();
                    progressBarLogin.setVisibility(View.GONE);
                }
            }
        });
    }

    public void abrirCadastro(View view){
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    public void iniciarComponetes() {
        campoEmailLogin = findViewById(R.id.editTextLoginEmail);
        campoSenhaLogin = findViewById(R.id.editTextLoginSenha);
        buttonLogin = findViewById(R.id.buttonLogar);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        campoEmailLogin.requestFocus();
    }
}
