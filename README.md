# Scaler
Scaler is a smooth and light library that include classes for calculation **Java Object** sizes, which this object size will
occupies in **RAM Memory** when instantiated.

### Additional Informations
Scaler is a project in development versions, current version is **1.0** and can be have a bugs. However this project has been
tested and the results is great.

### Example
```java

// @Note Makes the instantiation from Scaler class, which make the calculations from object size.
Scaler scaler = new Scaler();

// @Note This represents a some generic object.
Object someObject = new Object();

// @Note The method "scaleObject" calculates the object size, and returns this size with an integer value.
int size = scaler.scaleObject(someObject);

```
