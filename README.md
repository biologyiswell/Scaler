# Scaler
Scaler is a smooth and light library that include classes for calculation **Java Object** sizes, which this object size will
occupies in **RAM Memory** when instantiated. **The calculate size from objects represents an approximation from real object size.**

### Example
```java

// @Note This represents a some generic object.
Object object = new Object();

// @Note The method "scaleObject" calculates the object size, and returns this size with an integer value.
// @Note The method "sizeof" that is provided by Scaler class calculates the input object and returns the object size in integer value.
int size = Scaler.sizeof(object);

```
