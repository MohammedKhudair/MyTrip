package com.mohammed.mytrip.domain;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohammed.mytrip.callback.CallBack;
import com.mohammed.mytrip.callback.StatusCallBack;
import com.mohammed.mytrip.domain.entitis.Driver;
import com.mohammed.mytrip.domain.entitis.FullStatus;
import com.mohammed.mytrip.domain.entitis.Rider;
import com.mohammed.mytrip.domain.entitis.Trip;

import java.util.UUID;

public class TripManager {
    private static String USER_REF_PATH = "users";
    private static String DRIVER_REF_PATH = "drivers";
    private static String TRIP_REF_PATH = "trips";
    private Rider rider;
    private Trip trip;
    private Driver driver;

    private DatabaseReference riderRef;
    private ValueEventListener tripListener;
    private FirebaseDatabase database;
    private static TripManager INSTANCE;
    private StatusCallBack statusCallBack;

    private TripManager() {
        database = FirebaseDatabase.getInstance();
    }

    public static TripManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TripManager();
        }
        return INSTANCE;
    }

    //===============================================
    // Login Activitys
    //================================================

    public void login(CallBack callBack) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("TAG","onDataChange is called"+ task.isSuccessful());
                if (task.isSuccessful()) {
                    getOrCreateAndGetUserInfoRef(task.getResult().getUser().getUid(), callBack);
                } else
                    callBack.onComplete(false);
            }
        });
    }
    private void getOrCreateAndGetUserInfoRef(String uid, CallBack callBack) {
        riderRef = database.getReference(USER_REF_PATH).child(uid);

        riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG","onDataChange is called");

                if (snapshot.exists()) {
                    rider = snapshot.getValue(Rider.class);
                    callBack.onComplete(true);
                } else {
                    createRiderInfo(uid, callBack);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createRiderInfo(String uid, CallBack callBack) {
        rider = new Rider(uid);
        rider.setStatus(Rider.Status.FREE.name());
        riderRef.setValue(rider).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callBack.onComplete(task.isSuccessful());
            }
        });
    }

    //===============================================
    // Listening Activitys
    //================================================

    public void startListeningToUpdates(StatusCallBack statusCallBack) {
        this.statusCallBack = statusCallBack;
        startMonitoringState();
    }

    private void startMonitoringState() {
        String riderState = rider.getStatus();
        if (riderState.equals(Rider.Status.FREE.name())) {
            FullStatus fullStatus = new FullStatus();
            fullStatus.setRider(rider);
        } else if (riderState.equals(Rider.Status.ON_TRIP.name())) {
            startMonitoringTrip(rider.getAssignedTrip());
        }
    }

    private void startMonitoringTrip(String assignedTrip) {
        tripListener = database.getReference(TRIP_REF_PATH).child(assignedTrip)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trip = snapshot.getValue(Trip.class);
                        if (driver == null) {
                            database.getReference(DRIVER_REF_PATH).child(trip.getDriverId())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            driver = snapshot.getValue(Driver.class);
                                            updateStatusWithTrip();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        } else
                            updateStatusWithTrip();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void notifyListeners(FullStatus fullStatus) {
        if (statusCallBack != null) {
            statusCallBack.onUpdate(fullStatus);
        }
    }


    private void updateStatusWithTrip() {
        FullStatus fullStatus = new FullStatus();
        fullStatus.setRider(rider);
        fullStatus.setDriver(driver);
        fullStatus.setTrip(trip);

        if (trip.getStatus().equals(Trip.Status.ARRIVED.name())) {
            removeTripListener();

            rider.setStatus(Rider.Status.ARRIVED.name());
            notifyListeners(fullStatus);

            rider.setStatus(Rider.Status.FREE.name());
            rider.setAssignedTrip(null);

            driver = null;
            trip = null;
            fullStatus.setDriver(null);
            fullStatus.setTrip(null);

            database.getReference(USER_REF_PATH).child(rider.getId()).setValue(rider);
            notifyListeners(fullStatus);

        } else
            notifyListeners(fullStatus);


    }

    private void removeTripListener() {
        if (tripListener != null && trip != null) {
            database.getReference(TRIP_REF_PATH).child(trip.getId()).removeEventListener(tripListener);
            tripListener = null;
        }
    }

    //===============================================
    // Requesting Trip.
    //===============================================

    public void requestTrip(LatLng pickup, LatLng destination) {
        FullStatus fullStatus = new FullStatus();
        rider.setStatus(Rider.Status.REQUESTING_TRIP.name());
        fullStatus.setRider(rider);
        notifyListeners(fullStatus);

        database.getReference(DRIVER_REF_PATH).orderByChild("status")
                .equalTo(Driver.Status.AVAILABLE.name()).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot current : snapshot.getChildren()) {
                            driver = current.getValue(Driver.class);
                        }

                        if (driver == null) {
                            notifyNoDriverFoundAndFreeStatus();
                        } else
                            createAndSubscribeToTrip(pickup, destination);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void notifyNoDriverFoundAndFreeStatus() {
        rider.setStatus(Rider.Status.REQUEST_FAILED.name());
        FullStatus fullStatus = new FullStatus();
        fullStatus.setRider(rider);
        notifyListeners(fullStatus);

        rider.setStatus(Rider.Status.FREE.name());
        notifyListeners(fullStatus);
    }

    private void createAndSubscribeToTrip(LatLng pickup, LatLng destination) {
        String id = UUID.randomUUID().toString();
        trip = new Trip();
        trip.setId(id);
        trip.setDriverId(driver.getId());
        trip.setRiderId(rider.getId());
        trip.setPickUpLat(pickup.latitude);
        trip.setPickUpLng(pickup.longitude);
        trip.setDestinationLat(destination.latitude);
        trip.setDestinationLng(destination.longitude);

        trip.setStatus(Trip.Status.GOING_TO_PICKUP.name());

        database.getReference(TRIP_REF_PATH).child(id).setValue(trip)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           driver.setAssignedTrip(id);
                           driver.setStatus(Driver.Status.ON_TRIP.name());
                           database.getReference(DRIVER_REF_PATH).child(driver.getId()).setValue(driver);

                           rider.setAssignedTrip(id);
                           rider.setStatus(Rider.Status.ON_TRIP.name());
                           database.getReference(USER_REF_PATH).child(rider.getId()).setValue(rider);

                           startMonitoringTrip(id);
                        }
                    }
                });

    }

    public void stopListeningToUpDates(){
        statusCallBack = null;
        removeTripListener();
    }

}
