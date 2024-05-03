package uk.ac.nulondon;

import java.util.InputMismatchException;
import java.util.Scanner;


/* This class handles the user's interactions with the program. It provides menus, takes the user's desired inputs,
and uses LinkedImage to make the user's desired changes to the image.
 */

public class UserInterface {


    // prints a menu of options for changes to the image
    private static void printMenu(LinkedImage image) {
        if(image.width>1) {
            System.out.println("\nPlease make a selection (b, e, u, q)");
            System.out.println("- remove the bluest seam (b)");
            System.out.println("- remove lowest energy seam (e)");
            System.out.println("- Undo last change (u)");
            System.out.println("- Quit (q)\n");
        }else{
            System.out.println("\nPlease make a selection (u, q)");
            System.out.println("- Undo last change (u)");
            System.out.println("- Quit (q)\n");

        }

    }


    // prints a response depending on what the user picked, with a default message if the selection was invalid
    private static void printResponse(String selection, LinkedImage image) {
        switch(selection) {
            case "b":
                if(image.width>1) {
                    System.out.println("You picked remove bluest seam.");
                }else{
                    System.out.println("Cannot remove the final column.");
                }
                break;
            case "e":
                if(image.width>1) {
                    System.out.println("You picked the lowest energy seam.");
                }else{
                    System.out.println("Cannot remove the final column.");
                }
                break;
            case "u":
                System.out.println("You picked undo.");
                break;
            case "q":
                System.out.println("Thanks for using my program!");
                break;
            default:
                System.out.println("That is not a valid option. Please try again.");
                break;

        }
    }

    // this public static method allows the user to confirm their choice of change to the image. It returns a boolean
    // representing the user's confirmation status
    public static boolean confirm(String choice){
        // declare a scanner
        Scanner scan;

        // if the choice is not to quit
        if(!choice.equals("q")) {
            // loop until a return statement is reached (a valid response to the yes or no question is given)
            while(true) {
                // ask the user if they want to quit
                System.out.println("Are you sure you would like to proceed? (Y/N)");
                // take the user input. if it is y, return true, otherwise assume the answer is no. Ignore case.
                try {
                    scan = new Scanner(System.in);
                    return scan.next().equalsIgnoreCase("y");

                    // if the input is not a string, tell user to try again (loop will continue)
                } catch (InputMismatchException e){
                    System.out.println("Please enter a string. Try again.");
                }
            }
        }
        // default to no
        return false;

    }


    // the main class, where variables specific to the user's image are created, and the while loops required for the program run
    public static void main(String[] args) {

        // variable that determines if the main for loop will continue to run
        boolean shouldQuit = false;
        // declaration of path, image,scan, and choice variables. Path is the path of the image, image is the LinkedImage variable for the image,
        // scan will be used to take user input, and choice refers to which action the user wants (b, e, u, or q)
        String path;
        LinkedImage image;
        Scanner scan;
        String choice;
        // this loop is for the user to enter a file path. It will continue to run until the user has entered a valid path.
        while(true) {
            System.out.println("Enter file path:\n");
            try {

                scan = new Scanner(System.in);
                path = scan.next();
                image = new LinkedImage(path);
                break;

            } catch(Exception e) {
                System.out.println("Invalid path. Please try again.");
            }
        }

        // while shouldQuit is false, keep going
        while(!shouldQuit) {
            // display options to the user
            printMenu(image);

            // try and get user input, if input is invalid, try again until it is valid
            try {
                scan = new Scanner(System.in);
                choice = scan.next();
                choice = choice.toLowerCase();
            } catch (InputMismatchException e) {
                System.out.println("That is not a valid option. Please try again.");
                choice = "";
            }
            printResponse(choice,image);

            // call the corresponding LinkedImage method for each choice. Also set shouldQuit to true if choice is quit
            switch (choice) {
                case "b" -> image.removeBluest();
                case "e" -> image.removeLowestE();
                case "u" -> image.undo();
                case "q" -> {
                    shouldQuit = true;
                    image.quit();
                }
            }


        }
        // close scanner
        scan.close();
    }

}

