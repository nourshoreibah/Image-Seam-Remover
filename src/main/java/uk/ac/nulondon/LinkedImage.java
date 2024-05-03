package uk.ac.nulondon;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;


public class LinkedImage {
    // counter to track how many times the image has been saved
    private String version = "0";

    // string for the path to the folder where the images are stored
    private final String folderLocation;

    // string for the original name of the image
    private String name;

    PixelNode head;
    int height;
    int width;

    Stack<List<PixelNode>> history = new Stack<>();

    // takes a string path to a file and, if found, creates a buffered image runs buffered image through createLL
    public LinkedImage(String path) throws Exception {
        // split the path by /
        String[] splitPath = path.split("/");
        // initialize the folderLocation class variable as the whole path excluding the name and file type
        folderLocation = path.substring(0,path.length() - splitPath[splitPath.length-1].length());
        // initialize the class variable name as everything after the last /, then remove the .png part
        name = splitPath[splitPath.length-1];
        name = name.substring(0,name.length()-4);

        // create new file using string path
        File file = new File(path);

        // create BufferedImage using created file
        BufferedImage img = ImageIO.read(file);
        this.head = createLL(img);
    }



    // creates a linked list of nodes given a BufferedImage
    private PixelNode createLL(BufferedImage img) {
        width = img.getWidth();
        height = img.getHeight();

        PixelNode head = null;
        PixelNode[] previousRow = new PixelNode[width];

        for (int i = 0; i < height; i++) {
            PixelNode[] currentRow = new PixelNode[width];
            PixelNode previousNode = null;

            for (int j = 0; j < width; j++) {
                PixelNode newNode = new PixelNode(new Color(img.getRGB(j, i)));
                currentRow[j] = newNode;

                if (i == 0 && j == 0) {
                    head = newNode;
                }

                if (previousNode != null) {
                    previousNode.right = newNode;
                    newNode.left = previousNode;
                }


                if (i > 0) {
                    previousRow[j].down = newNode;
                    newNode.up = previousRow[j];
                }

                previousNode = newNode;
            }

            previousRow = currentRow;
        }
        return head;
    }


    // creates a buffered image using the linked list of pixels
    public BufferedImage generateBufferedImage() {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        PixelNode rowHead = head;
        PixelNode current;

        for (int i = 0; i < height; i++) {
            current = rowHead;
            for (int j = 0; j < width; j++) {

                if (current != null) {
                    if(current.highlight==null) {
                        image.setRGB(j, i, current.getColor().getRGB());
                    }else{
                        image.setRGB(j,i,current.highlight.getRGB());
                    }
                    current = current.right;
                }
            }
            if (rowHead != null) {
                rowHead = rowHead.down;
            }
        }

        return image;
    }

    // calculates each pixels individual energy
    public void calculateEnergies() {
        PixelNode curRow = this.head;
        PixelNode cur;
        while (curRow != null) {
            cur = curRow;
            while (cur != null) {
                cur.calcIndividE();
                cur = cur.right;
            }
            curRow = curRow.down;
        }
    }


    // finds the cumulative energy for each pixel
    private void findCumE(){
        calculateEnergies();
        // first row's cumulative energy is just their energies since they have no parents
        PixelNode firstRowIter = head;
        while(firstRowIter != null) {
            firstRowIter.setCumulativeEnergy(firstRowIter.getEnergy());
            firstRowIter = firstRowIter.right;
        }

        PixelNode curRow = head.down;
        // go through each node, find parent with lowest cumE then add it's cumulative energy to node's energy
        while(curRow != null){
            PixelNode cur = curRow;
            while(cur!=null){
                PixelNode bestParent = cur.up;

                if(cur.getUpLeft() != null && (bestParent.getCumulativeEnergy()>cur.getUpLeft().getCumulativeEnergy())){
                    bestParent = cur.getUpLeft();
                }
                if(cur.getUpRight() != null && (bestParent.getCumulativeEnergy()>cur.getUpRight().getCumulativeEnergy())){
                    bestParent = cur.getUpRight();
                }
                cur.parent = bestParent;
                cur.setCumulativeEnergy(bestParent.getCumulativeEnergy()+cur.getEnergy());
                cur = cur.right;
            }
            curRow = curRow.down;
        }
    }

    // finds the cumulative blue values using the same method as above
    private void findCumB(){
        PixelNode curRow = head.down;

        while(curRow != null){
            PixelNode cur = curRow;
            while(cur!=null){
                PixelNode bestParent = cur.up;
                if(cur.getUpLeft() != null && (bestParent.getColor().getBlue()<cur.getUpLeft().getColor().getBlue())){
                    bestParent = cur.getUpLeft();
                }
                if(cur.getUpRight() != null && (bestParent.getColor().getBlue()<cur.getUpRight().getColor().getBlue())){
                    bestParent = cur.getUpRight();
                }
                cur.parent = bestParent;
                cur.setCumB(bestParent.getCumB()+cur.getColor().getBlue());
                cur = cur.right;
            }
            curRow = curRow.down;
        }

    }

    // finds the lowest energy seam by starting from the bottom node with lowest cumE then working back up
    public List<PixelNode> findLowestE() {
        findCumE();
        PixelNode cur = head;

        while(cur.down != null){
            cur = cur.down;
        }

        PixelNode min = cur;
        while(cur != null){
            if(cur.getCumulativeEnergy() < min.getCumulativeEnergy()) {
                min = cur;
            }
            cur = cur.right;
        }

        List<PixelNode> seam = new ArrayList<>();

        while (min != null) {
            seam.add(min);
            min = min.parent;
        }
        Collections.reverse(seam);
        return seam;

    }


    // finds the bluest seam using same method as above
    public List<PixelNode> findBluest() {
        findCumB();
        PixelNode cur = head;

        while(cur.down != null){
            cur = cur.down;
        }

        PixelNode max = cur;
        while(cur !=null){
            if(cur.getCumB() > max.getCumB()){
                max = cur;

            }
            cur = cur.right;
        }

        List<PixelNode> seam = new ArrayList<>();

        while (max != null) {
            seam.add(max);
            max = max.parent;
        }
        Collections.reverse(seam);
        return seam;


    }


    // highlights a seam given the seam list and desired color, saves image, then unhighlights
    public void highlightSeam(List<PixelNode> seam,Color color){
        for(PixelNode pixel:seam){
            pixel.highlight =  color;
        }
//        saveImage();



    }

    public void unHighlightSeam(List<PixelNode> seam){
        for(PixelNode pixel: seam){
            pixel.highlight = null;

        }
    }


    // removes the seam with lowest energy if user confirms
    public void removeLowestE(){
        if(width>1) {
            highlightSeam(findLowestE(), Color.RED);
            if (UserInterface.confirm("e")) {
                removeSeam(findLowestE());
            }else{
//                saveImage();
            }
        }
    }

    // removes the seam with most blue if user confirms
    public void removeBluest(){
        if(width>1) {
            highlightSeam(findBluest(), Color.BLUE);
            if (UserInterface.confirm("b")) {
                removeSeam(findBluest());
            }else{
//                saveImage();
            }
        }
    }


    // This function reinserts the most recently removed seam
    public void reinsertLastSeam(){
        List<PixelNode> seam = history.pop();
        for (int i = 0; i < seam.size(); i++) {
            PixelNode pixel = seam.get(i);
            if(pixel.right == head){
                head = pixel;
            }
            if(pixel.right!=null){
                pixel.right.left = pixel;
            }
            if(pixel.left!=null){
                pixel.left.right = pixel;
            }
            if(i<seam.size()-1){
                if(pixel.directionNext.equals("downRight")){
                    pixel.right.down = seam.get(i+1);
                    pixel.down.up = pixel;
                }
                else  if(pixel.directionNext.equals("downLeft")){
                    pixel.left.down=seam.get(i+1);
                    pixel.down.up = pixel;
                }
            }


        }
        width+=1;
//        saveImage();


    }
    //  saves the seam in history stack, then removes it from the image
    public void removeSeam(List<PixelNode> seam){
        history.add(seam);
        // for each pixel in the seam
        for (int i = 0; i < seam.size(); i++) {

            PixelNode pixel = seam.get(i);
            if(pixel == head){
                head = head.right;
            }
            if(pixel.right !=null){
                pixel.right.left = pixel.left;
            }
            if(pixel.left!=null){
                pixel.left.right = pixel.right;
            }

            if(i <seam.size()-1){
                if(seam.get(i+1) == pixel.getDownRight()){
                    pixel.right.down = pixel.down;
                    pixel.down.up = pixel.right;
                    pixel.directionNext = "downRight";

                }
                if(seam.get(i+1) == pixel.getDownLeft()){
                    pixel.left.down = pixel.down;
                    pixel.down.up = pixel.left;
                    pixel.directionNext = "downLeft";
                }

            }

        }

        while (head.left != null) {
            head = head.left;
        }
        while(head.up!=null){
            head = head.up;
        }

        width--;
//        saveImage();
    }

    // if final image, image saves as newImage, and if not, image is saves as tempImage
    private void saveImage(){
        // declare a path variable
        String path;
        // check if this is the final version
        if (this.version.equals("FINAL")){
            // if it is, set path to folderLocation concatenated to newImage.png
            path = this.folderLocation + "newImage.png";
            // if version is not final...
        } else {
            // set path such that the result will be in the format $folderLocationtemp$name$version.png
            path = this.folderLocation + "temp" + this.name + this.version + ".png";
            // increment the version
            this.version = String.valueOf((Integer.parseInt(version) + 1));
        }
        // create a new file in this path
        File newFile = new File(path);

        // new buffer for alteredImage
        BufferedImage newImg = generateBufferedImage();

        // this try catch should never error out as far as I know, but java seemingly requires it.
        try {
            // write the image to the file
            ImageIO.write(newImg, "png", newFile);
            // print a statement confirming it worked
            System.out.println("Saved " + path);
        } catch(Exception e){
            // print error if this somehow errors out
            System.out.println("Error");
        }
    }

    // undos the latest alternation done to image, this can be done until no alternations are possible
    public void undo(){
        // check if history is empty
        if(history.empty()){
            // if it is empty, say there is nothing to undo.
            System.out.println("Nothing to undo.");
        } else {
            // if it is not empty, set the pixelGrid equal to the top of the history stack and remove the top
            reinsertLastSeam();
            System.out.println("Undo successful.");
        }

    }

    // quits out of user interface and saves final image
    public void quit(){
        this.name = "newImage";
        this.version ="FINAL";
        saveImage();
    }

    public static void main(String args[]) throws Exception{
        LinkedImage nature = new LinkedImage("src/main/resources/nature.jpeg");



        for (int i = 0; i < 100; i++) {
            List<PixelNode> lowE = nature.findLowestE();
            nature.highlightSeam(lowE,Color.RED);
            nature.removeSeam(lowE);
            nature.quit();



        }

    }

}


