package benchmarks;

import org.junit.Test;
import org.la4j.Matrix;
import org.la4j.matrix.SparseMatrix;
import org.la4j.matrix.sparse.CRSMatrix;

import java.io.IOException;

public class La4jTest {

    @Test
    public void test() throws IOException {
        int n = 1_000_000;
        SparseMatrix a = CRSMatrix.zero(n, n);
        for (int i = 0; i < n; i++) {
            a.set(i, i, 1);
        }

        final Matrix c = a.multiply(a);
    }

}
