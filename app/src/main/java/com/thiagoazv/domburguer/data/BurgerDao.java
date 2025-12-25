package com.thiagoazv.domburguer.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.thiagoazv.domburguer.model.Insumo;
import com.thiagoazv.domburguer.model.Produto;
import com.thiagoazv.domburguer.model.ProdutoIngrediente;
import com.thiagoazv.domburguer.model.Venda; // Import novo

import java.util.List;

@Dao
public interface BurgerDao {

    // --- INSUMOS (ESTOQUE) ---
    @Insert
    void insertInsumo(Insumo insumo);

    @Update
    void updateInsumo(Insumo insumo);

    @Delete
    void deleteInsumo(Insumo insumo);

    @Query("SELECT * FROM insumos")
    List<Insumo> getAllInsumos();

    // --- PRODUTOS (LANCHES) ---
    @Insert
    void insertProduto(Produto produto);

    @Update
    void updateProduto(Produto produto);

    @Delete
    void deleteProduto(Produto produto);

    @Query("SELECT * FROM produtos")
    List<Produto> getAllProdutos();

    // --- RECEITA (FICHA TÉCNICA) ---
    @Insert
    void insertIngredienteReceita(ProdutoIngrediente item);

    @Delete
    void deleteIngredienteReceita(ProdutoIngrediente item);

    @Query("SELECT * FROM produto_ingrediente WHERE produtoId = :prodId")
    List<ProdutoIngrediente> getIngredientesDoProduto(int prodId);

    // --- VENDAS (HISTÓRICO) - NOVO ---
    @Insert
    void insertVenda(Venda venda);

    @Query("SELECT * FROM vendas ORDER BY id DESC")
    List<Venda> getAllVendas();

    @Delete
    void deleteVenda(Venda venda);
}