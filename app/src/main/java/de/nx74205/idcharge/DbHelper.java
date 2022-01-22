package de.nx74205.idcharge;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.nx74205.idcharge.model.LocalChargeData;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DbHelper extends SQLiteOpenHelper {

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public DbHelper( Context context) {
        super(context, "Chargedata", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table local_charges(" +
                "charge_id INTEGER PRIMARY KEY, " +
                "remote_charge_id INTEGER," +
                "timestamp TEXT," +
                "mileage INTEGER," +
                "charge_kw_paid TEXT," +
                "price TEXT," +
                "target_soc INTEGER, " +
                "charge_typ TEXT," +
                "bc_consumption TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //db.execSQL("drop Table if exists Userdetails");

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
        contentValues.put("charge_kw_paid", chargeData.getChargedKwPaid().toString());
        contentValues.put("price", chargeData.getPrice().toString());
        contentValues.put("target_soc", chargeData.getTargetSoc());
        contentValues.put("charge_typ", chargeData.getChargeTyp());
        contentValues.put("bc_consumption", chargeData.getBcConsumption().toString());

        return contentValues;

    }

}
