package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.ICustomerStore;
import uk.ac.warwick.cs126.models.Customer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.HashMap;
import uk.ac.warwick.cs126.util.MergeSort;
import uk.ac.warwick.cs126.structures.ArrayList;
import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.StringFormatter;

public class CustomerStore implements ICustomerStore {

    private HashMap<Long, Customer> customerHashMap;
    private DataChecker dataChecker;
    private ArrayList<Long> blacklist;

    public CustomerStore() {
        // Initialise variables here
        customerHashMap = new HashMap<>(20);
        dataChecker = new DataChecker();
        blacklist = new ArrayList<>();
    }

    public Customer[] loadCustomerDataToArray(InputStream resource) {
        Customer[] customerArray = new Customer[0];

        try {
            byte[] inputStreamBytes = IOUtils.toByteArray(resource);
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int lineCount = 0;
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (!("".equals(line))) {
                    lineCount++;
                }
            }
            lineReader.close();

            Customer[] loadedCustomers = new Customer[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int customerCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");

                    Customer customer = (new Customer(
                            Long.parseLong(data[0]),
                            data[1],
                            data[2],
                            formatter.parse(data[3]),
                            Float.parseFloat(data[4]),
                            Float.parseFloat(data[5])));

                    loadedCustomers[customerCount++] = customer;
                }
            }
            csvReader.close();

            customerArray = loadedCustomers;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return customerArray;
    }

    public boolean addCustomer(Customer customer) {
        if (customer.equals(null)) return false;
        if (dataChecker.isValid(customer) == false) return false;
        if (customerHashMap.get(customer.getID()) != null) {
            blacklist.add(customer.getID());
            customerHashMap.remove(customer.getID());
            return false;
        }

        if (blacklist.contains(customer.getID())) return false;

        // Customers added to the hashmap must have unique IDs, so can use the IDs as keys for the elements in the customer hashmap
        customerHashMap.add(customer.getID(), customer);

        return true;
    }

    public boolean addCustomer(Customer[] customers) {
        if (customers.equals(null)) return false;

        boolean decision = true;
        for (int i = 0; i < customers.length; i++) {
            if (addCustomer(customers[i]) == false)
                decision = false;
        }

        return decision;
    }

    public Customer getCustomer(Long id) {
        if (id == null) return null;
        return customerHashMap.get(id);
    }

    public Customer[] getCustomers() {
        Customer[] customers = customerHashMap.toArray(Customer.class);
        return getCustomers(customers);
    }

    public Customer[] getCustomers(Customer[] customers) {
        if (customers.equals(null)) return new Customer[0];
        String[] comparators = {"getID"};
        boolean[] ascending = {true};
        return (Customer[]) MergeSort.sort(customers,comparators,ascending);
    }

    public Customer[] getCustomersByName() {
        Customer[] customers = customerHashMap.toArray(Customer.class);
        return getCustomersByName(customers);

    }

    public Customer[] getCustomersByName(Customer[] customers) {
        if (customers.equals(null)) return new Customer[0];
        String[] comparators = {"getLastName","getFirstName","getID"};
        boolean[] ascending = {true,true,true};
        return (Customer[]) MergeSort.sort(customers,comparators,ascending);
    }

    public Customer[] getCustomersContaining(String searchTerm) {
        if (searchTerm.equals("")) return new Customer[0];

        String convertedSearchTerm = StringFormatter.convertAccentsFaster(searchTerm).toLowerCase();

        // Removes trailing spaces, leading spaces and turns multiple spaces into single spaces in convertedSearchTerm
        String formattedSearchTerm = convertedSearchTerm.replace("  "," ").trim();
        Customer[] customers = customerHashMap.toArray(Customer.class);
        Customer[] searchResults = new Customer[customers.length];

        int n = 0;
        for (int i = 0; i < customers.length; i++) {
            String convertedFirstName = StringFormatter.convertAccentsFaster(customers[i].getFirstName()).toLowerCase();
            String convertedLastName = StringFormatter.convertAccentsFaster(customers[i].getLastName()).toLowerCase();
            String formattedName = convertedFirstName + " " + convertedLastName.replace("  "," ").trim();

            if (formattedName.contains(formattedSearchTerm) == true) {
                searchResults[n] = customers[i];
                n++;
            }
        }

        Customer[] temp = searchResults;
        searchResults = new Customer[n];
        for (int i = 0; i < searchResults.length; i++) {
            searchResults[i] = temp[i];
        }

        String[] comparators = {"getLastName","getFirstName","getID"};
        boolean[] ascending = {true,true,true};
        Customer[] sortedResults = (Customer[]) MergeSort.sort(searchResults,comparators,ascending);
        return sortedResults;
    }

}
