package com.raimundo.instagramclone.helper;

import android.net.Uri;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.raimundo.instagramclone.model.Usuario;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();
        return usuario.getCurrentUser();
    }

    public static void atualizarNomeUsuario(String nome){
        try {
            FirebaseUser userLogado = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(nome)
                    .build();
            userLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar perfil!");
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void atualizarFotoUsuario(Uri uri){
        try {
            FirebaseUser userLogado = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(uri)
                    .build();
            userLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar a foto!");
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(firebaseUser.getUid());
        usuario.setNomeUsuario(firebaseUser.getDisplayName());
        usuario.setEmailUsuario(firebaseUser.getEmail());

        if (firebaseUser.getPhotoUrl() == null){
            usuario.setFotoUsuario("");
        } else {
            usuario.setFotoUsuario(firebaseUser.getPhotoUrl().toString());
        }
        return usuario;
    }

    public static String getIdUsuarioLogado(){
        return getUsuarioAtual().getUid();
    }
}
