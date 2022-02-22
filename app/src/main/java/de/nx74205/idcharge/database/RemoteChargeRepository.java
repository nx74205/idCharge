package de.nx74205.idcharge.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.nx74205.idcharge.model.ChargingStatus;
import de.nx74205.idcharge.model.RemoteChargeData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RemoteChargeRepository {
    private final DbHelper dbHelper;
    private static final String TABLE_NAME = "remote_charges";

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public RemoteChargeRepository(Context context) {
        this.dbHelper = DbHelper.getInstance(context);

    }

    public long insert(RemoteChargeData chargeData) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long result = db.insertOrThrow(TABLE_NAME, null, setContentValues(chargeData));

        return result;
    }

    public Boolean update(RemoteChargeData chargeData) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] arguments = {chargeData.getId().toString()};
        Cursor cursor = db.rawQuery("select * from "+ TABLE_NAME + " where charge_id = ?", arguments);

        if (cursor.getCount() > 0) {
            long result = db.update(TABLE_NAME, setContentValues(chargeData), "charge_id=?", arguments);

            return (result >= 0);

        } else {
            return (insert(chargeData) > 0);
        }
    }

    public Boolean delete(RemoteChargeData chargeData) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean returnCode;

        String[] arguments = {chargeData.getId().toString()};

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where charge_id = ?", arguments);

        if (cursor.getCount() > 0) {
            long result = db.delete(TABLE_NAME, "charge_id=?", arguments);

            returnCode = result != -1;
        } else {
            returnCode = false;
        }
        
        cursor.close();
        return returnCode;
    }

    @SuppressLint("Range")
    public ArrayList<RemoteChargeData> selectAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<RemoteChargeData> chargeDataList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            RemoteChargeData chargeRecord = new RemoteChargeData();

            chargeRecord.setId(cursor.getInt(cursor.getColumnIndex("charge_id")));
            chargeRecord.setVehicleVin(cursor.getString(cursor.getColumnIndex("vehicle_vin")));
            chargeRecord.setChargingStatus(ChargingStatus.valueOf(cursor.getString(cursor.getColumnIndex("charging_status"))));
            chargeRecord.setTimestamp(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("timestamp")), DATE_FORMAT));
            chargeRecord.setStartOfCharge(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("start_of_charge")), DATE_FORMAT));
            chargeRecord.setEndOfCharge(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("end_of_charge")), DATE_FORMAT));
            chargeRecord.setQuantityChargedKwh(Double.parseDouble(cursor.getString(cursor.getColumnIndex("quantity_charged"))));
            chargeRecord.setAverageChargingPower(Double.parseDouble(cursor.getString(cursor.getColumnIndex("average_charging_power"))));
            chargeRecord.setSocStart(cursor.getInt(cursor.getColumnIndex("soc_start")));
            chargeRecord.setSocEnd(cursor.getInt(cursor.getColumnIndex("soc_end")));
            chargeRecord.setMobileChargeId(cursor.getInt(cursor.getColumnIndex("mobile_charge_id")));
            chargeDataList.add(chargeRecord);

            cursor.moveToNext();
        }
        return chargeDataList;
    }

    private ContentValues setContentValues(RemoteChargeData chargeData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("charge_id", chargeData.getId());
        contentValues.put("vehicle_vin", chargeData.getVehicleVin());
        contentValues.put("charging_status", chargeData.getChargingStatus().name());
        contentValues.put("timestamp", chargeData.getTimestamp().toString());
        if (chargeData.getStartOfCharge() != null) {
            contentValues.put("start_of_charge", chargeData.getStartOfCharge().toString());
        }
        if (chargeData.getEndOfCharge() != null) {
            contentValues.put("end_of_charge", chargeData.getEndOfCharge().toString());
        }
        contentValues.put("quantity_charged", chargeData.getQuantityChargedKwh().toString());
        contentValues.put("average_charging_power", chargeData.getAverageChargingPower().toString());
        contentValues.put("soc_start", chargeData.getSocStart());
        contentValues.put("soc_end", chargeData.getSocEnd());
        contentValues.put("mobile_charge_id", chargeData.getMobileChargeId());

        return contentValues;
    }

}
