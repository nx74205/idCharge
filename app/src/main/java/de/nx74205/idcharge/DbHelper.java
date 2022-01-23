package de.nx74205.idcharge;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.nx74205.idcharge.model.LocalChargeData;

public class DbHelper extends SQLiteOpenHelper {

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final int DATABASE_VERSION = 2;
    private final Context context;

    public DbHelper( Context context) {
        super(context, "Chargedata", null, DATABASE_VERSION);
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
                String migrationName = String.format("from_%02d_to_%02d.sql", i, (i + 1));
                Log.d(TAG, "Looking for migration file: " + migrationName);
                readAndExecuteSQLScript(db, context, migrationName);
            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception running upgrade script:", exception);
        }
    }

    public long insertCharges(LocalChargeData chargeData) {
        SQLiteDatabase db = this.getReadableDatabase();


        long result = db.insert("local_charges", null, setContentValues(chargeData));

        return result;
    }

    public Boolean updateCharges (LocalChargeData chargeData) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] arguments = {chargeData.getChargeId().toString()};
        Cursor cursor = db.rawQuery("select * from local_charges where charge_id = ?", arguments);

        if (cursor.getCount() > 0) {
            long result = db.update("local_charges", setContentValues(chargeData), "charge_id=?", arguments);

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Boolean deleteCharges(LocalChargeData chargeData) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] arguments = {chargeData.getChargeId().toString()};

        Cursor cursor = db.rawQuery("select * from local_charges where charge_id = ?", arguments);

        if (cursor.getCount() > 0) {
            long result = db.delete("local_charges", "charge_id=?", arguments);

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @SuppressLint("Range")
    public ArrayList<LocalChargeData> getCharges(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<LocalChargeData> chargeDataList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from local_charges", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            LocalChargeData chargeRecord = new LocalChargeData();

            chargeRecord.setChargeId(cursor.getInt(cursor.getColumnIndex("charge_id")));
            chargeRecord.setTimeStamp(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("timestamp")), DATE_FORMAT));
            chargeRecord.setMileage(cursor.getLong(cursor.getColumnIndex("mileage")));
            chargeRecord.setDistance(cursor.getLong(cursor.getColumnIndex("distance")));
            chargeRecord.setChargedKwPaid(Double.parseDouble(cursor.getString(cursor.getColumnIndex("charge_kw_paid"))));
            chargeRecord.setPrice(Double.parseDouble(cursor.getString(cursor.getColumnIndex("price"))));
            chargeRecord.setTargetSoc(cursor.getInt(cursor.getColumnIndex("target_soc")));
            chargeRecord.setChargeTyp(cursor.getString(cursor.getColumnIndex("charge_typ")));
            chargeRecord.setBcConsumption(Double.parseDouble(cursor.getString(cursor.getColumnIndex("bc_consumption"))));
            chargeDataList.add(chargeRecord);

            cursor.moveToNext();
        }
        return chargeDataList;
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

    private ContentValues setContentValues(LocalChargeData chargeData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", chargeData.getTimeStamp().format(DATE_FORMAT));
        contentValues.put("mileage", chargeData.getMileage());
        contentValues.put("distance", chargeData.getDistance());
        contentValues.put("charge_kw_paid", chargeData.getChargedKwPaid().toString());
        contentValues.put("price", chargeData.getPrice().toString());
        contentValues.put("target_soc", chargeData.getTargetSoc());
        contentValues.put("charge_typ", chargeData.getChargeTyp());
        contentValues.put("bc_consumption", chargeData.getBcConsumption().toString());

        return contentValues;

    }

}
