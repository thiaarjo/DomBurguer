package com.thiagoazv.domburguer.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "produtos")
public class Produto {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;
    public double precoVenda;
    public String descricao;
    public String categoria;

    // --- CAMPO QUE ESTAVA FALTANDO ---
    public String caminhoFoto;

    // Construtor vazio (Necessário para o Room)
    public Produto() {
    }

    // Construtor útil
    public Produto(String nome, double precoVenda, String descricao, String categoria, String caminhoFoto) {
        this.nome = nome;
        this.precoVenda = precoVenda;
        this.descricao = descricao;
        this.categoria = categoria;
        this.caminhoFoto = caminhoFoto;
    }
}