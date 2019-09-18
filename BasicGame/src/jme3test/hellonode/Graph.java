/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.hellonode;
import com.jme3.scene.shape.Line;
import com.jme3.math.Vector3f;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import org.nfunk.jep.type.Complex;
/**
 *
 * @author Robert
 */
public class Graph {
    private int res;
    private float xMin,yMin = -5;
    private float xMax, yMax = 5;
    private float xStep,yStep;
    private String function;
    private float error = 1;
    private org.nfunk.jep.JEP myParser;
    
    public Graph()           
    {       
        function = "";
        try {
            FileReader reader = new FileReader("Function.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);
 
            String line;
            int n = 0;
            while((line = bufferedReader.readLine()) != null)
            {
                System.out.println(n);
                if(n==0) //read function
                    function = line.substring(line.indexOf("=")+2);   
                if(n==1) //read x-range
                {
                    xMin = Float.parseFloat(line.substring(line.indexOf("(")+1,line.indexOf(",")));
                    xMax = Float.parseFloat(line.substring(line.indexOf(",")+1,line.indexOf(")")));
                    System.out.println("x-Min: "+ xMin);
                }
                if(n==2) //read y-range
                {
                    yMin = Float.parseFloat(line.substring(line.indexOf("(")+1,line.indexOf(",")));
                    yMax = Float.parseFloat(line.substring(line.indexOf(",")+1,line.indexOf(")")));
                    System.out.println("y-max: "+yMax);
                }
                if(n==3) //read resolution
                    res = Integer.parseInt(line.substring(line.indexOf(":")+2));
                n++;
            }                                     
            reader.close(); 
        } catch (IOException e) {
            function = "x^2+y^2";
            xMin = -10;
            xMax = 10;
            yMin = -10;
            yMax = 10;
            res = 100;
        }
        System.out.println(function);
        xStep = (xMax-xMin)/res;
        yStep = (yMax-yMin)/res;
        myParser = new org.nfunk.jep.JEP();
        myParser.addStandardFunctions();
        myParser.addStandardConstants();
        myParser.addVariable("x", 0);
        myParser.addVariable("y", 0);
    }
    public Graph(String function,float xMin,float xMax,float yMin, float yMax, int res)
    {
        this.function = "";
        System.out.println(function.length());
        for(int i = 0; i<function.length(); i++)
            if(!function.substring(i,i+1).equals("")){
                this.function += function.substring(i, i+1);
            }
        for(int i = 0; i<function.length(); i++)
        {
            System.out.println(this.function.charAt(i));
        }
        this.function = function;
        
        System.out.println(this.function);
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.res = res;
        xStep = (xMax-xMin)/res;
        yStep = (yMax-yMin)/res;
        myParser = new org.nfunk.jep.JEP();
        
        myParser.addStandardFunctions();
        myParser.addStandardConstants();
        myParser.addVariable("x", 0);
        myParser.addVariable("y", 0);
    }
    private float function(float x, float y)
    {
        
        myParser.addVariable("x", x);
        myParser.addVariable("y",y);
        myParser.parseExpression(function);
        
        if(myParser.hasError() ||Double.isNaN(myParser.getValue()) || Double.isInfinite(myParser.getValue()) || myParser.getComplexValue().im() != 0)
        {
           error = 1;          
           return 0;
           
        }
        
            return (float)myParser.getValue();
    }
    public Line[] getLines()
    {
        Line[] lines = new Line[2*res*(res+1)];
        
        int index = 0;
        for(int x=0; x<=res; x++) //vertical
        {
            for(int y=0;y<res;y++)
            {
                float x1 = xMin + x*xStep;
                float y1 = yMin + y*yStep;
                float y2 = yMin + (y+1)*yStep;
                float z1 = function(x1,y1);
                float z2 = function(x1,y2);
                if(error == 1)
                {
                   lines[index] = null;
                   error = 0;
                }
                    
                else
                    lines[index] = new Line(new Vector3f(x1, z1, y1), new Vector3f(x1, z2, y2));
                index++;
            }
        }       
        for(int y=0;y<=res;y++) //horizontal
        {
            for(int x=0; x<res;x++)
            {
                float x1 = xMin + x*xStep;
                float x2 = xMin + (x+1)*xStep;
                float y1 = yMin + y*yStep;
                float z1 = function(x1,y1);
                float z2 = function(x2,y1);
                if(error == 1)
                {
                    lines[index] = null;
                    error = 0;
                }
                else
                    lines[index] = new Line(new Vector3f(x1, z1, y1), new Vector3f(x2, z2, y1));
                index++;
            }
        }
        System.out.println(Arrays.toString(lines));
        return lines;
    }
    public float getXMin()
    {
            return xMin;
    }
    public float getXMax(){
        return xMax;
    }
    public float getYMin(){
        return yMin;
    }
    public float getYMax(){
        return yMax;
    }
    public int getResolution(){
        return res;
    }
    public String getFunction(){
        return function;
    }
    public boolean hasError(){
        return myParser.hasError();
    }
    public String getErrorInfo(){
        return myParser.getErrorInfo();
    }
}
