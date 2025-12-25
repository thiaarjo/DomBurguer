package com.thiagoazv.domburguer.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vendas")
public class Venda {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nomeProduto;
    public int quantidade;
    public double valorTotal;
    public String dataHora; // Vamos salvar como texto "24/12/2023 - 19:30"

    public Venda(String nomeProduto, int quantidade, double valorTotal, String dataHora) {
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.dataHora = dataHora;
    }
}