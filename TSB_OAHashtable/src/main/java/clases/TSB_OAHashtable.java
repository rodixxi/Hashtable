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
    public String toString(){
    String str = "";
    for (int i = 0; i < this.table.length; i++){
        if (this.table[i] != null){
        str += "[";
        str += this.table[i].toString();
        str += "]";
    }
    }
    return str;
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
        V v;
        int i = h((K)key, this.table.length);
        int aux =i;
        try{
        if (this.table[i].key.equals(key)) return this.table[i].value;}
        catch(NullPointerException e){}
        double power = 1;
        double j = 1;
        boolean flag = true;
        
        while (count <= this.table.length && (flag == i + power >= aux)){
            if (this.table[i + (int)power] != null && this.table[i + (int)power].key != null)
                
            if (key.equals(this.table[i + (int)power])){
                return this.table[i + (int)power].value;
            }
            j++; 
            power = Math.pow(j, 2);
            count ++;
            if (i + (int)power >= this.table.length) {
                i -= this.table.length; 
                flag = false;
            } 
            
        }
        v = null;
        //if (v == null) throw new NullPointerException("get(): clave no existe");
        return v;
                
    }

    @Override
    public V put(K key, V value) {
        int k = h(key, this.table.length);
        double power = 1, j = 1;
        boolean flag = true;
        
        if (this.table[k] == null){
            this.table[k] = new Entry(key, value);
            this.count++;
            this.modCount++;
            if (this.count >= this.table.length / 2) rehash();
            return value;
        }
        
        while (this.table[k + (int)power] != null && this.table[k + (int)power].key != null){
            j++; 
            power = Math.pow(j, 2);
                        
        }
        this.table[k + (int)power] = new Entry(key, value);
        return value;
    }

    @Override
    public V remove(Object key) {
        if(key == null) throw new NullPointerException("remove(): parámetro null");
       
        int i = this.h(key.hashCode()), count = 0;
        V old = null;
        double power = 1;
        double j = 1;
        boolean found = false;
        
        while ((this.table[i + (int)power].key != null || this.table[i + (int)power].key != null) && !found){
            if (key.equals(this.table[i + (int)power])){
                found = true;
                break;
            }
            j++; 
            power = Math.pow(j, 2);
            count ++;
            if (count == this.table.length) break;
            if (i >= this.table.length) i -= this.table.length;      
        }       
        if (found){
            old = this.table[i + (int)power].value;
            this.table[i + (int)power].key = null;
            this.count--;
            this.modCount++;
            return old;
        }
        return null;
    }
    
    public Map.Entry<K, V> remove(int k){
    Map.Entry<K, V> v = this.table[k];
    this.table[k].key = null;
    return v;
    }
    
    private static final int siguientePrimo ( int n )
    {
        n *= 2;
        n++;
        for ( ; !esPrimo(n); n+=2 ) ;
        return n;
    }
    
    private static boolean esPrimo(int num) {
        if (num % 2 == 0) return false;
        for (int i = 3; i * i < num; i += 2)
            if (num % i == 0) return false;
        return true;
    }
    
    protected void rehash()
    {
        int old_length = this.table.length;
        
        // nuevo tamaño: doble del anterior pero primo
        int new_length = siguientePrimo(old_length);
        
        // no permitir que la tabla tenga un tamaño mayor al límite máximo...
        // ... para evitar overflow y/o desborde de índices...
        if(new_length > TSB_OAHashtable.MAX_SIZE) 
        { 
            new_length = TSB_OAHashtable.MAX_SIZE;
        }

        
        Entry<K, V> temp[] = new Entry[new_length];
                
        // notificación fail-fast iterator... la tabla cambió su estructura...
        this.modCount++;  
       
        // recorrer el viejo arreglo y redistribuir los objetos que tenia...
        for(int i = 0; i < this.table.length; i++)
        {
            Entry<K, V> x = this.table[i];
            if (x != null){
                K key = x.getKey();
                int y = this.h(key, temp.length);
                temp[y] = x;
            }
           }
        
       
        // cambiar la referencia table para que apunte a temp...
        this.table = temp;
    }
    /* Función hash. Toma una clave entera k y calcula y retorna un índice 
     * válido para esa clave para entrar en la tabla.     
     */
    private int h(int k)
    {
        return h(k, this.table.length);
    }
    
    /*
     * Función hash. Toma un objeto key que representa una clave y calcula y 
     * retorna un índice válido para esa clave para entrar en la tabla.     
     */
    private int h(K key)
    {
        return h(key.hashCode(), this.table.length);
    }
    
    /*
     * Función hash. Toma un objeto key que representa una clave y un tamaño de 
     * tabla t, y calcula y retorna un índice válido para esa clave dedo ese
     * tamaño.     
     */
    private int h(K key, int t)
    {
        return h(key.hashCode(), t);
    }
    
    /*
     * Función hash. Toma una clave entera k y un tamaño de tabla t, y calcula y 
     * retorna un índice válido para esa clave dado ese tamaño.     
     */
    private int h(int k, int t)
    {
        if(k < 0) k *= -1;
        return k % t;        
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
                Map.Entry<K, V> garbage = TSB_OAHashtable.this.remove(current_entry);

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
