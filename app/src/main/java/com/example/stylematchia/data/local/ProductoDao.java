package com.example.stylematchia.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.stylematchia.model.Producto;

import java.util.List;

@Dao
public interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    List<Producto> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Producto producto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Producto> productos);

    @Query("DELETE FROM productos WHERE id = :id")
    void deleteById(String id);

    @Query("DELETE FROM productos")
    void deleteAll();

    @Transaction
    default void replaceAll(List<Producto> productos) {
        deleteAll();
        insertAll(productos);
    }
}
