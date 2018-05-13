package jvm.mapping;

import java.util.List;
import java.util.ArrayList;

public class MappingImperative {
  public static void main(String[] args) {
    // Source collection
    List<Integer> numbers = new ArrayList<Integer>();
    numbers.add(1);
    numbers.add(2);
    numbers.add(3);

    // Squared numbers
    List<Integer> result = new ArrayList<Integer>();
    for (Integer n: numbers)
      result.add(n * n);

    System.out.println(result);
  }
}