package com.thiagoazv.domburguer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.thiagoazv.domburguer.data.AppDatabase;
import com.thiagoazv.domburguer.model.Insumo;
import com.thiagoazv.domburguer.model.Produto;
import com.thiagoazv.domburguer.model.ProdutoIngrediente;
import com.thiagoazv.domburguer.model.Venda;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VendasActivity extends AppCompatActivity {

    private AppDatabase db;
    private Spinner spinnerLanche;
    private EditText edQtd;
    private TextView tvTotal;
    private Button btnFinalizar;

    private List<Produto> listaProdutos;
    private Produto produtoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendas);

        db = AppDatabase.getInstance(this);

        spinnerLanche = findViewById(R.id.spinnerLancheVenda);
        edQtd = findViewById(R.id.edQtdVenda);
        tvTotal = findViewById(R.id.tvTotalVenda);
        btnFinalizar = findViewById(R.id.btnFinalizarVenda);

        carregarProdutos();

        // Atualiza o preço total quando muda a quantidade
        edQtd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calcularTotal();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnFinalizar.setOnClickListener(v -> realizarVenda());
    }

    private void carregarProdutos() {
        listaProdutos = db.burgerDao().getAllProdutos();

        List<String> nomes = new ArrayList<>();
        for (Produto p : listaProdutos) {
            nomes.add(p.nome);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nomes);
        spinnerLanche.setAdapter(adapter);

        spinnerLanche.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                produtoSelecionado = listaProdutos.get(position);
                calcularTotal();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void calcularTotal() {
        if (produtoSelecionado == null) return;

        String qtdStr = edQtd.getText().toString();
        int qtd = qtdStr.isEmpty() ? 0 : Integer.parseInt(qtdStr);

        double total = produtoSelecionado.precoVenda * qtd;
        tvTotal.setText(String.format("R$ %.2f", total));
    }

    private void realizarVenda() {
        if (produtoSelecionado == null) return;

        String qtdStr = edQtd.getText().toString();
        if (qtdStr.isEmpty()) {
            Toast.makeText(this, "Digite a quantidade", Toast.LENGTH_SHORT).show();
            return;
        }
        int qtdVenda = Integer.parseInt(qtdStr);
        double valorTotalVenda = produtoSelecionado.precoVenda * qtdVenda;

        // --- 1. BAIXA DE ESTOQUE ---
        // Pega os ingredientes que esse lanche usa
        List<ProdutoIngrediente> receita = db.burgerDao().getIngredientesDoProduto(produtoSelecionado.id);

        if (!receita.isEmpty()) {
            for (ProdutoIngrediente itemReceita : receita) {
                // Procura o ingrediente no estoque para descontar
                List<Insumo> todosInsumos = db.burgerDao().getAllInsumos();
                for (Insumo estoque : todosInsumos) {
                    if (estoque.id == itemReceita.insumoId) {
                        // Cálculo: Qtd na Receita * Qtd Vendida
                        double gasto = itemReceita.quantidade * qtdVenda;
                        estoque.quantidadeAtual = estoque.quantidadeAtual - gasto;

                        // Atualiza no banco
                        db.burgerDao().updateInsumo(estoque);
                    }
                }
            }
        }

        // --- 2. SALVAR NO HISTÓRICO ---
        // Formata a data e hora atual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dataAtual = sdf.format(new Date());

        Venda novaVenda = new Venda(
                produtoSelecionado.nome,
                qtdVenda,
                valorTotalVenda,
                dataAtual
        );

        db.burgerDao().insertVenda(novaVenda);

        // --- 3. AVISO E LIMPEZA ---
        Toast.makeText(this, "Venda Realizada!", Toast.LENGTH_SHORT).show();

        new AlertDialog.Builder(this)
                .setTitle("Sucesso!")
                .setMessage("Venda de R$ " + String.format("%.2f", valorTotalVenda) + " registrada.\nEstoque atualizado.")
                .setPositiveButton("Nova Venda", (dialog, which) -> {
                    edQtd.setText("1");
                })
                .setNegativeButton("Sair", (dialog, which) -> finish())
                .show();
    }
}