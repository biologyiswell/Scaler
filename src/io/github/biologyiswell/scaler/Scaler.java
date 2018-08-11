package io.github.biologyiswell.scaler;

import java.lang.reflect.Field;

/**
 * @author biologyiswell (26/07/2018 18:28)
 * @since 1.0
 * @version 1.1
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

        // @Note The size starts with 8 bytes, because the each class use 8 bytes.
        int size = 8;

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
                        final Object currentObjectArray = field.get(object);

                        if (currentObjectArray == null) {
                            continue;
                        }

                        // @Note The boilerplate code that this switch turn is because each primitive data type can not
                        // be cast by a Object[], then the each data type must have your cast.

                        switch (type.charAt(7)) {
                            case 'Z': size += ARRAY_BYTES + BOOLEAN_BYTES * ((boolean[]) currentObjectArray).length;
                                break;
                            case 'B': size += ARRAY_BYTES + Byte.BYTES * ((byte[]) currentObjectArray).length;
                                break;
                            case 'S': size += ARRAY_BYTES + Short.BYTES * ((short[]) currentObjectArray).length;
                                break;
                            case 'C': size += ARRAY_BYTES + Character.BYTES * ((char[]) currentObjectArray).length;
                                break;
                            case 'I': size += ARRAY_BYTES + Integer.BYTES * ((int[]) currentObjectArray).length;
                                break;
                            case 'F': size += ARRAY_BYTES + Float.BYTES * ((float[]) currentObjectArray).length;
                                break;
                            case 'D': size += ARRAY_BYTES + Double.BYTES * ((double[]) currentObjectArray).length;
                                break;
                            case 'J': size += ARRAY_BYTES + Long.BYTES * ((long[]) currentObjectArray).length;
                                break;
                            case 'L':
                                // @Note This condition check if the type is a String or is a generic object.
                                if (type.equals("class [Ljava.lang.String") || type.equals("class [Ljava.lang.Object")) {
                                    size += ARRAY_BYTES + BOOLEAN_BYTES * ((Object[]) currentObjectArray).length;
                                }

                                // @Note This represents that the data-type from the array is not a primitive type and is not
                                // string and generic object, then the size from the class of this data type must be
                                // calculated and used to arrive more near to right.
                                else {
                                    final Object[] array = (Object[]) currentObjectArray;

                                    size += ARRAY_BYTES;

                                    for (Object element : array) {
                                        // @Note This condition check if the element that contains in array is null,
                                        // because the method "sizeof" check if the object is null and throws
                                        // an exception.
                                        if (element == null) {
                                            continue;
                                        }

                                        size += sizeof(element);
                                    }
                                }
                                break;
                            default:
                                throw new RuntimeException("Field type (" + type + ") can not be parse.");
                        }
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
                        // size += currentObject != null ? this.sizeof(currentObject) : this.scaleClass(Class.forName(field.getType().toString().substring(6)));
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
}
