/*
 * Copyright 2012 Michael Bischoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jpaw.bonaparte.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.meta.ClassDefinition;

public class BonaPortableFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(BonaPortableFactory.class);

    // Skip the next field and PathResolverTest may fail. Required to load the meta data classes in the correct order, required due to cyclic dependencies
    // of static fields and their initialization.
    public static final Object UNUSED = ClassDefinition.class$MetaData();
    // it will definitely fail if for example FieldDefinition is loaded before ClassDefinition

    /** Stub to call to force initialization, in case no other initialization is required. */
    public static void init() {
    }

    private static final ConcurrentMap<String, BonaPortableClass<? extends BonaPortable>> mapByPQON = new ConcurrentHashMap<String, BonaPortableClass<? extends BonaPortable>>(2048);
    private static final ConcurrentMap<String, BonaPortableClass<? extends BonaPortable>> mapByFQON = new ConcurrentHashMap<String, BonaPortableClass<? extends BonaPortable>>(1024);

    private static String bonaparteClassDefaultPackagePrefix = "de.jpaw.bonaparte.pojos";
    private static Map<String, String> packagePrefixMap = new ConcurrentHashMap<String,String>(16);
    static {
        // mappings for bonaparte-core. Install a fresh map if you don't want these. Otherwise, add single mappings to them, or overwrite these
        packagePrefixMap.put("bonaparte", "de.jpaw.bonaparte");                                 // bonaparte-core, sub-packages core, meta, ui
        packagePrefixMap.put("meta",      bonaparteClassDefaultPackagePrefix + ".meta");        // bonaparte-core
        packagePrefixMap.put("ui",        bonaparteClassDefaultPackagePrefix + ".ui");          // bonaparte-core
        packagePrefixMap.put("api",       bonaparteClassDefaultPackagePrefix + ".api");         // bonaparte-api
        packagePrefixMap.put("apip",      bonaparteClassDefaultPackagePrefix + ".apip");        // bonaparte-api (primitive long primary keys)
        packagePrefixMap.put("apiw",      bonaparteClassDefaultPackagePrefix + ".apiw");        // bonaparte-api (object wrapped long primary keys)
        packagePrefixMap.put("adapters",  bonaparteClassDefaultPackagePrefix + ".adapters");    // bonaparte-adapters-*
    }
    private static class HiddenClass {
    }
    private static final HiddenClass A_WAY_TO_GET_MY_CLASSLOADER = new HiddenClass();
    private static ClassLoader classLoaderToUse = null;

    public static final String BONAPARTE_DEFAULT_PACKAGE_PREFIX = "bonapartePrefix"; // "BONAPARTE_DEFAULT_PACKAGE_PREFIX";  // system property name which can be used as a source
    public static boolean publishDefaultPrefix = true;          // required in environments which instantiate multiple classloaders for isolation (vert.x)
    private static boolean bonaparteClassDefaultPackagePrefixShouldBeRetrieved = true;
    private static final AtomicInteger initializationCounter = new AtomicInteger();

    // prevent instance creation
    private BonaPortableFactory() {
    }

    public static void useFixedClassLoader(ClassLoader loader) {
        classLoaderToUse = loader == null ? A_WAY_TO_GET_MY_CLASSLOADER.getClass().getClassLoader() : loader;
    }

    /** Maps the partially qualified object name (PQON) into a fully qualified name / canonical name.
    * Returns null if no mapping specific for this package is found.
    * Examines the map packagePrefixMap, looking for specific packages first.
    * This means it is possible to map test.zzz.* to another package than test.*.
    * */
    public static String mapPackage(String name) {
        int lastDot = name.lastIndexOf('.');  // duplicate evaluation accepted for sake of API simplicity
        while (lastDot > 0) {
            String mappedPackagePart = packagePrefixMap.get(name.substring(0, lastDot));
            if (mappedPackagePart != null) {
                return mappedPackagePart + name.substring(lastDot);
            }
            // try another attempt, looking at a prior dot
            lastDot = name.lastIndexOf('.', lastDot-1);
        }
        return null;
    }

    /** For a partially qualified name, return the fully qualified name of the class.
     * Uses the default prefix no no specific mapping has been found. */
    public static String mapPqonToFqon(String name) throws MessageParserException {
        String FQON = null;
        int lastDot = name.lastIndexOf('.');
        if ((lastDot == 0) || (lastDot >= (name.length() - 1))) {
            throw new MessageParserException(MessageParserException.BAD_OBJECT_NAME, null, -1, name);
        }
        if (packagePrefixMap != null && lastDot > 0) {
            FQON = mapPackage(name);  // attempt, perhaps returns null
        }

        if (FQON != null)
            return FQON;
        else
            return getBonaparteClassDefaultPackagePrefix() + "." + name;
    }

    // generalized factory: create an instance of the requested type, which is specified by partially qualified name.
    // We receive PQCN (partially qualified class name) as parameter.
    // Anything before the last '.' is the Bonaparte package name (which is null is there is no '.').
    // The package determines the possible bundle specification, not-null bundles are loaded in their
    // own classloaders, so they can be unloaded again.
    // Package to bundle mapping is contained in the static class data, however we cannot known that
    // before actually loading the class. Therefore, bundle information must be fed in separately
    // and can only be consistency-checked afterwards.


    // new method, caches the BClass, to avoid reflection to create a new instance
    // parameter name is the PQON of the desired class
    public static BonaPortable createObject(String pqon) throws MessageParserException {
        final BonaPortableClass<? extends BonaPortable> bclass = mapByPQON.get(pqon);
        if (bclass != null)
            return bclass.newInstance();  // new instance without reflection

        // determine fully qualified pqon of class and use reflection to retrieve an instance the first time
        BonaPortable instance = createObjectSub(mapPqonToFqon(pqon));

        // store it in the cache for next time
        mapByPQON.putIfAbsent(pqon, instance.ret$BonaPortableClass());
        return instance;
    }

    public static BonaPortable createObjectByFqon(String fqon) throws MessageParserException {
        final BonaPortableClass<? extends BonaPortable> bclass = mapByFQON.get(fqon);
        if (bclass != null)
            return bclass.newInstance();  // new instance without reflection

        // determine fully qualified pqon of class and use reflection to retrieve an instance the first time
        BonaPortable instance = createObjectSub(fqon);

        // store it in the cache for next time
        mapByFQON.putIfAbsent(fqon, instance.ret$BonaPortableClass());
        return instance;
    }

    private static BonaPortable createObjectSub(String FQON) throws MessageParserException {
        try {
            LOGGER.debug("Factory: loading class {}", FQON);
            final ClassLoader loader = classLoaderToUse == null ? Thread.currentThread().getContextClassLoader() : classLoaderToUse;
            final Class<? extends BonaPortable> f = Class.forName(FQON, true, loader).asSubclass(BonaPortable.class);
            return f.newInstance();
        } catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFound exception for {}", FQON);
            logClassloaders();
        } catch (InstantiationException e) {
            LOGGER.error("Instantiation exception for {}", FQON);
            logClassloaders();
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccess exception for {}", FQON);
            logClassloaders();
        }
        throw new MessageParserException(MessageParserException.CLASS_NOT_FOUND, "class", 0, FQON);
    }


    public static BonaPortableClass<? extends BonaPortable> getBClassForPqon(String pqon) throws MessageParserException {
        BonaPortableClass<? extends BonaPortable> bclass = mapByPQON.get(pqon);
        if (bclass == null) {
            bclass = createObjectSub(mapPqonToFqon(pqon)).ret$BonaPortableClass();
            // also cache it now
            mapByPQON.putIfAbsent(pqon, bclass);
        }
        return bclass;
    }

    public static BonaPortableClass<? extends BonaPortable> getBClassForFqon(String fqon) throws MessageParserException {
        BonaPortableClass<? extends BonaPortable> bclass = mapByFQON.get(fqon);
        if (bclass == null) {
            bclass = createObjectSub(fqon).ret$BonaPortableClass();
            // also cache it now
            mapByFQON.putIfAbsent(fqon, bclass);
        }
        return bclass;
    }

    // auto getters and setters only following
    public static Map<String, String> getPackagePrefixMap() {
        return packagePrefixMap;
    }

    public static void setPackagePrefixMap(Map<String, String> packagePrefixMap) {
        BonaPortableFactory.packagePrefixMap = packagePrefixMap;
    }

    public static void logClassloaders() {
        LOGGER.info("My classloader is {}, thread context classloader is {}",
                A_WAY_TO_GET_MY_CLASSLOADER.getClass().getClassLoader().toString(),
                Thread.currentThread().getContextClassLoader().toString());
    }

    public static String getBonaparteClassDefaultPackagePrefix() {
        if (bonaparteClassDefaultPackagePrefixShouldBeRetrieved) {
            String possibleOverride = System.getProperty(BONAPARTE_DEFAULT_PACKAGE_PREFIX);
            // bonaparteClassDefaultPackagePrefix = System.getProperty(BONAPARTE_DEFAULT_PACKAGE_PREFIX, bonaparteClassDefaultPackagePrefix);
            if (possibleOverride != null) {
                bonaparteClassDefaultPackagePrefix = possibleOverride;
                LOGGER.info("Setting default package prefix to {} from system property", bonaparteClassDefaultPackagePrefix);
                if (LOGGER.isDebugEnabled())
                    logClassloaders();
            }
            bonaparteClassDefaultPackagePrefixShouldBeRetrieved = false;
        }
        return bonaparteClassDefaultPackagePrefix;
    }

    public static void setBonaparteClassDefaultPackagePrefix(
            String bonaparteClassDefaultPackagePrefix) {
        BonaPortableFactory.bonaparteClassDefaultPackagePrefix = bonaparteClassDefaultPackagePrefix;
        bonaparteClassDefaultPackagePrefixShouldBeRetrieved = false;   // I got it from the application now
        if (publishDefaultPrefix) {
            LOGGER.info("Publishing new default package prefix {}, count = {}", bonaparteClassDefaultPackagePrefix, initializationCounter.incrementAndGet());
            if (LOGGER.isDebugEnabled())
                logClassloaders();
            System.setProperty(BONAPARTE_DEFAULT_PACKAGE_PREFIX, bonaparteClassDefaultPackagePrefix);
        }
    }

    public static String addToPackagePrefixMap(String packagePrefix, String newFullPackage) {
        if (newFullPackage == null) {
            // remove a mapping.
            return packagePrefixMap.remove(packagePrefix);
        } else {
            return packagePrefixMap.put(packagePrefix, newFullPackage);
        }
    }
}
