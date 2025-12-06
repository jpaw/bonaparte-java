# Deprecated Constructors Analysis and Fixes

This document summarizes the analysis of deprecated constructors in the Bonaparte Java codebase and the replacements that were made.

## Executive Summary

A comprehensive scan of all Java source files was performed to identify deprecated constructors. The following deprecated constructors were found and fixed:

- **9 deprecated wrapper class constructors** were replaced with factory methods
- **0 deprecated Date constructors** with parameters (none found)
- **BigInteger and BigDecimal string constructors are NOT deprecated** and require no changes

## Detailed Findings

### 1. Integer Constructor (Deprecated since Java 9)

**Deprecated:** `new Integer(int)`  
**Replacement:** `Integer.valueOf(int)`  
**Reason:** The factory method uses caching for values between -128 and 127, improving performance and memory usage.

#### Occurrences Fixed (5 total):

1. **AbstractCompactParser.java:906**
   ```java
   // Before:
   return new Integer(((c & 15) << 8) | needToken());
   // After:
   return Integer.valueOf(((c & 15) << 8) | needToken());
   ```

2. **AbstractCompactParser.java:966**
   ```java
   // Before:
   return new Integer(readFixed2ByteInt());
   // After:
   return Integer.valueOf(readFixed2ByteInt());
   ```

3. **AbstractCompactParser.java:968**
   ```java
   // Before:
   return new Integer(readFixed3ByteInt());
   // After:
   return Integer.valueOf(readFixed3ByteInt());
   ```

4. **AbstractCompactParser.java:970**
   ```java
   // Before:
   return new Integer(readFixed4ByteInt());
   // After:
   return Integer.valueOf(readFixed4ByteInt());
   ```

5. **AnalyzerWorkerFactory.java:232**
   ```java
   // Before:
   private Integer mergeLock = new Integer(876511);
   // After (improved):
   private final Object mergeLock = new Object();
   ```
   **Note:** This was changed to use a plain Object instead of a boxed Integer, which is better practice for synchronization locks. Using boxed types as locks can lead to unintended behavior and the magic number had no semantic meaning.

### 2. Long Constructor (Deprecated since Java 9)

**Deprecated:** `new Long(long)`  
**Replacement:** `Long.valueOf(long)`  
**Reason:** The factory method uses caching for values between -128 and 127, improving performance and memory usage.

#### Occurrences Fixed (2 total):

1. **AbstractCompactParser.java:972**
   ```java
   // Before:
   return new Long(readFixed6ByteLong());
   // After:
   return Long.valueOf(readFixed6ByteLong());
   ```

2. **AbstractCompactParser.java:974**
   ```java
   // Before:
   return new Long(readFixed8ByteLong());
   // After:
   return Long.valueOf(readFixed8ByteLong());
   ```

### 3. Double Constructor (Deprecated since Java 9)

**Deprecated:** `new Double(double)` and `new Double(String)`  
**Replacement:** `Double.valueOf(String)`  
**Reason:** It is rarely appropriate to use this constructor. The static factory `valueOf(String)` is generally a better choice, as it is likely to yield significantly better space and time performance.

#### Occurrences Fixed (1 total):

1. **XmlJsonAdapter.java:131**
   ```java
   // Before:
   dst.add(new Double(o.toString()));
   // After:
   dst.add(Double.valueOf(o.toString()));
   ```

### 4. Boolean Constructor (Deprecated since Java 9)

**Deprecated:** `new Boolean(boolean)`  
**Replacement:** `Boolean.valueOf(boolean)`  
**Reason:** The factory method uses caching for TRUE and FALSE, ensuring object reuse and better memory efficiency.

#### Occurrences Fixed (1 total):

1. **FillBoxedTypeArrays.java:39**
   ```java
   // Before:
   for (int i = 0; i < 1001; ++i) booleana[i] = new Boolean((i & 1) == 0);
   // After:
   for (int i = 0; i < 1001; ++i) booleana[i] = Boolean.valueOf((i & 1) == 0);
   ```

## Constructors NOT Deprecated (No Changes Needed)

### 1. Date() No-Arg Constructor

**Status:** NOT deprecated  
**Occurrences:** 10 (all in benchmark/test files)

The no-argument `Date()` constructor that creates a Date object representing the current time is NOT deprecated. Only the constructors with parameters (year, month, day, etc.) are deprecated.

**Files with Date() usage:**
- EventBusCodecBench.java (2 occurrences)
- EventbusMain.java (2 occurrences)
- Benchmark.java (2 occurrences)
- OneThread.java (2 occurrences)
- TestExt.java (2 occurrences)

### 2. BigInteger(String) Constructor

**Status:** NOT deprecated  
**Occurrences:** Multiple (in parsing and test code)

The `BigInteger(String)` constructor is still the standard way to create a BigInteger from a string representation and is NOT deprecated.

**Example files:**
- StringParserUtil.java
- StringCSVParser.java
- ExternalizableParser.java
- AbstractCompactParser.java
- Various test files

### 3. BigDecimal(String) Constructor

**Status:** NOT deprecated  
**Occurrences:** Multiple (in parsing and test code)

The `BigDecimal(String)` constructor is still the standard way to create a BigDecimal from a string representation and is NOT deprecated. Note: `BigDecimal(double)` is discouraged in favor of `BigDecimal.valueOf(double)` or `BigDecimal(String)`, but the string constructor is the preferred method.

**Example files:**
- StringParserUtil.java
- StringCSVParser.java
- Various test files

## Benefits of These Changes

1. **Performance:** Factory methods like `Integer.valueOf()` use caching for common values (-128 to 127), reducing object creation overhead.

2. **Memory Efficiency:** Object reuse through caching reduces memory footprint.

3. **Future Compatibility:** These deprecated constructors may be removed in future Java versions. Using factory methods ensures forward compatibility.

4. **Best Practices:** Following modern Java conventions and recommendations.

## Build and Test Impact

These changes are backward compatible and do not change the behavior of the code. The factory methods return the same wrapper type and have identical semantics to the deprecated constructors.

## Recommendations

1. **Code Reviews:** Consider adding a linting rule to detect deprecated constructor usage in future code reviews.

2. **IDE Configuration:** Configure IDEs to warn about deprecated constructor usage.

3. **Documentation:** Update coding standards to recommend factory methods over deprecated constructors.

## References

- [JEP 277: Enhanced Deprecation](https://openjdk.org/jeps/277)
- [Java Documentation - Deprecated APIs](https://docs.oracle.com/en/java/javase/)
- [Effective Java (3rd Edition) - Item 1: Consider static factory methods instead of constructors](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
