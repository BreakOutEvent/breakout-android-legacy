package org.break_out.breakout.sync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.break_out.breakout.sync.model.Posting;
import org.w3c.dom.Comment;

import java.sql.SQLException;

/**
 * Created by Tino on 14.12.2015.
 */
public class PostingsDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "postings";
    private static final int DATABASE_VERSION = 1;

    private Dao<Posting, Integer> _postingsDao = null;
    private RuntimeExceptionDao<Posting, Integer> _postingsRuntimeDao = null;

    public PostingsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Posting.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Posting.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<Posting, Integer> getPostingsDao() throws SQLException {
        if (_postingsDao == null) {
            _postingsDao = getDao(Posting.class);
        }
        return _postingsDao;
    }

    @Override
    public void close() {
        super.close();
        _postingsDao = null;
    }

}