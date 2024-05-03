package uk.ac.nulondon;
import java.awt.*;

// This class represents a pixel in our image data structure. It is a 4 way linked list with many data points.
public class PixelNode {
    private final Color color;  // RGB values
    private final double brightness;
    private double cumulativeEnergy;
    private int cumulativeBlue;
    private double energy;
    public PixelNode parent;
    public PixelNode up, down, left, right;

    public String directionNext ="";

    public Color highlight = null;



    // This constructor makes a PixelNode from a color
    public PixelNode(Color color) {
        this.color = color;
        this.energy = Double.MAX_VALUE;
        this.brightness = (color.getBlue() + color.getGreen() + color.getRed())/3.0;
        this.cumulativeEnergy = 0;  // Initially set to max value
        this.parent = null;
        this.up=this.down=this.left=this.right;
    }

    // This function returns the color
    public Color getColor(){
        return this.color;
    }

    // This function returns the energy
    public double getEnergy(){
        return this.energy;
    }

    // This function returns the cumulative energy
    public double getCumulativeEnergy(){
        return this.cumulativeEnergy;
    }

    // This function takes an int and sets the cumulative blue value to it
    public void setCumB(int cumulativeBlue){
        this.cumulativeBlue = cumulativeBlue;

    }

    // This function gets the cumulative blue value
    public int getCumB(){
        return this.cumulativeBlue;
    }

    // This function takes in the cumulative energy as an int and sets it
    public void setCumulativeEnergy(double cumE){
        this.cumulativeEnergy = cumE;
    }


    // This function returns the UpLeft pixel if it exists or null if it doesn't
    public PixelNode getUpLeft(){
        if(up!=null){
            return up.left;
        }
        return null;
    }

    // This function returns the UpRight pixel if it exists or null if it doesn't
    public PixelNode getUpRight(){
        if(up!=null){
            return up.right;
        }
        return null;
    }

    // This function returns the DownLeft pixel if it exists or null if it doesn't
    public PixelNode getDownLeft(){
        if(down!=null){
            return down.left;
        }
        return null;
    }

    // This function returns the DownRight pixel if it exists or null if it doesn't
    public PixelNode getDownRight(){
        if(down!=null){
            return down.right;
        }
        return null;
    }


    // This function calculates and sets the individual energy of a pixel
    public void calcIndividE(){
        double horizontalE = 0;
        double verticalE = 0;

        if(this.left!=null){
            horizontalE += 2.0*this.left.brightness;
        }else{
            horizontalE +=2.0*this.brightness;
        }

        if(this.getUpLeft()!=null){
            horizontalE += this.getUpLeft().brightness;
            verticalE += this.getUpLeft().brightness;
        }else{
            horizontalE +=this.brightness;
            verticalE +=this.brightness;
        }

        if(this.getDownLeft()!=null){
            horizontalE += this.getDownLeft().brightness;
            verticalE -= this.getDownLeft().brightness;
        }else{
            horizontalE +=this.brightness;
            verticalE -=this.brightness;
        }

        if(this.getUpRight()!=null){
            horizontalE -= this.getUpRight().brightness;
            verticalE += this.getUpRight().brightness;
        }else{
            horizontalE -=this.brightness;
            verticalE +=this.brightness;
        }


        if(this.getDownRight()!=null){
            horizontalE -= this.getDownRight().brightness;
            verticalE -= this.getDownRight().brightness;
        }else{
            horizontalE -=this.brightness;
            verticalE -=this.brightness;
        }

        if(this.right!=null){
            horizontalE -= 2.0*this.right.brightness;
        }else{
            horizontalE -=2.0*this.brightness;
        }

        if(this.up!=null){
            verticalE += 2.0*this.up.brightness;
        }else{
            verticalE +=2.0*this.brightness;
        }

        if(this.down!=null){
            verticalE -= 2.0*this.down.brightness;
        }else{
            verticalE -=2.0*this.brightness;
        }

        // calculate the overall energy
        this.energy = Math.sqrt(Math.pow(horizontalE, 2)+Math.pow(verticalE, 2));
    }
}