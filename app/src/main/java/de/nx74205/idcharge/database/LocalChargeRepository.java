package de.nx74205.idcharge.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.nx74205.idcharge.model.LocalChargeData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LocalChargeRepository {

    private final DbHelper dbHelper;
    private static final String TABLE_NAME = "local_charges";

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public LocalChargeRepository(Context context) {
        this.dbHelper = DbHelper.getInstance(context);

    }

    public long insert(LocalChargeData chargeData) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long result = db.insert(TABLE_NAME, null, setContentValues(chargeData));

        return result;
    }

    public Boolean update(LocalChargeData chargeData) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] arguments = {chargeData.getChargeId().toString()};
        Cursor cursor = db.rawQuery("select * from "+ TABLE_NAME + " where charge_id = ?", arguments);

        if (cursor.getCount() > 0) {
            long result = db.update(TABLE_NAME, setContentValues(chargeData), "charge_id=?", arguments);

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Boolean delete(LocalChargeData chargeData) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] arguments = {chargeData.getChargeId().toString()};

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where charge_id = ?", arguments);

        if (cursor.getCount() > 0) {
            long result = db.delete(TABLE_NAME, "charge_id=?", arguments);

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
    public ArrayList<LocalChargeData> selectAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<LocalChargeData> chargeDataList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
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
