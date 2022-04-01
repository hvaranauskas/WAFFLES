package uk.ac.warwick.cs126.structures;

import java.lang.reflect.Array;

public class HashMap<K extends Comparable<K>,V> {

    protected KeyValuePairLinkedList[] table;
    int size;
    private V[] elements;

    public HashMap(int size) {
        table = new KeyValuePairLinkedList[size];
        initTable();
        this.size = 0;
    }

    protected void initTable() {
        for(int i = 0; i < table.length; i++) {
            table[i] = new KeyValuePairLinkedList<>();
        }
    }

    protected int hash(K key) {
        int code = key.hashCode();
        return Math.abs(code);
    }


    public void add(K key, V value) {

        int hash_code = hash(key);
        int location = hash_code % table.length;

        // Doubles the size of the table and reassigns all elements in the hashmap to new locations
        // Keeps the hash map efficient by making sure the linked lists aren't too full
        if (table[location].size() > 5) {
            ListElement<KeyValuePair<K,V>> ptr;
            KeyValuePairLinkedList[] temp = new KeyValuePairLinkedList[table.length*2];
            for (int i = 0; i < table.length; i++) {
                do {

                    ptr = table[i].head;
                    V v = ptr.getValue().getValue();
                    K k = ptr.getValue().getKey();

                    hash_code = hash(k);
                    location = hash_code % temp.length;
                    temp[location].add(k, v);

                    ptr = ptr.getNext();

                } while (ptr != null);
            }
            table = temp;
            hash_code = hash(key);
            location = hash_code % table.length;
        }

        table[location].add(key,value);
        size++;
    }

    public V get(K key) {
        int hash_code = hash(key);
        int location = hash_code % table.length;

        ListElement<KeyValuePair> ptr = table[location].head;

        KeyValuePair<K,V> kvp = table[location].get(key);
        if (kvp == null) return null;
        else return kvp.getValue();
    }

    // Returns an array of all the objects in the hashmap
    // Array is not sorted
    public V[] toArray(Class<V> clazz) {
        V[] elements = (V[]) Array.newInstance(clazz,this.size);
        int n = 0;
        for (int i = 0; i < table.length; i++) {
            ListElement<KeyValuePair<K,V>> ptr = table[i].head;
            while (ptr != null) {
                elements[n] = ptr.getValue().getValue();
                ptr = ptr.getNext();
                n++;
            }
        }
        return elements;
    }

    public boolean remove(K key) {
        if (key == null) return false;
        int hash_code = hash(key);
        int location = hash_code % table.length;

        table[location].remove(key);
        size--;
        return true;
    }

    public int size() { return size; }
}
