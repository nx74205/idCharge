package de.nx74205.idcharge.charge;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawableWrapper;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.model.LocalChargeData;

public class ChargeDataActivity extends AppCompatActivity
        implements AssignRemoteButtonFragment.EditChargeButtonClickedListener {

    private LocalChargeData data;
    private EditChargeDataFragment editChargeDataFragment;
    private AssignRemoteButtonFragment assignRemoteButtonFragment;
    private UpdateChargeData updateChargeData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_data);

        Intent intent = getIntent();
        data = (LocalChargeData)intent.getSerializableExtra("DATA");

        updateChargeData = new UpdateChargeData(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Eintrag");
        actionBar.setBackgroundDrawable(this.getDrawable(R.drawable.grey_background));
        actionBar.setDisplayHomeAsUpEnabled(true);

        editChargeDataFragment = new EditChargeDataFragment(data);
        assignRemoteButtonFragment = new AssignRemoteButtonFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.addChargeDataFrame, editChargeDataFragment)
                .replace(R.id.remoteChargeDataFrame, assignRemoteButtonFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.charge_data_menu, menu);
        startRefreshAnimation(menu);
        updateChargeData.update();

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent resultIntent = new Intent();
        switch (item.getItemId()) {
            case R.id.save_page:
                resultIntent.putExtra("DATA", editChargeDataFragment.getData());

                setResult(RESULT_OK, resultIntent);
                finish();

                break;
            case R.id.delete_entry:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Auswahl").setMessage("Datensatz wirklich löschen?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        data.setMileage(-1L);
                        resultIntent.putExtra("DATA", data);
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
        Toast t = Toast.makeText(this, "Button clicked", Toast.LENGTH_LONG);
        t.show();
    }
}
