package org.break_out.breakout.sync.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.break_out.breakout.sync.model.Posting;

import java.sql.SQLException;

/**
 * Created by Tino on 14.12.2015.
 */
public class PostingsDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "postings";
    private static final int DATABASE_VERSION = 2;

    private Dao<Posting, Void> _dao = null;

    public PostingsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Posting.class);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Posting.class, true);
            onCreate(db, connectionSource);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<Posting, Void> getDao() throws SQLException {
        if(_dao == null) {
            _dao = getDao(Posting.class);
        }
        return _dao;
    }

    @Override
    public void close() {
        super.close();
        _dao = null;
    }

}