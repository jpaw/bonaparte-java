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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BonaPortableFactory {
	private static final Logger logger = LoggerFactory.getLogger(BonaPortableFactory.class);
	static private Map<String, Class<? extends BonaPortable>> map = new HashMap<String, Class<? extends BonaPortable>>();
	static private String bonaparteClassDefaultPackagePrefix = "de.jpaw.bonaparte.pojos";
	static private Map<String, String> packagePrefixMap = null;

	private static synchronized void registerClass(String name, Class<? extends BonaPortable> clatz) {
		map.put(name, clatz);
		logger.debug("Factory: registered class {}", name);
	}
	
	// prevent instance creation
	private BonaPortableFactory() {
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
		if (lastDot <= 0 || lastDot >= name.length() - 1)
			throw new MessageParserException(MessageParserException.BAD_OBJECT_NAME,
					null, -1, name);
		String myPackage = name.substring(0, lastDot);
		
		if (packagePrefixMap != null) {
			String mappedPackagePart = packagePrefixMap.get(myPackage);
			if (mappedPackagePart != null)
				FQON = mappedPackagePart + "." + name.substring(lastDot+1);
		}
		if (FQON == null)
			// prefix by fixed package
			FQON = bonaparteClassDefaultPackagePrefix + "." + name;
			
		Class<? extends BonaPortable> f = map.get(FQON);
		if (f == null) {
			try {
				// String classname = "de.adata.server." + name;
				logger.debug("Factory: loading class {}", FQON);
				f = Class.forName(FQON).asSubclass(BonaPortable.class);
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


}
