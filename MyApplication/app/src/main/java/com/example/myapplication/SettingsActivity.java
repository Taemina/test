package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.myapplication.MainActivity.CHEKC_RATE;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String USD_TEXT = "USD_TEXT";
    public static final String EUR_TEXT = "EUR_TEXT";
    private EditText etUSD;
    private EditText etEUR;
    private CheckBox chbRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etUSD = findViewById(R.id.USD_text);
        etEUR = findViewById(R.id.EUR_text);
        chbRate = findViewById(R.id.rate_check);

        chbRate.setOnClickListener(this);


        if (getIntent() != null && getIntent().getExtras() != null) {
            etUSD.setText(getIntent().getStringExtra(USD_TEXT));
            etEUR.setText(getIntent().getStringExtra(EUR_TEXT));
        }

        onCheck();
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

                if (etUSD.getText().toString().equals("")) {
                    etUSD.setError("не заполнено");
                } else if (etEUR.getText().toString().equals("")) {
                    etEUR.setError("не заполнено");
                } else {
                Intent activity = new Intent(getApplicationContext(), MainActivity.class);

                activity.putExtra(USD_TEXT, etUSD.getText().toString());
                activity.putExtra(EUR_TEXT, etEUR.getText().toString());
                CHEKC_RATE = chbRate.isChecked();

                startActivity(activity);}
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (etUSD.getText().toString().equals("")) {
            etUSD.setError("не заполнено");
        } else if (etEUR.getText().toString().equals("")) {
            etEUR.setError("не заполнено");
        } else {
        Intent intent = new Intent(this, MainActivity.class);

        CHEKC_RATE = chbRate.isChecked();
        intent.putExtra(USD_TEXT, etUSD.getText().toString());
        intent.putExtra(EUR_TEXT, etEUR.getText().toString());

        startActivity(intent);
        finish();}
    }

    public void onClick(View v) {
        if (v.getId() == R.id.rate_check) {

            CHEKC_RATE = chbRate.isChecked();
            onCheck();

            if (CHEKC_RATE) {
                chbRate.setChecked(true);
                Intent activity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity);
            }
            onCheck();
        }
    }

    public void onCheck() {
        if (CHEKC_RATE) {
            etUSD.setEnabled(false);
            etEUR.setEnabled(false);
            chbRate.setChecked(true);

        } else {
            etUSD.setEnabled(true);
            etEUR.setEnabled(true);
            chbRate.setChecked(false);
        }
    }


}
