package com.thiagoazv.domburguer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
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
        fab.setOnClickListener(v -> mostrarDialogProduto(null));

        carregarProdutos();
    }

    private void carregarProdutos() {
        List<Produto> lista = db.burgerDao().getAllProdutos();

        adapter = new ProdutoAdapter(lista, new ProdutoAdapter.OnProdutoClickListener() {
            @Override
            public void onProdutoClick(Produto produto) {
                mostrarDialogProduto(produto); // Editar
            }

            @Override
            public void onProdutoLongClick(Produto produto) {
                confirmarExclusao(produto); // Excluir
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialogProduto(Produto produtoEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // --- AQUI A MÁGICA: Inflamos o layout bonito que criamos (dialog_produto.xml) ---
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_produto, null);
        builder.setView(view);

        // Mapeamos os campos do XML novo (agora usando TextInputEditText)
        TextView tvTitulo = view.findViewById(R.id.tvTituloDialogProduto);
        TextInputEditText inputNome = view.findViewById(R.id.edNomeProduto);
        TextInputEditText inputPreco = view.findViewById(R.id.edPrecoProduto);

        // Se for EDIÇÃO, preenchemos os dados
        if (produtoEditar != null) {
            tvTitulo.setText("Editar Lanche");
            inputNome.setText(produtoEditar.nome);
            inputPreco.setText(String.valueOf(produtoEditar.precoVenda));

            // --- BOTÃO DA RECEITA (FICHA TÉCNICA) ---
            // Adicionamos ele via código para ele aparecer logo abaixo dos campos
            Button btnReceita = new Button(this);
            btnReceita.setText("📋 Editar Ficha Técnica");
            btnReceita.setBackgroundColor(Color.parseColor("#FFB300")); // Amarelo da Marca
            btnReceita.setTextColor(Color.BLACK); // Texto Preto
            btnReceita.setTextSize(14);

            // Margem para separar dos campos de cima
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 40, 0, 0); // 40px de margem no topo
            btnReceita.setLayoutParams(params);

            // Ação do botão: Abrir a tela de Receita
            btnReceita.setOnClickListener(v -> {
                Intent intent = new Intent(ProdutosActivity.this, ReceitaActivity.class);
                intent.putExtra("produto_id", produtoEditar.id);
                intent.putExtra("produto_nome", produtoEditar.nome);
                startActivity(intent);
                // Opcional: fechar o dialog ao ir para a receita
                // Mas geralmente deixamos aberto para salvar preço se quiser.
            });

            // Adiciona o botão na tela
            LinearLayout container = (LinearLayout) view;
            container.addView(btnReceita);
        }

        // Botão SALVAR do Dialog
        builder.setPositiveButton("SALVAR", (dialog, which) -> {
            String nome = inputNome.getText().toString();
            String precoStr = inputPreco.getText().toString();

            if (!nome.isEmpty() && !precoStr.isEmpty()) {
                double preco = Double.parseDouble(precoStr);

                if (produtoEditar == null) {
                    // CRIAR NOVO
                    Produto novo = new Produto(nome, preco);
                    db.burgerDao().insertProduto(novo);
                    Toast.makeText(this, "Lanche Criado!", Toast.LENGTH_SHORT).show();
                } else {
                    // ATUALIZAR EXISTENTE
                    produtoEditar.nome = nome;
                    produtoEditar.precoVenda = preco;
                    db.burgerDao().updateProduto(produtoEditar);
                    Toast.makeText(this, "Lanche Atualizado!", Toast.LENGTH_SHORT).show();
                }
                carregarProdutos();
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("CANCELAR", null);

        // --- ESTILIZANDO OS BOTÕES DO DIALOG ---
        AlertDialog dialog = builder.create();
        dialog.show();

        // Pinta os botões "SALVAR" e "CANCELAR" de Preto (Black Strong) para combinar
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#121212"));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#121212"));
    }

    private void confirmarExclusao(Produto produto) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Lanche")
                .setMessage("Deseja apagar o " + produto.nome + "?")
                .setPositiveButton("Sim, apagar", (dialog, which) -> {
                    db.burgerDao().deleteProduto(produto);
                    carregarProdutos();
                    Toast.makeText(this, "Lanche removido!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}