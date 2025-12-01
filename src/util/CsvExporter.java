package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.Transaction;

public class CsvExporter {
    public static void exportTransactions(List<Transaction> transactions, String path) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("timestamp,type,amount,fromAccount,toAccount,balanceAfter,description\n");
            for (Transaction t : transactions) {
                if (t.getType() != null && t.getType().equalsIgnoreCase("CREATE")) continue;
                fw.write(t.toCsvLine() + "\n");
            }
        }
    }
}
