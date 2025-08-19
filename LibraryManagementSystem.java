import java.io.*;
import java.util.*;

// Book class
class Book implements Serializable {
    private int bookId;
    private String title;
    private String author;
    private int year;
    private boolean isIssued;
    private String issuedTo;
    private Date dueDate;

    public Book(int bookId, String title, String author, int year) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.year = year;
        this.isIssued = false;
        this.issuedTo = null;
        this.dueDate = null;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public boolean isIssued() { return isIssued; }
    public String getIssuedTo() { return issuedTo; }
    public Date getDueDate() { return dueDate; }

    public void issueBook(String memberId, Date dueDate) {
        this.isIssued = true;
        this.issuedTo = memberId;
        this.dueDate = dueDate;
    }

    public void returnBook() {
        this.isIssued = false;
        this.issuedTo = null;
        this.dueDate = null;
    }

    @Override
    public String toString() {
        return String.format("%-5d %-25s %-20s %-6d %-10s %-10s",
                bookId, title, author, year,
                (isIssued ? "Yes" : "No"),
                (issuedTo == null ? "-" : issuedTo));
    }
}

// Member class (Inheritance Example)
class Member {
    protected String memberId;
    protected String name;

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
}

// Library class
class Library {
    private ArrayList<Book> books = new ArrayList<>();
    private static final String FILE_NAME = "C:\\Users\\hp\\OneDrive\\Desktop\\library.dat";

    // Add Book
    public void addBook(Book book) throws Exception {
        for (Book b : books) {
            if (b.getBookId() == book.getBookId()) {
                throw new Exception("Duplicate Book ID!");
            }
        }
        books.add(book);
        System.out.println("Book added successfully.");
    }

    // View All Books
    public void viewAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        System.out.printf("%-5s %-25s %-20s %-6s %-10s %-10s\n",
                "ID", "Title", "Author", "Year", "Issued", "IssuedTo");
        for (Book b : books) {
            System.out.println(b);
        }
    }

    // Search Book
    public void searchBook(String keyword) {
        boolean found = false;
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(b);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Book not found!");
        }
    }

    // Issue Book
    public void issueBook(int bookId, String memberId) throws Exception {
        for (Book b : books) {
            if (b.getBookId() == bookId) {
                if (b.isIssued()) throw new Exception("Book already issued!");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 7); // due in 7 days
                b.issueBook(memberId, cal.getTime());
                System.out.println("Book issued successfully. Due Date: " + cal.getTime());
                return;
            }
        }
        throw new Exception("Book not found!");
    }

    // Return Book
    public void returnBook(int bookId) throws Exception {
        for (Book b : books) {
            if (b.getBookId() == bookId) {
                if (!b.isIssued()) throw new Exception("Book was not issued!");
                Date today = new Date();
                long diff = today.getTime() - b.getDueDate().getTime();
                if (diff > 0) {
                    long daysLate = diff / (1000 * 60 * 60 * 24);
                    int fine = (int) daysLate * 10; // Rs.10 per late day
                    System.out.println("Book returned late. Fine = Rs." + fine);
                } else {
                    System.out.println("Book returned on time.");
                }
                b.returnBook();
                return;
            }
        }
        throw new Exception("Book not found!");
    }

    // Delete Book
    public void deleteBook(int bookId) throws Exception {
        Iterator<Book> it = books.iterator();
        while (it.hasNext()) {
            Book b = it.next();
            if (b.getBookId() == bookId) {
                it.remove();
                System.out.println("Book deleted successfully.");
                return;
            }
        }
        throw new Exception("Book not found!");
    }

    // Save books to file
    public void saveToFile() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
        oos.writeObject(books);
        oos.close();
    }

    // Load books from file
    public void loadFromFile() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            books = (ArrayList<Book>) ois.readObject();
            ois.close();
        }
    }
}

// Main App
public class LibraryManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library lib = new Library();

        try {
            lib.loadFromFile();
        } catch (Exception e) {
            System.out.println("No previous records found.");
        }

        while (true) {
            System.out.println("\n===== Library Menu =====");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. Delete Book");
            System.out.println("7. Save & Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter Book ID: ");
                        int id = sc.nextInt(); sc.nextLine();
                        System.out.print("Enter Title: ");
                        String title = sc.nextLine();
                        System.out.print("Enter Author: ");
                        String author = sc.nextLine();
                        System.out.print("Enter Year: ");
                        int year = sc.nextInt();
                        lib.addBook(new Book(id, title, author, year));
                        break;
                    case 2:
                        lib.viewAllBooks();
                        break;
                    case 3:
                        System.out.print("Enter Title/Author keyword: ");
                        String keyword = sc.nextLine();
                        lib.searchBook(keyword);
                        break;
                    case 4:
                        System.out.print("Enter Book ID to issue: ");
                        int issueId = sc.nextInt(); sc.nextLine();
                        System.out.print("Enter Member ID: ");
                        String memberId = sc.nextLine();
                        lib.issueBook(issueId, memberId);
                        break;
                    case 5:
                        System.out.print("Enter Book ID to return: ");
                        int returnId = sc.nextInt();
                        lib.returnBook(returnId);
                        break;
                    case 6:
                        System.out.print("Enter Book ID to delete: ");
                        int deleteId = sc.nextInt();
                        lib.deleteBook(deleteId);
                        break;
                    case 7:
                        lib.saveToFile();
                        System.out.println("Records saved. Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
