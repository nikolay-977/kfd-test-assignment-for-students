package com.example.library;

public class Student extends User {
    public Student(String id, String name) {
        super(id, name);
    }

    @Override
    public int getMaxBooks() {
        return 3;
    }

    @Override
    public int getBorrowDays() {
        return 14;
    }
}