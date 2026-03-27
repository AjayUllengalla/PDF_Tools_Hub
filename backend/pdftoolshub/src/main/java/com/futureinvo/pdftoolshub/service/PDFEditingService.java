package com.futureinvo.pdftoolshub.service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.bouncycastle.util.Integers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.exception.CustomException;
import com.futureinvo.pdftoolshub.util.FileUtil;

@Service
public class PDFEditingService {

	@Autowired
	 private  FileUtil fileUtil;

	    /**
	     * Merges two or more PDFs into a single document in the order provided.
	     *
	     * @param files list of PDF files (minimum 2)
	     * @return merged PDF bytes
	     * @throws Exception 
	     */
	    public byte[] mergePdfs(List<MultipartFile> files) throws Exception {
	        if (files == null || files.size() < 2) {
	            throw new CustomException("At least 2 PDF files are required for merging.");
	        }

	        List<Path> tempPaths = new ArrayList<>();
	        try {
	            // Save all files to temp
	            for (MultipartFile f : files) {
	                fileUtil.validatePdf(f);
	                tempPaths.add(fileUtil.saveToTemp(f, "pdf"));
	            }

	            PDFMergerUtility merger = new PDFMergerUtility();
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            merger.setDestinationStream(out);

	            for (Path p : tempPaths) {
	                merger.addSource(p.toFile());
	            }
	            merger.mergeDocuments(null);
	            return out.toByteArray();

	        } finally {
	            tempPaths.forEach(fileUtil::deleteFile);
	        }
	    }

	    public byte[] splitPdf(MultipartFile file, int startPage, int endPage) throws Exception {
	    	
	    	fileUtil.validatePdf(file);
	    	if(startPage < 1) {
	    		throw new CustomException("Page should start from 1");
	    	}
	    	Path tempPath = null;
	    	try {
	    		tempPath = fileUtil.saveToTemp(file, "pdf");
	    		
	    		try(PDDocument source = PDDocument.load(tempPath.toFile())) { 
	    			int totalPages = source.getNumberOfPages();
	    			int end = (endPage < 1 || endPage > totalPages) ? totalPages :endPage;
	    			
	    			if(startPage > end) {
	    				throw new CustomException("Start page must be < endPage");
	    			}
	    			
	    			Splitter splitter = new Splitter();
	    			splitter.setStartPage(startPage);
	    			splitter.setEndPage(end);
	    			splitter.setSplitAtPage(end - startPage + 1);
	    			
	    			List<PDDocument> parts = splitter.split(source);
	    			
	    			try(PDDocument result = parts.get(0);
	    					ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	    				result.save(out);
	    				
	    				for(int i=0 ;i<parts.size();i++) {
	    					parts.get(i).close();
	    				}
	    				
	    				return out.toByteArray();
	    			}
	    		}
	    	} finally {
	    		fileUtil.deleteFile(tempPath);
	    	}
	    }
	    
	    public byte[] rotatePdf(MultipartFile file, int angle, String pageNums) throws Exception {
	    	
	    	fileUtil.validatePdf(file);
	    	
	    	if(angle !=90 && angle !=180 && angle != 270) {
	    		throw new CustomException("Rotation angle must be 90,180 or 270 degress");
	    	}
	    	Path tempPath = null;
	    	try  {
	    		tempPath = fileUtil.saveToTemp(file, "pdf");
	    		
	    		try(PDDocument pdf = PDDocument.load(tempPath.toFile());
	    				ByteArrayOutputStream out = new ByteArrayOutputStream()){
	    			
	    			List<PDPage> pages = new ArrayList<>();
	    			pdf.getPages().forEach(pages::add);
	    			
	    			Set<Integer> targets = resolvePages(pageNums, pages.size());
	    			
	    			for(int i = 0;i<pages.size();i++) {
	    				if(targets.contains(i+1)) {
	    					PDPage page = pages.get(i);
	    					int current = page.getRotation();
	    					page.setRotation((current + angle) % 360);
	    				}
	    			}
	    			pdf.save(out);
	    			return out.toByteArray();
	    		}
	    	} finally {
	    		fileUtil.deleteFile(tempPath);
	    	}
	    }

	    private Set<Integer> resolvePages(String pageNums, int total) {
	        Set<Integer> result = new LinkedHashSet<>();
	        if ("all".equalsIgnoreCase(pageNums) || pageNums == null || pageNums.isBlank()) {
	            for (int i = 1; i <= total; i++) result.add(i);
	            return result;
	        }
	        for (String part : pageNums.split(",")) {
	            try {
	                int p = Integer.parseInt(part.trim());
	                if (p >= 1 && p <= total) result.add(p);
	            } catch (NumberFormatException ex) {
	                throw new CustomException("Enter Number Correct");
	            }
	        }
	        if (result.isEmpty()) {
	            throw new CustomException("No valid page numbers found in: " + pageNums);
	        }
	        return result;
	    }
	    
}
