package com.raimundo.instagramclone.activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.raimundo.instagramclone.R;
import com.raimundo.instagramclone.fragment.FeedFragment;
import com.raimundo.instagramclone.fragment.PerfilFragment;
import com.raimundo.instagramclone.fragment.PesquisaFragment;
import com.raimundo.instagramclone.fragment.PostagemFragment;
import com.raimundo.instagramclone.helper.ConfiguracaoFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Instagram Clone");
        setSupportActionBar(toolbar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        configurarBottomNavigation();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager,  new FeedFragment()).commit();
    }

    private void configurarBottomNavigation(){
        BottomNavigationViewEx navigationViewEx = findViewById(R.id.bottomNavigartion);
        /*navigationViewEx.enableAnimation(true);
        navigationViewEx.enableItemShiftingMode(false);
        navigationViewEx.enableShiftingMode(false);
        navigationViewEx.setTextVisibility(false);*/

        habilitarNavegacao(navigationViewEx);

        Menu menu = navigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    private void habilitarNavegacao(BottomNavigationViewEx navigationViewEx){
        navigationViewEx.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()){
                    case R.id.ic_home:
                        fragmentTransaction.replace(R.id.viewPager,  new FeedFragment()).commit();
                        return true;
                    case R.id.ic_pesquisa:
                        fragmentTransaction.replace(R.id.viewPager,  new PesquisaFragment()).commit();
                        return true;
                    case R.id.ic_postagem:
                        fragmentTransaction.replace(R.id.viewPager,  new PostagemFragment()).commit();
                        return true;
                    case R.id.ic_perfil:
                        fragmentTransaction.replace(R.id.viewPager,  new PerfilFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
