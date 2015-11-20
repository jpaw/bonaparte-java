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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.joda.time.Instant;

import de.jpaw.bonaparte.pojos.meta.BundleInformation;
import de.jpaw.bonaparte.pojos.meta.BundleStatus;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;

/**
 * The Bundles class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implements JVM-wide management of loaded bundles.
 *
 *          This is unfinished WIP, maybe should use full OSGi instead if required.
 *
 */
public class Bundle {
    private static ConcurrentMap<String,BundleInformation> bundleMap = new ConcurrentHashMap<String,BundleInformation>(32);
    private static ConcurrentMap<String,Bundle> packageToBundle = new ConcurrentHashMap<String,Bundle>(32);

    private BundleInformation staticBundleData;
    private ConcurrentMap<String,ClassDefinition> loadedClasses;
    private URLClassLoader loader;

    // create a new bundle entry for tracking
    Bundle(String bundleName, String classPath) throws MalformedURLException {
        URL [] searchPath;
        if (classPath == null) {
            searchPath = new URL[0];
        } else {
            searchPath = new URL[1];
            searchPath[0] = new URL(classPath);
        }
        loader = new URLClassLoader(searchPath);
        loadedClasses = new ConcurrentHashMap<String,ClassDefinition>(100);
        staticBundleData = new BundleInformation();
        staticBundleData.setName(bundleName);
        staticBundleData.setBundleStatus(BundleStatus.UNUSED);
        staticBundleData.setWhenStatusChanged(Instant.now());
    }

    static Bundle getBundleByPackageName(String packageName) {
        return packageToBundle.get(packageName);
    }

    void addClass(ClassDefinition cl) {
        loadedClasses.put(cl.ret$PQON(), cl.ret$MetaData());
    }

    ClassDefinition [] getAllClasses() {
        return loadedClasses.values().toArray(new ClassDefinition [0]);
    }
}
