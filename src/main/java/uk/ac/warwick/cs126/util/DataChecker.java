package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.interfaces.IDataChecker;

import uk.ac.warwick.cs126.models.Customer;
import uk.ac.warwick.cs126.models.Restaurant;
import uk.ac.warwick.cs126.models.Favourite;
import uk.ac.warwick.cs126.models.Review;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DataChecker implements IDataChecker {

    public DataChecker() {
        // Initialise things here
    }

    public Long extractTrueID(String[] repeatedID) {
        if (repeatedID == null) return null;

        if (repeatedID.length != 3) return null;

        // Compares all IDs to one another
        // If any of the IDs are equal to eachother, it means a 'consensus' is reached and the equal IDs are returned
        for (int n = 0; n < 3; n++) {
            for (int i = n+1; i < 3; i++) {
                if (repeatedID[n].equals(repeatedID[i])) return Long.parseLong(repeatedID[n]);
            }
        }
        return null;
    }

    public boolean isValid(Long inputID) {
        if (inputID == null) return false;

        String id = String.valueOf(inputID);
        if (id.length() != 16) return false;
        // 0 is not a valid digit for an ID
        if (id.contains("0")) return false;

        // Tallies how many times each digit appears in the given ID and stores the tally in the array 'tally' under the respective index
        int[] tally = new int[9];
        for (int i = 0; i < id.length(); i++) {
            tally[Integer.parseInt(String.valueOf(id.charAt(i)))-1]++;
        }
        // If any of the elements in the 'tally' array tallied to more than 3, the ID is not valid
        for (int i = 0; i < tally.length; i++) {
            if (tally[i] > 3) return false;
        }
        return true;
    }

    public boolean isValid(Customer customer) {
        if (customer == null) return false;
        if (isValid(customer.getID()) == false) return false;

        Method[] methods = customer.getClass().getMethods();
        try {
            // Invokes all methods with no parameters, essentially invoking all getter methods, except for "toString"...
            // Invoking all getter methods allows the program to check if any field of 'customer' is null
            // If any of the invoked methods return a null value, 'customer' is invalid
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getParameterTypes().length == 0) {
                    Class clazz = methods[i].getReturnType();
                    // "toString" is the only method that has no parameters and isn't a getter, so an exception is made for it
                    if (methods[i].invoke(clazz) == null && methods[i].getName().equals("toString") == false) return false;
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println(e);
        } catch (IllegalArgumentException e){
            System.out.println(e);
        } catch (InvocationTargetException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            System.out.println(e);
        } catch (ExceptionInInitializerError e) {
            System.out.println(e);
        }
        return true;
    }

    // Essentially identical to the 'isValid' method for customers, favourites and reviews, except it invokes all the getter methods of 'restaurant' instead
    public boolean isValid(Restaurant restaurant) {
        if (restaurant == null) return false;
        if (isValid(restaurant.getID()) == false) return false;

        Method[] methods = restaurant.getClass().getMethods();
        try {
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getParameterTypes().length == 0) {
                    Class clazz = methods[i].getReturnType();
                    if (methods[i].invoke(clazz) == null && methods[i].getName().equals("toString") == false) return false;
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println(e);
        } catch (IllegalArgumentException e){
            System.out.println(e);
        } catch (InvocationTargetException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            System.out.println(e);
        } catch (ExceptionInInitializerError e) {
            System.out.println(e);
        }
        return true;
    }

    // Same as 'isValid' methods for customers and restaurants and reviews, but for favourites
    public boolean isValid(Favourite favourite) {
        if (favourite == null) return false;
        if (isValid(favourite.getID()) == false) return false;

        Method[] methods = favourite.getClass().getMethods();
        try {
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getParameterTypes().length == 0) {
                    Class clazz = methods[i].getReturnType();
                    if (methods[i].invoke(clazz) == null && methods[i].getName().equals("toString") == false) return false;
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println(e);
        } catch (IllegalArgumentException e){
            System.out.println(e);
        } catch (InvocationTargetException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            System.out.println(e);
        } catch (ExceptionInInitializerError e) {
            System.out.println(e);
        }
        return true;
    }

    // Same as 'isValid' methods for customers and restaurants and favourites, but for reviews
    public boolean isValid(Review review) {
        if (review == null) return false;
        if (isValid(review.getID()) == false) return false;

        Method[] methods = review.getClass().getMethods();
        try {
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getParameterTypes().length == 0) {
                    Class clazz = methods[i].getReturnType();
                    if (methods[i].invoke(clazz) == null && methods[i].getName().equals("toString") == false) return false;
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println(e);
        } catch (IllegalArgumentException e){
            System.out.println(e);
        } catch (InvocationTargetException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            System.out.println(e);
        } catch (ExceptionInInitializerError e) {
            System.out.println(e);
        }
        return true;
    }
}