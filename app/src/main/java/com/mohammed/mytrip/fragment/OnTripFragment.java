package com.mohammed.mytrip.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mohammed.mytrip.R;
import com.mohammed.mytrip.databinding.OnTripFragmentBinding;
import com.mohammed.mytrip.domain.entitis.FullStatus;
import com.mohammed.mytrip.domain.entitis.Trip;

public class OnTripFragment extends Fragment {
    private static final String INITIAL_EXTRA_STATUS = "INITIAL_EXTRA_STATUS";
    private OnTripFragmentBinding binding;

    public static OnTripFragment getInstance(FullStatus status) {
        OnTripFragment fragment = new OnTripFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INITIAL_EXTRA_STATUS, status);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OnTripFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        FullStatus status = (FullStatus) getArguments().getSerializable(INITIAL_EXTRA_STATUS);
        updateWithStatus(status);
    }

    public void updateWithStatus(FullStatus status) {
        binding.textViewDriverName.setText(status.getDriver().getName());
        binding.textViewPlateNumber.setText(String.valueOf(status.getDriver().getPlateNumber()));

        String tripStatus = status.getTrip().getStatus();
        String tripStatusText = "";

        if (tripStatus.equals(Trip.Status.GOING_TO_PICKUP.name())) {
            tripStatusText = getString(R.string.driver_going_to_pickup);

        } else if (tripStatus.equals(Trip.Status.GOING_TO_DESTINATION.name())) {
            tripStatusText = getString(R.string.going_to_destination);

        } else if (tripStatus.equals(Trip.Status.ARRIVED.name())) {
            tripStatusText = getString(R.string.arrived);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(tripStatusText);
            builder.setPositiveButton(R.string.ok, null);
            builder.show();
        }
        binding.textViewTripStatus.setText(tripStatusText);
    }

}
