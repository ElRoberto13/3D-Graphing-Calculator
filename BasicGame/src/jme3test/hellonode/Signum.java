/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jme3test.hellonode;

import java.util.Stack;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 *
 * @author Robert
 */

public class Signum extends PostfixMathCommand{
    public Signum(){
        numberOfParameters = 1;
    }
    public void run(Stack inStack) throws ParseException {
   // check the stack
   checkStack(inStack);
   
   // get the parameter from the stack
   Object param = inStack.pop();

   // check whether the argument is of the right type
   if (param instanceof Double) {
      // calculate the result
      double r = ((Double)param).doubleValue() / 2;
      // push the result on the inStack
      inStack.push(new Double(r)); 
   } else {
      throw new ParseException("Invalid parameter type");
   }
}
}
