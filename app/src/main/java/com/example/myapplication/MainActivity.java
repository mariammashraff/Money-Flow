package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //String List[] = {"1 \n 1.5" , "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5", "2 \n 2.5"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inside onDataChange method
        ListView listView = findViewById(R.id.list_item);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customer");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Map<String, String>> customerDataList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Customer customer = snapshot.getValue(Customer.class);
                    String id = snapshot.getKey();
                    if (customer != null) {
                        Map<String, String> customerData = new HashMap<>();
                        customerData.put("name", customer.getName());
                        customerData.put("email", customer.getEmail());
                        customerData.put("balance", String.valueOf(customer.getBalance()));
                        customerData.put("image", customer.getImage());
                        customerData.put("id",id);
                        customerDataList.add(customerData);
                    }
                }


                SimpleAdapter adapter = new SimpleAdapter(
                        MainActivity.this,
                        customerDataList,
                        R.layout.list_item_layout,
                        new String[]{"name", "email", "balance", "image","id"},
                        new int[]{R.id.textName, R.id.textEmail, R.id.textBalance, R.id.imageCustomer,R.id.textId}
                )
                {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent)
                    {
                        View view = super.getView(position, convertView, parent);

                        // Find the button within the list item layout
                        Button buttonAction = view.findViewById(R.id.button);

                        // Set the position as a tag for the button
                        buttonAction.setTag(position);

                        // Set an OnClickListener for the button
                        buttonAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position = (int) v.getTag();


                                Intent intent = new Intent(MainActivity.this, MoneyTransfer.class);

                                // Add the customer's name as an extra to the intent
                                intent.putExtra("position", position); // Replace with the appropriate method to get the customer's ID

                                // Start the next activity
                                startActivity(intent);
                                finish();

                            }
                        });

                        // Load the image into ImageView using Glide
                        ImageView imageView = view.findViewById(R.id.imageCustomer);
                        String imageUrl = customerDataList.get(position).get("image");

                        Glide.with(MainActivity.this)
                                .load(imageUrl)
                                .into(imageView);

                        return view;
                    }
            };
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

        }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });






    }
}