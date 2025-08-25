import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a quiz question with text, options, and the correct answer.
 */
class Question {
    private String questionText;
    private String[] options;
    private int correctAnswer; // index (1-4)

    public Question(String questionText, String[] options, int correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}

/**
 * Main class for the Online Quiz Application.
 */
public class OnlineQuizApp {
    private List<Question> questions;
    private int score;
    private Scanner scanner;

    public OnlineQuizApp() {
        questions = new ArrayList<>();
        score = 0;
        scanner = new Scanner(System.in);
    }

    /**
     * Load questions from an external text file.
     * Format of each line in file:
     * Question?;Option1;Option2;Option3;Option4;CorrectOptionNumber
     */
    public void loadQuestionsFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    String questionText = parts[0];
                    String[] options = {parts[1], parts[2], parts[3], parts[4]};
                    int correctAnswer = Integer.parseInt(parts[5]);
                    questions.add(new Question(questionText, options, correctAnswer));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading questions: " + e.getMessage());
        }
    }

    /**
     * Conducts the quiz and keeps track of score.
     */
    public void startQuiz() {
        System.out.println("Welcome to the Online Quiz!");
        System.out.println("Answer each question within 20 seconds.\n");

        for (Question q : questions) {
            System.out.println(q.getQuestionText());
            String[] options = q.getOptions();
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }

            int answer = getAnswerWithTimer();
            if (answer == q.getCorrectAnswer()) {
                score++;
            }
            System.out.println();
        }

        System.out.println("Quiz Finished!");
        System.out.println("You scored " + score + "/" + questions.size());
    }

    /**
     * Gets the user's answer within 20 seconds. If time expires, returns 0.
     */
    private int getAnswerWithTimer() {
        final int[] answer = {-1};
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (answer[0] == -1) {
                    System.out.println("\nTime's up! Moving to next question.");
                    answer[0] = 0;
                }
            }
        }, 20000); // 20 seconds timer

        try {
            System.out.print("Enter your answer (1-4): ");
            if (scanner.hasNextInt()) {
                answer[0] = scanner.nextInt();
            } else {
                scanner.next(); // consume invalid input
                answer[0] = 0;
            }
        } catch (Exception e) {
            answer[0] = 0;
        } finally {
            timer.cancel();
        }

        return answer[0];
    }

    public static void main(String[] args) {
        OnlineQuizApp app = new OnlineQuizApp();

        // Load questions from file
        app.loadQuestionsFromFile("C:\\Users\\hp\\OneDrive\\Desktop\\question.txt");

        // If no file found or empty, add default questions
        if (app.questions.isEmpty()) {
            System.out.println("No external questions found. Using default questions.");
            app.questions.add(new Question("What is the capital of France?", new String[]{"Berlin", "Paris", "Rome", "Madrid"}, 2));
            app.questions.add(new Question("Which language runs in a web browser?", new String[]{"Java", "C", "Python", "JavaScript"}, 4));
            app.questions.add(new Question("What is 2 + 2?", new String[]{"3", "4", "5", "6"}, 2));
            app.questions.add(new Question("Who developed Java?", new String[]{"Oracle", "James Gosling", "Microsoft", "Google"}, 2));
            app.questions.add(new Question("Which is not an OOP principle?", new String[]{"Encapsulation", "Polymorphism", "Abstraction", "Compilation"}, 4));
        }

        app.startQuiz();
    }
}
