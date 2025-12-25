package com.thiagoazv.domburguer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.thiagoazv.domburguer.adapter.InsumoAdapter;
import com.thiagoazv.domburguer.data.AppDatabase;
import com.thiagoazv.domburguer.model.Insumo;

import java.util.List;

public class EstoqueActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppDatabase db;
    private InsumoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estoque);

        db = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recyclerEstoque);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAdicionar);
        fab.setOnClickListener(v -> abrirFormulario(null));

        carregarDados();
    }

    private void carregarDados() {
        List<Insumo> lista = db.burgerDao().getAllInsumos();
        adapter = new InsumoAdapter(lista, new InsumoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Insumo insumo) {
                abrirFormulario(insumo);
            }
            @Override
            public void onItemLongClick(Insumo insumo) {
                confirmarExclusao(insumo);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void abrirFormulario(Insumo insumoParaEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_insumo, null);
        builder.setView(view);

        // --- Mapeando os campos ---
        TextView tvTitulo = view.findViewById(R.id.tvTituloDialog);
        TextInputEditText edNome = view.findViewById(R.id.edNomeInsumo);
        Spinner spinner = view.findViewById(R.id.spinnerTipoEntrada);
        TextInputEditText edEstoqueMin = view.findViewById(R.id.edEstoqueMin);

        // Layouts variáveis
        LinearLayout layoutPacote = view.findViewById(R.id.layoutModoPacote);
        LinearLayout layoutUnidade = view.findViewById(R.id.layoutModoUnidade);

        // Campos Pacote
        TextInputEditText edPrecoPacote = view.findViewById(R.id.edPrecoPacote);
        TextInputEditText edQtdNoPacote = view.findViewById(R.id.edQtdNoPacote);
        TextInputEditText edQtdPacotesEstoque = view.findViewById(R.id.edQtdPacotesEstoque);

        // Campos Unidade Simples
        TextInputEditText edQtdUnidade = view.findViewById(R.id.edQtdUnidade);
        TextInputEditText edCustoUnidade = view.findViewById(R.id.edCustoUnidade);

        // --- Configura o Dropdown ---
        String[] opcoes = {"Por Embalagem (Caixa, Fardo)", "Por Unidade Avulsa"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcoes);
        spinner.setAdapter(adapterSpinner);

        // Lógica de mostrar/esconder campos
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Pacote
                    layoutPacote.setVisibility(View.VISIBLE);
                    layoutUnidade.setVisibility(View.GONE);
                } else { // Unidade
                    layoutPacote.setVisibility(View.GONE);
                    layoutUnidade.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // --- Se for Edição ---
        if (insumoParaEditar != null) {
            tvTitulo.setText("Editar " + insumoParaEditar.nome);
            edNome.setText(insumoParaEditar.nome);
            edEstoqueMin.setText(String.valueOf(insumoParaEditar.estoqueMinimo));

            // Na edição, vamos mostrar no modo simples (Unidade) para facilitar,
            // já que o banco salva em unidades.
            spinner.setSelection(1);
            edQtdUnidade.setText(String.valueOf(insumoParaEditar.quantidadeAtual));
            edCustoUnidade.setText(String.valueOf(insumoParaEditar.custoUnitario));
        }

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String nome = edNome.getText().toString();
            String minStr = edEstoqueMin.getText().toString();
            double estoqueMin = minStr.isEmpty() ? 0 : Double.parseDouble(minStr);

            double custoUnitarioFinal = 0;
            double quantidadeTotalFinal = 0;

            if (nome.isEmpty()) {
                Toast.makeText(this, "Coloque um nome!", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- CÁLCULO INTELIGENTE ---
            if (spinner.getSelectedItemPosition() == 0) {
                // MODO PACOTE
                String precoPacoteStr = edPrecoPacote.getText().toString();
                String qtdNoPacoteStr = edQtdNoPacote.getText().toString();
                String qtdPacotesEstoqueStr = edQtdPacotesEstoque.getText().toString();

                if (precoPacoteStr.isEmpty() || qtdNoPacoteStr.isEmpty() || qtdPacotesEstoqueStr.isEmpty()) {
                    Toast.makeText(this, "Preencha os dados do pacote!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double precoPacote = Double.parseDouble(precoPacoteStr);
                double qtdNoPacote = Double.parseDouble(qtdNoPacoteStr);
                double pacotesEstoque = Double.parseDouble(qtdPacotesEstoqueStr);

                // Mágica 1: Preço por Unidade = Preço Pacote / Qtd no Pacote
                custoUnitarioFinal = precoPacote / qtdNoPacote;

                // Mágica 2: Total Estoque = Pacotes que tenho * Qtd que vem dentro
                quantidadeTotalFinal = pacotesEstoque * qtdNoPacote;

            } else {
                // MODO UNIDADE (Simples)
                String qtdStr = edQtdUnidade.getText().toString();
                String custoStr = edCustoUnidade.getText().toString();

                if (qtdStr.isEmpty()) {
                    Toast.makeText(this, "Diga a quantidade!", Toast.LENGTH_SHORT).show();
                    return;
                }
                quantidadeTotalFinal = Double.parseDouble(qtdStr);
                custoUnitarioFinal = custoStr.isEmpty() ? 0 : Double.parseDouble(custoStr);
            }

            // --- SALVAR NO BANCO ---
            if (insumoParaEditar == null) {
                Insumo novo = new Insumo(nome, "Un", quantidadeTotalFinal, estoqueMin, custoUnitarioFinal);
                db.burgerDao().insertInsumo(novo);
                Toast.makeText(this, "Calculado e Salvo!", Toast.LENGTH_SHORT).show();
            } else {
                insumoParaEditar.nome = nome;
                insumoParaEditar.quantidadeAtual = quantidadeTotalFinal;
                insumoParaEditar.estoqueMinimo = estoqueMin;
                insumoParaEditar.custoUnitario = custoUnitarioFinal;
                db.burgerDao().updateInsumo(insumoParaEditar);
                Toast.makeText(this, "Atualizado!", Toast.LENGTH_SHORT).show();
            }
            carregarDados();
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void confirmarExclusao(Insumo insumo) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Apagar " + insumo.nome + "?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    db.burgerDao().deleteInsumo(insumo);
                    carregarDados();
                    Toast.makeText(this, "Apagado!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Não", null)
                .show();
    }
}