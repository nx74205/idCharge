package de.nx74205.idcharge.database;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {

    @SuppressLint("StaticFieldLeak")
    private static DbHelper sInstance;

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME ="Chargedata";
    private Context context;

    public static synchronized DbHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DbHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        readAndExecuteSQLScript(db, context, "createDb.sql");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);

        try {
            for (int i = oldVersion; i < newVersion; ++i) {
                String migrationName = String.format(Locale.GERMAN, "from_%02d_to_%02d.sql", i, (i + 1));
                Log.d(TAG, "Looking for migration file: " + migrationName);
                readAndExecuteSQLScript(db, context, migrationName);
            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception running upgrade script:", exception);
        }
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "SQL script file name is empty");
            return;
        }

        Log.d(TAG, "Script found. Executing...");
        AssetManager assetManager = ctx.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e(TAG, "IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException:", e);
                }
            }
        }

    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.endsWith(";")) {
                db.execSQL(statement.toString());
                statement = new StringBuilder();
            }
        }
    }
}
