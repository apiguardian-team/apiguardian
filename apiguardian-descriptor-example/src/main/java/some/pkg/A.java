package some.pkg;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE)
public class A {
    public A() {
    }

    public void foo(int i){}

    public String foo(){ return ""; }

    public int bar(){return 0;}

    private void baz(){}
}
