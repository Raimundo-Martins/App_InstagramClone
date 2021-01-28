package com.raimundo.instagramclone.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String idUsuario;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String fotoUsuario;
    private int postagens = 0;
    private int seguidores = 0;
    private int seguindo = 0;

    public Usuario() {
    }

    public void salvarUsuario(){
        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference referenceUsuario = referenceFirebase.child("usuarios").child(getIdUsuario());
        referenceUsuario.setValue(this);
    }

    public void atualizarUsuario(){
        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();

        Map objeto = new HashMap();
        objeto.put("/usuarios/" + getIdUsuario() + "/nomeUsuario/", getNomeUsuario());
        objeto.put("/usuarios/" + getIdUsuario() + "/fotoUsuario/", getFotoUsuario());

        referenceFirebase.updateChildren(objeto);
    }


    public void atualizarPostagemUsuario(){
        DatabaseReference referenceFirebase = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference referenceUsuario = referenceFirebase.child("usuarios").child(getIdUsuario());

        HashMap<String, Object> postagensUsuario = new HashMap<>();
        postagensUsuario.put("postagens", getPostagens());
        referenceUsuario.updateChildren(postagensUsuario);
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("idUsuario", getIdUsuario());
        usuarioMap.put("nomeUsuario", getNomeUsuario());
        usuarioMap.put("emailUsuario", getEmailUsuario());
        usuarioMap.put("fotoUsuario", getFotoUsuario());
        usuarioMap.put("postagens", getPostagens());
        usuarioMap.put("seguidores", getSeguidores());
        usuarioMap.put("seguindo", getSeguindo());
        return usuarioMap;
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
        this.nomeUsuario = nomeUsuario.toUpperCase();
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    @Exclude
    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }
}
