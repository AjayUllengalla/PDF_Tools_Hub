package com.futureinvo.pdftoolshub.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.futureinvo.pdftoolshub.exception.CustomException;
import com.futureinvo.pdftoolshub.util.FileUtil;

@Service
public class PDFService {

    @Autowired  
    private FileUtil fileUtil;


    public byte[] pdfToWord(MultipartFile pdfFile) throws RuntimeException, Exception {

        fileUtil.validatePdf(pdfFile);
        Path tempPdf = null;

        try {
            tempPdf = fileUtil.saveToTemp(pdfFile, "pdf");
            String extractedText;

            try (PDDocument pdf = PDDocument.load(tempPdf.toFile())) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                extractedText = stripper.getText(pdf);
            }

            try (XWPFDocument doc = new XWPFDocument();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                XWPFParagraph title = doc.createParagraph();
                title.setAlignment(ParagraphAlignment.CENTER);

                XWPFRun titleRun = title.createRun();
                titleRun.setText(fileUtil.baseName(pdfFile.getOriginalFilename()));
                titleRun.setBold(true);
                titleRun.setFontSize(16);

                String[] lines = extractedText.split("\\r?\\n");

                for (String line : lines) {
                    XWPFParagraph para = doc.createParagraph();
                    XWPFRun run = para.createRun();
                    run.setText(line.isEmpty() ? " " : line);
                    run.setFontSize(12);
                }

                doc.write(out);
                return out.toByteArray();
            }

        } finally {
            fileUtil.deleteFile(tempPdf);
        }
    }


    public byte[] wordToPdf(MultipartFile wordFile) throws Exception {

        if (wordFile == null || wordFile.isEmpty()) {
            throw new CustomException("Uploaded file is empty or null");
        }

        String originalName = wordFile.getOriginalFilename();

        // prevent wrong file type crash
        if (originalName == null || !originalName.toLowerCase().endsWith(".docx")) {
            throw new CustomException("Only .docx files are supported");
        }

        Path tempDocx = null;

        try {
            tempDocx = fileUtil.saveToTemp(wordFile, "docx");

            StringBuilder content = new StringBuilder();

            try (XWPFDocument doc = new XWPFDocument(
                    new FileInputStream(tempDocx.toFile()))) {

                for (XWPFParagraph para : doc.getParagraphs()) {
                    content.append(para.getText()).append("\n");
                }
            }

            try (PDDocument pdf = new PDDocument();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                PDPage page = new PDPage();
                pdf.addPage(page);

                PDPageContentStream stream =
                        new PDPageContentStream(pdf, page);

                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA, 12);
                stream.setLeading(14.5f);
                stream.newLineAtOffset(50, 750);

                String[] lines = content.toString().split("\\n");

                float yPosition = 750;

                for (String line : lines) {

                    if (yPosition < 50) {
                        stream.endText();
                        stream.close();

                        page = new PDPage();
                        pdf.addPage(page);

                        stream = new PDPageContentStream(pdf, page);
                        stream.beginText();
                        stream.setFont(PDType1Font.HELVETICA, 12);
                        stream.setLeading(14.5f);
                        stream.newLineAtOffset(50, 750);

                        yPosition = 750;
                    }

                    stream.showText(line.isEmpty() ? " " : line);
                    stream.newLine();
                    yPosition -= 14.5f;
                }

                stream.endText();
                stream.close();

                pdf.save(out);
                return out.toByteArray();
            }

        } catch (Exception e) {
            throw new CustomException("Error converting Word to PDF");
        } finally {
            if (tempDocx != null) {
                fileUtil.deleteFile(tempDocx);
            }
        }
    }


    public byte[] excelToPdf(MultipartFile excelFile) throws Exception {

        if (excelFile == null || excelFile.isEmpty()) {
            throw new CustomException("Uploaded Excel file is empty");
        }

        Path tempXlsx = null;

        try {
            tempXlsx = fileUtil.saveToTemp(excelFile, "xlsx");

            try (Workbook workbook = new XSSFWorkbook(new FileInputStream(tempXlsx.toFile()));
                 PDDocument pdf = new PDDocument();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                float margin = 40f;
                float pageWidth = 841f;
                float pageHeight = 595f;
                float fontSize = 10f;
                float leading = fontSize * 1.4f;

                for (int si = 0; si < workbook.getNumberOfSheets(); si++) {

                    Sheet sheet = workbook.getSheetAt(si);

                    PDPage page = new PDPage(
                            new PDRectangle(pageWidth, pageHeight)
                    );
                    pdf.addPage(page);

                    try (PDPageContentStream cs = new PDPageContentStream(pdf, page)) {

                        float y = pageHeight - margin;

                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA_BOLD, 13f);
                        cs.newLineAtOffset(margin, y);
                        cs.showText("Sheet: " + sheet.getSheetName());
                        cs.endText();

                        y -= 24f;

                        cs.setFont(PDType1Font.HELVETICA, fontSize);

                        for (Row row : sheet) {

                            if (y - leading < margin) break;

                            StringBuilder rowLine = new StringBuilder();

                            for (org.apache.poi.ss.usermodel.Cell cell : row) {
                                String val = getCellValue(cell);
                                rowLine.append(String.format("%-18s", val));
                            }

                            String safeText = rowLine.toString()
                                    .replaceAll("[^\\x00-\\xFF]", "?");

                            cs.beginText();
                            cs.newLineAtOffset(margin, y);
                            cs.showText(safeText.isEmpty() ? " " : safeText);
                            cs.endText();

                            y -= leading;
                        }
                    }
                }

                pdf.save(out);
                return out.toByteArray();
            }

        } catch (Exception e) {
            throw new CustomException("Error converting Excel to PDF or uploaded not valid file ");
        } finally {
            fileUtil.deleteFile(tempXlsx);
        }
    }

    private String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {

        if (cell == null) return "";

        return switch (cell.getCellType()) {

            case STRING -> cell.getStringCellValue();

            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.valueOf(cell.getNumericCellValue());

            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());

            case FORMULA -> cell.getCellFormula();

            default -> "";
        };
    }
}