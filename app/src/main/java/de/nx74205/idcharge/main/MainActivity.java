package de.nx74205.idcharge.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.OptionalLong;

import de.nx74205.idcharge.charge.ChargeDataActivity;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.database.LocalChargeRepository;
import de.nx74205.idcharge.database.RemoteChargeRepository;
import de.nx74205.idcharge.model.LocalChargeData;
import de.nx74205.idcharge.model.RemoteChargeData;


public class MainActivity extends AppCompatActivity {

    private final int NEW_CHARGE_DATA_ACTIVITY = 1;
    private final int CHANGE_CHARGE_DATA_ACTIVITY = 2;
    FloatingActionButton addChargeButton;

    private ChargeDataAdapter chargeDataAdapter;

    ArrayList<LocalChargeData> chargeDataList;

    private LocalChargeRepository mobileDataRepository;
    private RemoteChargeRepository remoteChargeRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mobileDataRepository = new LocalChargeRepository(this);
        remoteChargeRepository = new RemoteChargeRepository(this);


        buildRecyclerView();
        addChargeButton = findViewById(R.id.add_charge);
        addChargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long mileage;
                OptionalLong optionalMileage = chargeDataList
                        .stream()
                        .mapToLong(LocalChargeData::getMileage)
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

                Intent intent = new Intent(MainActivity.this, ChargeDataActivity.class);
                intent.putExtra(LocalChargeData.class.getSimpleName(), chargeData);
                startActivityIfNeeded(intent, NEW_CHARGE_DATA_ACTIVITY);

            }
        });
    }

    private void buildRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.chargeItemsView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        chargeDataList = mobileDataRepository.selectAll();
        chargeDataAdapter = new ChargeDataAdapter(chargeDataList);
        recyclerView.setAdapter(chargeDataAdapter);

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    addChargeButton.hide();
                } else {
                    addChargeButton.show();
                }
            }
        });

        chargeDataAdapter.setOnItemClickListener(new ChargeDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                LocalChargeData data = chargeDataList.get(position);
                data.setViewPosition(position);

                Intent intent = new Intent(MainActivity.this, ChargeDataActivity.class);
                intent.putExtra(LocalChargeData.class.getSimpleName(), data);
                startActivityIfNeeded(intent, CHANGE_CHARGE_DATA_ACTIVITY);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LocalChargeData mobileChargeData = (LocalChargeData)data.getSerializableExtra(LocalChargeData.class.getSimpleName());
        RemoteChargeData remoteChargeData = (RemoteChargeData)data.getSerializableExtra(RemoteChargeData.class.getSimpleName());

        int position = mobileChargeData.getViewPosition();

        switch (requestCode) {
            case NEW_CHARGE_DATA_ACTIVITY:
                long chargeId = mobileDataRepository.insert(mobileChargeData);
                if (chargeId != -1) {
                    mobileChargeData.setChargeId((int)chargeId);
                    chargeDataList.add(position, mobileChargeData);
                    chargeDataAdapter.notifyItemInserted(position);
                };
                break;
            case CHANGE_CHARGE_DATA_ACTIVITY:
                if (mobileChargeData.getMileage() == -1L) {
                    if (mobileDataRepository.delete(mobileChargeData)) {
                        if (remoteChargeData != null) {
                            remoteChargeData.setMobileChargeId(null);
                        }
                        chargeDataList.remove(position);
                        chargeDataAdapter.notifyItemRemoved(position);
                    }
                } else {
                        if (mobileDataRepository.update(mobileChargeData)) {
                            chargeDataList.set(position, mobileChargeData);
                            chargeDataAdapter.notifyItemChanged(position);
                        }
                }
                break;
        }
        if (remoteChargeData != null) {
            remoteChargeRepository.update(remoteChargeData);
        }
    }
}