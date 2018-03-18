package org.apiguardian.descriptor.model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
public class PackageNode {
    String partialName;
    List<PackageNode> subpackages;
    List<ClassNode> classes;

    PackageNode(String partialName, List<PackageNode> subpackages, List<ClassNode> classes) {
        this.partialName = partialName;
        this.subpackages = subpackages;
        this.classes = classes;
    }

    public PackageNode addSubpackage(String name){
        PackageNode node = new PackageNode(name, new LinkedList<>(), new LinkedList());
        subpackages.add(node);
        return node;
    }

    public ClassNode addClass(String name){
        ClassNode node = new ClassNode(name, new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        classes.add(node);
        return node;
    }
}
