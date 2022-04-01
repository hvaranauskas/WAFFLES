package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.models.Place;

import java.util.Date;

public class BinarySearch {

    // Returns index of 'key' in 'sortedArray', if it finds it
    // Returns -1 if 'key' is not found in 'sortedArray'
    public static int search(Object[] sortedArray, Object key, int low, int high, boolean ascending) {
        int middle = (low+high)/2;

        // Occurs only when 'key' find is not present in the array
        if (high < low) {
            return -1;
        }

        // Checks if the search algorithm has  the found the desired place coordinates if we are searching for a PlaceCoords object
        if (key.getClass() == Place.class && sortedArray[middle].getClass() == Place.class) {
            if (((Place) key).getLatitude() == ((Place) sortedArray[middle]).getLatitude() && ((Place) key).getLongitude() == ((Place) sortedArray[middle]).getLongitude()) {
                return middle;
            }
        }
        if (key.equals(sortedArray[middle])) {
            return middle;
        } else if (Comparer(key,sortedArray[middle],ascending)) {
            return search(sortedArray, key, low, middle - 1, ascending);
        } else {
            return search(sortedArray, key, middle + 1, high, ascending);
        }
    }

    // Tells us where the 'middle' is in the array in relation to the object we are trying to find
    // Returns true if obj1 is before obj2 in the array
    // Returns false if obj2 is before obj1 in the array
    public static boolean Comparer(Object obj1, Object obj2, boolean ascending) {
        float n;

        if (obj1.getClass().equals(String.class) && obj2.getClass().equals(String.class)) {
            n = ((String) obj1).compareTo((String) obj2);
        }
        else if (obj1.getClass().equals(Character.class) && obj2.getClass().equals(Character.class)) {
            n = ((Character) obj1).compareTo((Character) obj2);
        }
        else if (obj1.getClass() == Date.class && obj2.getClass() == Date.class) {
            n = ((Date) obj1).compareTo((Date)obj2);
        }
        else if (obj1.getClass() == Place.class && obj2.getClass() == Place.class) {
            n = ((Place) obj1).getLatitude() - ((Place) obj2).getLatitude();
            if (n == 0.0f) {
                n = ((Place) obj1).getLongitude() - ((Place) obj2).getLongitude();
            }
        }
        else {
            n = Float.parseFloat( String.valueOf(obj1) ) - Float.parseFloat( String.valueOf(obj2) );
        }

        if (n < 0 && ascending == true || n > 0 && ascending == false) return true;
        else if (n > 0 && ascending == true || n < 0 && ascending == false) return false;
        else return true;
    }

}
