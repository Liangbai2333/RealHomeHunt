package site.liangbai.realhomehunt.util;

public class Ref {
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("could not find a empty constructor for: " + clazz.getSimpleName(), e);
        }
    }
}
