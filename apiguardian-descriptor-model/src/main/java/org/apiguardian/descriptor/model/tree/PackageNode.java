package org.apiguardian.descriptor.model.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apiguardian.descriptor.utils.Visibility;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@Getter
public class PackageNode<ElementDescriptor> {
    @JsonProperty
    private String partialName;
    @JsonProperty
    private List<PackageNode<ElementDescriptor>> subpackages;
    @JsonProperty
    private List<ClassNode<ElementDescriptor>> classes;

    //ugly, but required for serialization
    public PackageNode() {
    }

    public PackageNode(String partialName, List<PackageNode<ElementDescriptor>> subpackages, List<ClassNode<ElementDescriptor>> classes) {
        //todo: add null checks
        this.partialName = partialName;
        this.subpackages = subpackages;
        this.classes = classes;
    }

    public PackageNode addSubpackage(String name){
        PackageNode<ElementDescriptor> node = new PackageNode<>(name, new LinkedList<>(), new LinkedList());
        subpackages.add(node);
        subpackages = subpackages.stream().sorted(Comparator.comparing(p -> p.partialName)).collect(toList());
        return node;
    }

    public Optional<PackageNode<ElementDescriptor>> findSubpackage(String name){
        return subpackages.stream().filter(spkg -> spkg.getPartialName().equals(name)).findAny();
    }

    public PackageNode<ElementDescriptor> getSubpackage(String name){
        Optional<PackageNode<ElementDescriptor>> result = findSubpackage(name);
        if (result.isPresent())
            return result.get();
        return addSubpackage(name);
    }

    public PackageNode resolvePackage(String name){
        String[] segments = name.split("[.]");
        String firstSegment = segments[0];
        PackageNode<ElementDescriptor> subpackage = getSubpackage(firstSegment);
        if (segments.length == 1)
            return subpackage;
        String rest = name.substring(name.indexOf(".")+1);
        return subpackage.resolvePackage(rest);
    }

    public Optional<ClassNode<ElementDescriptor>> findClass(String name){
        return classes.stream().filter(c -> c.getName().equals(name)).findAny();
    }

    public ClassNode<ElementDescriptor> getClass(String name, Visibility visibility){
        Optional<ClassNode<ElementDescriptor>> wanted = findClass(name);
        if (wanted.isPresent()){
            if (wanted.get().getVisibility() != visibility)
                throw new RuntimeException("Wrong visibility"); //todo
            return wanted.get();
        }
        ClassNode<ElementDescriptor> node = new ClassNode<>(name, visibility, new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        classes.add(node);
        classes = classes.stream().sorted(Comparator.comparing(c -> c.getName())).collect(toList());
        return node;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        PackageNode<ElementDescriptor> that = (PackageNode<ElementDescriptor>) o;
//
//        if (!partialName.equals(that.partialName)) return false;
//        if (!subpackages.equals(that.subpackages)) return false;
//        System.out.println("this: "+this.partialName+" that: "+that.partialName+".classes not in this.classes: "+that.classes.stream().map(c -> ""+c.getName()+" -> "+classes.contains(c)).collect(toList()));
//        for (ClassNode<ElementDescriptor> c: classes)
//            System.out.println(c);
//        System.out.println("---");
//        for (ClassNode<ElementDescriptor> c: that.classes)
//            System.out.println(c);
//        System.out.println("------");
//        return classes.equals(that.classes);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = partialName.hashCode();
//        result = 31 * result + subpackages.hashCode();
//        result = 31 * result + classes.hashCode();
//        return result;
//    }
}
