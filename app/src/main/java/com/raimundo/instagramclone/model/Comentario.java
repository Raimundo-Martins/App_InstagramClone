package com.raimundo.instagramclone.model;

import com.google.firebase.database.DatabaseReference;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;

public class Comentario {

    private String idComentario;
    private String idPostagem;
    private String idUsuario;
    private String nomeUsuario;
    private String fotoUsuario;
    private String comentario;

    public Comentario() {
    }

    public boolean salvarComentario(){
        DatabaseReference referenceComentario = ConfiguracaoFirebase.getDatabaseReference()
                .child("comentarios")
                .child(getIdPostagem());
        String chaveComentario = referenceComentario.push().getKey();
        setIdComentario(chaveComentario);
        referenceComentario.child(getIdComentario()).setValue(this);

        return true;
    }

    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
