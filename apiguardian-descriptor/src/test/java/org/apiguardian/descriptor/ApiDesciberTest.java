package org.apiguardian.descriptor;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apiguardian.descriptor.model.ApiDescriptor;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class ApiDesciberTest {
    @Test
    @SneakyThrows
    public void doMe(){
        @Cleanup BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/example_path.txt")));
        String path = r.readLine();
        ApiDescriptor descriptor = new ApiDesciber().describe(new File(path));
        System.out.println(descriptor);
    }
}