package de.nx74205.idcharge.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.nx74205.idcharge.model.ChargingStatus;
import de.nx74205.idcharge.model.LocalChargeData;
import de.nx74205.idcharge.model.RemoteChargeData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RemoteChargeRepository {
    private final DbHelper dbHelper;
    private final Context context;

    private static final String TABLE_NAME = "remote_charges";

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public RemoteChargeRepository(Context context) {
        this.context = context;
        this.dbHelper = DbHelper.getInstance(context);

    }

    public long insert(RemoteChargeData chargeData) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.insertOrThrow(TABLE_NAME, null, setContentValues(chargeData));
    }

    public Boolean update(RemoteChargeData chargeData) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] arguments = {chargeData.getId().toString()};
        Cursor cursor = db.rawQuery("select * from "+ TABLE_NAME + " where charge_id = ?", arguments);

        if (cursor.getCount() > 0) {
            long result = db.update(TABLE_NAME, setContentValues(chargeData), "charge_id=?", arguments);
            cursor.close();
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
            returnCode = deleteById(chargeData.getId());

        } else {
            returnCode = false;
        }
        
        cursor.close();
        return returnCode;
    }

    public boolean deleteUnUsed() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return (db.delete(TABLE_NAME, "mobile_charge_id is null", null) > 0);
    }

    public Boolean deleteById(Integer id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] arguments = {id.toString()};

        return (db.delete(TABLE_NAME, "charge_id=?", arguments) != -1);

    }

    public ArrayList<RemoteChargeData> selectAll(){

        LocalChargeRepository localChargeRepository = new LocalChargeRepository(context);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<RemoteChargeData> chargeDataList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            RemoteChargeData chargeData = getContentValues(cursor);

            if (chargeData.getMobileChargeId() == 0) {
                LocalChargeData localChargeData = localChargeRepository.findByChargeDataId(chargeData.getId());
                if (localChargeData != null) {
                    chargeData.setMobileChargeId(localChargeData.getChargeId());
                }
            }
            chargeDataList.add(chargeData);
            cursor.moveToNext();
        }
        cursor.close();

        return chargeDataList;
    }

    public RemoteChargeData findById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] arguments = {Integer.valueOf(id).toString()};
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where charge_id = ?", arguments);

        if (cursor.getCount() != 1) {
            return null;
        } else {
            cursor.moveToFirst();
            return getContentValues(cursor);
        }
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

    @SuppressLint("Range")
    private RemoteChargeData getContentValues(Cursor cursor) {
        RemoteChargeData chargeRecord = new RemoteChargeData();

        chargeRecord.setId(cursor.getInt(cursor.getColumnIndex("charge_id")));
        chargeRecord.setVehicleVin(cursor.getString(cursor.getColumnIndex("vehicle_vin")));
        chargeRecord.setChargingStatus(ChargingStatus.valueOf(cursor.getString(cursor.getColumnIndex("charging_status"))));
        chargeRecord.setTimestamp(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("timestamp"))));
        if (cursor.getString(cursor.getColumnIndex("start_of_charge")) != null) {
            chargeRecord.setStartOfCharge(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("start_of_charge"))));
        }
        if(cursor.getString(cursor.getColumnIndex("end_of_charge")) != null) {
            chargeRecord.setEndOfCharge(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("end_of_charge"))));
        }
        chargeRecord.setQuantityChargedKwh(Double.parseDouble(cursor.getString(cursor.getColumnIndex("quantity_charged"))));
        chargeRecord.setAverageChargingPower(Double.parseDouble(cursor.getString(cursor.getColumnIndex("average_charging_power"))));
        chargeRecord.setSocStart(cursor.getInt(cursor.getColumnIndex("soc_start")));
        chargeRecord.setSocEnd(cursor.getInt(cursor.getColumnIndex("soc_end")));
        chargeRecord.setMobileChargeId(cursor.getInt(cursor.getColumnIndex("mobile_charge_id")));

        return chargeRecord;

    }

}
