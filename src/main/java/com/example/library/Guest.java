package com.example.library;

public class Guest extends User {
    public Guest(String id, String name) {
        super(id, name);
    }

    @Override
    public int getMaxBooks() {
        return 1;
    }

    @Override
    public int getBorrowDays() {
        return 7;
    }
}