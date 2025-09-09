package com.example.library;

public class Faculty extends User {
    public Faculty(String id, String name) {
        super(id, name);
    }

    @Override
    public int getMaxBooks() {
        return 10;
    }

    @Override
    public int getBorrowDays() {
        return 30;
    }
}