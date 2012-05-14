/*
 * Copyright 2012 modjn Project
 * 
 * The modjn Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package modbus.provider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class ModbusProcessor {

    public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, ParserConfigurationException, SAXException, URISyntaxException, ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL plcxUrl = cl.getResource("META-INF/plc.xml");
        //DEBUG
        System.out.println(plcxUrl.getPath());

        //create a DocumentBuilderFactory and setNamespaceAware
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);

        //create a DocumentBuilder
        DocumentBuilder db = dbf.newDocumentBuilder();

        //parse the input file to get a Document object
        Document doc = db.parse(plcxUrl.openStream()); //plcxUrl.openStream()

        NodeList elementsByTagName = doc.getElementsByTagName("class");
        Object[] entityClasses = new Object[elementsByTagName.getLength()];
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            Node item = elementsByTagName.item(i);
            String entityClass = item.getTextContent();
            Class<?> loadClass = cl.loadClass(entityClass);
            entityClasses[i] = loadClass;
            //DEBUG
            System.out.println(entityClass);
        }

//        Reflections reflections = new Reflections(entityClasses, new FieldAnnotationsScanner());
//        
//        Set<Field> coilFields = reflections.getFieldsAnnotatedWith(Coil.class);
//        for (Field field : coilFields) {
//            Class<?> declaringClass = field.getDeclaringClass();
//            //DEBUG
//            System.out.println(declaringClass.getName() + " -- " + field.getName());
//        }
//
//        Set<Field> registerFields = reflections.getFieldsAnnotatedWith(Register.class);
//        for (Field field : registerFields) {
//            Class<?> declaringClass = field.getDeclaringClass();
//            //DEBUG
//            System.out.println(declaringClass.getName() + " -- " + field.getName());
//        }
        
        //container initialisieren
        final WeldContainer weldContainer = new Weld().initialize();
    }
}
