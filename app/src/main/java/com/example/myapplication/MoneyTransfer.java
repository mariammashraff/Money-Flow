package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MoneyTransfer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer);
        Spinner spinner = findViewById(R.id.spinner);
        Button confirmButton = findViewById(R.id.confirmBtn);
        Button backButton = findViewById(R.id.backBtn);

        EditText editText = findViewById(R.id.amount);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customer");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                List<String> customerDataList = new ArrayList<>();
                Bundle extras = getIntent().getExtras();
                int tempPosition = extras.getInt("position") * 10 + 1910;


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String customerId = snapshot.getKey();

                    String customerName = String.valueOf(snapshot.child("name").getValue());
                    if (customerId != null && customerName != null && tempPosition != Integer.parseInt(customerId)) {
                        customerDataList.add(customerName + " " + customerId);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MoneyTransfer.this, R.layout.spinner_item, customerDataList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                progressBar.setVisibility(View.GONE);
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            int flag = -1;

            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty())
                {
                    String sendTo = spinner.getSelectedItem().toString();
                    String[] nameId = sendTo.split(" ");
                    String toId = nameId[1];
                    int amount = Integer.parseInt(editText.getText().toString());
                    Bundle extras = getIntent().getExtras();
                    int position;
                    int sendFrom = 0;
                    if (extras != null)
                    {
                        position = extras.getInt("position");
                        sendFrom = 10 * position + 1910;
                    }

                     Locale locale = new Locale("fr", "FR");
                     DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                     String currentDate = dateFormat.format(new Date());
                    Transaction transaction = new Transaction(amount, currentDate, sendFrom + "", toId);
                    int finalSendFrom = sendFrom;
                    FirebaseDatabase.getInstance().getReference("Customer").child(sendFrom + "").child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {

                            int oldBalance = (int) snapshot.getValue(Integer.class);
                            int newBalance = oldBalance - amount;
                            if (newBalance > 0)
                            {
                                FirebaseDatabase.getInstance().getReference("Customer").child(finalSendFrom + "").child("balance").setValue(newBalance);
                                flag = 0;
                                Toast.makeText(MoneyTransfer.this, "The Transaction has been confirmed.", Toast.LENGTH_SHORT).show();


                            }else
                            {
                                Toast.makeText(MoneyTransfer.this, "make sure that there is a sufficient fund for this transaction", Toast.LENGTH_SHORT).show();
                                flag = 1;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference("Customer").child(toId).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int oldBalance = (int) snapshot.getValue(Integer.class);
                            int newBalance = oldBalance + amount;
                            if(flag == 0)
                                FirebaseDatabase.getInstance().getReference("Customer").child(toId).child("balance").setValue(newBalance);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference("Transaction").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            long maxNumber = Long.MIN_VALUE; // Initialize with the minimum possible value

                            for (DataSnapshot childSnapshot : snapshot.getChildren())
                            {
                                String key = childSnapshot.getKey();

                                if (key != null) {
                                    try {
                                        long number = Long.parseLong(key);
                                        if (number > maxNumber) {
                                            maxNumber = number;
                                        }
                                    } catch (NumberFormatException e) {
                                        // Handle the case where the key is not a valid number
                                    }
                                }
                            }
                            if(flag == 0)
                                FirebaseDatabase.getInstance().getReference("Transaction").child(maxNumber + 1 + "").setValue(transaction);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if(flag == 1)
                        Toast.makeText(MoneyTransfer.this, "Please enter a numerical value in the 'Amount' field.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        backButton.setOnClickListener(view -> {

            Intent intent = new Intent(MoneyTransfer.this, MainActivity.class);
            startActivity(intent);
            finish();

        });


    }

}