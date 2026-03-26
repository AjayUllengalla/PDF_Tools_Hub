package com.futureinvo.pdftoolshub.service;

import com.futureinvo.pdftoolshub.exception.CustomException;
import com.futureinvo.pdftoolshub.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PDFService {

	private final FileUtil fileUtil = new FileUtil();


	public byte[] pdfToWord(MultipartFile pdfFile) throws RuntimeException, Exception {
		
		fileUtil.validatePdf(pdfFile);
		Path tempPdf = null;
		try {
			tempPdf = fileUtil.saveToTemp(pdfFile, "pdf");
			 String extractedText;
			 
			 try(PDDocument pdf = PDDocument.load(tempPdf.toFile())){
				 PDFTextStripper stripper = new PDFTextStripper();
				 stripper.setSortByPosition(true);
				 extractedText = stripper.getText(pdf);
			 }
			 
			 try(XWPFDocument doc = new XWPFDocument();
					 ByteArrayOutputStream out = new ByteArrayOutputStream();){
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

	    // ✅ Step 1: Validate file
	    if (wordFile == null || wordFile.isEmpty()) {
	        throw new CustomException("Uploaded file is empty or null");
	    }

	    String originalName = wordFile.getOriginalFilename();

	    // ✅ 🔥 IMPORTANT: Validate DOCX file
	    if (originalName == null || !originalName.toLowerCase().endsWith(".docx")) {
	        throw new CustomException("Only .docx files are supported");
	    }

	    Path tempDocx = null;

	    try {
	        // ✅ Step 2: Save file to temp
	        tempDocx = fileUtil.saveToTemp(wordFile, "docx");

	        // ✅ Step 3: Read Word content
	        StringBuilder content = new StringBuilder();

	        try (XWPFDocument doc = new XWPFDocument(
	                new FileInputStream(tempDocx.toFile()))) {

	            for (XWPFParagraph para : doc.getParagraphs()) {
	                content.append(para.getText()).append("\n");
	            }
	        }

	        // ✅ Step 4: Convert to PDF
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

	                // ✅ New page if space ends
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
	        // ✅ Step 5: Clean temp file
	        if (tempDocx != null) {
	            fileUtil.deleteFile(tempDocx);
	        }
	    }
	}}

