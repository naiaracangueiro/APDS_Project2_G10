import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main class with interactive menu for APDS of Warcraft Project 2.
 * Allows users to solve the Wall of Deadlines (WOD) and Festival of Infinite Quests (FIQ) problems
 * using different combinatorial optimization algorithms.
 */
public class Main {
    private static final Scanner sc = new Scanner(System.in);

    private static List<Quest> quests;

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("Welcome to APDS of Warcraft! [PART 2]\n");

        while (running) {
            int num = -1;
            while (num < 0) {
                num = chooseNumElements();
            }

            while (true) {
                System.out.print("Enter the filename: ");
                String filename = sc.nextLine().trim();
                String path = "datasets/" + filename;

                try {
                    quests = DSLoader.loadFile(path, num);
                    int questsFN = quests.size();

                    if (questsFN < num) {
                        System.out.println("Not enough quests in that file. Please try again.");
                    } else {
                        System.out.println("Successfully loaded!");
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Error reading file: " + filename);
                }
            }

            String choice = printMenu();

            int maxTime = -1;
            switch (choice) {
                case "1":
                    while (maxTime < 0) {
                        maxTime = readMaxTime();
                    }
                    String p1 = printWODStrategies();

                    switch(p1) {
                        case "1":
                            WODBruteForce.runWithTimer(quests, maxTime, new Timer());
                            break;
                        case "2":
                            WODBacktracking.runWithTimer(quests, maxTime, new Timer());
                            break;
                        case "3":
                            WODBnB.runWithTimer(quests, maxTime, new Timer());
                            break;
                        case "4":
                            break;
                        default:
                            System.out.println("Invalid option.\n");
                            break;
                    }

                    break;
                case "2":
                    String p2 = printFIQStrategies();

                    switch(p2) {
                        case "1":
                            FIQBruteForce.runWithTimer(quests, new Timer());
                            break;
                        case "2":
                            FIQBacktracking.runWithTimer(quests, new Timer());
                            break;
                        case "3":
                            String greedyChoice = printGreedyHeuristics();
                            switch(greedyChoice) {
                                case "1":
                                    FIQGreedy.runFirstFit(quests, new Timer());
                                    break;
                                case "2":
                                    FIQGreedy.runFirstFitDecreasing(quests, new Timer());
                                    break;
                                case "3":
                                    FIQGreedy.runPriorityBased(quests, new Timer());
                                    break;
                                case "4":
                                    FIQGreedy.runBestFit(quests, new Timer());
                                    break;
                                default:
                                    System.out.println("Invalid option.\n");
                                    break;
                            }
                            break;
                        case "4":
                            break;
                        default:
                            System.out.println("Invalid option.\n");
                            break;
                    }

                    break;
                case "3":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option.\n");
                    break;
            }

            if (!running) break;

            System.out.print("\nWould you like to continue (Y/N) ? ");
            String answer = sc.nextLine().trim();
            if (answer.equalsIgnoreCase("N")) {
                running = false;
            }
        }


    }

    private static String printMenu() {
        System.out.println("\n................... MENU ...................");
        System.out.println("Which problem would you wish to solve?");
        System.out.println("\t1. The Wall of Deadlines");
        System.out.println("\t\tSolved using brute force, backtracking or BnB");
        System.out.println("\t2. The Festival of Infinite Quests");
        System.out.println("\t\tSolved using brute force, backtracking or greedy");
        System.out.println("\t3. Exit");
        System.out.print("Enter your choice [1, 2, 3]: ");
        String choice = sc.nextLine().trim();

        return choice;
    }

    private static String printWODStrategies() {
        System.out.println("\n........... WALL OF DEADLINES ...........");
        System.out.println("Choose the strategy: ");
        System.out.println("\t1. Brute force");
        System.out.println("\t2. Backtracking");
        System.out.println("\t3. Branch and bound");
        System.out.println("\t4. Exit");
        System.out.print("Enter your choice: ");
        String choice = sc.nextLine().trim();

        return choice;
    }


    private static String printFIQStrategies() {
        System.out.println("\n....... FESTIVAL OF INFINITE QUESTS .......");
        System.out.println("Choose the strategy: ");
        System.out.println("\t1. Brute force");
        System.out.println("\t2. Backtracking");
        System.out.println("\t3. Greedy");
        System.out.println("\t4. Exit");
        System.out.print("Enter your choice: ");
        String choice = sc.nextLine().trim();

        return choice;
    }

    private static String printGreedyHeuristics() {
        System.out.println("\n....... GREEDY HEURISTICS .......");
        System.out.println("Choose the heuristic: ");
        System.out.println("\t1. First Fit");
        System.out.println("\t2. First Fit Decreasing");
        System.out.println("\t3. Priority-Based");
        System.out.println("\t4. Best Fit");
        System.out.print("Enter your choice: ");
        String choice = sc.nextLine().trim();

        return choice;
    }

    private static int chooseNumElements() {
        System.out.print("How many quests do you want to organize? ");
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Please try again.");
            return -1;
        }
    }

    private static int readMaxTime() {
        System.out.print("\nSet the time limit (minutes): ");
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Please try again.");
            return -1;
        }
    }
}