

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;

public class StudentGradeCalculatorGUI extends JFrame {

    private JTextField nameField;
    private JTextField subjectCountField;
    private JPanel marksPanel;
    private JButton calculateButton;
    private JButton saveButton;
    private JTextArea resultArea;
    private JTextField[] marksFields;

    public StudentGradeCalculatorGUI() {
        setTitle("Student Grade Calculator");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel - Student Info
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        topPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        topPanel.add(nameField);

        topPanel.add(new JLabel("Number of Subjects:"));
        subjectCountField = new JTextField();
        topPanel.add(subjectCountField);

        JButton setSubjectsButton = new JButton("Set Subjects");
        setSubjectsButton.addActionListener(e -> setSubjects());
        topPanel.add(setSubjectsButton);

        add(topPanel, BorderLayout.NORTH);

        // Marks Panel
        marksPanel = new JPanel();
        marksPanel.setLayout(new GridLayout(0, 2, 5, 5));
        marksPanel.setBorder(BorderFactory.createTitledBorder("Enter Marks"));
        add(marksPanel, BorderLayout.CENTER);

        // Bottom Panel - Buttons & Results
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(e -> calculateResults());
        calculateButton.setEnabled(false);
        buttonPanel.add(calculateButton);

        saveButton = new JButton("Save Results");
        saveButton.addActionListener(e -> saveResults());
        saveButton.setEnabled(false);
        buttonPanel.add(saveButton);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        bottomPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets the subject fields dynamically based on input count.
     */
    private void setSubjects() {
        try {
            int count = Integer.parseInt(subjectCountField.getText().trim());
            if (count <= 0) {
                JOptionPane.showMessageDialog(this, "Number of subjects must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            marksPanel.removeAll();
            marksFields = new JTextField[count];

            for (int i = 0; i < count; i++) {
                marksPanel.add(new JLabel("Marks for Subject " + (i + 1) + ":"));
                marksFields[i] = new JTextField();
                marksPanel.add(marksFields[i]);
            }

            marksPanel.revalidate();
            marksPanel.repaint();
            calculateButton.setEnabled(true);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for subjects.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calculates total marks, percentage, and grade.
     */
    private void calculateResults() {
        String studentName = nameField.getText().trim();
        if (studentName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the student's name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double total = 0;
        int subjectCount = marksFields.length;

        for (int i = 0; i < subjectCount; i++) {
            try {
                double marks = Double.parseDouble(marksFields[i].getText().trim());
                if (marks < 0 || marks > 100) {
                    JOptionPane.showMessageDialog(this, "Marks must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                total += marks;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid marks entered for subject " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        double percentage = (total / (subjectCount * 100)) * 100;
        String grade = getGrade(percentage);

        String result = String.format(
                "=== Result Summary ===\n" +
                        "Student Name: %s\n" +
                        "Total Marks: %.2f / %d\n" +
                        "Percentage: %.2f%%\n" +
                        "Grade: %s\n",
                studentName, total, subjectCount * 100, percentage, grade
        );

        resultArea.setText(result);
        saveButton.setEnabled(true);
    }

    /**
     * Determines the grade based on percentage.
     */
    private String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else return "Fail";
    }

    /**
     * Saves results to a text file.
     */
    private void saveResults() {
        try (FileWriter writer = new FileWriter("C:\\Users\\hp\\OneDrive\\Desktop\\results.txt", true)) {
            writer.write(resultArea.getText() + "\n");
            JOptionPane.showMessageDialog(this, "Results saved to results.txt", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving results: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Main method to run the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentGradeCalculatorGUI frame = new StudentGradeCalculatorGUI();
            frame.setVisible(true);
        });
    }
}
