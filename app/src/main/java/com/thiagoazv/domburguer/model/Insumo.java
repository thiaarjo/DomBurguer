package com.thiagoazv.domburguer.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "insumos")
public class Insumo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;
    public String unidadeMedida;
    public double quantidadeAtual;
    public double estoqueMinimo;
    public double custoUnitario;

    public Insumo() {}

    public Insumo(String nome, String unidadeMedida, double quantidadeAtual, double estoqueMinimo, double custoUnitario) {
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.quantidadeAtual = quantidadeAtual;
        this.estoqueMinimo = estoqueMinimo;
        this.custoUnitario = custoUnitario;
    }
}
