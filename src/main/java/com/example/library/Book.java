package com.example.library;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private int availableCopies;

    public Book(String isbn, String title, String author, int availableCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.availableCopies = availableCopies;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getAvailableCopies() { return availableCopies; }

    public void increaseCopies(int count) { availableCopies += count; }
    public boolean decreaseCopies() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ISBN: %s, Название: %s, Автор: %s, Доступно: %d",
                isbn, title, author, availableCopies);
    }
}

