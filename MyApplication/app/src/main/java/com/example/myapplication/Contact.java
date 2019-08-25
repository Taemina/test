package com.example.myapplication;

// класс для кошельков
public class Contact {
    private int Id;
    private String Name;
    private String Sum;

    public String getName() {
        return Name;
    }

    public String getSum() {
        return Sum;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setSum(String sum) {
        Sum = sum;
    }

}