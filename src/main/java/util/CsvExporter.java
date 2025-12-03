package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.Transaction;

public class CsvExporter {
    public static void exportTransactions(List<Transaction> transactions, String path) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("Data/Hora,Tipo,Valor,Conta Origem,Conta Destino,Saldo Após,Descrição\n");
            for (Transaction t : transactions) {
                 if (t.getType() == null) continue;
                 String type = t.getType().toUpperCase();
                 if (!(type.equals("DEPOSIT") || type.equals("WITHDRAW") || type.equals("TRANSFER") || type.equals("TRANSFER_IN"))) continue;
                fw.write(t.toCsvLine() + "\n");
            }
        }
    }
}
