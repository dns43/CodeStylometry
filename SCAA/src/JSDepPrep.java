/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dns43
 */
public class JSDepPrep {
    
     public static void main(String args[]){
        String path = "C:\\Users\\dns43\\Documents\\NetBeansProjects\\CodeStylometry\\testJS\\js_dep.txt";
        File depfile = new File(path);
        String source = "";
        char nextChar;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(depfile));
                while(reader.ready()){
                nextChar = (char) reader.read();
                String charStr = "" + nextChar;
                source = source + nextChar;
                }        
        String tokenpart = source.substring(source.indexOf("\"tokens\": ["), source.length()-3);
        System.out.println("#tokens: "+tokenpart.length()/77);
        String[] tokens = tokenpart.split("},");
        
        
        System.out.println(tokenpart.indexOf("\"value\": \"function\""));
        String[] functionIDs = tokenpart.split("\"value\": \"function\"");
        for(int i = 0; i<functionIDs.length; i++){
            functionIDs[i] = functionIDs[i].replace("\n", "\n"+i+"\t");
        }
        

        PrintWriter out = new PrintWriter("C:\\Users\\dns43\\Documents\\NetBeansProjects\\CodeStylometry\\testJS\\fileIDs.txt");
        for(int i = 0; i<functionIDs.length;i++){
        out.print(functionIDs[i]);
        }
        
        out.flush();
        out.close();
        
      //  for(int i = 0; i<tokens.length; i++){
        //    System.out.println("Token "+i+": "+tokens[i]);
        //}
        }
              catch (FileNotFoundException ex) {
                Logger.getLogger(EsprimaWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EsprimaWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
}
