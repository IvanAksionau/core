package core.context

/**
 * Basic implementation of {@link IContext}.
 * Is backed by ${@link LinkedHashMap}.
 */
class Context implements IContext {

    private final Map<Object, Object> map = [:]

    @Override
    void put(Object key, Object value) {
        map.put(key, value)
    }

    @Override
    void remove(Object key) {
        map.remove(key)
    }

    @Override
    <T> T get(Object key) {
        (T) map.get(key)
    }

    @Override
    <T> T get(Object key, Class<T> type) {
        type.cast(map.get(key))
    }

    @Override
    void clear() {
        map.clear()
    }
}
