package com.thiagoazv.domburguer.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.thiagoazv.domburguer.model.Insumo;
import com.thiagoazv.domburguer.model.Produto;
import com.thiagoazv.domburguer.model.ProdutoIngrediente;
import com.thiagoazv.domburguer.model.Venda;

// *** IMPORTANTE: MUDE O NÚMERO DA VERSÃO AQUI (Ex: para 7) ***
@Database(entities = {Produto.class, Insumo.class, Venda.class, ProdutoIngrediente.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BurgerDao burgerDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "dom_burguer_db")
                            // ESTA LINHA EVITA O CRASH SE A VERSÃO MUDAR
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}