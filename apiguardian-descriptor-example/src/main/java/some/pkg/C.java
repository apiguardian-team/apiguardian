package some.pkg;

import org.apiguardian.api.API;

public class C {
    @API(status = API.Status.MAINTAINED)
    int x;

    @API(status = API.Status.EXPERIMENTAL)
    public C(int x) {
        this.x = x;
    }
}
