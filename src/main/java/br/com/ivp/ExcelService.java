package br.com.ivp;

import org.apache.poi.ss.usermodel.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExcelService {
    public List<Host> readHosts(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("Planilha não encontrada: " + path);
        }
        List<Host> hosts = new ArrayList<>();
        try (InputStream input = Files.newInputStream(path);
             Workbook workbook = WorkbookFactory.create(input)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            boolean firstRow = true;
            for (Row row : sheet) {
                if (firstRow) { firstRow = false; continue; } // cabeçalho na primeira linha
                String hostname = formatter.formatCellValue(row.getCell(0), evaluator).trim();
                String ip = formatter.formatCellValue(row.getCell(1), evaluator).trim();
                String location = formatter.formatCellValue(row.getCell(2), evaluator).trim();
                if (hostname.isEmpty() && ip.isEmpty() && location.isEmpty()) continue;
                hosts.add(new Host(hostname, ip, location));
            }
        }
        return hosts;
    }
}
