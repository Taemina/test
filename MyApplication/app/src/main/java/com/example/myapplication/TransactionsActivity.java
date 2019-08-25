package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.myapplication.SettingsActivity.EUR_TEXT;
import static com.example.myapplication.SettingsActivity.USD_TEXT;

public class TransactionsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btAdd;
    private Button btWithdraw;
    private Button btTransfer;
    private Spinner spinnerRate;
    private Spinner spinnerAccounts;
    private String[] accountsList;
    private ArrayList<Contact> accountsArrayList;
    private String USBtext;
    private String EURtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        btAdd = findViewById(R.id.bt_add);
        btWithdraw = findViewById(R.id.bt_withdraw);
        btTransfer = findViewById(R.id.bt_transfer);

        btAdd.setOnClickListener(this);
        btWithdraw.setOnClickListener(this);
        btTransfer.setOnClickListener(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            USBtext = getIntent().getStringExtra(USD_TEXT);
            EURtext = getIntent().getStringExtra(EUR_TEXT);
        }
        accountsListUp();

    }

    //формирования списка кошельков
    private void accountsListUp() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        accountsArrayList = databaseHelper.getAllPosts();
        accountsList = new String[accountsArrayList.size()];

        for (int i = 0; i < accountsArrayList.size(); i++) {
            Contact contact = accountsArrayList.get(i);
            accountsList[i] = contact.getName() + "  Сумма: " + contact.getSum() + " руб.";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sett_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_revert:
                Intent activity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //обработка нажатий кнопок
    public void onClick(View v) {
        accountsListUp();
        if (v.getId() == R.id.bt_add) {
            addMoney();
        }
        if (v.getId() == R.id.bt_withdraw) {
            withdrawMoney();
        }
        if (v.getId() == R.id.bt_transfer) {
            transferMoney();
        }
    }

    // кнопка перевода денег
    private void transferMoney() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_money, null);

        AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setView(promptsView);
        final EditText userInput = promptsView.findViewById(R.id.input_money);
        final EditText userInputComments = promptsView.findViewById(R.id.input_comments);

        TextView text1 = promptsView.findViewById(R.id.tx1);
        text1.setText(R.string.with_wallet);
        text1 = promptsView.findViewById(R.id.tx2);
        text1.setText(R.string.in_wallet);
        spinnerRate = promptsView.findViewById(R.id.rate);
        spinnerAccounts = promptsView.findViewById(R.id.accounts);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, accountsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAccounts.setAdapter(adapter);
        spinnerRate.setAdapter(adapter);
        ad.setTitle(R.string.transfer_money);  // заголовок

        ad.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                if (userInput.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Операция не выполнена. Вы не ввели сумму", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (userInputComments.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Операция не выполнена. Вы не ввели комментарий", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    int checkAccountsWith = spinnerAccounts.getSelectedItemPosition();
                    int checkAccountsIn = spinnerRate.getSelectedItemPosition();
                    double money = Double.parseDouble(userInput.getText().toString());
                    String userInputComment = userInputComments.getText().toString();

                    //если операция успешна дабавить информацию о ней в БД
                    if (transferMoney(checkAccountsWith, checkAccountsIn, money)) {
                        transactions("расход", userInputComment, checkAccountsWith, money, "RUS");
                        transactions("приход", userInputComment, checkAccountsIn, money, "RUS");
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Операция выполнена", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    dialog.cancel();
                }
            }
        });
        ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.setCancelable(false);

        ad.show();


    }
    //кнопка снятия денег
    private void withdrawMoney() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_money, null);

        AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setView(promptsView);

        final EditText userInput = promptsView.findViewById(R.id.input_money);
        final EditText userInputComments = promptsView.findViewById(R.id.input_comments);

        TextView text1 = promptsView.findViewById(R.id.tx1);
        text1.setText(R.string.choose_a_wallet);
        text1 = promptsView.findViewById(R.id.tx2);
        text1.setText(R.string.chosen_currency);
        spinnerRate = promptsView.findViewById(R.id.rate);
        spinnerAccounts = promptsView.findViewById(R.id.accounts);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, accountsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAccounts.setAdapter(adapter);
        ad.setTitle(R.string.withdraw_money);
        ad.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                if (userInput.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Операция не выполнена. Вы не ввели сумму", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (userInputComments.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Операция не выполнена. Вы не ввели комментарий", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    String checkRate = spinnerRate.getSelectedItem().toString();
                    int checkAccounts = spinnerAccounts.getSelectedItemPosition();
                    double money = Double.parseDouble(userInput.getText().toString());
                    String userInputComment = userInputComments.getText().toString();

                    //если операция успешна дабавить информацию о ней в БД
                    if (withdrawMoney(checkRate, checkAccounts, money)) {
                        transactions("расход", userInputComment, checkAccounts, money, checkRate);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Операция выполнена", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    dialog.cancel();
                }
            }
        });
        ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.setCancelable(false);

        ad.show();
    }
    //кнопка пополнения средств
    private void addMoney() {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_money, null);

        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setView(promptsView);

        final EditText userInput = promptsView.findViewById(R.id.input_money);
        final EditText userInputComments = promptsView.findViewById(R.id.input_comments);

        TextView text1 = promptsView.findViewById(R.id.tx1);
        text1.setText(R.string.choose_a_wallet);
        text1 = promptsView.findViewById(R.id.tx2);
        text1.setText(R.string.chosen_currency);
        spinnerRate = promptsView.findViewById(R.id.rate);
        spinnerAccounts = promptsView.findViewById(R.id.accounts);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, accountsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAccounts.setAdapter(adapter);
        ad.setTitle(R.string.up_balance);
        ad.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int arg1) {
                if (userInput.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Операция не выполнена. Вы не ввели сумму", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (userInputComments.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Операция не выполнена. Вы не ввели комментарий", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    String checkRate = spinnerRate.getSelectedItem().toString();
                    int checkAccounts = spinnerAccounts.getSelectedItemPosition();
                    double money = Double.parseDouble(userInput.getText().toString());
                    String userInputComment = userInputComments.getText().toString();
                    addMoney(checkRate, checkAccounts, money);
                    //дабавить информацию об операции в БД
                    transactions("приход", userInputComment, checkAccounts, money, checkRate);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Операция выполнена", Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.cancel();
                }

            }
        });
        ad.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.setCancelable(false);

        ad.show();

    }

    //перевод денег работа с БД
    private boolean transferMoney(int withPosition, int inPosition, double money) {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        accountsArrayList = databaseHelper.getAllPosts();
        Contact contactWith = accountsArrayList.get(withPosition);
        Contact contactIn = accountsArrayList.get(inPosition);

        if (Double.parseDouble(contactWith.getSum()) >= money) {
            contactWith.setSum(String.valueOf(Double.parseDouble(contactWith.getSum()) - money));
            contactIn.setSum(String.valueOf(Double.parseDouble(contactIn.getSum()) + money));

            databaseHelper.updateVal(contactWith,0);
            databaseHelper.updateVal(contactIn,0);
            return true;
        } else {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Ошибка");  // заголовок
            ad.setMessage("На счете не хватает денег"); // сообщение
            ad.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                }
            });
            ad.setCancelable(false);
            ad.show();
            return false;
        }

    }
    //снятие денег работа с БД
    private boolean withdrawMoney(String rate, int position, double money) {
        double rate_add = 1;

        if (rate.equals("USD")) {
            rate_add = Double.parseDouble(USBtext);

        } else if (rate.equals("EUR")) {
            rate_add = Double.parseDouble(EURtext);
        }
        rate_add = new BigDecimal(rate_add).setScale(3, RoundingMode.UP).doubleValue();

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        accountsArrayList = databaseHelper.getAllPosts();
        Contact contact = accountsArrayList.get(position);

        if (Double.parseDouble(contact.getSum()) >= money * rate_add) {
            double d = Double.parseDouble(contact.getSum()) - (money * rate_add);
            d = new BigDecimal(d).setScale(3, RoundingMode.UP).doubleValue();

            contact.setSum(String.valueOf(d));
            databaseHelper.updateVal(contact,0);
            return true;
        } else {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Ошибка");  // заголовок
            ad.setMessage("На счете не хватает денег"); // сообщение
            ad.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                }
            });
            ad.setCancelable(false);
            ad.show();
            return false;
        }

    }
    //пополнение кошелька работа с БД
    private void addMoney(String rate, int position, double money) {
        double rate_add = 1;
        if (rate.equals("USD")) {
            rate_add = Double.parseDouble(USBtext);

        } else if (rate.equals("EUR")) {
            rate_add = Double.parseDouble(EURtext);
        }
        rate_add = new BigDecimal(rate_add).setScale(3, RoundingMode.UP).doubleValue();
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);

        accountsArrayList = databaseHelper.getAllPosts();
        Contact contact = accountsArrayList.get(position);
        double d = Double.parseDouble(contact.getSum()) + (money * rate_add);
        d = new BigDecimal(d).setScale(3, RoundingMode.UP).doubleValue();

        contact.setSum(String.valueOf(d));
        databaseHelper.updateVal(contact,0);

    }
    //добавление информации о операции в БД
    private void transactions(String type, String comment, int position, double money, String rate) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        accountsArrayList = databaseHelper.getAllPosts();

        Contact contact = accountsArrayList.get(position);
        Date date = new Date();
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("dd.MM.yyyy");
        String newDate = format1.format(date);
        String name = contact.getName();
        money = new BigDecimal(money).setScale(3, RoundingMode.UP).doubleValue();

        Transaction trans = new Transaction();
        trans.setType(type);
        trans.setSum(String.valueOf(money));
        trans.setCurrency(rate);
        trans.setComment(comment);
        trans.setNameWallet(name);
        trans.setDate(newDate);

        databaseHelper.addTrans(trans);
    }
}
