import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

/**
 * Smart Library Management System (Console-Based)
 *
 * Single-file Java program using CSV files for persistence.
 * - Roles: Admin, Student
 * - Admin: add/update/delete books, view issued books
 * - Student: search, issue, return, view history
 * - Max 3 concurrently issued books per student
 * - Files: books.csv, users.csv, issues.csv (auto-created on first run)
 *
 * Compile:   javac LibraryApp.java
 * Run:       java LibraryApp
 */
public class LibraryApp {

    // ====== CONFIG ======
    private static final String DATA_DIR = "C:\\Users\\hp\\OneDrive\\Desktop\\data"; // all files will live here
    private static final String BOOKS_FILE = DATA_DIR + File.separator + "books.csv";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.csv";
    private static final String ISSUES_FILE = DATA_DIR + File.separator + "issues.csv";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    // ====== DOMAIN MODELS ======
    static class Book {
        String bookId;
        String title;
        String author;
        String category;
        boolean available; // true if not currently issued

        Book(String bookId, String title, String author, String category, boolean available) {
            this.bookId = bookId.trim();
            this.title = title.trim();
            this.author = author.trim();
            this.category = category.trim();
            this.available = available;
        }

        static Book fromCsv(String line) {
            // bookId,title,author,category,available
            String[] p = safeSplit(line);
            if (p.length < 5) throw new IllegalArgumentException("Malformed book csv: " + line);
            return new Book(p[0], p[1], p[2], p[3], Boolean.parseBoolean(p[4]));
        }

        String toCsv() {
            return String.join(",",
                    esc(bookId), esc(title), esc(author), esc(category), String.valueOf(available)
            );
        }

        @Override public String toString() {
            return String.format("[%s] %s — %s (%s) | %s", bookId, title, author, category, available?"Available":"Issued");
        }
    }

    enum Role { ADMIN, STUDENT }

    static class User {
        String username;
        String password; // plaintext for demo simplicity (consider hashing in real apps)
        Role role;

        User(String username, String password, Role role) {
            this.username = username.trim();
            this.password = password;
            this.role = role;
        }

        static User fromCsv(String line) {
            // username,password,role
            String[] p = safeSplit(line);
            if (p.length < 3) throw new IllegalArgumentException("Malformed user csv: " + line);
            return new User(p[0], p[1], Role.valueOf(p[2]));
        }

        String toCsv() {
            return String.join(",", esc(username), esc(password), role.name());
        }
    }

    static class IssueRecord {
        String bookId;
        String username; // student username
        LocalDate issueDate;
        LocalDate returnDate; // null if not returned yet

        IssueRecord(String bookId, String username, LocalDate issueDate, LocalDate returnDate) {
            this.bookId = bookId;
            this.username = username;
            this.issueDate = issueDate;
            this.returnDate = returnDate;
        }

        static IssueRecord fromCsv(String line) {
            // bookId,username,issueDate,returnDate
            String[] p = safeSplit(line);
            if (p.length < 4) throw new IllegalArgumentException("Malformed issue csv: " + line);
            LocalDate issued = LocalDate.parse(p[2]);
            LocalDate returned = p[3].isBlank() ? null : LocalDate.parse(p[3]);
            return new IssueRecord(p[0], p[1], issued, returned);
        }

        String toCsv() {
            return String.join(",",
                    esc(bookId), esc(username), issueDate.format(DATE_FMT),
                    returnDate == null ? "" : returnDate.format(DATE_FMT)
            );
        }

        @Override public String toString() {
            return String.format("Book %s -> %s | Issued: %s | Returned: %s",
                    bookId, username, issueDate, returnDate == null ? "-" : returnDate);
        }
    }

    // ====== REPOSITORIES (CSV-backed) ======
    static class BookRepo {
        Map<String, Book> books = new LinkedHashMap<>();

        void load() throws IOException {
            ensureFile(BOOKS_FILE, "bookId,title,author,category,available\n");
            List<String> lines = Files.readAllLines(Path.of(BOOKS_FILE));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                Book b = Book.fromCsv(line);
                books.put(b.bookId, b);
            }
        }

        void save() throws IOException {
            String header = "bookId,title,author,category,available\n";
            String body = books.values().stream().map(Book::toCsv).collect(Collectors.joining("\n"));
            Files.writeString(Path.of(BOOKS_FILE), header + body + (body.isEmpty()?"":"\n"));
        }

        boolean add(Book b) { return books.putIfAbsent(b.bookId, b) == null; }
        Book get(String id) { return books.get(id); }
        boolean remove(String id) { return books.remove(id) != null; }
        Collection<Book> all() { return books.values(); }
    }

    static class UserRepo {
        Map<String, User> users = new LinkedHashMap<>();

        void load() throws IOException {
            ensureFile(USERS_FILE, "username,password,role\n");
            List<String> lines = Files.readAllLines(Path.of(USERS_FILE));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                User u = User.fromCsv(line);
                users.put(u.username, u);
            }
            // bootstrap default admin if absent
            if (!users.values().stream().anyMatch(u -> u.role == Role.ADMIN)) {
                User admin = new User("admin", "admin123", Role.ADMIN);
                users.put(admin.username, admin);
                save();
                System.out.println("[Setup] Default admin created -> username: admin, password: admin123");
            }
        }

        void save() throws IOException {
            String header = "username,password,role\n";
            String body = users.values().stream().map(User::toCsv).collect(Collectors.joining("\n"));
            Files.writeString(Path.of(USERS_FILE), header + body + (body.isEmpty()?"":"\n"));
        }

        User get(String username) { return users.get(username); }
        boolean add(User u) { return users.putIfAbsent(u.username, u) == null; }
    }

    static class IssueRepo {
        List<IssueRecord> records = new ArrayList<>();

        void load() throws IOException {
            ensureFile(ISSUES_FILE, "bookId,username,issueDate,returnDate\n");
            List<String> lines = Files.readAllLines(Path.of(ISSUES_FILE));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                records.add(IssueRecord.fromCsv(line));
            }
        }

        void save() throws IOException {
            String header = "bookId,username,issueDate,returnDate\n";
            String body = records.stream().map(IssueRecord::toCsv).collect(Collectors.joining("\n"));
            Files.writeString(Path.of(ISSUES_FILE), header + body + (body.isEmpty()?"":"\n"));
        }

        List<IssueRecord> byUser(String username) {
            return records.stream().filter(r -> r.username.equals(username)).collect(Collectors.toList());
        }

        Optional<IssueRecord> activeIssue(String bookId) {
            return records.stream().filter(r -> r.bookId.equals(bookId) && r.returnDate == null).findFirst();
        }

        long activeCountByUser(String username) {
            return records.stream().filter(r -> r.username.equals(username) && r.returnDate == null).count();
        }

        List<IssueRecord> activeAll() {
            return records.stream().filter(r -> r.returnDate == null).collect(Collectors.toList());
        }
    }

    // ====== LIBRARY SERVICE ======
    static class LibraryService {
        private final BookRepo bookRepo;
        private final UserRepo userRepo;
        private final IssueRepo issueRepo;

        LibraryService(BookRepo b, UserRepo u, IssueRepo i) {
            this.bookRepo = b; this.userRepo = u; this.issueRepo = i;
        }

        // --- Admin ops ---
        boolean addBook(String id, String title, String author, String category) throws IOException {
            if (bookRepo.get(id) != null) return false;
            bookRepo.add(new Book(id, title, author, category, true));
            bookRepo.save();
            return true;
        }

        boolean updateBook(String id, String title, String author, String category, Boolean available) throws IOException {
            Book b = bookRepo.get(id);
            if (b == null) return false;
            if (title != null) b.title = title;
            if (author != null) b.author = author;
            if (category != null) b.category = category;
            if (available != null) b.available = available;
            bookRepo.save();
            return true;
        }

        boolean deleteBook(String id) throws IOException {
            Optional<IssueRecord> active = issueRepo.activeIssue(id);
            if (active.isPresent()) return false; // cannot delete active issued
            boolean ok = bookRepo.remove(id);
            if (ok) bookRepo.save();
            return ok;
        }

        List<IssueRecord> viewAllIssued() { return issueRepo.activeAll(); }

        // --- Student ops ---
        List<Book> search(String q, String field) {
            String needle = q.toLowerCase();
            Stream<Book> s = bookRepo.all().stream();
            switch (field.toLowerCase()) {
                case "title":
                    s = s.filter(b -> b.title.toLowerCase().contains(needle)); break;
                case "author":
                    s = s.filter(b -> b.author.toLowerCase().contains(needle)); break;
                case "category":
                    s = s.filter(b -> b.category.toLowerCase().contains(needle)); break;
                default:
                    // search all fields
                    s = s.filter(b -> b.title.toLowerCase().contains(needle)
                            || b.author.toLowerCase().contains(needle)
                            || b.category.toLowerCase().contains(needle));
            }
            return s.collect(Collectors.toList());
        }

        String issueBook(String bookId, String username) throws IOException {
            Book b = bookRepo.get(bookId);
            if (b == null) return "Book not found";
            if (!b.available) return "Book is already issued";
            long active = issueRepo.activeCountByUser(username);
            if (active >= 3) return "You have reached the maximum of 3 active books";
            b.available = false;
            issueRepo.records.add(new IssueRecord(bookId, username, LocalDate.now(), null));
            issueRepo.save();
            bookRepo.save();
            return "Issued successfully";
        }

        String returnBook(String bookId, String username) throws IOException {
            Optional<IssueRecord> rec = issueRepo.records.stream()
                    .filter(r -> r.bookId.equals(bookId) && r.username.equals(username) && r.returnDate == null)
                    .findFirst();
            if (rec.isEmpty()) return "No active issue found for this book and user";

            rec.get().returnDate = LocalDate.now();
            Book b = bookRepo.get(bookId);
            if (b != null) b.available = true;
            issueRepo.save();
            bookRepo.save();
            return "Returned successfully";
        }

        List<IssueRecord> history(String username) {
            return issueRepo.byUser(username);
        }

        // --- Auth ---
        boolean registerStudent(String username, String password) throws IOException {
            if (userRepo.get(username) != null) return false;
            userRepo.add(new User(username, password, Role.STUDENT));
            userRepo.save();
            return true;
        }

        User login(String username, String password) {
            User u = userRepo.get(username);
            return (u != null && Objects.equals(u.password, password)) ? u : null;
        }
    }

    // ====== UI (Console) ======
    static class ConsoleUI {
        private final Scanner in = new Scanner(System.in);
        private final LibraryService service;

        ConsoleUI(LibraryService s) { this.service = s; }

        void start() throws IOException {
            while (true) {
                System.out.println("\n=== Smart Library Management System ===");
                System.out.println("1) Login");
                System.out.println("2) Register (Student)");
                System.out.println("0) Exit");
                System.out.print("Choose: ");
                String choice = in.nextLine().trim();
                switch (choice) {
                    case "1": handleLogin(); break;
                    case "2": handleRegister(); break;
                    case "0": System.out.println("Bye!"); return;
                    default: System.out.println("Invalid option");
                }
            }
        }

        private void handleRegister() throws IOException {
            System.out.print("Choose username: ");
            String u = in.nextLine().trim();
            System.out.print("Choose password: ");
            String p = in.nextLine().trim();
            boolean ok = service.registerStudent(u, p);
            System.out.println(ok ? "Registered successfully. You can login now." : "Username already exists.");
        }

        private void handleLogin() throws IOException {
            System.out.print("Username: ");
            String u = in.nextLine().trim();
            System.out.print("Password: ");
            String p = in.nextLine().trim();
            User user = service.login(u, p);
            if (user == null) {
                System.out.println("Login failed.");
                return;
            }
            if (user.role == Role.ADMIN) adminMenu(user);
            else studentMenu(user);
        }

        private void adminMenu(User admin) throws IOException {
            while (true) {
                System.out.println("\n--- Admin Menu ---");
                System.out.println("1) Add Book");
                System.out.println("2) Update Book");
                System.out.println("3) Delete Book");
                System.out.println("4) List All Books");
                System.out.println("5) View All Issued Books");
                System.out.println("0) Logout");
                System.out.print("Choose: ");
                String c = in.nextLine().trim();
                switch (c) {
                    case "1": addBook(); break;
                    case "2": updateBook(); break;
                    case "3": deleteBook(); break;
                    case "4": listBooks(); break;
                    case "5": viewIssued(); break;
                    case "0": return;
                    default: System.out.println("Invalid option");
                }
            }
        }

        private void studentMenu(User student) throws IOException {
            while (true) {
                System.out.println("\n--- Student Menu ---");
                System.out.println("1) Search Books");
                System.out.println("2) Issue Book");
                System.out.println("3) Return Book");
                System.out.println("4) View My History");
                System.out.println("0) Logout");
                System.out.print("Choose: ");
                String c = in.nextLine().trim();
                switch (c) {
                    case "1": searchBooks(); break;
                    case "2": issueBook(student.username); break;
                    case "3": returnBook(student.username); break;
                    case "4": viewHistory(student.username); break;
                    case "0": return;
                    default: System.out.println("Invalid option");
                }
            }
        }

        private void addBook() throws IOException {
            System.out.print("Book ID: ");
            String id = in.nextLine().trim();
            System.out.print("Title: ");
            String title = in.nextLine().trim();
            System.out.print("Author: ");
            String author = in.nextLine().trim();
            System.out.print("Category: ");
            String category = in.nextLine().trim();
            boolean ok = service.addBook(id, title, author, category);
            System.out.println(ok ? "Added." : "A book with that ID already exists.");
        }

        private void updateBook() throws IOException {
            System.out.print("Book ID to update: ");
            String id = in.nextLine().trim();
            System.out.print("New Title (leave blank to keep): ");
            String title = blankToNull(in.nextLine());
            System.out.print("New Author (leave blank to keep): ");
            String author = blankToNull(in.nextLine());
            System.out.print("New Category (leave blank to keep): ");
            String category = blankToNull(in.nextLine());
            System.out.print("Force Availability? (blank keep / true / false): ");
            String availStr = in.nextLine().trim();
            Boolean available = availStr.isBlank() ? null : Boolean.parseBoolean(availStr);
            boolean ok = service.updateBook(id, title, author, category, available);
            System.out.println(ok ? "Updated." : "Book not found.");
        }

        private void deleteBook() throws IOException {
            System.out.print("Book ID to delete: ");
            String id = in.nextLine().trim();
            boolean ok = service.deleteBook(id);
            System.out.println(ok ? "Deleted." : "Cannot delete (book not found or currently issued)." );
        }

        private void listBooks() {
            System.out.println("\nAll Books:");
            service.bookRepo.all().forEach(System.out::println);
        }

        private void viewIssued() {
            System.out.println("\nActive Issues:");
            List<IssueRecord> list = service.viewAllIssued();
            if (list.isEmpty()) System.out.println("(none)");
            else list.forEach(System.out::println);
        }

        private void searchBooks() {
            System.out.print("Search by (title/author/category/all): ");
            String field = in.nextLine().trim();
            System.out.print("Query: ");
            String q = in.nextLine().trim();
            List<Book> results = service.search(q, field);
            if (results.isEmpty()) System.out.println("No results.");
            else results.forEach(System.out::println);
        }

        private void issueBook(String username) throws IOException {
            System.out.print("Book ID to issue: ");
            String id = in.nextLine().trim();
            String msg = service.issueBook(id, username);
            System.out.println(msg);
        }

        private void returnBook(String username) throws IOException {
            System.out.print("Book ID to return: ");
            String id = in.nextLine().trim();
            String msg = service.returnBook(id, username);
            System.out.println(msg);
        }

        private void viewHistory(String username) {
            System.out.println("\nYour History:");
            List<IssueRecord> history = service.history(username);
            if (history.isEmpty()) System.out.println("(none)");
            else history.forEach(System.out::println);
        }

        private static String blankToNull(String s) {
            String t = s == null ? "" : s.trim();
            return t.isEmpty() ? null : t;
        }
    }

    // ====== UTIL ======
    private static void ensureFile(String path, String header) throws IOException {
        Path dir = Path.of(DATA_DIR);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        Path p = Path.of(path);
        if (!Files.exists(p)) Files.writeString(p, header);
    }

    private static String esc(String s) {
        String v = s == null ? "" : s;
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            v = v.replace("\"", "\"\"");
            v = "\"" + v + "\"";
        }
        return v;
    }

    private static String[] safeSplit(String line) {
        // CSV split supporting quotes
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"'); i++; // escaped quote
                } else { inQuotes = !inQuotes; }
            } else if (c == ',' && !inQuotes) {
                out.add(sb.toString()); sb.setLength(0);
            } else { sb.append(c); }
        }
        out.add(sb.toString());
        return out.toArray(new String[0]);
    }

    // ====== MAIN ======
    public static void main(String[] args) {
        try {
            BookRepo b = new BookRepo(); b.load();
            UserRepo u = new UserRepo(); u.load();
            IssueRepo i = new IssueRepo(); i.load();
            LibraryService service = new LibraryService(b, u, i);
            new ConsoleUI(service).start();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
