package service.core;

/**
 * Integers were unable to be passed with REST so this class was created to bypass this problem
 */
public class ClientChoice {
      
      public ClientChoice(){}

      private int referenceNumber;          // clients choice of flight or hotel etc.

      public int getReferenceNumber(){
            return referenceNumber;
      }

      public void setReferenceNumber(int referenceNumber){
            this.referenceNumber = referenceNumber;
      }
}
