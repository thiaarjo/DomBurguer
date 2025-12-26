package com.thiagoazv.domburguer.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore; // <--- IMPORTANTE: Adicione este import
import androidx.room.PrimaryKey;

@Entity(tableName = "produto_ingrediente",
        foreignKeys = {
                @ForeignKey(entity = Produto.class, parentColumns = "id", childColumns = "produtoId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Insumo.class, parentColumns = "id", childColumns = "insumoId", onDelete = ForeignKey.CASCADE)
        })
public class ProdutoIngrediente {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int produtoId;
    public int insumoId;
    public double quantidade;

    // --- CONSTRUTOR PRINCIPAL (O Room vai usar este) ---
    public ProdutoIngrediente(int produtoId, int insumoId, double quantidade) {
        this.produtoId = produtoId;
        this.insumoId = insumoId;
        this.quantidade = quantidade;
    }

    // --- CONSTRUTOR AUXILIAR (O Room vai ignorar este) ---
    // Usamos esse apenas na tela de cadastro para facilitar (padrão quantidade = 1.0)
    @Ignore
    public ProdutoIngrediente(int produtoId, int insumoId) {
        this.produtoId = produtoId;
        this.insumoId = insumoId;
        this.quantidade = 1.0;
    }
}