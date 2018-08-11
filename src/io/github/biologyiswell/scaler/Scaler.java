package io.github.biologyiswell.scaler;

import java.lang.reflect.Field;

/**
 * @author biologyiswell (26/07/2018 18:28)
 * @since 1.0
 */
public class Scaler {

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
     * This method calculates the size that the Object when instantiated will occupies in RAM Memory. But this
     * calculation has a margin of error that is increased when the objects constitutes only by Object data types
     * without instantiation and when the class is much bigger with non instantiated objects resuming. However this
     * method calculates the size from an object the maximum possible arrives to more right.
     *
     * @param object the object that will be calculated your size.
     * @return the size from the object.
     * @since 1.0
     */
    public int scaleObject(Object object) {
        // @Note This condition check if the object is null.
        if (object == null) {
            throw new NullPointerException("object");
        }

        // @Note The size starts with 8 bytes, because the each class use 8 bytes.
        int size = 8;

        // @Note This represents the for-each loop from the all declared fields that contains in the object class.
        for (Field field : object.getClass().getDeclaredFields()) {
            String type = field.getType().toString();

            // @Note This condition check if the type starts from "class".
            if (type.equals("class java.lang.String")) {
                // @Note Make the field accessible.
                field.setAccessible(true);

                try {
                    Object stringObject = field.get(object);

                    // @Note The size calculates if the string object is different from null then the object occupies
                    // the 8 bytes from the object more 2 times length of String, otherwise if the string object is
                    // equals  null then the object only occupies the 8 bytes from object.
                    size += stringObject != null ? 8 + (2 * ((String) stringObject).length()) : 8;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                continue;
            } else if (type.startsWith("class")) {
                // @Note This condition check if the object is an array.
                if (type.contains("[")) {
                    // @Note Make the field accessible.
                    field.setAccessible(true);

                    try {
                        // @Note This represents the generic object to reference the array.
                        Object currentObjectArray = field.get(object);

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
                                // @Note This represents that the data-type from the array is not a primive type and is not
                                // string and generic object, then the size from the class of this data type must be
                                // calculated and used to arrive more near to right.
                                else {
                                    // @Note Data Type Size: "this.scaleClass(Class.forName(type.substring(7)))".
                                    size += ARRAY_BYTES + this.scaleClass(Class.forName(type.substring(7))) * ((Object[]) currentObjectArray).length;
                                }
                                break;
                            default:
                                throw new RuntimeException("Field type (" + type + ") can not be parse.");
                        }
                    } catch (IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                // @Note This represents that the object is not an array.
                else {
                    // @Note Make the field accessible.
                    field.setAccessible(true);

                    try {
                        Object currentObject = field.get(object);
                        System.out.println(field.getType());

                        // @Note This condition check if the current object is equals null and if the type name from the
                        // field is equals "class java.lang.Object".
                        if (currentObject == null && type.equals("class java.lang.Object")) {
                            // @Note Sum the default Object size, because can not calculate the fields that contains in
                            // this object because is null. Because the object can be a generic object.
                            size += 8;

                            continue;
                        }

                        size += currentObject != null ? this.scaleObject(currentObject) : this.scaleClass(Class.forName(field.getType().toString().substring(6)));
                    } catch (IllegalAccessException | ClassNotFoundException e) {
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
     * This method calculates the size from the all field data types that contains in the class, this method is only be
     * used when the object from the class that should be calculated is null, then to the current size from the object
     * arrives more near to right this class calculates the field data types size.
     *
     * @Note This object make more generic checks. This can be change in the next versions.
     *
     * @param klass the class that is used to calculated the all field data types.
     * @since 1.0
     */
    private int scaleClass(Class<?> klass) {
        // @Note This condition check if the class is null.
        if (klass == null) {
            throw new NullPointerException("klass");
        }

        // @Note The size starts with 8 bytes, because the each class use 8 bytes.
        int size = OBJECT_BYTES;

        // @Note For-each loop from the all declared fields that contains in this class.
        for (Field field : klass.getDeclaredFields()) {
            String type = field.getType().toString();

            // @Note This condition check if the field data type is a class.
            if (type.startsWith("class")) {
                size += OBJECT_BYTES;

                continue;
            } else if (type.startsWith("class [")) {
                size += ARRAY_BYTES;

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
