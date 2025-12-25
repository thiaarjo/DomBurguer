package com.thiagoazv.domburguer.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.thiagoazv.domburguer.model.Insumo;
import com.thiagoazv.domburguer.model.Produto;
import com.thiagoazv.domburguer.model.ProdutoIngrediente;
import com.thiagoazv.domburguer.model.Venda; // Import novo

// ATENÇÃO: Version mudou para 3
@Database(entities = {Insumo.class, Produto.class, ProdutoIngrediente.class, Venda.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BurgerDao burgerDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "domburguer-db")
                            .allowMainThreadQueries()
                            // Essa linha permite apagar o banco antigo e criar o novo sem travar o app
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}