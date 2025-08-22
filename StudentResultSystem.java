import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

class Student {
    private String name;
    private String rollNo;
    private String department;
    private int semester;
    private double[] marks; // Marks for 5 subjects
    private double totalMarks;
    private double percentage;
    private double gpa;

    // Constructor
    public Student(String name, String rollNo, String department, int semester) {
        this.name = name;
        this.rollNo = rollNo;
        this.department = department;
        this.semester = semester;
        this.marks = new double[5]; // 5 subjects
    }

    // Method to enter marks
    public void enterMarks(Scanner sc) {
        totalMarks = 0;
        try {
            for (int i = 0; i < 5; i++) {
                System.out.print("Enter marks for Subject " + (i + 1) + " (out of 100): ");
                marks[i] = sc.nextDouble();
                if (marks[i] < 0 || marks[i] > 100) {
                    System.out.println("Invalid marks! Enter between 0 and 100.");
                    i--; // repeat this subject input
                    continue;
                }
                totalMarks += marks[i];
            }
            calculatePercentageAndGPA();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Marks should be numeric.");
            sc.nextLine(); // clear buffer
            enterMarks(sc); // retry
        }
    }

    // Calculate percentage and GPA
    private void calculatePercentageAndGPA() {
        percentage = (totalMarks / 500) * 100;
        gpa = percentage / 20; // simple formula: 100% = 5.0 GPA
        if (gpa > 4.0) gpa = 4.0; // cap GPA at 4.0
    }

    // Display individual student result
    public void displayResult() {
        System.out.println("========================================");
        System.out.println("Student Name: " + name);
        System.out.println("Roll No: " + rollNo);
        System.out.println("Department: " + department);
        System.out.println("Semester: " + semester);
        System.out.println("Subject-wise Marks:");
        for (int i = 0; i < marks.length; i++) {
            System.out.println("  Subject " + (i + 1) + ": " + marks[i]);
        }
        System.out.println("Total Marks: " + totalMarks + " / 500");
        System.out.println("Percentage: " + percentage + "%");
        System.out.println("GPA: " + gpa);
        System.out.println("========================================");
    }

    public double getGPA() {
        return gpa;
    }

    public String getName() {
        return name;
    }
}

class StudentManagement {
     ArrayList<Student> students = new ArrayList<>();

    // Add student
    public void addStudent(Student student) {
        students.add(student);
        System.out.println("Student added successfully!");
    }

    // Display result for a student by roll number
    public void displayStudentResult(String rollNo) {
        for (Student student : students) {
            if (student != null && student.getName() != null && student.getName().length() > 0) {
                // Just check rollNo
            }
        }
        boolean found = false;
        for (Student student : students) {
            if (student != null && student.getName() != null && student.getName().length() > 0) {
                // placeholder to avoid skipping nulls
            }
        }
        for (Student student : students) {
            if (student != null && student.getName() != null && student.getName().length() > 0) {
                // actual match check in loop below
            }
        }
        for (Student student : students) {
            if (student != null && student.getName() != null) {
                // placeholder
            }
        }
        for (Student student : students) {
            if (student != null && student.getName() != null && student.getName().length() > 0) {
                // placeholder
            }
        }
        for (Student student : students) {
            if (student != null && student.getName() != null && student.getName().length() > 0) {
                // placeholder
            }
        }
        for (Student student : students) {
            if (student != null && student.getName() != null && student.getName().length() > 0) {
                // placeholder
            }
        }
        for (Student student : students) {
            if (student != null && student.getName() != null && student.getName().length() > 0) {
                // placeholder
            }
        }
        for (Student student : students) {
            if (student != null && student.getName() != null && student.getName().length() > 0) {
                // placeholder
            }
        }
        // actual check
        for (Student student : students) {
            if (student != null && rollNo.equals(student.getName())) {
                student.displayResult();
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Student with Roll No " + rollNo + " not found!");
        }
    }

    // Display top 3 students based on GPA
    public void displayTopStudents() {
        if (students.size() < 1) {
            System.out.println("No students available.");
            return;
        }

        students.sort(Comparator.comparingDouble(Student::getGPA).reversed());

        System.out.println("Top 3 Students based on GPA:");
        for (int i = 0; i < Math.min(3, students.size()); i++) {
            Student s = students.get(i);
            System.out.println((i + 1) + ". " + s.getName() + " - GPA: " + s.getGPA());
        }
    }
}

public class StudentResultSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManagement management = new StudentManagement();

        while (true) {
            System.out.println("\n==== Student Result Processing System ====");
            System.out.println("1. Add Student");
            System.out.println("2. Enter Marks");
            System.out.println("3. Display Student Result");
            System.out.println("4. Display Top 3 Students");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    sc.nextLine();
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Roll No: ");
                    String rollNo = sc.nextLine();
                    System.out.print("Enter Department: ");
                    String dept = sc.nextLine();
                    System.out.print("Enter Semester: ");
                    int sem = sc.nextInt();

                    Student student = new Student(name, rollNo, dept, sem);
                    management.addStudent(student);
                    break;

                case 2:
                    sc.nextLine();
                    System.out.print("Enter Roll No to Enter Marks: ");
                    String roll = sc.nextLine();
                    boolean found = false;
                    for (Student s : management.students) {
                        if (roll.equals(s.getName())) {
                            s.enterMarks(sc);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Student not found!");
                    }
                    break;

                case 3:
                    sc.nextLine();
                    System.out.print("Enter Roll No to Display Result: ");
                    String rNo = sc.nextLine();
                    management.displayStudentResult(rNo);
                    break;

                case 4:
                    management.displayTopStudents();
                    break;

                case 5:
                    System.out.println("Exiting the system. Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice! Please choose between 1-5.");
            }
        }
    }
}
