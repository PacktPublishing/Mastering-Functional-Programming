package jvm;

import java.util.List;
import java.util.ArrayList;

public class MainJava {
  public static void main(String[] args) {
    // Source collection
    List<String> employees = new ArrayList<String>();
    employees.add("Ann");
    employees.add("John");
    employees.add("Amos");
    employees.add("Jack");

    // Those employees with their names starting with 'A'
    List<String> result = new ArrayList<String>();
    for (String e: employees)
      if (e.charAt(0) == 'A') result.add(e);

    System.out.println(result);
  }
}