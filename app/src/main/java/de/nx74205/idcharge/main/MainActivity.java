package de.nx74205.idcharge.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.OptionalLong;

import de.nx74205.idcharge.charge.ChargingActivity;
import de.nx74205.idcharge.DbHelper;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.model.LocalChargeData;


public class MainActivity extends AppCompatActivity {

    private final int NEW_CHARGE_DATA_ACTIVITY = 1;
    private final int CHANGE_CHARGE_DATA_ACTIVITY = 2;
    FloatingActionButton addChargeButton;

    private RecyclerView recyclerView;
    private ChargeDataAdapter chargeDataAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<LocalChargeData> chargeDataList;

    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DbHelper(this);

        chargeDataList = db.getCharges();

        buildRecyclerView();
        addChargeButton = findViewById(R.id.add_charge);
        addChargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long mileage;
                OptionalLong optionalMileage = chargeDataList
                        .stream()
                        .mapToLong(m -> m.getMileage())
                        .max();
                if(optionalMileage.isPresent()) {
                    mileage = optionalMileage.getAsLong();
                } else {
                    mileage = 0L;
                }
                LocalChargeData chargeData = new LocalChargeData();
                chargeData.setTimeStamp(LocalDateTime.now());
                chargeData.setViewPosition(chargeDataList.size());
                chargeData.setMileage(mileage);

                Intent intent = new Intent(MainActivity.this, ChargingActivity.class);
                intent.putExtra("DATA", chargeData);
                startActivityIfNeeded(intent, NEW_CHARGE_DATA_ACTIVITY);

            }
        });
    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.chargeItemsView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        chargeDataAdapter = new ChargeDataAdapter(chargeDataList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
        recyclerView.setAdapter(chargeDataAdapter);

        chargeDataAdapter.setOnItemClickListener(new ChargeDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                LocalChargeData data = chargeDataList.get(position);
                data.setViewPosition(position);

                Intent intent = new Intent(MainActivity.this, ChargingActivity.class);
                intent.putExtra("DATA", data);
                startActivityIfNeeded(intent, CHANGE_CHARGE_DATA_ACTIVITY);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LocalChargeData chargeData = (LocalChargeData)data.getSerializableExtra("DATA");
        int position = chargeData.getViewPosition();

        if (chargeData != null) {
            switch (requestCode) {
                case NEW_CHARGE_DATA_ACTIVITY:
                    long chargeId = db.insertCharges(chargeData);
                    if (chargeId != -1) {
                        chargeData.setChargeId((int)chargeId);
                        chargeDataList.add(position, chargeData);
                        chargeDataAdapter.notifyItemInserted(position);
                    };
                    break;
                case CHANGE_CHARGE_DATA_ACTIVITY:
                    if (chargeData.getMileage().longValue() == -1L) {
                        if (db.deleteCharges(chargeData)) {
                            chargeDataList.remove(position);
                            chargeDataAdapter.notifyItemRemoved(position);
                        }
                    } else {
                            if (db.updateCharges(chargeData)) {
                                chargeDataList.set(position, chargeData);
                                chargeDataAdapter.notifyItemChanged(position);
                            }
                    }
                    break;
            }
        }
    }
}