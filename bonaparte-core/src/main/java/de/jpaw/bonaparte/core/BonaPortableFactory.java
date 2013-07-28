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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BonaPortableFactory {
    private static final Logger logger = LoggerFactory.getLogger(BonaPortableFactory.class);
    static private ConcurrentMap<String, Class<? extends BonaPortable>> map = new ConcurrentHashMap<String, Class<? extends BonaPortable>>();
    static private String bonaparteClassDefaultPackagePrefix = "de.jpaw.bonaparte.pojos";
    static private Map<String, String> packagePrefixMap = new ConcurrentHashMap<String,String>(10);
    static {
        // mappings for bonaparte-core. Install a fresh map if you don't want these. Otherwise, add single mappings to them, or overwrite these
        packagePrefixMap.put("meta", bonaparteClassDefaultPackagePrefix + ".meta");         // bonaparte-core
        packagePrefixMap.put("wrappers", bonaparteClassDefaultPackagePrefix + ".wrappers"); // bonaparte-core
        packagePrefixMap.put("money", bonaparteClassDefaultPackagePrefix + ".money");       // bonaparte-money
    }

    private static void registerClass(String name, Class<? extends BonaPortable> clatz) {
        map.putIfAbsent(name, clatz);
        logger.debug("Factory: registered class {}", name);
    }

    // prevent instance creation
    private BonaPortableFactory() {
    }

    // map the object name into a full package name
    // returns null if no mapping specific for this package is found
    // examines the map packagePrefixMap, looking for specific packages first
    // this means it is possible to map test.zzz.* to another package than test.*
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
    
    // generalized factory: create an instance of the requested type. Caches class types.
    // We receive PQCN (partially qualified class name) as parameter.
    // Anything before the last '.' is the Bonaparte package name (which is null is there is no '.').
    // The package determines the possible bundle specification, not-null bundles are loaded in their
    // own classloaders, so they can be unloaded again.
    // Package to bundle mapping is contained in the static class data, however we cannot known that
    // before actually loading the class. Therefore, bundle information must be fed in separately
    // and can only be consistency-checked afterwards.
    public static BonaPortable createObject(String name) throws MessageParserException {
        String FQON = null;
        BonaPortable instance = null;
        int lastDot = name.lastIndexOf('.');
        if ((lastDot == 0) || (lastDot >= (name.length() - 1))) {
            throw new MessageParserException(MessageParserException.BAD_OBJECT_NAME, null, -1, name);
        }
        if (packagePrefixMap != null && lastDot > 0) {
            FQON = mapPackage(name);
        }
        
        if (FQON == null) {
            // prefix by fixed package
            FQON = bonaparteClassDefaultPackagePrefix + "." + name;
        }

        Class<? extends BonaPortable> f = map.get(FQON);
        if (f == null) {
            try {
                logger.debug("Factory: loading class {}", FQON);
                f = Class.forName(FQON, true, Thread.currentThread().getContextClassLoader()).asSubclass(BonaPortable.class);
                registerClass(FQON, f);
            } catch (ClassNotFoundException e) {
                logger.error("ClassNotFound exception for {}", FQON);
            }
        }
        if (f != null) {
            try {
                instance = f.newInstance();
            } catch (InstantiationException e) {
                logger.error("Instantiation exception for {}", name);
            } catch (IllegalAccessException e) {
                logger.error("IllegalAccess exception for {}", name);
            }
        }
        if (instance == null)
            throw new MessageParserException(MessageParserException.CLASS_NOT_FOUND, FQON, 0, FQON);
        return instance;
    }


    // auto getters and setters only following
    public static Map<String, String> getPackagePrefixMap() {
        return packagePrefixMap;
    }

    public static void setPackagePrefixMap(Map<String, String> packagePrefixMap) {
        BonaPortableFactory.packagePrefixMap = packagePrefixMap;
    }

    public static String getBonaparteClassDefaultPackagePrefix() {
        return bonaparteClassDefaultPackagePrefix;
    }

    public static void setBonaparteClassDefaultPackagePrefix(
            String bonaparteClassDefaultPackagePrefix) {
        BonaPortableFactory.bonaparteClassDefaultPackagePrefix = bonaparteClassDefaultPackagePrefix;
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
