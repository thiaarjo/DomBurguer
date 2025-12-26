package com.thiagoazv.domburguer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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
        // Infla o layout novo que acabamos de criar (item_produto)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto, parent, false);
        return new ProdutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        Produto produto = listaProdutos.get(position);

        holder.tvNome.setText(produto.nome);
        holder.tvPreco.setText(String.format("R$ %.2f", produto.precoVenda));

        // Exibe descrição ou texto padrão
        if (produto.descricao != null && !produto.descricao.isEmpty()) {
            holder.tvDescricao.setText(produto.descricao);
        } else {
            holder.tvDescricao.setText(produto.categoria);
        }

        // --- AQUI A MÁGICA DA FOTO ---
        if (produto.caminhoFoto != null && !produto.caminhoFoto.isEmpty()) {
            // Se tem foto salva, carrega com Glide
            Glide.with(holder.itemView.getContext())
                    .load(produto.caminhoFoto)
                    .transform(new CenterCrop()) // Garante que preenche o quadrado sem distorcer
                    .into(holder.imgFoto);
        } else {
            // Se não tem foto, coloca o hambúrguer padrão
            holder.imgFoto.setImageResource(R.drawable.ic_menu_burger); // Use seu ícone genérico aqui
        }

        // Configura os cliques
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
        TextView tvNome, tvPreco, tvDescricao;
        ImageView imgFoto;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeProduto);
            tvPreco = itemView.findViewById(R.id.tvPrecoProduto);
            tvDescricao = itemView.findViewById(R.id.tvDescricaoProduto);
            imgFoto = itemView.findViewById(R.id.imgItemProduto); // O ID da imagem no XML
        }
    }
}