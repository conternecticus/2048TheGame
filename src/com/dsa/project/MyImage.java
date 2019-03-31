package com.dsa.project;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MyImage{
    public static void main(String args[])throws IOException{
        int width = 963;    //width of the image
        int height = 640;   //height of the image
        BufferedImage image = null;
        File f = null;

        //read image
        try{
            f = new File("F:\\Entertainment\\Untitled.png"); //image file path
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image = ImageIO.read(f);
            System.out.println("Reading complete.");
        }catch(IOException e){
            System.out.println("Error: "+e);
        }

        //write image
        try{
            f = new File("F:\\Entertainment\\posie humor.jpg");  //output file path
            ImageIO.write(image, "jpg", f);
            System.out.println("Writing complete.");
        }catch(IOException e){
            System.out.println("Error: "+e);
        }
    }//main() ends here
}//class ends here
