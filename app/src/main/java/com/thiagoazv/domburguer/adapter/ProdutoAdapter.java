package com.thiagoazv.domburguer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.thiagoazv.domburguer.R;
import com.thiagoazv.domburguer.model.Produto;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder> {

    private List<Produto> listaProdutos;
    private OnProdutoClickListener listener;

    public interface OnProdutoClickListener {
        void onProdutoClick(Produto produto);
        void onProdutoLongClick(Produto produto);
    }

    public ProdutoAdapter(List<Produto> listaProdutos, OnProdutoClickListener listener) {
        this.listaProdutos = listaProdutos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // AQUI MUDOU: Usa o layout bonito item_produto
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto, parent, false);
        return new ProdutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        Produto produto = listaProdutos.get(position);

        holder.tvNome.setText(produto.nome);
        holder.tvPreco.setText(String.format("R$ %.2f", produto.precoVenda));

        holder.itemView.setOnClickListener(v -> listener.onProdutoClick(produto));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onProdutoLongClick(produto);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }

    static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvPreco;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Mapeando os IDs do novo layout XML
            tvNome = itemView.findViewById(R.id.tvNomeProdutoItem);
            tvPreco = itemView.findViewById(R.id.tvPrecoProdutoItem);
        }
    }
}