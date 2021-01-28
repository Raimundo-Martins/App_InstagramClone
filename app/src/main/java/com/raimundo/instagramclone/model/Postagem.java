package com.raimundo.instagramclone.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Postagem implements Serializable {

    private String id;
    private String idUsuario;
    private String descricao;
    private String foto;

    public Postagem() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference referencePostagem = databaseReference.child("postagens");
        String idPostagem = referencePostagem.push().getKey();
        setId(idPostagem);

    }

    public Postagem(String id, String idUsuario, String descricao, String foto) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.descricao = descricao;
        this.foto = foto;
    }

    public Boolean salvarPostagem(DataSnapshot snapshotSeguidores){
        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();

        String compinacaoId = "/" + getIdUsuario() + "/" + getId();
        objeto.put("/postagens" + compinacaoId, this);

        for (DataSnapshot seguidores : snapshotSeguidores.getChildren()){
            String idSeguidor = seguidores.getKey();

            HashMap<String, Object> dadosSeguidores = new HashMap<>();
            dadosSeguidores.put("fotoPostagem", getFoto());
            dadosSeguidores.put("descricao", getDescricao());
            dadosSeguidores.put("id", getId());

            dadosSeguidores.put("nomeUsuario", usuarioLogado.getNomeUsuario());
            dadosSeguidores.put("fotoUsuario", usuarioLogado.getFotoUsuario());

            String idsAtulizacao = "/" + idSeguidor + "/" + getId();
            objeto.put("/feed" + idsAtulizacao, dadosSeguidores);
        }

        databaseReference.updateChildren(objeto);
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
