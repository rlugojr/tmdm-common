/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.annotation;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.talend.mdm.commmon.metadata.builder.FieldBuilder;
import org.talend.mdm.commmon.metadata.builder.TypeBuilder;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class SchematronProcessor implements XmlSchemaAnnotationProcessor {

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    @Override
    public void process(XSDAnnotation annotation, TypeBuilder typeBuilder) {
        if (annotation != null) {
            EList<Element> annotations = annotation.getApplicationInformation();
            for (Element appInfo : annotations) {
                if ("X_Schematron".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    StringWriter sw = new StringWriter();
                    Transformer transformer;
                    try {
                        transformer = transformerFactory.newTransformer();
                    } catch (TransformerConfigurationException e) {
                        throw new RuntimeException("Could not create transformer instance.", e);
                    }
                    transformer.setOutputProperty("omit-xml-declaration", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
                    try {
                        // TODO This is not really efficient but doing it nicely would require to rewrite a
                        // StringEscapeUtils.unescapeXml()
                        transformer.transform(new StreamSource(new StringReader(appInfo.getTextContent())), new StreamResult(sw));
                        typeBuilder.validationRule("<schema>" + sw.toString() + "</schema>"); //$NON-NLS-1$ //$NON-NLS-2$
                    } catch (TransformerException e) {
                        try {
                            transformer.transform(
                                    new StreamSource(new StringReader(StringEscapeUtils.unescapeXml(appInfo.getTextContent()))),
                                    new StreamResult(sw));
                            typeBuilder.validationRule("<schema>" + sw.toString() + "</schema>"); //$NON-NLS-1$ //$NON-NLS-2$
                        } catch (TransformerException e1) {
                            throw new RuntimeException(e1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public FieldBuilder process(XSDAnnotation annotation, FieldBuilder fieldBuilder) {
        throw new UnsupportedOperationException();
    }
}
