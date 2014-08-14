package org.talend.mdm.commmon.metadata;

import org.w3c.dom.Element;

/**
*
*/
public class NoOpValidationHandler implements ValidationHandler {

    public static ValidationHandler INSTANCE = new NoOpValidationHandler();

    private NoOpValidationHandler() {
    }

    @Override
    public void error(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error) {
        // Nothing to do (No op validation)
    }

    @Override
    public void fatal(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error) {
        // Nothing to do (No op validation)
    }

    @Override
    public void error(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error) {
        // Nothing to do (No op validation)
    }

    @Override
    public void warning(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error) {
        // Nothing to do (No op validation)
    }

    @Override
    public void fatal(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error) {
        // Nothing to do (No op validation)
    }

    @Override
    public void warning(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error) {
        // Nothing to do (No op validation)
    }

    @Override
    public void end() {
        // Nothing to do (No op validation)
    }

    @Override
    public int getErrorCount() {
        return 0;
    }
}
