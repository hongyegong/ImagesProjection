package com.ggyc.contents;

import java.io.*;
import java.util.*;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

public class Combination {
	
  public static void main(String[] args) {
    ArrayList<String> strings = new ArrayList<String>();
    strings.add("Hello, World!");
    strings.add("Welcome to CoderPad.");
    strings.add("This pad is running Java 8.");
    
    Combination sol = new Combination();
    ArrayList<ArrayList<String>> stringList = new  ArrayList<>();
    ArrayList<String> firstList = new ArrayList<String>();
    firstList.add("quick");
    firstList.add("lazy");
    
    ArrayList<String> secondList = new ArrayList<String>();
    secondList.add("brown");
    secondList.add("grey");
    secondList.add("black");
    
    ArrayList<String> thirdList = new ArrayList<String>();
//    firstList.add("fox");
//    firstList.add("dog");
    
    stringList.add(firstList);
    stringList.add(secondList); 
    stringList.add(thirdList);
    
    ArrayList<ArrayList<String>> lists = new ArrayList<>();
	ArrayList<String> list1 = new ArrayList<String>();
	list1.add("quick");
	list1.add("lazy");
	ArrayList<String> list2 = new ArrayList<String>();
	list2.add("brown");
	list2.add("black");
	list2.add("grey");
	ArrayList<String> list3 = new ArrayList<String>();
//	list3.add("fox");
//	list3.add("dog");

	lists.add(list1);
	lists.add(list2);
	lists.add(list3);
	
	for (Iterator<ArrayList<String>> it = stringList.iterator(); it
            .hasNext();) {
        if (it.next().isEmpty()) {
            it.remove();
        }
    }
    
    strings = sol.combinations(stringList);
    
    for (String string : strings) {
      System.out.println(string);
    }
  }
  
  // Algorithm: we keep building partial combinations and store them in a queue, then poping the partial 
  // combinations from queue in first come first out way and combine them with another list to build bigger 
  // combinations by. We just keep doing this operation until we run out of all the list and we get the final
  // combinations.
  // [a, b], [c, d], [e, f]  => [a, b] => [b, ac, ad], [ac, ad, bc, bd] => [ad, bc, bd, ace, acf] ... doing this for
  // ad, bc and bd then we get[ace, acf, ade, adf, bce, bcf, bde, bdf]
  // average length: l; Time complexity: l + l^2 + l^3 + l^4 + l^5 .... + l^n
	public ArrayList<String> combinations(ArrayList<ArrayList<String>> strings) {
		strings.remove(null);
		LinkedList<String> list = new LinkedList<>();
		list.add("");
		for (int i = 0; i < strings.size(); i++) {
			// combine each existing element of list with elements of next string list
			while (list.get(0).split(" ").length == i + 1) {
				String tmp = list.remove();
				for (String str : strings.get(i)) {
					list.add((tmp + " " + str));
				}
			}
		}
		// Remove redundant heading space
		for(int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i).trim());
		}
		return new ArrayList<String>(list);
	}
}

/*
You're given a vector of vectors of words, e.g.: 
[['quick', 'lazy'], ['brown', 'black', 'grey'], ['fox', 'dog']]. 

Write a generalized function that prints all combinations of one word from the first vector, one word from the second vector, etc. 
The solution may not use recursion. 
NOTE: the number of vectors and number of elements within each vector may vary.

For the input above, it should print (in any order): 
quick brown fox 
quick brown dog 
quick black fox 
quick black dog 
... 
lazy grey dog
*/

