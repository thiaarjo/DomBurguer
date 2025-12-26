package com.thiagoazv.domburguer.adapter;

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

    private List<Insumo> lista;

    public InsumoAdapter(List<Insumo> lista) {
        this.lista = lista;
    }

    public void setLista(List<Insumo> novaLista) {
        this.lista = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InsumoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Agora usamos nosso layout personalizado bonitão
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estoque, parent, false);
        return new InsumoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsumoViewHolder holder, int position) {
        Insumo item = lista.get(position);

        holder.tvNome.setText(item.nome);

        // Formatação: Quantidade Atual + Unidade | Mínimo
        String detalhes = String.format("%.0f %s  |  Min: %.0f", item.quantidadeAtual, item.unidadeMedida, item.estoqueMinimo);
        holder.tvDetalhe.setText(detalhes);

        holder.tvPreco.setText(String.format("R$ %.2f", item.precoUnitario));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class InsumoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvDetalhe, tvPreco;

        public InsumoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vincula com os IDs do item_estoque.xml
            tvNome = itemView.findViewById(R.id.tvNomeInsumo);
            tvDetalhe = itemView.findViewById(R.id.tvDetalheInsumo);
            tvPreco = itemView.findViewById(R.id.tvPrecoInsumo);
        }
    }
}