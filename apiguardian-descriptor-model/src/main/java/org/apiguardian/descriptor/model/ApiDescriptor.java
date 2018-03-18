package org.apiguardian.descriptor.model;

import lombok.Data;
import org.apiguardian.contract.APIElementState;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
public class ApiDescriptor {
    String version;
    List<PackageNode> library = new LinkedList<>();

    public PackageNode addPackage(String name){
        String[] parts = name.split("[.]");
        PackageNode currentNode = new PackageNode("", library, new LinkedList<>());
        for (String part: parts){
            Optional<PackageNode> existingNode = currentNode.getSubpackages().stream().filter(n -> n.getPartialName().equals(part)).findAny();
            if (existingNode.isPresent())
                currentNode = existingNode.get();
            else {
                currentNode = currentNode.addSubpackage(part);
            }
        }
        return currentNode;
    }

    public void registerMethod(Method method, APIElementState state){
        PackageNode pkg = addPackage(method.getDeclaringClass().getPackage().getName());
        ClassNode clazz = pkg.addClass(method.getDeclaringClass().getSimpleName());
        clazz.addMethod(method, state);
    }
}
