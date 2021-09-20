package service.core;

/**
 * Integers were unable to be passed with REST so this class was created to bypass this problem
 */
public class ClientChoices {
      
      public ClientChoices(){}

      private int[] referenceNumbers;          // clients choices of attraction or activity

      public int[] getReferenceNumbers(){
            return referenceNumbers;
      }

      public void setReferenceNumbers(int[] referenceNumbers){
            this.referenceNumbers = referenceNumbers;
      }
}
