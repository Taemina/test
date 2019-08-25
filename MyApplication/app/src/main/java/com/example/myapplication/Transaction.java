package com.example.myapplication;
// класс для операций
public class Transaction {

    private String NameWallet;
    private String sum;
    private String type;
    private String date;
    private String currency;
    private String comment;

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getComment() {
        return comment;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDate() {
        return date;
    }

    public String getNameWallet() {
        return NameWallet;
    }

    public String getSum() {
        return sum;
    }

    public String getType() {
        return type;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNameWallet(String nameWallet) {
        NameWallet = nameWallet;
    }

    public void setType(String type) {
        this.type = type;
    }
}
