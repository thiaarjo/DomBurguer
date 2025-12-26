package com.thiagoazv.domburguer;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Navegação da Dashboard (Grade Principal) ---

        // 1. Nova Venda
        findViewById(R.id.cardVendas).setOnClickListener(v ->
                startActivity(new Intent(this, VendasActivity.class))
        );

        // 2. Estoque
        findViewById(R.id.cardEstoque).setOnClickListener(v ->
                startActivity(new Intent(this, EstoqueActivity.class))
        );

        // 3. Cardápio / Produtos
        findViewById(R.id.cardProdutos).setOnClickListener(v ->
                startActivity(new Intent(this, ProdutosActivity.class))
        );

        // 4. Financeiro
        findViewById(R.id.cardRelatorio).setOnClickListener(v ->
                startActivity(new Intent(this, FinanceiroActivity.class))
        );

        // --- Configuração da NavBar Inferior ---
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_financeiro) {
                startActivity(new Intent(this, FinanceiroActivity.class));
                return true;
            }
            return true;
        });
    }
}