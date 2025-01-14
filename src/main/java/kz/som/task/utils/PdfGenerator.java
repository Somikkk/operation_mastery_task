package kz.som.task.utils;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import kz.som.task.dto.ContactResponseDTO;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PdfGenerator {
    public static byte[] generateContactsPdf(List<ContactResponseDTO> contacts) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(byteArrayOutputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            Table table = new Table(new float[]{3, 3, 4, 4});
            table.addHeaderCell(new Cell().add(new Paragraph("Name")));
            table.addHeaderCell(new Cell().add(new Paragraph("Phone Number")));
            table.addHeaderCell(new Cell().add(new Paragraph("Email")));
            table.addHeaderCell(new Cell().add(new Paragraph("Address")));

            for (ContactResponseDTO contact : contacts) {
                table.addCell(new Cell().add(new Paragraph(contact.name())));
                table.addCell(new Cell().add(new Paragraph(contact.phoneNumber())));
                table.addCell(new Cell().add(new Paragraph(contact.email())));
                table.addCell(new Cell().add(new Paragraph(contact.address())));
            }

            document.add(table);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return byteArrayOutputStream.toByteArray();
    }

}
