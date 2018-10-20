package benchmarks;

import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.data.DMatrixSparseTriplet;
import org.ejml.ops.ConvertDMatrixStruct;
import org.ejml.simple.SimpleMatrix;
import org.ejml.sparse.csc.CommonOps_DSCC;
import org.junit.Test;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class EjmlTest {

    @Test
    public void test() throws IOException {
        int n = 626_892;

        System.out.println("Load M1");
        DMatrixSparseCSC M1 = fillMatrix("src/test/resources/f1.txt", n);
        System.out.println("Load M2");
        DMatrixSparseCSC M2 = fillMatrix("src/test/resources/f2.txt", n);

        DMatrixSparseCSC M1M2 = new DMatrixSparseCSC(n, n, 0);
        DMatrixSparseCSC EW = new DMatrixSparseCSC(n, n, 0);

        long t1 = System.currentTimeMillis();
        CommonOps_DSCC.mult(M1, M2, M1M2, null, null);
        long t2 = System.currentTimeMillis();
        CommonOps_DSCC.elementMult(M1M2, M2, EW, null, null);

        long t3 = System.currentTimeMillis();
        System.out.println("MM: " + (t2 - t1));
        System.out.println("EW: " + (t3 - t2));

        final int runs = 10000;

        long total1 = 0;
        for (int i = 0; i < runs; i++) {
            long u1 = System.currentTimeMillis();
            DMatrixRMaj rowSum1 = new DMatrixRMaj(n, 1);
            sumRows1(M1M2, rowSum1);
            long u2 = System.currentTimeMillis();

            total1 += u2 - u1;
        }

        System.out.println("Total 1: " + total1 / runs);

        long total2 = 0;
        for (int i = 0; i < runs; i++) {
            long u1 = System.currentTimeMillis();
            DMatrixRMaj rowSum2 = new DMatrixRMaj(n, 1);
            sumRows2(M1M2, rowSum2);
            long u2 = System.currentTimeMillis();

            total2 += u2 - u1;
        }
        System.out.println("Total 2: " + total2 / runs);

    }

    public static DMatrixRMaj sumRows1(DMatrixSparseCSC input , DMatrixRMaj output ) {
        if( output == null ) {
            output = new DMatrixRMaj(input.getNumRows(),1);
        } else {
            output.reshape(input.getNumRows(),1);
        }
        SimpleMatrix ones = new SimpleMatrix(input.getNumCols(), 1);
        ones.fill(1);

        CommonOps_DSCC.mult(input, ones.getMatrix(), output);
        return output;
    }


    public static DMatrixRMaj sumRows2(DMatrixSparseCSC input , DMatrixRMaj output ) {
        if( output == null ) {
            output = new DMatrixRMaj(input.numRows,1);
        } else {
            output.reshape(input.numRows,1);
        }

        for (int col = 0; col < input.numCols; col++) {
            int idx0 = input.col_idx[col];
            int idx1 = input.col_idx[col+1];

            for (int i = idx0; i < idx1; i++) {
                int row = input.nz_rows[i];
                double value = input.nz_values[i];

                output.add(row, 0, value);
            }
        }
        return output;
    }

    public DMatrixSparseCSC fillMatrix(String filename, int n) throws IOException {
        DMatrixSparseTriplet triplets = new DMatrixSparseTriplet(n, n, 0);

        ICsvListReader listReader = null;
        try {
            listReader = new CsvListReader(new FileReader(filename), CsvPreference.TAB_PREFERENCE);

            listReader.getHeader(false);
            final CellProcessor[] processors = new CellProcessor[] { new ParseInt(), new ParseInt() };

            List<Object> list;
            while( (list = listReader.read(processors)) != null ) {
                final int i = (int) list.get(0);
                final int j = (int) list.get(1);
                triplets.addItem(i, j, 1);
                triplets.addItem(j, i, 1);
            }
        }
        finally {
            if( listReader != null ) {
                listReader.close();
            }
        }
        DMatrixSparseCSC m = ConvertDMatrixStruct.convert(triplets, (DMatrixSparseCSC) null);
        return m;
    }

}
