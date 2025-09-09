package com.example.library;

import java.time.LocalDate;
import java.util.*;

public abstract class User {
    private final String id;
    private final String name;
    private final Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract int getMaxBooks();
    public abstract int getBorrowDays();

    public String getId() { return id; }
    public String getName() { return name; }

    public boolean canBorrow() {
        return borrowedBooks.size() < getMaxBooks();
    }

    public boolean isBorrowed(Book book) {
        return borrowedBooks.containsKey(book);
    }

    public boolean borrowBook(Book book, LocalDate borrowDate) {
        if (isBorrowed(book) || !canBorrow()) return false;
        borrowedBooks.put(book, borrowDate.plusDays(getBorrowDays()));
        return true;
    }

    public boolean returnBook(Book book) {
        return borrowedBooks.remove(book) != null;
    }

    public List<Book> getOverdueBooks(LocalDate today) {
        List<Book> overdue = new ArrayList<>();
        for (Map.Entry<Book, LocalDate> entry : borrowedBooks.entrySet()) {
            if (entry.getValue().isBefore(today)) {
                overdue.add(entry.getKey());
            }
        }
        return overdue;
    }

    public Map<Book, LocalDate> getBorrowedBooks() {
        return Collections.unmodifiableMap(borrowedBooks);
    }

    @Override
    public String toString() {
        return String.format("Пользователь ID: %s, Имя: %s, Тип: %s", id, name, this.getClass().getSimpleName());
    }
}