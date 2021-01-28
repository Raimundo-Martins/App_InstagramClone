package com.raimundo.instagramclone.model;

import com.google.firebase.database.DatabaseReference;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;

import java.util.HashMap;

public class PostagemCurtida {

    public Feed feed;
    public Usuario usuario;
    public int totalCurtidas = 0;

    public PostagemCurtida() {
    }

    public void salvar(){
        HashMap<String, Object> hashMapUsuario = new HashMap<>();
        hashMapUsuario.put("nomeUsuario", usuario.getNomeUsuario());
        hashMapUsuario.put("fotoUsuario", usuario.getFotoUsuario());

        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference referencePostagemCurtidas = referenceFirebase.child("postagens-curtidas")
                .child(feed.getId()).child(usuario.getIdUsuario());
        referencePostagemCurtidas.setValue(hashMapUsuario);

        atualizarTotalCurtidas(1);
    }

    public void atualizarTotalCurtidas(int total){
        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference referencePostagemCurtidas = referenceFirebase
                .child("postagens-curtidas")
                .child(feed.getId())
                .child("totalCurtidas");
        setTotalCurtidas(getTotalCurtidas() + total);
        referencePostagemCurtidas.setValue(getTotalCurtidas());
    }

    public void removerCurtidas(){
        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference referencePostagemCurtidas = referenceFirebase
                .child("postagens-curtidas")
                .child(feed.getId())
                .child(usuario.getIdUsuario());
        referencePostagemCurtidas.removeValue();

        atualizarTotalCurtidas(-1);
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getTotalCurtidas() {
        return totalCurtidas;
    }

    public void setTotalCurtidas(int totalCurtidas) {
        this.totalCurtidas = totalCurtidas;
    }
}
