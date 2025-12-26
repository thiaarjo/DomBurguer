package com.thiagoazv.domburguer;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.thiagoazv.domburguer.data.AppDatabase;
import com.thiagoazv.domburguer.model.Insumo;
import com.thiagoazv.domburguer.model.Produto;
import com.thiagoazv.domburguer.model.ProdutoIngrediente;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class NovoProdutoActivity extends AppCompatActivity {

    private EditText etNome, etPreco, etDescricao;
    private Spinner spinnerCategoria;
    private ImageView imgFotoProduto;
    private TextView tvTextoFoto;
    private View containerFoto;

    // INGREDIENTES
    private Button btnSelecionarIngredientes;
    private TextView tvIngredientesSelecionados;
    private List<Insumo> listaTodosInsumos = new ArrayList<>();
    private boolean[] insumosMarcados;
    private List<Integer> listaIdsSelecionados = new ArrayList<>();

    private int produtoId = -1;
    private AppDatabase db;
    private Uri imagemUriTemporaria = null;
    private Uri uriFotoCamera = null;
    private String caminhoFotoAtual = null;

    // LAUNCHERS DE IMAGEM
    private final ActivityResultLauncher<String> selecionarGaleriaLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) { imagemUriTemporaria = uri; exibirImagemComGlide(uri); }
            });

    private final ActivityResultLauncher<Uri> tirarFotoLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), sucesso -> {
                if (sucesso) { imagemUriTemporaria = uriFotoCamera; exibirImagemComGlide(uriFotoCamera); }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto);

        db = AppDatabase.getInstance(this);

        etNome = findViewById(R.id.etNomeProduto);
        etPreco = findViewById(R.id.etPrecoProduto);
        etDescricao = findViewById(R.id.etDescricaoProduto);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        imgFotoProduto = findViewById(R.id.imgFotoProduto);
        containerFoto = findViewById(R.id.containerFoto);
        tvTextoFoto = findViewById(R.id.tvTextoFoto);
        btnSelecionarIngredientes = findViewById(R.id.btnSelecionarIngredientes);
        tvIngredientesSelecionados = findViewById(R.id.tvIngredientesSelecionados);

        configurarSpinner();
        carregarInsumosDoEstoque();

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());
        findViewById(R.id.btnSalvarProduto).setOnClickListener(v -> salvarProduto());
        containerFoto.setOnClickListener(v -> mostrarOpcoesFoto());
        btnSelecionarIngredientes.setOnClickListener(v -> mostrarDialogIngredientes());

        if (getIntent().hasExtra("produto_id")) {
            produtoId = getIntent().getIntExtra("produto_id", -1);
            carregarDadosEdicao(produtoId);
        }
    }

    // --- LÓGICA DE INGREDIENTES ---
    private void carregarInsumosDoEstoque() {
        Executors.newSingleThreadExecutor().execute(() -> {
            listaTodosInsumos = db.burgerDao().getAllInsumos();
            insumosMarcados = new boolean[listaTodosInsumos.size()];
        });
    }

    private void mostrarDialogIngredientes() {
        if (listaTodosInsumos.isEmpty()) {
            Toast.makeText(this, "Estoque vazio! Cadastre insumos primeiro.", Toast.LENGTH_LONG).show();
            return;
        }
        String[] nomes = new String[listaTodosInsumos.size()];
        for (int i = 0; i < listaTodosInsumos.size(); i++) nomes[i] = listaTodosInsumos.get(i).nome;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingredientes");
        builder.setMultiChoiceItems(nomes, insumosMarcados, (dialog, which, isChecked) -> insumosMarcados[which] = isChecked);
        builder.setPositiveButton("OK", (dialog, which) -> atualizarTextoIngredientes());
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void atualizarTextoIngredientes() {
        StringBuilder resumo = new StringBuilder();
        listaIdsSelecionados.clear();
        for (int i = 0; i < listaTodosInsumos.size(); i++) {
            if (insumosMarcados[i]) {
                resumo.append("• ").append(listaTodosInsumos.get(i).nome).append("\n");
                listaIdsSelecionados.add(listaTodosInsumos.get(i).id);
            }
        }
        if (listaIdsSelecionados.isEmpty()) {
            tvIngredientesSelecionados.setText("Nenhum ingrediente vinculado.");
            btnSelecionarIngredientes.setText("Selecionar do Estoque...");
        } else {
            tvIngredientesSelecionados.setText(resumo.toString());
            btnSelecionarIngredientes.setText(listaIdsSelecionados.size() + " Ingredientes Selecionados");
        }
    }

    // --- SALVAR PRODUTO ---
    private void salvarProduto() {
        String nome = etNome.getText().toString();
        String precoStr = etPreco.getText().toString();

        if (nome.isEmpty() || precoStr.isEmpty()) {
            Toast.makeText(this, "Preencha nome e preço!", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            String caminhoFinal = caminhoFotoAtual;
            if (imagemUriTemporaria != null) caminhoFinal = salvarImagemInternamente(imagemUriTemporaria);

            Produto p = new Produto();
            p.id = (produtoId != -1) ? produtoId : 0;
            p.nome = nome;
            p.descricao = etDescricao.getText().toString();
            p.categoria = spinnerCategoria.getSelectedItem().toString();
            p.caminhoFoto = caminhoFinal;
            try { p.precoVenda = Double.parseDouble(precoStr.replace(",", ".")); } catch (Exception e) { p.precoVenda = 0.0; }

            long idSalvo;

            if (produtoId != -1) {
                p.id = produtoId;
                db.burgerDao().updateProduto(p);
                idSalvo = produtoId;
                db.burgerDao().limparIngredientesDoProduto(produtoId); // Limpa antigos
            } else {
                idSalvo = db.burgerDao().insertProduto(p); // Pega ID novo
            }

            // Salva os novos vínculos
            for (int insumoId : listaIdsSelecionados) {
                // Aqui usamos o construtor simplificado que criamos no modelo
                ProdutoIngrediente vinculo = new ProdutoIngrediente((int) idSalvo, insumoId);
                db.burgerDao().insertProdutoIngrediente(vinculo);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Produto Salvo!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void carregarDadosEdicao(int id) {
        TextView titulo = findViewById(R.id.tvTituloHeader);
        if (titulo != null) titulo.setText("Editar Produto");

        Executors.newSingleThreadExecutor().execute(() -> {
            Produto produto = db.burgerDao().getProdutoById(id);
            List<Integer> idsVinculados = db.burgerDao().getIdsIngredientesDoProduto(id);

            if (produto != null) {
                while (listaTodosInsumos.isEmpty()) { try { Thread.sleep(50); } catch (Exception e) {} }

                runOnUiThread(() -> {
                    etNome.setText(produto.nome);
                    etPreco.setText(String.format("%.2f", produto.precoVenda).replace(",", "."));
                    etDescricao.setText(produto.descricao);
                    if (produto.caminhoFoto != null) {
                        caminhoFotoAtual = produto.caminhoFoto;
                        exibirImagemComGlide(caminhoFotoAtual);
                    }
                    if (produto.categoria != null) {
                        ArrayAdapter adapter = (ArrayAdapter) spinnerCategoria.getAdapter();
                        int pos = adapter.getPosition(produto.categoria);
                        if (pos >= 0) spinnerCategoria.setSelection(pos);
                    }

                    listaIdsSelecionados.clear();
                    for (int i = 0; i < listaTodosInsumos.size(); i++) {
                        if (idsVinculados.contains(listaTodosInsumos.get(i).id)) {
                            insumosMarcados[i] = true;
                            listaIdsSelecionados.add(listaTodosInsumos.get(i).id);
                        }
                    }
                    atualizarTextoIngredientes();
                });
            }
        });
    }

    // --- MÉTODOS DE CÂMERA E GALERIA ---
    private void mostrarOpcoesFoto() {
        String[] opcoes = {"Tirar Foto (Câmera)", "Escolher da Galeria"};
        new AlertDialog.Builder(this)
                .setTitle("Adicionar Imagem")
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) verificarPermissaoCamera();
                    else selecionarGaleriaLauncher.launch("image/*");
                })
                .show();
    }

    private void verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void abrirCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Lanche_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        uriFotoCamera = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        tirarFotoLauncher.launch(uriFotoCamera);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirCamera();
        }
    }

    private void exibirImagemComGlide(Object modelo) {
        if (tvTextoFoto != null) tvTextoFoto.setVisibility(View.GONE);
        RequestOptions options = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(30));
        Glide.with(this).load(modelo).apply(options).into(imgFotoProduto);
        containerFoto.setBackground(null);
    }

    private String salvarImagemInternamente(Uri uri) {
        try {
            String nome = "img_" + System.currentTimeMillis() + ".jpg";
            File destino = new File(getExternalFilesDir("imagens_produtos"), nome);
            InputStream in = getContentResolver().openInputStream(uri);
            FileOutputStream out = new FileOutputStream(destino);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            out.close();
            in.close();
            return destino.getAbsolutePath();
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    private void configurarSpinner() {
        String[] itens = {"Lanches", "Bebidas", "Porções", "Sobremesas"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itens);
        spinnerCategoria.setAdapter(adapter);
    }
}