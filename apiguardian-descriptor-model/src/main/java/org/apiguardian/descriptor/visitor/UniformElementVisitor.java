package org.apiguardian.descriptor.visitor;

public interface UniformElementVisitor<ElementDescriptor> extends ElementVisitor<ElementDescriptor> {
    enum ElementType {
        CONSTRUCTOR, METHOD, FIELD;
    }

    default void visitConstructor(ElementDefinition definition, ElementDescriptor descriptor){
        visit(ElementType.CONSTRUCTOR, definition, descriptor);
    }
    default void visitMethod(ElementDefinition definition, ElementDescriptor descriptor){
        visit(ElementType.METHOD, definition, descriptor);
    }
    default void visitField(ElementDefinition definition, ElementDescriptor descriptor){
        visit(ElementType.FIELD, definition, descriptor);
    }

    void visit(ElementType type, ElementDefinition definition, ElementDescriptor descriptor);
}
