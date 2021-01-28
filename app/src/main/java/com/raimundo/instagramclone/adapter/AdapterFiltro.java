package com.raimundo.instagramclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raimundo.instagramclone.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFiltro extends RecyclerView.Adapter<AdapterFiltro.MyViewHolder> {

    private List<ThumbnailItem> listFiltros;
    private Context context;

    public AdapterFiltro(List<ThumbnailItem> listFiltros, Context context) {
        this.listFiltros = listFiltros;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filtros, parent, false);

        return new AdapterFiltro.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ThumbnailItem item = listFiltros.get(position);
        holder.textViewNomeFiltro.setText(item.filterName);
        holder.imageViewFotoFiltro.setImageBitmap(item.image);
    }

    @Override
    public int getItemCount() {
        return listFiltros.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewFotoFiltro;
        TextView textViewNomeFiltro;

        public MyViewHolder(View view){
            super(view);

            textViewNomeFiltro = view.findViewById(R.id.textViewNomeFiltro);
            imageViewFotoFiltro = view.findViewById(R.id.imageViewFotoFiltro);
        }
    }
}
