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

package de.jpaw.bonaparte.test.jaxb;

import de.jpaw.bonaparte.pojos.test.jaxb.TestXml;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlRootElement;
import org.testng.annotations.Test;

@Test
public class XmlAnnoTest {

    public void testIfAnnoPresent() {
        Annotation x = TestXml.class.getAnnotation(XmlRootElement.class);
        if (x == null) {
            System.out.println("TestXml is not annotated with @XmlRootElement");
        } else {
            System.out.println("TestXml has been annotated with @XmlRootElement");
        }
    }

    public void anotherTestIfAnnoPresent() {
        Annotation[] x = TestXml.class.getAnnotations();
        System.out.println("TestXml has " + x.length + " annotations");
    }
}
