package jvm.soda;


public class SodaImperative {
  public static void main(String[] args) {
    ImperativeSodaMachine sm = new ImperativeSodaMachine(2);
    SodaCan can1 = sm.insertCoin();
    SodaCan can2 = sm.insertCoin();
    can2.drink();

    SodaCan can3 = sm.insertCoin();  // Out of soda cans
  }
}
