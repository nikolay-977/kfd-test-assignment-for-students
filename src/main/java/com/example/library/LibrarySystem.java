package com.example.library;

import java.time.LocalDate;
import java.util.*;

public class LibrarySystem {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();
    private final List<String> borrowingHistory = new ArrayList<>();

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n--- Система управления библиотекой ---");
            System.out.println("1. Добавить книгу");
            System.out.println("2. Удалить книгу");
            System.out.println("3. Зарегистрировать пользователя");
            System.out.println("4. Взять книгу");
            System.out.println("5. Вернуть книгу");
            System.out.println("6. Поиск книг");
            System.out.println("7. Просмотр взятых книг");
            System.out.println("8. Просмотр просроченных книг");
            System.out.println("9. Выход");
            int choice = readInt("Выберите пункт меню: ", 1, 9);

            switch (choice) {
                case 1: addBook(); break;
                case 2: removeBook(); break;
                case 3: registerUser (); break;
                case 4: borrowBook(); break;
                case 5: returnBook(); break;
                case 6: searchBooks(); break;
                case 7: viewUsersBooks(); break;
                case 8: viewOverdueBooks(); break;
                case 9:
                    System.out.println("Выход из программы...");
                    return;
                default:
                    System.out.println("Неверный пункт меню, попробуйте снова.");
            }
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.printf("Введите число от %d до %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Пожалуйста, введите число.");
            }
        }
    }

    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Ввод не может быть пустым. Попробуйте снова.");
            } else {
                return input;
            }
        }
    }

    private void addBook() {
        String isbn = readNonEmptyString("Введите ISBN: ");
        if (books.containsKey(isbn)) {
            System.out.println("Книга с таким ISBN уже существует.");
            return;
        }
        String title = readNonEmptyString("Введите название книги: ");
        String author = readNonEmptyString("Введите автора книги: ");
        int copies = readInt("Введите количество копий: ", 1, Integer.MAX_VALUE);

        books.put(isbn, new Book(isbn, title, author, copies));
        System.out.println("Книга успешно добавлена.");
    }

    private void removeBook() {
        String isbn = readNonEmptyString("Введите ISBN книги для удаления: ");
        Book removed = books.remove(isbn);
        if (removed != null) {
            System.out.println("Книга удалена.");
        } else {
            System.out.println("Книга с таким ISBN не найдена.");
        }
    }

    private void registerUser () {
        String id = readNonEmptyString("Введите ID пользователя: ");
        if (users.containsKey(id)) {
            System.out.println("Пользователь с таким ID уже существует.");
            return;
        }
        String name = readNonEmptyString("Введите имя пользователя: ");
        System.out.println("Выберите тип пользователя: 1 - Студент, 2 - Преподаватель, 3 - Гость");
        int type = readInt("Введите номер типа пользователя: ", 1, 3);

        User user;
        switch (type) {
            case 1: user = new Student(id, name); break;
            case 2: user = new Faculty(id, name); break;
            case 3: user = new Guest(id, name); break;
            default:
                System.out.println("Неверный тип пользователя.");
                return;
        }
        users.put(id, user);
        System.out.println("Пользователь успешно зарегистрирован.");
    }

    private void borrowBook() {
        String userId = readNonEmptyString("Введите ID пользователя: ");
        User user = users.get(userId);
        if (user == null) {
            System.out.println("Пользователь с таким ID не найден.");
            return;
        }
        String isbn = readNonEmptyString("Введите ISBN книги для взятия: ");
        Book book = books.get(isbn);
        if (book == null) {
            System.out.println("Книга с таким ISBN не найдена.");
            return;
        }
        if (book.getAvailableCopies() <= 0) {
            System.out.println("Нет доступных копий книги.");
            return;
        }
        if (user.isBorrowed(book)) {
            System.out.println("Пользователь уже взял эту книгу");
            return;
        }
        if (!user.canBorrow()) {
            System.out.println("Пользователь достиг лимита по количеству взятых книг.");
            return;
        }
        LocalDate today = LocalDate.now();
        if (user.borrowBook(book, today)) {
            book.decreaseCopies();
            borrowingHistory.add(String.format("%s: Пользователь %s взял книгу %s", today, user.getId(), book.getIsbn()));
            System.out.printf("Книга успешно выдана. Вернуть до %s%n", today.plusDays(user.getBorrowDays()));
        } else {
            System.out.println("Не удалось выдать книгу.");
        }
    }

    private void returnBook() {
        String userId = readNonEmptyString("Введите ID пользователя: ");
        User user = users.get(userId);
        if (user == null) {
            System.out.println("Пользователь с таким ID не найден.");
            return;
        }
        String isbn = readNonEmptyString("Введите ISBN книги для возврата: ");
        Book book = books.get(isbn);
        if (book == null) {
            System.out.println("Книга с таким ISBN не найдена.");
            return;
        }
        if (user.returnBook(book)) {
            book.increaseCopies(1);
            LocalDate today = LocalDate.now();
            borrowingHistory.add(String.format("%s: Пользователь %s вернул книгу %s", today, user.getId(), book.getIsbn()));
            System.out.println("Книга успешно возвращена.");
        } else {
            System.out.println("Этот пользователь не брал эту книгу.");
        }
    }

    private void searchBooks() {
        System.out.println("Поиск по: 1 - Названию, 2 - Автору, 3 - ISBN");
        int option = readInt("Выберите опцию: ", 1, 3);
        List<Book> results = new ArrayList<>();
        switch (option) {
            case 1:
                String titleKey = readNonEmptyString("Введите ключевое слово в названии: ").toLowerCase();
                for (Book b : books.values()) {
                    if (b.getTitle().toLowerCase().contains(titleKey)) {
                        results.add(b);
                    }
                }
                break;
            case 2:
                String authorKey = readNonEmptyString("Введите ключевое слово в авторе: ").toLowerCase();
                for (Book b : books.values()) {
                    if (b.getAuthor().toLowerCase().contains(authorKey)) {
                        results.add(b);
                    }
                }
                break;
            case 3:
                String isbn = readNonEmptyString("Введите ISBN: ");
                Book b = books.get(isbn);
                if (b != null) results.add(b);
                break;
            default:
                System.out.println("Неверная опция.");
                return;
        }
        if (results.isEmpty()) {
            System.out.println("Книги не найдены.");
        } else {
            System.out.println("Найденные книги:");
            for (Book book : results) {
                System.out.println(book);
            }
        }
    }

    private void viewUsersBooks() {
        boolean found = false;
        for (User  user : users.values()) {
            Map<Book, LocalDate> borrowedBooks = user.getBorrowedBooks();
            if (!borrowedBooks.isEmpty()) {
                found = true;
                System.out.printf("Пользователь %s (%s) взял книги:%n", user.getId(), user.getName());
                for (Map.Entry<Book, LocalDate>  entry: borrowedBooks.entrySet()) {
                    System.out.printf(" - %s (ISBN: %s)%n", entry.getKey().getTitle(), entry.getKey().getIsbn());
                    System.out.printf(" - Вернуть до %s%n", entry.getValue());
                }
            }
        }
        if (!found) {
            System.out.println("Взятых книг не найдено.");
        }
    }

    private void viewOverdueBooks() {
        LocalDate today = LocalDate.now();
        boolean found = false;
        for (User  user : users.values()) {
            List<Book> overdue = user.getOverdueBooks(today);
            if (!overdue.isEmpty()) {
                found = true;
                System.out.printf("Пользователь %s (%s) имеет просроченные книги:%n", user.getId(), user.getName());
                for (Book book : overdue) {
                    System.out.printf(" - %s (ISBN: %s)%n", book.getTitle(), book.getIsbn());
                }
            }
        }
        if (!found) {
            System.out.println("Просроченных книг не найдено.");
        }
    }

    public static void main(String[] args) {
        new LibrarySystem().run();
    }
}
