package com.thiagoazv.domburguer.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// Aqui a gente amarra o Produto ao Insumo
@Entity(tableName = "produto_ingrediente",
        foreignKeys = {
                @ForeignKey(entity = Produto.class, parentColumns = "id", childColumns = "produtoId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Insumo.class, parentColumns = "id", childColumns = "insumoId", onDelete = ForeignKey.CASCADE)
        })
public class ProdutoIngrediente {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int produtoId; // Qual lanche é? (Ex: X-Bacon)
    public int insumoId;  // Qual ingrediente? (Ex: Bacon)
    public double quantidade; // Quanto gasta? (Ex: 0.2 pacotes ou 2 unidades)

    public ProdutoIngrediente(int produtoId, int insumoId, double quantidade) {
        this.produtoId = produtoId;
        this.insumoId = insumoId;
        this.quantidade = quantidade;
    }
}