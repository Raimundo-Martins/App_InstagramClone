package com.raimundo.instagramclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.adapter.AdapterFeed;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;
import com.raimundo.instagramclone.helper.UsuarioFirebase;
import com.raimundo.instagramclone.model.Feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView recyclerViewFeed;
    private AdapterFeed adapterFeed;
    private List<Feed> feedList = new ArrayList<>();
    private ValueEventListener valueEventListenerFeed;
    private DatabaseReference referenceFeed;
    private String idUsuarioLogado;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        idUsuarioLogado = UsuarioFirebase.getIdUsuarioLogado();
        referenceFeed = ConfiguracaoFirebase.getDatabaseReference().child("feed")
                .child(idUsuarioLogado);

        recyclerViewFeed = view.findViewById(R.id.recyclerViewFeed);

        adapterFeed = new AdapterFeed(feedList, getActivity());
        recyclerViewFeed.setHasFixedSize(true);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewFeed.setAdapter(adapterFeed);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listarFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        referenceFeed.removeEventListener(valueEventListenerFeed);
    }

    private void listarFeed(){

        valueEventListenerFeed = referenceFeed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    feedList.add(dataSnapshot.getValue(Feed.class));
                }
                Collections.reverse(feedList);
                adapterFeed.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
