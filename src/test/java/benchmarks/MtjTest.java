package benchmarks;

import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;
import org.junit.Test;

import java.io.IOException;

public class MtjTest {

    @Test
    public void test() throws IOException {
        int n = 50_000;
        int[][] nz = new int[n][];
        for (int i = 0; i < n; i++) {
            nz[i] = new int[] { i };
        }
        System.out.println("nz initialized");
        CompRowMatrix mx = new CompRowMatrix(n, n, nz);
        for (int i = 0; i < n; i++) {
            mx.set(i, i, 1);
        }
        System.out.println("mx initialized");
        LinkedSparseMatrix result = new LinkedSparseMatrix(n, n);
        mx.mult(mx, result);
        System.out.println(result.get(0,0));
        System.out.println(result.get(1,1));
    }

}
