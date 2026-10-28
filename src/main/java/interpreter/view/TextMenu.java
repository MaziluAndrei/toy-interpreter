package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.Repository;
import java.util.Scanner;

public class TextMenu {
    public void show() {
        Scanner scanner = new Scanner(System.in);
        IStmt selectedProgram = null;

        while (true) {
            System.out.println("\n=== TOY LANGUAGE INTERPRETER MENU ===");
            System.out.println("1. Select a program");
            System.out.println("2. Run the selected program completely");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> selectedProgram = selectProgram(scanner);
                case "2" -> {
                    if (selectedProgram == null) {
                        System.out.println("Please select a program first (option 1).");
                    } else {
                        runProgram(selectedProgram);
                    }
                }
                case "0" -> {
                    System.out.println("Exiting interpreter.");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private IStmt selectProgram(Scanner scanner) {
        System.out.println("\nAvailable Programs:");
        System.out.println("1: int v; v = 2; print(v)");
        System.out.println("2: int a; int b; a = 2 + 3 * 5; b = a + 1; print(b)");
        System.out.println("3: bool a; int v; a = true; (if a then v = 2 else v = 3); print(v)");
        System.out.print("Select program number (1-3): ");

        String progChoice = scanner.nextLine().trim();
        return switch (progChoice) {
            case "1" -> {
                System.out.println("Selected Program 1.");
                yield Examples.EX1;
            }
            case "2" -> {
                System.out.println("Selected Program 2.");
                yield Examples.EX2;
            }
            case "3" -> {
                System.out.println("Selected Program 3.");
                yield Examples.EX3;
            }
            default -> {
                System.out.println("Invalid program selection.");
                yield null;
            }
        };
    }

    private void runProgram(IStmt program) {
        PrgState state = new PrgState(
                new MyStack<>(),
                new MyDictionary<>(),
                new MyList<>(),
                program
        );
        Controller controller = new Controller(new Repository(state), true);
        try {
            controller.allStep();
        } catch (MyException e) {
            System.out.println("Interpreter Error: " + e.getMessage());
        }
    }
}