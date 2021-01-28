package com.raimundo.instagramclone.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static Boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode){
        if (Build.VERSION.SDK_INT >= 23){
            List<String> listaPermissoes = new ArrayList<>();
            for (String permissao : permissoes){
                Boolean permitido = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!permitido) listaPermissoes.add(permissao);
            }
            if (listaPermissoes.isEmpty()) return true;
            String[] arrayPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(arrayPermissoes);

            ActivityCompat.requestPermissions(activity, arrayPermissoes, requestCode);
        }
        return true;
    }
}
