package com.thiagoazv.domburguer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.thiagoazv.domburguer.R;
import com.thiagoazv.domburguer.RelatorioActivity; // Importe sua activity
import com.thiagoazv.domburguer.model.Venda;
import java.util.List;

public class VendaAdapter extends RecyclerView.Adapter<VendaAdapter.VendaViewHolder> {

    private List<Venda> listaVendas;

    public VendaAdapter(List<Venda> listaVendas) {
        this.listaVendas = listaVendas;
    }

    @NonNull
    @Override
    public VendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venda, parent, false);
        return new VendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendaViewHolder holder, int position) {
        Venda venda = listaVendas.get(position);

        holder.txtNome.setText(venda.nomeProduto + " (" + venda.quantidade + "x)");
        holder.txtValor.setText(String.format("R$ %.2f", venda.valorTotal));

        if(venda.dataHora != null && venda.dataHora.length() > 5) {
            holder.txtHora.setText(venda.dataHora.substring(venda.dataHora.length() - 5));
        } else {
            holder.txtHora.setText("--:--");
        }

        // --- CLIQUE LONGO PARA EXCLUIR ---
        holder.itemView.setOnLongClickListener(v -> {
            if (v.getContext() instanceof RelatorioActivity) {
                ((RelatorioActivity) v.getContext()).confirmarExclusao(venda);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaVendas.size();
    }

    static class VendaViewHolder extends RecyclerView.ViewHolder {
        TextView txtHora, txtNome, txtValor;

        public VendaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHora = itemView.findViewById(R.id.txtHoraVenda);
            txtNome = itemView.findViewById(R.id.txtNomeVenda);
            txtValor = itemView.findViewById(R.id.txtValorVenda);
        }
    }
}