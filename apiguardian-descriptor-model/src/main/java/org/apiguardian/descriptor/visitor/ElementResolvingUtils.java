package org.apiguardian.descriptor.visitor;

import org.apiguardian.descriptor.model.ArtifactDescriptor;
import org.apiguardian.descriptor.model.tree.ClassElementNode;
import org.apiguardian.descriptor.model.tree.ClassNode;
import org.apiguardian.descriptor.model.tree.PackageNode;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ElementResolvingUtils {
    private static <V, E> Optional<PackageNode<E>> resolvePkgNoCreate(ArtifactDescriptor<V, E> descriptor, String pkg){
        String[] parts = pkg.split("[.]");
        PackageNode<E> pkgNode = descriptor.getRootPackage();
        for (String part: parts){
            Optional<PackageNode<E>> next = pkgNode.findSubpackage(part);
            if (!next.isPresent())
                return Optional.empty();
            pkgNode = next.get();
        }
        return Optional.of(pkgNode);
    }

    private static <V, E> Optional<ClassElementNode<E>> resolveElement(ArtifactDescriptor<V, E> descriptor,
                                                   ElementDefinition definition,
                                                   Function<ClassNode<E>, List<ClassElementNode<E>>> classDecomposer){
        return resolvePkgNoCreate(descriptor, definition.getPackageName()).flatMap(
            p -> p.findClass(definition.getClassName())
        ).flatMap(
            c -> c.getVisibility() == definition.getClassVisibility() ? Optional.of(c) : Optional.empty()
        ).flatMap(
            c -> classDecomposer.apply(c).stream().filter(
                e ->
                    e.getDeclaration().equals(definition.getDeclaration()) &&
                    e.getVisibility() == definition.getElementVisibility()
            ).findAny()
        );
    }

    public static <V, E> Optional<ClassElementNode<E>> resolveConstructor(ArtifactDescriptor<V, E> descriptor,
                                                                          ElementDefinition definition){
        return resolveElement(descriptor, definition, c -> c.getConstructors());
    }

    public static <V, E> Optional<ClassElementNode<E>> resolveMethod(ArtifactDescriptor<V, E> descriptor,
                                                                     ElementDefinition definition){
        return resolveElement(descriptor, definition, c -> c.getMethods());
    }

    public static <V, E> Optional<ClassElementNode<E>> resolveField(ArtifactDescriptor<V, E> descriptor,
                                                                    ElementDefinition definition){
        return resolveElement(descriptor, definition, c -> c.getFields());
    }
}
