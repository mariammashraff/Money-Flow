package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionHistory extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        ListView transactionHistoryListView = findViewById(R.id.list_item_transaction_history);
        List<Map<String, String>> transactionHistoryList = new ArrayList<>();
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Transaction").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    Map<String, String> transactionHistory = new HashMap<>();
                    if (transaction != null) {

                        transactionHistory.put("amount", String.valueOf(transaction.getAmount()));
                        transactionHistory.put("to", transaction.getToId());
                        transactionHistory.put("from", String.valueOf(transaction.getFrom()));
                        transactionHistory.put("date", transaction.getDate());
                        String toId = String.valueOf(transaction.getToId());
                        String fromId = String.valueOf(transaction.getFrom());
                        FirebaseDatabase.getInstance().getReference("Customer").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {

                                transactionHistory.put("toName", snapshot.child(toId).child("name").getValue(String.class));
                                transactionHistory.put("fromName", snapshot.child(fromId).child("name").getValue(String.class));
                                transactionHistoryList.add(transactionHistory);

                                Collections.reverse(transactionHistoryList);

                                SimpleAdapter simpleAdapter = new SimpleAdapter(
                                        TransactionHistory.this,
                                        transactionHistoryList,
                                        R.layout.list_item_transaction_history,
                                        new String[]{"toName", "fromName", "to", "from", "amount", "amount", "date", "date"},
                                        new int[]{R.id.paidInName, R.id.paidOutName, R.id.paidInId, R.id.paidOutId, R.id.amountIn, R.id.amountOut, R.id.dateIn, R.id.dateOut}
                                ) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        return view;

                                    }
                                };

                                transactionHistoryListView.setAdapter(simpleAdapter);
                                progressBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

        private void fetchAndSetCustomerName (DatabaseReference customerReference, String customerId, String key, Map < String, String > transactionHistory)
        {
            customerReference.child(customerId).child("name").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {

                        String name = snapshot.getValue(String.class);
                        transactionHistory.put(key, name);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
}
