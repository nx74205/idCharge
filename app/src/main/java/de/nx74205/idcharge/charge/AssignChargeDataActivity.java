package de.nx74205.idcharge.charge;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.database.LocalChargeRepository;
import de.nx74205.idcharge.database.RemoteChargeRepository;
import de.nx74205.idcharge.model.LocalChargeData;
import de.nx74205.idcharge.model.RemoteChargeData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssignChargeDataActivity extends AppCompatActivity
        implements AssignListFragment.RemoteChargeListClickedListener {

    RemoteChargeRepository remoteChargeRepository;
    LocalChargeData localChargeData;
    RemoteChargeData remoteChargeData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_charge_data);

        Intent intent = getIntent();
        localChargeData = (LocalChargeData) intent.getSerializableExtra(LocalChargeData.class.getSimpleName());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Eintrag zuordnen");
        actionBar.setBackgroundDrawable(this.getDrawable(R.drawable.grey_background));
        actionBar.setDisplayHomeAsUpEnabled(true);

        remoteChargeRepository = new RemoteChargeRepository(this);

        AssignListFragment listFragment = new AssignListFragment(getRemoteChargeData(localChargeData.getChargeId()), localChargeData.getChargeId());
        ShowLocalDataFragment localDataFragment = new ShowLocalDataFragment(localChargeData);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.showLocalChargeDataFrame, localDataFragment)
                .replace(R.id.actremoteChargeItemsRecyclerView, listFragment)
                .commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.assign_data_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.save_assigned_item) {
            Intent resultIntent = new Intent();
            if (remoteChargeData != null) {
                resultIntent.putExtra(RemoteChargeData.class.getSimpleName(), remoteChargeData);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Kein Datensatz zugeordnet", Toast.LENGTH_SHORT).show();
            }
        } else {
            finish();
        }
        return true;
    }

    @Override
    public void remoteChargeListClicked(RemoteChargeData data) {
        remoteChargeData = data;
    }

    private ArrayList<RemoteChargeData> getRemoteChargeData(Integer localChargeId) {
        RemoteChargeRepository remoteChargeRepository = new RemoteChargeRepository(this);
        ArrayList<RemoteChargeData> chargeDataList =
                remoteChargeRepository.selectAll().stream()
                        .filter(c -> (c.getMobileChargeId() == null) || (c.getMobileChargeId() <= 0) || (c.getMobileChargeId().equals(localChargeId)))
                        .collect(Collectors.toCollection(ArrayList::new));
        return chargeDataList;
    }
}
