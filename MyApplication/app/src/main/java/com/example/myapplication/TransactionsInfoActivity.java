package com.example.myapplication;

import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;



public class TransactionsInfoActivity extends AppCompatActivity {
    public static final String USD= "USD";
    public static final String EUR = "USD";

    public static final String INFO = "INFO";

    private ArrayList<Transaction> trans;
    private String name; //имя кошелька для вывода операций
    private String USBtext;
    private String EURtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);

        if (getIntent() != null && getIntent().getExtras() != null) {
            name = getIntent().getStringExtra(INFO);
            USBtext = getIntent().getStringExtra(USD);
            EURtext = getIntent().getStringExtra(EUR);
        }
        manageRecyclerView();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sett_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Обработка нажатий
        switch (item.getItemId()) {
            case R.id.menu_revert:
                Intent activity = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(activity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //отрисовка RecyclerView операций
    public void manageRecyclerView() {
        RecyclerView rvTrans = (RecyclerView) findViewById(R.id.rvTrans);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        trans = databaseHelper.getAllTransactions(name);

        TransactionAdapter adapter = new TransactionAdapter(trans,USBtext,EURtext);
        rvTrans.setAdapter(adapter);
        rvTrans.setLayoutManager(new LinearLayoutManager(this));

    }
}
