package org.apiguardian.descriptor.visitor;

import org.apiguardian.descriptor.model.ArtifactDescriptor;
import org.apiguardian.descriptor.model.tree.ClassElementNode;
import org.apiguardian.descriptor.model.tree.ClassNode;
import org.apiguardian.descriptor.model.tree.PackageNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.joining;

public class ArtifactDescriptorTraverser<ElementDescriptor> {
    public <V> void traverse(ArtifactDescriptor<V, ElementDescriptor> artifactDescriptor,
                             ElementVisitor<ElementDescriptor> visitor){
        for(PackageNode<ElementDescriptor> packageNode: artifactDescriptor.getLibrary())
            traverse(new ArrayList<>(), packageNode, visitor);
    }

    private void traverse(List<String> superPackageParts, PackageNode<ElementDescriptor> packageNode,
                          ElementVisitor<ElementDescriptor> visitor){
        List<String> packageParts = new ArrayList<>(superPackageParts);
        packageParts.add(packageNode.getPartialName());
        String packageName = packageParts.stream().collect(joining("."));
        for (ClassNode<ElementDescriptor> clazz: packageNode.getClasses())
            traverse(packageName, clazz, visitor);
        for(PackageNode<ElementDescriptor> subpackage: packageNode.getSubpackages())
            traverse(packageParts, subpackage, visitor);
    }

    private void traverse(String packageName, ClassNode<ElementDescriptor> classNode,
                          ElementVisitor<ElementDescriptor> visitor){
        ElementDefinition baseDefinition = ElementDefinition.builder().
            packageName(packageName).
            className(classNode.getName()).
            classVisibility(classNode.getVisibility()).
            declaration(null).
            elementVisibility(null).
            build();
        traverse(baseDefinition, classNode.getConstructors(), visitor::visitConstructor);
        traverse(baseDefinition, classNode.getMethods(), visitor::visitMethod);
        traverse(baseDefinition, classNode.getFields(), visitor::visitField);
    }

    private void traverse(ElementDefinition baseDefinition, List<ClassElementNode<ElementDescriptor>> elements,
                          BiConsumer<ElementDefinition, ElementDescriptor> callback){

        for (ClassElementNode<ElementDescriptor> element: elements) {
            ElementDefinition definition = baseDefinition.toBuilder().
                declaration(element.getDeclaration()).
                elementVisibility(element.getVisibility()).
                build();
            callback.accept(definition, element.getDescriptor());
        }
    }
}
