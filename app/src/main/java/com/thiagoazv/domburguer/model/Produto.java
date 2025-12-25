package com.thiagoazv.domburguer.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "produtos")
public class Produto {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;        // Ex: "X-Salada"
    public double precoVenda;  // Ex: 25.00
    public String imagemPath;  // Caminho da foto no celular (opcional por enquanto)

    public Produto() {}

    public Produto(String nome, double precoVenda) {
        this.nome = nome;
        this.precoVenda = precoVenda;
    }
}