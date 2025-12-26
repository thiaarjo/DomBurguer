package com.thiagoazv.domburguer.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.thiagoazv.domburguer.model.Insumo;
import com.thiagoazv.domburguer.model.Produto;
import com.thiagoazv.domburguer.model.ProdutoIngrediente;
import com.thiagoazv.domburguer.model.Venda;

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
    long insertProduto(Produto produto); // Retorna o ID

    @Update
    void updateProduto(Produto produto);

    @Delete
    void deleteProduto(Produto produto);

    @Query("SELECT * FROM produtos")
    List<Produto> getAllProdutos();

    @Query("SELECT * FROM produtos WHERE id = :id")
    Produto getProdutoById(int id);

    // --- FICHA TÉCNICA / INGREDIENTES ---

    // 1. Inserir vínculo
    @Insert
    void insertProdutoIngrediente(ProdutoIngrediente crossRef);

    // 2. Limpar (Usado na edição do NovoProdutoActivity)
    @Query("DELETE FROM produto_ingrediente WHERE produtoId = :prodId")
    void limparIngredientesDoProduto(int prodId);

    // 3. Pegar IDs (Usado nos Checkboxes do NovoProdutoActivity)
    @Query("SELECT insumoId FROM produto_ingrediente WHERE produtoId = :prodId")
    List<Integer> getIdsIngredientesDoProduto(int prodId);

    // 4. Pegar OBJETOS COMPLETOS (Usado na ReceitaActivity) <--- O QUE FALTAVA
    @Query("SELECT * FROM produto_ingrediente WHERE produtoId = :prodId")
    List<ProdutoIngrediente> getIngredientesDoProduto(int prodId);

    // 5. Deletar um item específico (Usado na ReceitaActivity)
    @Delete
    void deleteProdutoIngrediente(ProdutoIngrediente item);

    // --- VENDAS (HISTÓRICO) ---
    @Insert
    void insertVenda(Venda venda);

    @Update
    void updateVenda(Venda venda);

    @Delete
    void deleteVenda(Venda venda);

    @Query("SELECT * FROM vendas ORDER BY id DESC")
    List<Venda> getAllVendas();
}