package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.SettingsActivity.EUR_TEXT;
import static com.example.myapplication.SettingsActivity.USD_TEXT;
import static com.example.myapplication.TransactionsInfoActivity.EUR;
import static com.example.myapplication.TransactionsInfoActivity.INFO;
import static com.example.myapplication.TransactionsInfoActivity.USD;


public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    public static boolean CHEKC_RATE = true;

    private static final String URL = "https://www.cbr-xml-daily.ru/";

    private ArrayList<Contact> contacts;
    private OpenRate openRate;
    private String USBtext;
    private String EURtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        manageRecyclerView();

        initRetrofit();

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
       // load(sharedPref);

        //загрузка данных о курсах валют
        if (CHEKC_RATE) {
            requestRetrofit("USD");
            requestRetrofit("EUR");
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            USBtext = getIntent().getStringExtra(USD_TEXT);
            EURtext = getIntent().getStringExtra(EUR_TEXT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        manageRecyclerView();
        if (getIntent() != null && getIntent().getExtras() != null) {
            USBtext = getIntent().getStringExtra(USD_TEXT);
            EURtext = getIntent().getStringExtra(EUR_TEXT);
        }

    }

    // Обработка нажатия кнопки в меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add:
                addElement();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // Обработка нажатия кнопкок в BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_transaction: //переход к операциям
                    Intent trActivity = new Intent(getApplicationContext(), TransactionsActivity.class);

                    trActivity.putExtra(USD_TEXT, USBtext);
                    trActivity.putExtra(EUR_TEXT, EURtext);


                    startActivity(trActivity);
                    return true;
                case R.id.navigation_settings: //переход к настройкам
                    Intent setActivity = new Intent(getApplicationContext(), SettingsActivity.class);

                    setActivity.putExtra(USD_TEXT, USBtext);
                    setActivity.putExtra(EUR_TEXT, EURtext);



                    startActivity(setActivity);
                    return true;
            }
            return true;
        }
    };

    //отрисовка RecyclerView кошельков
    public void manageRecyclerView() {
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        contacts = databaseHelper.getAllPosts();
        ContactsAdapter adapter = new ContactsAdapter(contacts);
        rvContacts.setAdapter(adapter);
        adapter.setClickListener(this);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    //обработка нажатий кнопок из  RecyclerView кошельков
    public void onClick(View view, int position) {
        if (view.getId() == R.id.Delete_button) {
            deleteElement(position);
        }
        if (view.getId() == R.id.info_button) {
            infoElement(position);
        }
    }

    //отображение информации о операциях над определенным кошельком
    private void infoElement(int position) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        contacts = databaseHelper.getAllPosts();
        Contact contact = contacts.get(position);

        Intent trActivity = new Intent(getApplicationContext(), TransactionsInfoActivity.class);
        trActivity.putExtra(INFO, contact.getName());

        trActivity.putExtra(USD, USBtext);
        trActivity.putExtra(EUR, EURtext);

        startActivity(trActivity);
    }

    //удаление кошелька
    private void deleteElement(int position) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        contacts = databaseHelper.getAllPosts();
        Contact contact = contacts.get(position);

        if (Double.parseDouble(contact.getSum()) > 0) {
            errDel();
        } else {
            databaseHelper.delPost(contact.getId());
            for (int i = 0; i < contacts.size(); i++) {

                Contact contact1 = contacts.get(i);
                if (contact.getId() < contact1.getId()) {
                    contact1.setId(contact1.getId() - 1);
                    databaseHelper.updateVal(contact1,1);
                }
            }
            manageRecyclerView();
        }
    }

    //обработка ошибки удаления кошелька
    private void errDel() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.error);  // заголовок
        ad.setMessage(R.string.have_money); // сообщение
        ad.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.setCancelable(false);
        ad.show();
    }

    //добавление кошелька
    public void addElement() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_accounts, null);

        // создание диалога
        AlertDialog.Builder ad = new AlertDialog.Builder(this);

        ad.setView(promptsView);
        final EditText userInput = promptsView.findViewById(R.id.input_text);
        ad.setTitle(R.string.create_a_wallet);

        ad.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                if (userInput.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Кошелек не создан. Вы не ввели имя кошелька", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
                    Contact samplePost = new Contact();

                    samplePost.setName(userInput.getText().toString());
                    samplePost.setSum("0");
                    databaseHelper.addPost(samplePost);
                    manageRecyclerView();
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

    // инициализация оператора запроса
    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openRate = retrofit.create(OpenRate.class);

    }

    //делаем запрос курса валют
    private void requestRetrofit(String city) {

        openRate.loadWeather(city)
                .enqueue(new Callback<RateRequest>() {
                    @Override
                    public void onResponse(@NonNull Call<RateRequest> call,
                                           @NonNull Response<RateRequest> response) {
                        String info;

                        if (response.body() != null) {
                            info = Double.toString(response.body().getValutes().getValueUSD().getValue());
                            USBtext = info;
                            info = Double.toString(response.body().getValutes().getValueEUR().getValue());
                            EURtext = info;

                        }


                    }

                    @Override
                    public void onFailure(@NonNull Call<RateRequest> call,
                                          @NonNull Throwable throwable) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Ошибка загрузки курса", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

    }


}
