package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "nekretnina.db";

    private static final int DATABASE_VERSION = 1;

    private Dao<Nekretnina, Integer> nekretninaDao = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, Nekretnina.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {


        try {
            TableUtils.dropTable(connectionSource, Nekretnina.class, true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<Nekretnina, Integer> getNekretninaDao() throws SQLException {
        if (nekretninaDao == null) {
            nekretninaDao = getDao(Nekretnina.class);
        }

        return nekretninaDao;
    }

    @Override
    public void close() {
        nekretninaDao = null;

        super.close();
    }
}
