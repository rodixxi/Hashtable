/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author jpaganel
 */
public class TSB_OAHashtable <K,V> implements Map<K,V>, Cloneable, Serializable
{
    private Entry<K,V> table[];
    private final static int MAX_SIZE = Integer.MAX_VALUE;
    private int initial_capacity;
    private int count;
    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K,V>> entrySet = null;
    private transient Collection<V> values = null;
    protected transient int modCount;
    
    public TSB_OAHashtable()
    {
        this(5);
    }
    
    public TSB_OAHashtable(int initial_capacity)
    {
        if(initial_capacity <= 0) { initial_capacity = 11; }
        else
        {
            if(initial_capacity > TSB_OAHashtable.MAX_SIZE) 
            {
                initial_capacity = TSB_OAHashtable.MAX_SIZE;
            }
        }
        
        this.table = new Entry[initial_capacity];
        
        this.initial_capacity = initial_capacity;
        this.count = 0;
        this.modCount = 0;
    }
    
    
    public TSB_OAHashtable(Map<? extends K,? extends V> t)
    {
        this(11);
        this.putAll(t);
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException 
    {
        TSB_OAHashtable t = new TSB_OAHashtable(table.length);
        t.table = new Entry[table.length];
        for (int i = table.length ; i-- > 0 ; ) 
        {
            t.table[i] = (Entry<K, V>) table[i].clone();
        }
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        return t;
    } 
    @Override
    public int size() 
    {
        return this.count;
    }

    @Override
    public boolean isEmpty() 
    {
        return (this.count == 0);
    }

    @Override
    public boolean containsKey(Object key) 
    {
        return (this.get((K)key) != null);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.contains(value);
    }
    
    public boolean contains(Object value)
    {
        if(value == null) return false;
        
        for(Entry<K, V> item : this.table) if(value.equals(item.getValue())) return true;
        
        return false;
    }
    
    @Override
    public V get(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        {
        if(entrySet == null) 
        { 
            // entrySet = Collections.synchronizedSet(new EntrySet()); 
            entrySet = new EntrySet();
        }
        return entrySet;
    }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        public EntrySet() {
            
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntrySetIterator();
            
        }

        @Override
        public int size() {
            return 1;
        }
        
        private class EntrySetIterator implements Iterator<Map.Entry<K, V>>
        {
            
            // índice del elemento actual en el iterador (el que fue retornado 
            // la última vez por next() y será eliminado por remove())...
            private int current_entry;
                        
            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;
            
            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;
            
            /*
             * Crea un iterador comenzando en la primera lista. Activa el 
             * mecanismo fail-fast.
             */
            public EntrySetIterator()
            {
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya 
             * sido retornado por next(). 
             */
            @Override
            public boolean hasNext() 
            {
                // variable auxiliar t para simplificar accesos...
                Entry<K, V> t[] = TSB_OAHashtable.this.table;

                if(TSB_OAHashtable.this.isEmpty()) { return false; }
                if (t[current_entry + 1] != null) return true;
                return false;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public Map.Entry<K, V> next() 
            {
                // control: fail-fast iterator...
                if(TSB_OAHashtable.this.modCount != expected_modCount)
                {    
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }
                
                if(!hasNext()) 
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                
                // variable auxiliar t para simplificar accesos...
                Map.Entry<K, V> t[] = TSB_OAHashtable.this.table;
                current_entry++;
                
                // y retornar el elemento alcanzado...
                next_ok = true;
                return t[current_entry];
                
            }
            
            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posición anterior al que fue removido. El elemento removido es el
             * que fue retornado la última vez que se invocó a next(). El método
             * sólo puede ser invocado una vez por cada invocación a next().
             */
            @Override
            public void remove() 
            {
                if(!next_ok) 
                { 
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()..."); 
                }
                
                // eliminar el objeto que retornó next() la última vez...
                Map.Entry<K, V> garbage = TSB_OAHashtable.this.table.remove(current_entry);

                // quedar apuntando al anterior al que se retornó...                
                
                next_ok = false;
                                
                // la tabla tiene un elementon menos...
                TSB_OAHashtable.this.count--;

                // fail_fast iterator: todo en orden...
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;
            }     
        }
    }    
                
        
    
    
    
    private class Entry<K, V> implements Map.Entry<K, V>
    {
        private K key;
        private V value;
        
        public Entry(K key, V value) 
        {
            if(key == null || value == null)
            {
                throw new IllegalArgumentException("Entry(): parámetro null...");
            }
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() 
        {
            return key;
        }

        @Override
        public V getValue() 
        {
            return value;
        }

        @Override
        public V setValue(V value) 
        {
            if(value == null) 
            {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }
                
            V old = this.value;
            this.value = value;
            return old;
        }
       
        @Override
        public int hashCode() 
        {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);            
            return hash;
        }
        @Override 
        public Entry<K, V> clone(){
            Entry<K,V> aux = (Entry<K,V>)this.clone();
            aux.key = this.key;
            aux.value = this.value;
            return aux;
        }
        @Override
        public boolean equals(Object obj) 
        {
            if (this == obj) { return true; }
            if (obj == null) { return false; }
            if (this.getClass() != obj.getClass()) { return false; }
            
            final Entry other = (Entry) obj;
            if (!Objects.equals(this.key, other.key)) { return false; }
            if (!Objects.equals(this.value, other.value)) { return false; }            
            return true;
        }       
        
        @Override
        public String toString()
        {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }
    
}
