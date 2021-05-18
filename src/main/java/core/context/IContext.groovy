package core.context

interface IContext {

    /**
     * Store some key-value pair. If the context previously contained a mapping for the key, the old value is
     * replaced by the specified value.
     * @param key a key to associate value with.
     * @param value a value to store.
     */
    void put(Object key, Object value)

    /**
     * Remove a stored key-value pair if it is present.
     * @param key a key associated with a value that should be removed.
     */
    void remove(Object key)

    /**
     * Get the value associated with the specified key.
     * @param key the key whose associated value is to be returned.
     * @return the value to which the specified key is associated.
     */
    public <T> T get(Object key)

    /**
     * Get the value associated with the specified key and cast it to the specified type.
     * @param key the key whose associated value is to be returned.
     * @param type the type to which the returned value should be casted.
     * @return the casted value to which the specified key is associated.
     * @throws ClassCastException if the object is not null and is not assignable to the type T.
     */
    public <T> T get(Object key, Class<T> type)

    /**
     * Remove all stored values.
     */
    void clear()
}
