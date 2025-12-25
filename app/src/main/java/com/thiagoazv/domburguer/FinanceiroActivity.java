package com.thiagoazv.domburguer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class FinanceiroActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView tvFaturamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financeiro);

        barChart = findViewById(R.id.barChartVendas);
        tvFaturamento = findViewById(R.id.tvFaturamentoTotal);

        configurarGraficoEstilizado();
        configurarNavBar();

        // Simulação de valor total
        tvFaturamento.setText("R$ 4.520,00");
    }

    // Função para editar/apagar usando o tema nativo para evitar o erro de 'symbol'
    public void gerenciarVenda(int vendaId) {
        String[] opcoes = {"Editar Venda", "Apagar Venda", "Cancelar"};

        // Usamos o tema nativo 'Theme_DeviceDefault_Dialog_Alert' que é compatível com Dark Mode
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setTitle("Gerenciar Venda #" + vendaId);
        builder.setItems(opcoes, (dialog, which) -> {
            if (which == 0) {
                Intent i = new Intent(this, VendasActivity.class);
                i.putExtra("ID_VENDA", vendaId);
                startActivity(i);
            } else if (which == 1) {
                confirmarExclusao(vendaId);
            }
        });
        builder.show();
    }

    private void confirmarExclusao(int id) {
        new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle("Confirmar")
                .setMessage("Deseja realmente excluir esta venda?")
                .setPositiveButton("Sim, excluir", (d, w) -> {
                    Toast.makeText(this, "Venda removida!", Toast.LENGTH_SHORT).show();
                    // Aqui você chamaria seu banco de dados para deletar
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void configurarGraficoEstilizado() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 1200f));
        entries.add(new BarEntry(1, 1800f));
        entries.add(new BarEntry(2, 900f));
        entries.add(new BarEntry(3, 2100f));

        BarDataSet dataSet = new BarDataSet(entries, "Faturamento");
        dataSet.setColor(Color.parseColor("#FFC107"));
        dataSet.setValueTextColor(Color.WHITE);

        barChart.setData(new BarData(dataSet));
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void configurarNavBar() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_financeiro);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            return true;
        });
    }
}