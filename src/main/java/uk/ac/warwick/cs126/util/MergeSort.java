package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.models.Customer;
import uk.ac.warwick.cs126.models.Restaurant;
import uk.ac.warwick.cs126.models.RestaurantDistance;

import java.lang.reflect.Method;
import java.util.Date;

public class MergeSort {

    public MergeSort() {
    }

    // Each element in 'comparators' contains the name of a getter method of each object in 'elements'. The strings are names of getter methods of fields we wish to compare to decide the order of the objects in the sorted array, in order of importance.
    // 'comparators' should be a String array with element at index 0 "" if you want to compare the objects themselves rather than fields of the objects.
    // Each element of 'ascending' matches up to each element of the same index in 'comparators'. If an element is true, we want the fields (or objects) to be ascending (or in alphabetical order), and vice versa.
    public static Object[] sort(Object[] elements, String[] comparators, boolean[] ascending) {
        if (elements.length < 2) {
            return elements;
        }
        int midpoint = elements.length / 2;
        Object[] halfOne = new Object[midpoint];
        Object[] halfTwo = new Object[elements.length - midpoint];

        for (int i = 0; i < midpoint; i++) {
            halfOne[i] = elements[i];
        }
        for (int i = midpoint; i < elements.length; i++) {
            halfTwo[i - midpoint] = elements[i];
        }
        sort(halfOne, comparators, ascending);
        sort(halfTwo, comparators, ascending);
        return merge(elements, halfOne, halfTwo, midpoint, elements.length - midpoint, comparators, ascending);
    }

    private static Object[] merge(Object[] sortedArray, Object[] halfOne, Object[] halfTwo, int left, int right, String[] comparators, boolean[] ascending) {
        int i = 0, j = 0, k = 0;
        while (i < left && j < right) {
            if (comparer(halfOne[i], halfTwo[j], comparators, ascending)) {
                sortedArray[k++] = halfOne[i++];
            } else {
                sortedArray[k++] = halfTwo[j++];
            }
        }
        while (i < left) {
            sortedArray[k++] = halfOne[i++];
        }
        while (j < right) {
            sortedArray[k++] = halfTwo[j++];
        }
        return sortedArray;
    }

    // Returns true if obj1 should come before obj2 in the sorted array
    // Returns false if obj1 should come after obj2 in the sorted array
    // Decides the ordering in the sorted array depending on what fields of the objects we want to order (given by 'comparators') and whether the fields should be in ascending or descending order (given by 'ascending')
    private static boolean comparer(Object obj1, Object obj2, String[] comparators, boolean[] ascending) {
        float n;
        // val1 and val2 are the fields of the object that are going to be compared
        Object val1;
        Object val2;

        try {

            for (int i = 0; i < comparators.length; i++) {

                // If there are no comparators given, the fields being compared are the objects themselves
                if (comparators[0].equals("")) {
                    val1 = obj1;
                    val2 = obj2;
                } else {
                    // Invokes methods of obj1 and obj2 based of the comparators given in 'comparators' and sets val1 and val2 equal to the return values of the invoked methods
                    Method method1 = obj1.getClass().getMethod(comparators[i]);
                    Method method2 = obj2.getClass().getMethod(comparators[i]);
                    val1 = method1.invoke(obj1);
                    val2 = method2.invoke(obj2);
                }

                // Sets a value of n depending on the ordering of the two fields val1 and val2
                // Since this method is a generic method, have to identify the type of the variables and use the appropriate comparison method
                if (val1.getClass().equals(String.class) && val2.getClass().equals(String.class) || val1.getClass().equals(char.class) && val2.getClass().equals(char.class)) {
                    n = ((String) val1).compareTo((String) val2);
                } else if (val1.getClass().equals(Date.class) && val2.getClass().equals(Date.class)) {
                    n = ((Date) val1).compareTo((Date) val2);
                } else {
                    n = Float.parseFloat(String.valueOf(val1)) - Float.parseFloat(String.valueOf(val2));
                }

                // Returns whether obj1 should come before obj2 or vice versa based on the value of n and whether we want the sorted array to be ascending or descending, given by the value of the array 'ascending'
                // If val1 and val2 are equal, for loop makes it so that obj1 and obj2 are compared using the next comparator given in the array 'comparators'
                if (ascending[i] == true && n < 0 || ascending[i] == false && n > 0) return true;
                else if (ascending[i] == true && n > 0 || ascending[i] == false && n < 0) return false;

            }

        } catch (Exception e) {
            System.out.println(e);
        }

        // If both objects are equal, their order doesn't matter so we can return either true or false without negative consequences
        return true;
    }
}