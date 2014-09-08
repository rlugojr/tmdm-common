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

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.talend.mdm.commmon.metadata.builder.FieldBuilder;
import org.talend.mdm.commmon.metadata.builder.TypeBuilder;
import org.w3c.dom.Element;

import java.util.Locale;

public class LabelAnnotationProcessor implements XmlSchemaAnnotationProcessor {

    @Override
    public void process(XSDAnnotation annotation, TypeBuilder typeBuilder) {
        if (annotation != null) {
            EList<Element> appInfoElements = annotation.getApplicationInformation();
            for (Element appInfo : appInfoElements) {
                String source = appInfo.getAttribute("source"); //$NON-NLS-1$
                if (source != null && source.startsWith("X_Label_")) { //$NON-NLS-1$
                    String language = StringUtils.substringAfter(source, "X_Label_"); //$NON-NLS-1$
                    Locale locale = new Locale(language.toLowerCase());
                    typeBuilder.name(locale, appInfo.getTextContent());
                }
            }
        }
    }

    @Override
    public FieldBuilder process(XSDAnnotation annotation, FieldBuilder fieldBuilder) {
        if (annotation != null) {
            EList<Element> appInfoElements = annotation.getApplicationInformation();
            for (Element appInfo : appInfoElements) {
                String source = appInfo.getAttribute("source"); //$NON-NLS-1$
                if (source != null && source.startsWith("X_Label_")) { //$NON-NLS-1$
                    String language = StringUtils.substringAfter(source, "X_Label_"); //$NON-NLS-1$
                    Locale locale = new Locale(language.toLowerCase());
                    fieldBuilder.name(locale, appInfo.getTextContent());
                }
            }
        }
        return fieldBuilder;
    }
}
