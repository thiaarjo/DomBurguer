package com.thiagoazv.domburguer;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thiagoazv.domburguer.adapter.VendaAdapter;
import com.thiagoazv.domburguer.data.AppDatabase;
import com.thiagoazv.domburguer.model.Venda;
import com.thiagoazv.domburguer.model.Insumo;
import com.thiagoazv.domburguer.model.ProdutoIngrediente;

import java.util.ArrayList;
import java.util.List;

public class RelatorioActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecyclerView recyclerView;
    private TextView tvTotal, tvQtd;
    private VendaAdapter adapter;
    private List<Venda> listaVendas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        db = AppDatabase.getInstance(this);
        recyclerView = findViewById(R.id.recyclerRelatorio);
        tvTotal = findViewById(R.id.tvTotalFaturamento);
        tvQtd = findViewById(R.id.tvQtdVendas);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carregarDados();
    }

    private void carregarDados() {
        listaVendas = db.burgerDao().getAllVendas();
        atualizarTotalizadores();

        // Configura o clique longo para EXCLUIR
        adapter = new VendaAdapter(listaVendas);

        // Aqui usamos um truque simples: O Adapter nativo não tem clique fácil,
        // mas vamos adicionar o evento direto na view dentro do Adapter ou usar uma classe anônima.
        // Para simplificar, vou deixar o Adapter básico e tratar cliques de outra forma se precisar,
        // Mas o jeito certo e rápido é passar o listener pro Adapter.

        // Vamos recriar o Adapter rapidinho com suporte a clique?
        // Melhor não complicar. Vamos fazer um "ItemTouch" (deslizar) ou clique simples.
        // Vou manter simples:

        recyclerView.setAdapter(adapter);

        // TRUQUE: Como nosso Adapter é simples, vamos adicionar o clique lá dentro?
        // Não, vamos fazer o jeito certo aqui embaixo:
    }

    // ATENÇÃO: Para o clique funcionar, precisamos atualizar o VendaAdapter.java
    // Vou te mandar o código do Adapter com suporte a clique logo abaixo.

    public void confirmarExclusao(Venda venda) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar Venda?")
                .setMessage("Isso vai estornar o valor e devolver os itens ao estoque.")
                .setPositiveButton("Sim, cancelar", (dialog, which) -> {
                    estornarEstoque(venda);
                    db.burgerDao().deleteVenda(venda);
                    carregarDados(); // Atualiza a tela
                    Toast.makeText(this, "Venda cancelada!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void estornarEstoque(Venda venda) {
        // Tenta descobrir quais ingredientes foram gastos pra devolver
        // Nota: Isso é complexo porque o estoque já mudou, mas vamos tentar devolver baseados no nome do produto
        // Se o produto foi apagado ou mudou de nome, não devolve nada.

        // Essa parte é um "bônus". Se quiser simplificar, pode só apagar a venda.
        // Mas o correto é devolver a carne pro estoque:

        com.thiagoazv.domburguer.model.Produto produtoOriginal = null;
        for(com.thiagoazv.domburguer.model.Produto p : db.burgerDao().getAllProdutos()){
            if(p.nome.equals(venda.nomeProduto)){
                produtoOriginal = p;
                break;
            }
        }

        if(produtoOriginal != null){
            List<ProdutoIngrediente> receita = db.burgerDao().getIngredientesDoProduto(produtoOriginal.id);
            for(ProdutoIngrediente item : receita){
                List<Insumo> insumos = db.burgerDao().getAllInsumos();
                for(Insumo i : insumos){
                    if(i.id == item.insumoId){
                        i.quantidadeAtual += (item.quantidade * venda.quantidade);
                        db.burgerDao().updateInsumo(i);
                    }
                }
            }
        }
    }

    private void atualizarTotalizadores() {
        double total = 0;
        for (Venda v : listaVendas) {
            total += v.valorTotal;
        }
        tvTotal.setText(String.format("R$ %.2f", total));
        tvQtd.setText(listaVendas.size() + " vendas registradas");
    }
}