package com.thiagoazv.domburguer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thiagoazv.domburguer.adapter.ProdutoAdapter;
import com.thiagoazv.domburguer.data.AppDatabase;
import com.thiagoazv.domburguer.model.Produto;
import java.util.List;

public class ProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private ProdutoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        db = AppDatabase.getInstance(this);
        recyclerView = findViewById(R.id.recyclerProdutos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAddProduto);

        // AÇÃO: Clicou no +, vai para a tela de Novo Produto
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(ProdutosActivity.this, NovoProdutoActivity.class);
            startActivity(intent);
        });
    }

    // A MÁGICA: onResume garante que a lista atualize assim que você voltar da tela de cadastro
    @Override
    protected void onResume() {
        super.onResume();
        carregarProdutos();
    }

    private void carregarProdutos() {
        // Buscando dados em thread separada para não travar a tela
        new Thread(() -> {
            List<Produto> lista = db.burgerDao().getAllProdutos();
            runOnUiThread(() -> {
                adapter = new ProdutoAdapter(lista, new ProdutoAdapter.OnProdutoClickListener() {
                    @Override
                    public void onProdutoClick(Produto produto) {
                        // EDIÇÃO: Passamos o ID para a outra tela saber qual carregar
                        Intent intent = new Intent(ProdutosActivity.this, NovoProdutoActivity.class);
                        intent.putExtra("produto_id", produto.id);
                        startActivity(intent);
                    }

                    @Override
                    public void onProdutoLongClick(Produto produto) {
                        confirmarExclusao(produto);
                    }
                });
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }

    private void confirmarExclusao(Produto produto) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Apagar " + produto.nome + "?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    new Thread(() -> {
                        db.burgerDao().deleteProduto(produto);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Apagado!", Toast.LENGTH_SHORT).show();
                            carregarProdutos(); // Atualiza a lista
                        });
                    }).start();
                })
                .setNegativeButton("Não", null)
                .show();
    }
}