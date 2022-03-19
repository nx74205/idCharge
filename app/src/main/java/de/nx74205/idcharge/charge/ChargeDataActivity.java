package de.nx74205.idcharge.charge;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.database.RemoteChargeRepository;
import de.nx74205.idcharge.model.LocalChargeData;
import de.nx74205.idcharge.model.RemoteChargeData;

import java.util.ArrayList;
import java.util.Collections;

public class ChargeDataActivity extends AppCompatActivity
        implements AssignRemoteButtonFragment.EditChargeButtonClickedListener, AssignListFragment.RemoteChargeListClickedListener {

    private RemoteChargeData remoteChargeData;
    private EditChargeDataFragment editChargeDataFragment;
    private UpdateChargeData updateChargeData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_data);

        Intent intent = getIntent();
        LocalChargeData localChargeData = (LocalChargeData)intent.getSerializableExtra(LocalChargeData.class.getSimpleName());

        updateChargeData = new UpdateChargeData(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Eintrag");
        //actionBar.setBackgroundDrawable(this.getDrawable(R.drawable.grey_background));
        actionBar.setDisplayHomeAsUpEnabled(true);

        editChargeDataFragment = new EditChargeDataFragment(localChargeData);
        fragmentFrameReplace(R.id.addChargeDataFrame, editChargeDataFragment);

        if (localChargeData.getChargeDataId() != null && localChargeData.getChargeDataId() > 0) {

            remoteChargeData = new RemoteChargeRepository(this).findById(localChargeData.getChargeDataId());
            if (remoteChargeData != null) {
                remoteChargeData.setMobileChargeId(localChargeData.getChargeId());
                fragmentFrameReplace(R.id.remoteChargeDataFrame,
                        new AssignListFragment(new ArrayList<>(Collections.singletonList(remoteChargeData)), localChargeData.getChargeId()));
            } else {
                fragmentFrameReplace(R.id.remoteChargeDataFrame, new AssignRemoteButtonFragment());
            }
        } else {
            fragmentFrameReplace(R.id.remoteChargeDataFrame, new AssignRemoteButtonFragment());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.charge_data_menu, menu);
        startRefreshAnimation(menu);
        updateChargeData.getRemoteData();

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent resultIntent = new Intent();
        switch (item.getItemId()) {
            case R.id.save_page:
                resultIntent.putExtra(LocalChargeData.class.getSimpleName(), editChargeDataFragment.getData());
                resultIntent.putExtra(RemoteChargeData.class.getSimpleName(), remoteChargeData);

                setResult(RESULT_OK, resultIntent);
                finish();

                break;
            case R.id.delete_entry:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Auswahl").setMessage("Datensatz wirklich löschen?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LocalChargeData localChargeData = editChargeDataFragment.getData();
                        localChargeData.setMileage(-1L);
                        resultIntent.putExtra(LocalChargeData.class.getSimpleName(), localChargeData);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog myDialog=builder.create();
                myDialog.show();
                break;

            case R.id.refresh_data:
/*
                if(item.getActionView()!=null)
                {
                    // Remove the animation.
                    item.getActionView().clearAnimation();
                    item.setActionView(null);
                }
*/
        }

        return super.onOptionsItemSelected(item);
    }

    private void startRefreshAnimation(Menu menu) {
        MenuItem item = menu.findItem(R.id.refresh_data);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh_data,null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!updateChargeData.checkUpdateFinished()) {
                    iv.startAnimation(rotation);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        iv.startAnimation(rotation);
        item.setActionView(iv);
    }

    @Override
    public void assignRemoteButtonClicked() {

        Intent intent = new Intent(ChargeDataActivity.this, AssignChargeDataActivity.class);
        intent.putExtra(LocalChargeData.class.getSimpleName(), editChargeDataFragment.getData());
        startActivityIfNeeded(intent, 1);
    }

    @Override
    public void remoteChargeListClicked(RemoteChargeData data) {
        Intent intent = new Intent(ChargeDataActivity.this, AssignChargeDataActivity.class);
        intent.putExtra(LocalChargeData.class.getSimpleName(), editChargeDataFragment.getData());
        startActivityIfNeeded(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            remoteChargeData = (RemoteChargeData)data.getSerializableExtra(RemoteChargeData.class.getSimpleName());
            if (remoteChargeData.getMobileChargeId() > 0) {
                editChargeDataFragment.getData().setChargeDataId(remoteChargeData.getId());

                AssignListFragment assignListFragment = new AssignListFragment(new ArrayList<>(Collections.singletonList(remoteChargeData)),
                        remoteChargeData.getMobileChargeId());
                fragmentFrameReplace(R.id.remoteChargeDataFrame, assignListFragment);
            } else {
                editChargeDataFragment.getData().setChargeDataId(0);
                fragmentFrameReplace(R.id.remoteChargeDataFrame, new AssignRemoteButtonFragment());
            }
        }
    }

    private void fragmentFrameReplace(int frame, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(frame, fragment)
                .commit();
    }
}
