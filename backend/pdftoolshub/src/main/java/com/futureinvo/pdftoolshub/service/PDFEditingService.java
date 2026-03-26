package com.futureinvo.pdftoolshub.service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
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
}
