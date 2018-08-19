package io.github.biologyiswell.scaler;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author biologyiswell (26/07/2018 18:28)
 * @since 1.0
 * @version 1.2
 */
public final class Scaler {

    // non-instantiate.
    private Scaler() {
    }

    /**
     * The Boolean.class do not contains the variable "BYTES" that shows the quantity of bytes that the boolean data
     * types occupies in RAM Memory. Then to make more secure the write from the boolean bytes static final field with
     * the current Boolean Bytes size.
     * @since 1.0
     */
    private static final int BOOLEAN_BYTES = 1;

    /**
     * The Object.class do not contains the variable "BYTES" that shows the quantity of bytes that generic object data
     * type occupies in RAM Memory. Then to make more secure the write from the generic object bytes static final field
     * with the generic object bytes size.
     * @since 1.0
     */
    private static final int OBJECT_BYTES = 8;

    /**
     * Do not exists an Array class in "java.lang" package to show the quantity of bytes that a generic array data type
     * occupies in RAM Memory. Then to make more secure is written the generic Array bytes size in a static final field.
     * The calculation from a generic Array bytes size is: 8 + 4 = 12. The 8 bytes represents that the Array is a object
     * and the 4 bytes represents the length from the Array that is an integer value.
     * @since 1.0
     */
    private static final int ARRAY_BYTES = 12;

    /**
     * Current Java Version from OS that running this project.
     * @since 1.1
     */
    private static final int JAVA_VERSION = Integer.parseInt(System.getProperty("java.version").split("\\.")[1]);

    /**
     * The String.class do not contains the variable "BYTES" that each character that contains in string consumes in
     * RAM Memory, then this variable is created to show the amount of bytes that each chracter that contains in string
     * consumes, then with the implementation from Java 9 the characters from string bacmea byte values.
     * @since 1.1
     */
    private static final int STRING_CHARACTER_BYTES = JAVA_VERSION <= 8 ? 2 : 1;

    /**
     * This method calculates the size that the Object when instantiated will occupies in RAM Memory. But this
     * calculation has a margin of error that is increased when the objects constitutes only by Object data types
     * without instantiation and when the class is much bigger with non instantiated objects resuming. However this
     * method calculates the size from an object the maximum possible arrives to more right.
     *
     * @param object the object that will be calculated your size.
     * @return the size from the object.
     * @since 1.0
     */
    public static int sizeof(final Object object) {
        // @Note This condition check if the object is null.
        if (object == null) {
            throw new NullPointerException("object");
        }
        // @Note Now the collection classes is calculate without using much reflections, and now the calculation is
        // made by other methods.
        if (object instanceof List) {
            return sizeofList((List<?>) object);
        } else if (object instanceof Map) {
            return sizeofMap((Map<?, ?>) object);
        }
        // @Note The size starts with 8 bytes, because the each class use 8 bytes.
        int size = OBJECT_BYTES;
        // @Note This represents the for-each loop from the all declared fields that contains in the object class.
        for (final Field field : object.getClass().getDeclaredFields()) {
            final String type = field.getType().toString();
            // @Note Make the field accessible.
            field.setAccessible(true);
            // @Note This condition check if the current object is a String.
            if (type.equals("class java.lang.String")) {
                try {
                    final String string = (String) field.get(object);
                    // @Note The size calculates if the string object is different from null then the object occupies
                    // the 8 bytes from the object more 2 times length of String, otherwise if the string object is
                    // equals  null then the object only occupies the 8 bytes from object.
                    size += string != null ? OBJECT_BYTES + (STRING_CHARACTER_BYTES * string.length()) : OBJECT_BYTES;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                continue;
            } else if (type.startsWith("class")) {
                // @Note This condition check if the object is an array.
                if (type.contains("[")) {
                    try {
                        // @Note This represents the generic object to reference the array.
                        final Object objectArray = field.get(object);
                        if (objectArray == null) {
                            continue;
                        }
                        // @Note This method calculates the size from the object array.
                        size += sizeofArray(objectArray);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                // @Note This represents that the object is not an array.
                else {
                    try {
                        final Object currentObject = field.get(object);
                        if (currentObject == null) {
                            continue;
                        }
                        size += type.equals("class java.lang.Object") ? OBJECT_BYTES : sizeof(currentObject);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                continue;
            }
            switch (type) {
                case "boolean": size += BOOLEAN_BYTES;
                    break;
                case "byte": size += Byte.BYTES;
                    break;
                case "short": size += Short.BYTES;
                    break;
                case "char": size += Character.BYTES;
                    break;
                case "int": size += Integer.BYTES;
                    break;
                case "float": size += Float.BYTES;
                    break;
                case "double": size += Double.BYTES;
                    break;
                case "long": size += Long.BYTES;
                    break;
                default:
                    throw new RuntimeException("Field Type (" + type + ") has not found to be parse.");
            }
        }
        return size;
    }

    /**
     * This method calculates the size of from a class, this method is used when the object is null and can not get the
     * array values and some others fields, this method make the calculation from the field types to can approximate
     * to the object value.
     *
     * @param klass the input class.
     * @return the size of from a class.
     * @since 1.2
     */
    public static int sizeofClass(final Class<?> klass) {
        int size = OBJECT_BYTES;
        for (final Field field : klass.getDeclaredFields()) {
            // @Note Get the type name from the field to can compare the field types.
            final String type = field.getType().getTypeName();
            if (type.equals("boolean") || type.equals("boolean[]")) size += BOOLEAN_BYTES;
            else if (type.equals("byte") || type.equals("byte[]")) size += Byte.BYTES;
            else if (type.equals("char") || type.equals("char[]")) size += Character.BYTES;
            else if (type.equals("short") || type.equals("short[]")) size += Short.BYTES;
            else if (type.equals("int") || type.equals("int[]")) size += Integer.BYTES;
            else if (type.equals("float") || type.equals("float[]")) size += Float.BYTES;
            else if (type.equals("double") || type.equals("double[]")) size += Double.BYTES;
            else if (type.equals("long") || type.equals("long[]")) size += Long.BYTES;
            else if (type.equals("java.lang.Object") || type.equals("java.lang.String") || type.equals("java.lang.Object[]") || type.equals("java.lang.String[]")) size += OBJECT_BYTES;
            else {
                try {
                    size += sizeofClass(Class.forName(type.substring(0, type.indexOf('['))));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (type.contains("[]")) size += ARRAY_BYTES;
        }
        return size;
    }

    /**
     * This method calculates the size of a map.
     *
     * @param map the input map.
     * @return the size of a map.
     * @since 1.2
     */
    private static int sizeofMap(final Map<?, ?> map) {
        int size = OBJECT_BYTES + (3 * Integer.BYTES) + Float.BYTES;
        if (map.size() == 0) {
            return size;
        }
        final int tableSize = ARRAY_BYTES;
        final int nodeHashSize = Integer.BYTES;
        final Map.Entry<?, ?> entry = map.entrySet().iterator().next();
        final int keySize = sizeofBoxedType(entry.getKey());
        final int valueSize = sizeofBoxedType(entry.getValue());
        return size + tableSize + (map.size() * (keySize + valueSize + nodeHashSize));
    }

    /**
     * This method calculates the size of a list.
     *
     * @param list the input list.
     * @return the size of a list.
     * @since 1.2
     */
    private static int sizeofList(final List<?> list) {
        final int elementDataSize = ARRAY_BYTES;
        final int listSize = Integer.BYTES;
        int size = OBJECT_BYTES + elementDataSize + listSize;
        // @Note This condition check if the list size is equals 0, or empty.
        // Then return the calculation from the only of class size.
        if (list.size() == 0) {
            return size;
        }
        final Object element = list.get(0);
        final int elementSize = sizeofBoxedType(element);
        return size + (list.size() * elementSize);
    }

    /**
     * This method calculates the size of boxed type from a primitive type.
     *
     * @param object the input object that is boxed type.
     * @return the size of boxed type.
     * @since 1.2
     */
    private static int sizeofBoxedType(final Object object) {
        if (object == null) {
            return 0;
        }
        int size = 0;
        // @Note This check of conditions is made because each boxed type from a primitive type contains the value
        // of the primitive type.
        if (object instanceof Boolean) size += BOOLEAN_BYTES;
        else if (object instanceof Byte) size += Byte.BYTES;
        else if (object instanceof Character) size += Character.BYTES;
        else if (object instanceof Short) size += Short.BYTES;
        else if (object instanceof Integer) size += Integer.BYTES;
        else if (object instanceof Float) size += Float.BYTES;
        else if (object instanceof Double) size += Double.BYTES;
        else if (object instanceof Long) size += Long.BYTES;
        else if (object instanceof BigInteger) {
            size += OBJECT_BYTES + (5 * Integer.BYTES);
            int magSize;
            // @Note This represents the size of the mag array that the BigInteger class contains.
            try {
                magSize = sizeofArray(object.getClass().getDeclaredField("mag").get(object));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                magSize = 0;
                e.printStackTrace();
            }

            size += magSize;
        } else if (object instanceof BigDecimal) {
            // @Note This represents the size of a BigInteger, that contains into the BigDecimal.
            size += OBJECT_BYTES + (5 * Integer.BYTES);
            int magSize;
            // @Note This represents the size of the mag array that the BigInteger class contains.
            try {
                magSize = sizeofArray(object.getClass().getDeclaredField("mag").get(object));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                magSize = 0;
                e.printStackTrace();
            }
            // @Note Use the method "toString" from the BigDecimal is a way to get the "stringCache" more fast, without
            // use reflection.
            size += magSize + (2 * Integer.BYTES) + Long.BYTES + (object.toString().length() * STRING_CHARACTER_BYTES);
        }
        else size += sizeofArray(object);
        return size;
    }

    /**
     * This method calculates the size of from an array.
     *
     * @param object the input object that represents an array.
     * @return the size of from an array.
     * @since 1.2
     */
    private static int sizeofArray(final Object object) {
        // @Note This condition check if the object is equals null, then this condition makes the return of zero.
        // Because the method can not calculate a null object.
        if (object == null) {
            return 0;
        }
        // @Note The input parameter object can not be a array because this make the remove of
        // primitive types array.
        final String type = object.getClass().getTypeName();
        // @Note The initial size from a array is the array bytes that each array consumes.
        int size = ARRAY_BYTES;
        if (type.equals("boolean[]")) size += ((boolean[]) object).length * BOOLEAN_BYTES;
        else if (type.equals("byte[]")) size += ((byte[]) object).length * Byte.BYTES;
        else if (type.equals("char[]")) size += ((char[]) object).length * Character.BYTES;
        else if (type.equals("short[]")) size += ((short[]) object).length * Short.BYTES;
        else if (type.equals("int[]")) size += ((int[]) object).length * Integer.BYTES;
        else if (type.equals("float[]")) size += ((float[]) object).length * Float.BYTES;
        else if (type.equals("double[]")) size += ((double[]) object).length * Double.BYTES;
        else if (type.equals("long[]")) size += ((long[]) object).length * Long.BYTES;
        else if (type.equals("java.lang.Object[]")) size += ((Object[]) object).length * OBJECT_BYTES;
        else if (type.equals("java.lang.String[]")) size += ((String[]) object).length * STRING_CHARACTER_BYTES;
        else if (type.equals("java.lang.Boolean[]")) size += ((Boolean[]) object).length * BOOLEAN_BYTES;
        else if (type.equals("java.lang.Byte[]")) size += ((Byte[]) object).length * Byte.BYTES;
        else if (type.equals("java.lang.Character[]")) size += ((Character[]) object).length * Character.BYTES;
        else if (type.equals("java.lang.Short[]")) size += ((Short[]) object).length * Short.BYTES;
        else if (type.equals("java.lang.Integer[]")) size += ((Integer[]) object).length * Integer.BYTES;
        else if (type.equals("java.lang.Float[]")) size += ((Float[]) object).length * Float.BYTES;
        else if (type.equals("java.lang.Double[]")) size += ((Double[]) object).length * Double.BYTES;
        else if (type.equals("java.lang.Long[]")) size += ((Long[]) object).length * Long.BYTES;
        // @Note This represents that the object array can not be found, then this can represents a object
        // then calculates the object size and calculates the array consume.
        else {
            final String className = type.substring(0, type.indexOf('['));
            try {
                size += sizeofClass(Class.forName(className));
            } catch (ClassNotFoundException e) {
                System.err.println("An error occured when parse the array (" + object + ").");
                e.printStackTrace();
            }
        }
        return size;
    }
}
