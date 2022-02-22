package de.nx74205.idcharge.charge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.nx74205.idcharge.R;

public class AssignRemoteButtonFragment extends Fragment {

    EditChargeButtonClickedListener listener;
    private Button assignRemoteButton;

    public interface EditChargeButtonClickedListener {
        void assignRemoteButtonClicked();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_assign_remote_button, container, false);

        assignRemoteButton = v.findViewById(R.id.chargeAssignButton);
        assignRemoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.assignRemoteButtonClicked();
            }
        });

        return v;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditChargeButtonClickedListener) {
            listener = (EditChargeButtonClickedListener) context;
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
