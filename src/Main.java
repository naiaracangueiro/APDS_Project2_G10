import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("Welcome to APDS of Warcraft! [PART 2]");
        while (running) {
            int choice = printMenu();

            switch (choice) {
                case 1:
                    int p1 = printWODStrategies();

                    switch(p1) {
                        case 1:
                            WOFBruteForce.runWithTimer(quests, maxTime);
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                    }

                    break;
                case 2:
                    int p2 = printFIQStrategies();

                    switch(p2) {
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                    }

                    break;
                case 3:
                    running = false;
                    break;
            }

            System.out.println("\nWould you like to continue? (Y/N)");
            String answer = sc.nextLine();
            if (answer.equals("N")) {
                running = false;
            }
        }


    }

    private static int printMenu() {
        System.out.println("................... MENU ...................");
        System.out.println("Which problem would you wish to solve?");
        System.out.println("1. The Wall of Deadlines");
        System.out.println("2. The Festival of Infinite Quests");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();

        return choice;
    }

    private static int printWODStrategies() {
        System.out.println("........... WALL OF DEADLINES ...........");
        System.out.println("Choose the strategy: ");
        System.out.println("1. Brute force");
        System.out.println("2. Backtracking");
        System.out.println("3. Branch and bound");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();

        return choice;
    }


    private static int printFIQStrategies() {
        System.out.println("....... FESTIVAL OF INFINITE QUESTS .......");
        System.out.println("Choose the strategy: ");
        System.out.println("1. Brute force");
        System.out.println("2. Backtracking");
        System.out.println("3. Greedy");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();

        return choice;
    }
}