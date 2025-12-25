package com.thiagoazv.domburguer.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.thiagoazv.domburguer.R;
import com.thiagoazv.domburguer.model.Insumo;
import java.util.List;

public class InsumoAdapter extends RecyclerView.Adapter<InsumoAdapter.InsumoViewHolder> {

    private List<Insumo> listaInsumos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Insumo insumo);
        void onItemLongClick(Insumo insumo);
    }

    public InsumoAdapter(List<Insumo> listaInsumos, OnItemClickListener listener) {
        this.listaInsumos = listaInsumos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InsumoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_insumo, parent, false);
        return new InsumoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsumoViewHolder holder, int position) {
        Insumo insumo = listaInsumos.get(position);

        holder.tvNome.setText(insumo.nome);
        holder.tvQtd.setText(String.format("%.1f", insumo.quantidadeAtual)); // Formata bonito (10.0)
        holder.tvUnidade.setText(" " + insumo.unidadeMedida);
        holder.tvMin.setText(String.format("%.1f", insumo.estoqueMinimo));

        // --- LÓGICA DO ALERTA VERMELHO ---
        if (insumo.quantidadeAtual <= insumo.estoqueMinimo) {
            holder.tvQtd.setTextColor(Color.parseColor("#C62828")); // Vermelho
            holder.tvAlerta.setVisibility(View.VISIBLE);
        } else {
            holder.tvQtd.setTextColor(Color.parseColor("#181818")); // Preto
            holder.tvAlerta.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(insumo));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(insumo);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaInsumos.size();
    }

    static class InsumoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvQtd, tvUnidade, tvMin, tvAlerta;

        public InsumoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeInsumoItem);
            tvQtd = itemView.findViewById(R.id.tvQtdInsumoItem);
            tvUnidade = itemView.findViewById(R.id.tvUnidadeInsumoItem);
            tvMin = itemView.findViewById(R.id.tvMinInsumoItem);
            tvAlerta = itemView.findViewById(R.id.tvAlertaEstoque);
        }
    }
}