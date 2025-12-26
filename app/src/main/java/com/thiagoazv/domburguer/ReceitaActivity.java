package com.thiagoazv.domburguer;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thiagoazv.domburguer.data.AppDatabase;
import com.thiagoazv.domburguer.model.Insumo;
import com.thiagoazv.domburguer.model.ProdutoIngrediente;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ReceitaActivity extends AppCompatActivity {

    private AppDatabase db;
    private int produtoId;
    private String nomeProduto;

    private Spinner spinnerIngredientes;
    private EditText edQtd;
    private ListView listaReceita;
    private List<Insumo> listaInsumosDisponiveis = new ArrayList<>(); // Inicializado para evitar erro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);

        db = AppDatabase.getInstance(this);

        // Pega os dados que vieram da outra tela
        produtoId = getIntent().getIntExtra("produto_id", -1);
        nomeProduto = getIntent().getStringExtra("produto_nome");

        if (produtoId == -1) {
            Toast.makeText(this, "Erro ao carregar produto", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitulo = findViewById(R.id.tvNomeLancheReceita);
        tvTitulo.setText("Ficha Técnica: " + nomeProduto);

        spinnerIngredientes = findViewById(R.id.spinnerIngredientes);
        edQtd = findViewById(R.id.edQtdReceita);
        listaReceita = findViewById(R.id.listaReceita);

        Button btnAdd = findViewById(R.id.btnAddIngrediente);
        Button btnConcluir = findViewById(R.id.btnConcluirReceita);

        carregarSpinnerInsumos();
        atualizarListaReceita();

        btnAdd.setOnClickListener(v -> adicionarIngrediente());
        btnConcluir.setOnClickListener(v -> finish());
    }

    private void carregarSpinnerInsumos() {
        Executors.newSingleThreadExecutor().execute(() -> {
            listaInsumosDisponiveis = db.burgerDao().getAllInsumos();

            runOnUiThread(() -> {
                List<String> nomesInsumos = new ArrayList<>();
                for (Insumo i : listaInsumosDisponiveis) {
                    nomesInsumos.add(i.nome + " (" + i.unidadeMedida + ")");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ReceitaActivity.this, android.R.layout.simple_spinner_dropdown_item, nomesInsumos);
                spinnerIngredientes.setAdapter(adapter);
            });
        });
    }

    private void adicionarIngrediente() {
        if (listaInsumosDisponiveis.isEmpty()) {
            Toast.makeText(this, "Cadastre itens no Estoque primeiro!", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicaoSelecionada = spinnerIngredientes.getSelectedItemPosition();
        if (posicaoSelecionada < 0) return;

        Insumo insumoSelecionado = listaInsumosDisponiveis.get(posicaoSelecionada);

        String qtdStr = edQtd.getText().toString();
        if (qtdStr.isEmpty()) {
            Toast.makeText(this, "Digite a quantidade!", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantidade = Double.parseDouble(qtdStr);

        // Usa Thread para salvar no banco
        Executors.newSingleThreadExecutor().execute(() -> {
            // Usando o construtor completo (3 argumentos)
            ProdutoIngrediente novoItem = new ProdutoIngrediente(produtoId, insumoSelecionado.id, quantidade);

            // MÉTODO CORRIGIDO: insertProdutoIngrediente
            db.burgerDao().insertProdutoIngrediente(novoItem);

            runOnUiThread(() -> {
                edQtd.setText("");
                atualizarListaReceita();
                Toast.makeText(ReceitaActivity.this, "Ingrediente adicionado!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void atualizarListaReceita() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // MÉTODO DO DAO PARA PEGAR A LISTA (Certifique-se que existe no BurgerDao)
            List<ProdutoIngrediente> receita = db.burgerDao().getIngredientesDoProduto(produtoId);
            List<String> textoParaLista = new ArrayList<>();

            // Se a lista de insumos ainda não carregou, recarrega
            if (listaInsumosDisponiveis.isEmpty()) {
                listaInsumosDisponiveis = db.burgerDao().getAllInsumos();
            }

            for (ProdutoIngrediente itemReceita : receita) {
                String nomeInsumo = "Item removido do estoque";
                String unidade = "";

                for (Insumo insumo : listaInsumosDisponiveis) {
                    if (insumo.id == itemReceita.insumoId) {
                        nomeInsumo = insumo.nome;
                        unidade = insumo.unidadeMedida;
                        break;
                    }
                }
                textoParaLista.add(nomeInsumo + ": " + itemReceita.quantidade + " " + unidade);
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapterLista = new ArrayAdapter<>(ReceitaActivity.this, android.R.layout.simple_list_item_1, textoParaLista);
                listaReceita.setAdapter(adapterLista);

                listaReceita.setOnItemLongClickListener((parent, view, position, id) -> {
                    ProdutoIngrediente itemParaRemover = receita.get(position);
                    removerItem(itemParaRemover);
                    return true;
                });
            });
        });
    }

    private void removerItem(ProdutoIngrediente item) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // MÉTODO CORRIGIDO: deleteProdutoIngrediente
            db.burgerDao().deleteProdutoIngrediente(item);

            runOnUiThread(() -> {
                atualizarListaReceita();
                Toast.makeText(ReceitaActivity.this, "Removido da receita", Toast.LENGTH_SHORT).show();
            });
        });
    }
}