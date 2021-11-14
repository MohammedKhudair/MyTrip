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
import com.mohammed.mytrip.callback.RequestTripCommunicationInterface;
import com.mohammed.mytrip.databinding.RequestTripFragmentBinding;
import com.mohammed.mytrip.domain.entitis.FullStatus;
import com.mohammed.mytrip.domain.entitis.Rider;

public class RequestTripFragment extends Fragment {
    private static final String INITIAL_STATUS_EXTRA = "INITIAL_STATUS_EXTRA";
    private RequestTripFragmentBinding binding;
    private RequestTripCommunicationInterface requestTripActionDelegates;

    public RequestTripFragment getInstance(FullStatus status) {
        RequestTripFragment fragment = new RequestTripFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INITIAL_STATUS_EXTRA, status);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RequestTripFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSelectPicUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPickUpLocation();
            }
        });

        binding.buttonSelectDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDestination();
            }
        });

        binding.buttonRequestTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestTrip();
            }
        });

        assert getArguments() != null;
        FullStatus status = (FullStatus) getArguments().getSerializable(INITIAL_STATUS_EXTRA);
        updateWithStatus(status);

    }

    private void selectPickUpLocation() {
        if (requestTripActionDelegates != null && requestTripActionDelegates.setPickUp()) {
            hideAllViews();
            binding.imageViewLocationPin.setVisibility(View.VISIBLE);
            binding.buttonSelectDestination.setVisibility(View.VISIBLE);
        }
    }

    private void selectDestination() {
        if (requestTripActionDelegates != null && requestTripActionDelegates.setDestination()) {
            hideAllViews();
            binding.buttonRequestTrip.setVisibility(View.VISIBLE);
        }
    }

    private void requestTrip() {
        if (requestTripActionDelegates != null) {
            requestTripActionDelegates.requestTrip();
        }
    }

    private void hideAllViews() {
        binding.buttonSelectPicUp.setVisibility(View.GONE);
        binding.buttonSelectDestination.setVisibility(View.GONE);
        binding.buttonRequestTrip.setVisibility(View.GONE);
        binding.imageViewLocationPin.setVisibility(View.GONE);
        binding.linearLayoutFindingTrip.setVisibility(View.GONE);
    }

    public void setActionDelegates(RequestTripCommunicationInterface requestTripActionDelegates) {
        this.requestTripActionDelegates = requestTripActionDelegates;

    }

    public void updateWithStatus(FullStatus status) {
        String riderStatus = status.getRider().getStatus();
        if (riderStatus.equals(Rider.Status.FREE.name())) {
            showSelectPickUp();
        } else if (riderStatus.equals(Rider.Status.REQUESTING_TRIP.name())) {
            showRequesting();
        } else if (riderStatus.equals(Rider.Status.REQUEST_FAILED.name())) {
            showNoAvailableDriverMessage();
            showSelectPickUp();
        }
    }

    private void showSelectPickUp() {
        hideAllViews();
        binding.buttonSelectPicUp.setVisibility(View.VISIBLE);
        binding.imageViewLocationPin.setVisibility(View.VISIBLE);
    }

    private void showRequesting() {
        hideAllViews();
        binding.linearLayoutFindingTrip.setVisibility(View.VISIBLE);
    }

    private void showNoAvailableDriverMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.no_available_drivers);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }
}
