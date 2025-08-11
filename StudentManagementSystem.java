// StudentManagementSystem.java
// Console-based Student Management System

import java.util.ArrayList;
import java.util.Scanner;

//student class
class Student {
    private String name;
    private String rollNo;
    private String course;

    public Student(String name, String rollNo, String course) {
        this.name = name;
        this.rollNo = rollNo;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public String getRollNumber() {
        return rollNo;
    }

    public String getCourse() {
        return course;
    }

    @Override
    public String toString() {
        return "Name: " + name + " | Roll No: " + rollNo + " | Course: " + course;
    }
}


public class StudentManagementSystem {
    private static ArrayList<Student> students = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        // sample students
        students.add(new Student("Muhammad Iqbal", "101", "Software Engineering"));
        students.add(new Student("Abu sufiyan", "102", "Software Engineering"));

        do {
            System.out.println("\n===== Student Management System =====");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Search Student");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewStudents();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    System.out.println("Exiting the program");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while (choice != 4);
    }

    // Method to add a new student
    private static void addStudent() {
        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Roll Number: ");
        String rollNo = scanner.nextLine();

        System.out.print("Enter Course: ");
        String course = scanner.nextLine();

        students.add(new Student(name, rollNo, course));
        System.out.println("Student added successfully!");
    }

    // Method to display all students
    private static void viewStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found!");
            return;
        }

        System.out.println("\n--- List of Students ---");
        for (Student s : students) {
            System.out.println(s);
        }
    }

    // Method to search for a student by roll no
    private static void searchStudent() {
        System.out.print("Enter Roll No to Search: ");
        String rollNo = scanner.nextLine();

        boolean found = false;
        for (Student s : students) {
            if (s.getRollNumber().equalsIgnoreCase(rollNo)) {
                System.out.println(" Student Found: " + s);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println(" Student with Roll No " + rollNo + " not found.");
        }
    }
}
