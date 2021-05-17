package com.aditya_verma.foodies_delivery_partner;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cakery extends AppCompatActivity {

    FirebaseFirestore Cakery_db;

    private static final int REQUEST_CALL = 1;

    private FirestoreRecyclerAdapter<DataModal, ViewHolder> adapter;
   // Database main_act_database;
    public static String collection, on_off;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cakery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // main_act_database = new Database(this);

        Cakery_db = FirebaseFirestore.getInstance();


        new_method();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shubhraj:
                Intent Intent_inquiry1 = new Intent(Cakery.this,MainActivity.class);
                startActivity(Intent_inquiry1);
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.cakery:

                Intent Intent_inquiry2 = new Intent(Cakery.this,Cakery.class);
                startActivity(Intent_inquiry2);
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.foodies:
                Intent Intent_inquiry3 = new Intent(Cakery.this,Foodies.class);
                startActivity(Intent_inquiry3);
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void new_method(){

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

        final Query query = rootRef.collection("Cakery_Order")
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<DataModal> options = new FirestoreRecyclerOptions.Builder<DataModal>()
                .setQuery(query, DataModal.class)
                .build();

        // main_act_database.addModel(query);

        adapter = new FirestoreRecyclerAdapter<DataModal, ViewHolder>(options) {

            @Override
            public void onDataChanged() {

                startAlert();

                // Called each time there is a new query snapshot. You may want to use this method
                // to hide a loading spinner or check for the "no documents" state and update your UI.
                // ...
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                // Called when there is an error getting a query snapshot. You may want to update
                // your UI to display an error message to the user.
                // ...

            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final Cakery.ViewHolder holder, int position, @NonNull final DataModal model) {
                // setting data to our views in Recycler view items.
                // DataModal model = model.get(position);
                holder.name.setText("Name: " + model.getName());
                // holder.mobile.set(model.getMobile());
                holder.near_area.setText(model.getNear_area());
                holder.address.setText("Address: "+ model.getAddress());
                // holder.location_text.setText(model.getLocation_text());
                holder.mode_of_payment.setText("Payment :"+ model.getMode_of_payment());
                holder.total_bill_price.setText("Total_Bill: "+ model.getTotal_bill_price());

                final List<String> array_list = model.getFood_list();
                holder.food_list.setText(""+ array_list);


                holder.time.setText(""+ model.getDate());


                holder.mobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ActivityCompat.checkSelfPermission(Cakery.this,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(Cakery.this,new String[]
                                    {Manifest.permission.CALL_PHONE},REQUEST_CALL);
                        }
                        else {
                            Intent call = new Intent(Intent.ACTION_CALL);
                            call.setData(Uri.parse("tel:"+ model.getMobile()));
                            startActivity(call);
                        }
                    }
                });

                holder.location_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String location = model.getLocation_text();
                        Display_Track(location);
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }

    private  class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView name, near_area, address, food_list, mode_of_payment, total_bill_price;

        private TextView time;

        private ImageView location_text,mobile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            name = itemView.findViewById(R.id.name);
            mobile = itemView.findViewById(R.id.mobile);
            near_area = itemView.findViewById(R.id.near_area);
            address = itemView.findViewById(R.id.address);
            location_text = itemView.findViewById(R.id.location_text);
            mode_of_payment = itemView.findViewById(R.id.mode_of_payment);
            total_bill_price = itemView.findViewById(R.id.total_bill_price);
            food_list = itemView.findViewById(R.id.food_list);
            time = itemView.findViewById(R.id.time);
        }

    }

    public void Display_Track(String location){

        try{
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + "" + "/" + location);

            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
        catch(ActivityNotFoundException e){
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");

            Intent intent = new Intent(Intent.ACTION_VIEW,uri);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }


    public void startAlert() {

        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

}
