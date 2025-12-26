package com.thiagoazv.domburguer.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "insumos")
public class Insumo {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;
    public String unidadeMedida; // UN, KG, LT, PCT

    // CAMPOS NOVOS (Se faltar um, o app fecha!)
    public double precoUnitario;
    public double quantidadeAtual;
    public double estoqueMinimo;

    public Insumo() {
    }
}