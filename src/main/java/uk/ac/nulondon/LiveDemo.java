package uk.ac.nulondon;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Scanner;

import static java.util.Arrays.asList;

// Start this live demo to see a jframe window of the seams being identified and removed from the image in real time!
public class LiveDemo {


    public static void main(String args[]) throws Exception {
        List<String> options = asList("coffee","man","nature","penguin");
        Scanner scan = new Scanner(System.in);
        String choice;


        boolean valid;
        do {
            System.out.println("Please pick an image for the demo (type 'coffee', 'man', 'nature', 'penguin', or 'quit' to quit.");
            choice = scan.next();
            if(choice.equals("quit")) {
                System.exit(0);
            }
            valid = options.contains(choice);

        }while(!valid);

        LinkedImage testImage = new LinkedImage("src/main/resources/" + choice +".jpg");

        int width = 800;
        int height = 800;
        JFrame frame = new JFrame("Seam Slicing");
        frame.setSize(width, height);
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(testImage.generateBufferedImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int ogWidth = testImage.width;
        List<PixelNode> lowE;
        for (int i = 0; i < (ogWidth*.75); i++) {
            lowE = testImage.findLowestE();
            testImage.highlightSeam(lowE, Color.RED);
            label.setIcon(new ImageIcon(testImage.generateBufferedImage().getScaledInstance(width, height + i, Image.SCALE_SMOOTH)));
            testImage.unHighlightSeam(lowE);
            testImage.removeSeam(lowE);
            label.setIcon(new ImageIcon(testImage.generateBufferedImage().getScaledInstance(width, height + i, Image.SCALE_SMOOTH)));

        }





    }



    }

