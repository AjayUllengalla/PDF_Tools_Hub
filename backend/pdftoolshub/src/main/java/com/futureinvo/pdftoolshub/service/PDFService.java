package com.futureinvo.pdftoolshub.service;

import com.futureinvo.pdftoolshub.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
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

	@Autowired
    private final FileUtil fileUtil;

    PDFService(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

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
}
