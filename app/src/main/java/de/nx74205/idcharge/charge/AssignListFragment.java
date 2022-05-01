package de.nx74205.idcharge.charge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.nx74205.idcharge.R;
import de.nx74205.idcharge.database.RemoteChargeRepository;
import de.nx74205.idcharge.main.VerticalSpaceItemDecoration;
import de.nx74205.idcharge.model.RemoteChargeData;

import java.util.ArrayList;
import java.util.Optional;

public class AssignListFragment extends Fragment {

    private final ArrayList<RemoteChargeData> chargeDataList;
    private final Integer mobileChargeId;
    private int oldPosition;

    RemoteChargeListClickedListener listener;
    private RemoteChargeDataAdapter adapter;


    private View v;

    public interface RemoteChargeListClickedListener {
        void remoteChargeListClicked(RemoteChargeData data);
    }

    public AssignListFragment(ArrayList<RemoteChargeData> chargeDataList, Integer mobileChargeId) {
        this.chargeDataList = chargeDataList;
        this.mobileChargeId = (mobileChargeId != null ? mobileChargeId : Integer.MAX_VALUE);

        Optional<Integer> i =
         chargeDataList.stream()
                .filter(v -> v.getMobileChargeId().equals(mobileChargeId))
                .findFirst()
                .map(x -> chargeDataList.indexOf(x));

        if (i.isPresent()) {
            oldPosition = i.get();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_assign_remote_list, container, false);
        buildRecyclerView();

        return v;
    }

    private void buildRecyclerView() {
        RecyclerView recyclerView = v.findViewById(R.id.remoteChargeItemsRecyclerView);
        if (chargeDataList.size() == 1) {
            recyclerView.setBackground(null);
        }
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RemoteChargeDataAdapter(chargeDataList);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));

        adapter.setOnItemClickListener(new RemoteChargeDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {

                RemoteChargeRepository repository = new RemoteChargeRepository(v.getContext());

                if (oldPosition == position) {
                    if (chargeDataList.get(position).getMobileChargeId() == null) {
                        chargeDataList.get(position).setMobileChargeId(mobileChargeId);
                    } else {
                        chargeDataList.get(position).setMobileChargeId(null);
                    }
                } else {
                    chargeDataList.get(oldPosition).setMobileChargeId(null);
                    chargeDataList.get(position).setMobileChargeId(mobileChargeId);

                    repository.update(chargeDataList.get(oldPosition));
                    repository.update(chargeDataList.get(position));

                    adapter.notifyItemChanged(oldPosition);

                }
                adapter.notifyItemChanged(position);

                oldPosition = position;
                listener.remoteChargeListClicked(chargeDataList.get(position));
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RemoteChargeListClickedListener) {
            listener = (RemoteChargeListClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentAListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
