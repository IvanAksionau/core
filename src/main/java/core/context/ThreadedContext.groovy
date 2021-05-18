package core.context

/**
 * Implementation of {@link IContext}.
 */
class ThreadedContext implements IContext {

    private final IContext context = new Context()

    @Override
    void put(Object key, Object value) {
        context.put(key, value)
    }

    @Override
    void remove(Object key) {
        context ? context.remove(key) : void
    }

    @Override
    <T> T get(Object key) {
        context.get(key)
    }

    @Override
    <T> T get(Object key, Class<T> type) {
        context.get(key, type)
    }

    @Override
    void clear() {
        context.clear()
        context.remove()
    }
}
