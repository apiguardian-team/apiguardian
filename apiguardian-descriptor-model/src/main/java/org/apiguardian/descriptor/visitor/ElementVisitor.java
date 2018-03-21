package org.apiguardian.descriptor.visitor;

public interface ElementVisitor<ElementDescriptor> {
    void visitConstructor(ElementDefinition definition, ElementDescriptor descriptor);
    void visitMethod(ElementDefinition definition, ElementDescriptor descriptor);
    void visitField(ElementDefinition definition, ElementDescriptor descriptor);

}
