package com.raimundo.instagramclone.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.activity.PerfilAmigoActivity;
import com.raimundo.instagramclone.adapter.AdapterPesquisa;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.RecyclerItemClickListener;
import com.raimundo.instagramclone.helper.UsuarioFirebase;
import com.raimundo.instagramclone.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment {

    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisa;

    private List<Usuario> usuarioList;
    private DatabaseReference referenceUsuario;
    private AdapterPesquisa adapterPesquisa;

    private String idUsuarioLogado;

    public PesquisaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerViewPesquisa = view.findViewById(R.id.recyclerViewPesquisa);

        usuarioList = new ArrayList<>();
        referenceUsuario = ConfiguracaoFirebase.getDatabaseReference().child("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdUsuarioLogado();

        recyclerViewPesquisa.setHasFixedSize(true);
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterPesquisa = new AdapterPesquisa(usuarioList, getActivity());

        recyclerViewPesquisa.setAdapter(adapterPesquisa);

        recyclerViewPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                recyclerViewPesquisa, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Usuario usuarioSelecionado = usuarioList.get(position);
                Intent intent = new Intent(getActivity(), PerfilAmigoActivity.class);
                intent.putExtra("usuarioSelecionado", usuarioSelecionado);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pequisarUsuarios(textoDigitado);
                return true;
            }
        });

        return view;
    }

    private void pequisarUsuarios(String texto){

        usuarioList.clear();

        if (texto.length() > 0){
            Query query = referenceUsuario.orderByChild("nomeUsuario").startAt(texto).endAt(true + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    usuarioList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Usuario usuario = snapshot.getValue(Usuario.class);
                        if (idUsuarioLogado.equals(usuario.getIdUsuario()))
                            continue;
                        usuarioList.add(usuario);
                    }
                    adapterPesquisa.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
