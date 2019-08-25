package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
//адаптер для заполнения RecyclerView операций
public class TransactionAdapter extends
        RecyclerView.Adapter<TransactionAdapter.ViewHolder> {


    private ArrayList<Transaction> mTrans; //список операций
    private String USBtext;
    private String EURtext;
    public TransactionAdapter(ArrayList<Transaction> trans,String USB,String EUR) {
        mTrans = trans;
        USBtext=USB;
        EURtext=EUR;
    }


    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_trans, parent, false);

        TransactionAdapter.ViewHolder viewHolder = new TransactionAdapter.ViewHolder(contactView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(TransactionAdapter.ViewHolder viewHolder, final int position) {

        Transaction trans = mTrans.get(position);

        TextView textView1 = viewHolder.TextView1;
        TextView textView2 = viewHolder.TextView2;
        TextView textView3 = viewHolder.TextView3;

        double rate_add = 1;
        if (trans.getCurrency().equals("USD")) {
            rate_add = Double.parseDouble(USBtext);

        } else if (trans.getCurrency().equals("EUR")) {
            rate_add = Double.parseDouble(EURtext);
        }
        rate_add = new BigDecimal(rate_add).setScale(3, RoundingMode.UP).doubleValue();

        String s = "Кошелек: " + trans.getNameWallet() + " Дата: " + trans.getDate();
        textView1.setText(s);
        rate_add=rate_add*Double.parseDouble(trans.getSum());
        s = "Тип: " + trans.getType() + " : " + trans.getSum() + " " + trans.getCurrency()+ " "+ rate_add + " RUS";
        textView2.setText(s);
        s = "Комментарий: " + trans.getComment();
        textView3.setText(s);

    }


    @Override
    public int getItemCount() {
        return mTrans.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView TextView1;
        public TextView TextView2;
        public TextView TextView3;


        public ViewHolder(View itemView) {

            super(itemView);

            TextView1 = (TextView) itemView.findViewById(R.id.tv_1);
            TextView2 = (TextView) itemView.findViewById(R.id.tv_2);
            TextView3 = (TextView) itemView.findViewById(R.id.tv_3);


        }


    }


}
