package br.elissonsouza.controleordens;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class GeradorPDF {

    public static void main(String[] args) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("ordens.pdf"));
            document.open();

            // Adicionar título
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Relatório de Ordens de Ativos Financeiros", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            // Adicionar conteúdo das ordens
            Font fontOrdens = new Font(Font.FontFamily.COURIER, 12);
            String conteudo = "Ano: 2022\n\n";

            Paragraph paragrafoOrdens = new Paragraph(conteudo, fontOrdens);
            document.add(paragrafoOrdens);

            document.close();
            System.out.println("PDF gerado com sucesso!");
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
