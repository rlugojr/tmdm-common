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

import org.eclipse.xsd.XSDAnnotation;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.builder.FieldBuilder;
import org.talend.mdm.commmon.metadata.builder.TypeBuilder;

/**
 * Enrich a {@link ComplexTypeMetadata} being built with information contained in XML Schema information.
 */
public interface XmlSchemaAnnotationProcessor {

    /**
     * Process additional type information contained in {@link XSDAnnotation} for the
     * {@link org.talend.mdm.commmon.metadata.builder.TypeBuilder type}.
     * 
     * @param annotation An XML Schema annotation.
     * @param typeBuilder The type being built.
     */
    void process(XSDAnnotation annotation, TypeBuilder typeBuilder);

    /**
     * Process additional type information contained in {@link XSDAnnotation} for the
     * {@link org.talend.mdm.commmon.metadata.builder.FieldBuilder field}.
     *
     * @param annotation An XML Schema annotation.
     * @param fieldBuilder The field being built.
     */
    FieldBuilder process(XSDAnnotation annotation, FieldBuilder fieldBuilder);
}
