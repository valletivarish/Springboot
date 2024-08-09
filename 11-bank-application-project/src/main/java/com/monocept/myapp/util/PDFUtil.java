package com.monocept.myapp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.monocept.myapp.dto.AccountResponseDto;
import com.monocept.myapp.dto.TransactionResponseDto;
import com.monocept.myapp.entity.User;

@Service
public class PDFUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final float ROW_HEIGHT = 30f; 

    public byte[] generatePassbookPdf(Optional<User> user, List<TransactionResponseDto> transactions) 
            throws DocumentException, IOException {
    	String firstName=user.get().getCustomer().getFirstName();
    	String lastName=user.get().getCustomer().getLastName();
    	
    	double totalBalance=user.get().getCustomer().getTotalBalance();
    	
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        document.add(new Paragraph("Passbook"));
        document.add(new Paragraph(" ")); 
        document.add(new Paragraph("Name: " + firstName + " " + lastName));
        document.add(new Paragraph("Total Balance: " + String.format("%.2f", totalBalance)));
        document.add(new Paragraph(" ")); 

        PdfPTable table = new PdfPTable(5); 
        table.setWidthPercentage(100);

        float[] columnWidths = {1.5f, 1.5f, 2.5f, 2f, 1.5f};
        table.setWidths(columnWidths);

        // Add table headers
        addCell(table, "Sender Acc", true);
        addCell(table, "Receiver Acc", true);
        addCell(table, "Date", true);
        addCell(table, "Amount", true);
        addCell(table, "Type", true);


        for (TransactionResponseDto transaction : transactions) {
            addCell(table, getSafeAccountNumber(transaction.getSenderAccount()), false);
            addCell(table, getSafeAccountNumber(transaction.getReceiverAccount()), false);
            addCell(table, formatDate(transaction.getTransactionDate()), false);
            addCell(table, String.format("%.2f", transaction.getAmount()), false);
            addCell(table, transaction.getTransactionType() != null ? transaction.getTransactionType().name() : "N/A", false);
        }

        document.add(table);
        document.close();
        return baos.toByteArray();
    }

    private void addCell(PdfPTable table, String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setFixedHeight(ROW_HEIGHT);
        cell.setPadding(5);
        if (isHeader) {
            cell.setBackgroundColor(new BaseColor(200, 200, 200));
        }
        table.addCell(cell);
    }

    private String getSafeAccountNumber(Object account) {
        if (account == null) {
            return "N/A";
        } else if (account instanceof AccountResponseDto) {
            return String.valueOf(((AccountResponseDto) account).getAccountNumber());
        } else {
            return account.toString();
        }
    }

    private String formatDate(Object date) {
        if (date == null) {
            return "N/A";
        } else if (date instanceof Date) {
            return DATE_FORMAT.format((Date) date);
        } else if (date instanceof java.time.LocalDateTime) {
            return DATE_FORMAT.format(java.sql.Timestamp.valueOf((java.time.LocalDateTime) date));
        } else if (date instanceof String) {
            try {
                return DATE_FORMAT.format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) date));
            } catch (Exception e) {
                return "Invalid Date";
            }
        } else {
            return date.toString();
        }
    }
}
