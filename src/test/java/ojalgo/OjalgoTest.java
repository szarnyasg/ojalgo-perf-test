package ojalgo;

import org.junit.Test;
import org.ojalgo.matrix.store.SparseStore;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class OjalgoTest {

    @Test
    public void test() throws IOException {
        int size = 626892;
        SparseStore<Double> m1 = SparseStore.PRIMITIVE.make(size, size);
        SparseStore<Double> m2 = SparseStore.PRIMITIVE.make(size, size);
        SparseStore<Double> m1m2 = SparseStore.PRIMITIVE.make(size, size);

        System.out.println("Load M1");
        fillMatrix("/tmp/f1.txt", m1);
        System.out.println("Load M2");
        fillMatrix("/tmp/f2.txt", m2);

        System.out.println("M1 * M2");
        m1.multiply(m2).supplyTo(m1m2);
    }

    public void fillMatrix(String filename, SparseStore m) throws IOException {
        ICsvListReader listReader = null;
        try {
            listReader = new CsvListReader(new FileReader(filename), CsvPreference.TAB_PREFERENCE);

            listReader.getHeader(false);
            final CellProcessor[] processors = new CellProcessor[] { new ParseInt(), new ParseInt() };

            List<Object> list;
            while( (list = listReader.read(processors)) != null ) {
                final int i = (int) list.get(0);
                final int j = (int) list.get(1);
                m.set(i, j, 1);
                m.set(j, i, 1);
            }
        }
        finally {
            if( listReader != null ) {
                listReader.close();
            }
        }
    }

}
