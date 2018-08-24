package jvm.soda;


public class ImperativeSodaMachine {
  private int coins = 0;
  private int cans  = 0;

  public ImperativeSodaMachine(int initialCans) {
    this.cans = initialCans;
  }

  public SodaCan insertCoin() {
    if (cans > 0) {
      cans--;
      coins++;
      return new SodaCan();
    }
    else throw new RuntimeException("Out of soda cans!");
  }
}
