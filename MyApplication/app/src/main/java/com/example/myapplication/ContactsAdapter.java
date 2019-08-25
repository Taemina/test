package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//адаптер для заполнения RecyclerView кошельками
public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> {


    private ArrayList<Contact> mContacts; //список кошельков
    private OnItemClickListener clickListener; // слушатель

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
//конструктор адаптера
    public ContactsAdapter(ArrayList<Contact> contacts) {
        mContacts = contacts;
    }


    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder viewHolder, final int position) {

        Contact contact = mContacts.get(position);
        TextView textView = viewHolder.nameTextView;
        TextView textView1 = viewHolder.sumTextView;
        String s = "№" + contact.getId() + " Имя кошелька: " + contact.getName();
        textView.setText(s);
        s = "Сумма: " + contact.getSum();
        textView1.setText(s);


    }


    @Override
    public int getItemCount() {
        return mContacts.size();
    }

// класс шаблона
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTextView;
        public TextView sumTextView;
        public Button deleteButton;
        public Button infoButton;

        public ViewHolder(View itemView) {

            super(itemView);
            //инициализация
            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            sumTextView = (TextView) itemView.findViewById(R.id.sum_text);
            deleteButton = (Button) itemView.findViewById(R.id.Delete_button);
            infoButton = (Button) itemView.findViewById(R.id.info_button);

            //добавление слушателей
            deleteButton.setOnClickListener(this);
            infoButton.setOnClickListener(this);

        }
// обработка нажатия на элементы
        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }

    }


}
