package com.thiagoazv.domburguer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.thiagoazv.domburguer.adapter.InsumoAdapter;
import com.thiagoazv.domburguer.data.AppDatabase;
import com.thiagoazv.domburguer.model.Insumo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EstoqueActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecyclerView recyclerView;
    private InsumoAdapter adapter;
    private List<Insumo> listaInsumos = new ArrayList<>();
    private TextView tvTotalItens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estoque);

        db = AppDatabase.getInstance(this);

        tvTotalItens = findViewById(R.id.tvTotalItensEstoque);
        recyclerView = findViewById(R.id.recyclerEstoque);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // --- CORREÇÃO 1: Inicializar o Adapter (estava comentado) ---
        adapter = new InsumoAdapter(listaInsumos);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddInsumo);
        fab.setOnClickListener(v -> mostrarDialogEntradaEstoque());

        carregarEstoque();
    }

    private void carregarEstoque() {
        Executors.newSingleThreadExecutor().execute(() -> {
            listaInsumos = db.burgerDao().getAllInsumos();
            runOnUiThread(() -> {
                // --- CORREÇÃO 2: Atualizar a lista visual (estava comentado) ---
                if (adapter != null) {
                    adapter.setLista(listaInsumos);
                }

                if (tvTotalItens != null) {
                    tvTotalItens.setText(listaInsumos.size() + " itens cadastrados");
                }
            });
        });
    }

    private void mostrarDialogEntradaEstoque() {
        // --- CORREÇÃO 3: Construtor simplificado para evitar erro de tema ---
        BottomSheetDialog dialog = new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_entrada_estoque, null);
        dialog.setContentView(view);

        // 1. Vincular componentes
        TextInputEditText etNome = view.findViewById(R.id.etNomeItem);
        ChipGroup chipGroup = view.findViewById(R.id.chipGroupUnidade);
        TextInputEditText etMinimo = view.findViewById(R.id.etEstoqueMinimo);

        // Calculadora
        EditText etPrecoPacote = view.findViewById(R.id.etCalcPrecoPacote);
        EditText etQtdPacote = view.findViewById(R.id.etCalcQtdPacote);
        TextView tvResultado = view.findViewById(R.id.tvCustoUnitarioFinal);

        View btnSalvar = view.findViewById(R.id.btnSalvarEstoque);
        View btnCancelar = view.findViewById(R.id.btnCancelarEstoque);
        View btnFechar = view.findViewById(R.id.btnClose);

        // 2. Lógica da Calculadora
        TextWatcher calculadoraWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                calcularCustoAutomatico(etPrecoPacote, etQtdPacote, tvResultado);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        etPrecoPacote.addTextChangedListener(calculadoraWatcher);
        etQtdPacote.addTextChangedListener(calculadoraWatcher);

        // 3. Ações dos Botões
        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());
        if (btnCancelar != null) btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnSalvar.setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String precoStr = etPrecoPacote.getText().toString();
            String qtdPacoteStr = etQtdPacote.getText().toString();
            String minimoStr = etMinimo.getText().toString();

            String unidadeSelecionada = "UN";
            int chipId = chipGroup.getCheckedChipId();
            if (chipId != -1) {
                Chip chip = view.findViewById(chipId);
                if (chip != null) unidadeSelecionada = chip.getText().toString();
            }

            if (nome.isEmpty() || precoStr.isEmpty()) {
                Toast.makeText(this, "Preencha nome e preço!", Toast.LENGTH_SHORT).show();
                return;
            }

            double precoPacote = Double.parseDouble(precoStr.replace(",", "."));
            double qtdNoPacote = qtdPacoteStr.isEmpty() ? 1.0 : Double.parseDouble(qtdPacoteStr.replace(",", "."));
            double custoUnitario = precoPacote / qtdNoPacote;
            double estoqueMin = minimoStr.isEmpty() ? 0 : Double.parseDouble(minimoStr);

            salvarNoBanco(nome, unidadeSelecionada, custoUnitario, estoqueMin, dialog);
        });

        dialog.show();
    }

    private void calcularCustoAutomatico(EditText etPreco, EditText etQtd, TextView tvResult) {
        try {
            String pStr = etPreco.getText().toString().replace(",", ".");
            String qStr = etQtd.getText().toString().replace(",", ".");

            if (pStr.isEmpty()) {
                tvResult.setText("R$ 0,00");
                return;
            }

            double preco = Double.parseDouble(pStr);
            double qtd = qStr.isEmpty() ? 1.0 : Double.parseDouble(qStr);

            if (qtd <= 0) qtd = 1.0;

            double unitario = preco / qtd;
            tvResult.setText(String.format("R$ %.2f", unitario));

        } catch (NumberFormatException e) {
            tvResult.setText("R$ 0,00");
        }
    }

    private void salvarNoBanco(String nome, String unidade, double custo, double minimo, BottomSheetDialog dialog) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Certifique-se de que o Model Insumo.java já tem esses campos novos!
            Insumo novoInsumo = new Insumo();
            novoInsumo.nome = nome;
            novoInsumo.unidadeMedida = unidade;
            novoInsumo.precoUnitario = custo;
            novoInsumo.quantidadeAtual = 0;
            novoInsumo.estoqueMinimo = minimo;

            db.burgerDao().insertInsumo(novoInsumo);

            runOnUiThread(() -> {
                Toast.makeText(this, "Item Salvo!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                carregarEstoque();
            });
        });
    }
}