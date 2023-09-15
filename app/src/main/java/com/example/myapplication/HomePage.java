package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Check SharedPreferences for the welcome_shown flag
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean welcomeShown = preferences.getBoolean("welcome_shown", false);

        if (!welcomeShown)
        {
            // Launch the Welcome Activity
            startActivity(new Intent(this, WelcomePage.class));
        }
        Button TransactionButton = findViewById(R.id.transactionButton);
        Button UsersButton = findViewById(R.id.userButton);
        TextView userNo = findViewById(R.id.userNo);
        TextView transactionNo = findViewById(R.id.transactionNo);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        TransactionButton.setOnClickListener(view ->
        {
            Intent intent = new Intent(HomePage.this, TransactionHistory.class);
            startActivity(intent);

        });

        UsersButton.setOnClickListener(view ->
        {
            Intent intent = new Intent(HomePage.this, MainActivity.class);
            startActivity(intent);

        });

        databaseReference.child("Customer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    i++;
                }
                userNo.setText("      "+i+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Transaction").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    i++;
                }
                transactionNo.setText("      "+i+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}