package idk.bluecross.proxyParser.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Utils {
    public static <T> Collection<List<T>> chunkList(List<T> inputList, int chunkSize) {
        AtomicInteger counter = new AtomicInteger();
        return inputList.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize)).values();
    }


    public static Object toObject(Class clazz, String value) {
        if (Boolean.class.isAssignableFrom(clazz)) return Boolean.parseBoolean(value);
        if (Byte.class.isAssignableFrom(clazz)) return Byte.parseByte(value);
        if (Short.class.isAssignableFrom(clazz)) return Short.parseShort(value);
        if (Integer.class.isAssignableFrom(clazz)) return Integer.parseInt(value);
        if (Long.class.isAssignableFrom(clazz)) return Long.parseLong(value);
        if (Float.class.isAssignableFrom(clazz)) return Float.parseFloat(value);
        if (Double.class.isAssignableFrom(clazz)) return Double.parseDouble(value);
        if (Collection.class.isAssignableFrom(clazz)) return stringToCollection(clazz, value);
        if (String.class.isAssignableFrom(clazz)) return value;
        throw new RuntimeException("Unknown class to cast: " + clazz.getName());
    }

    public static Collection stringToCollection(Class clazz, String str) {
        return Arrays.stream(str.split(",")).collect(Collectors.toList());
    }

}
