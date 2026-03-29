package com.futureinvo.pdftoolshub.service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.exception.CustomException;
import com.futureinvo.pdftoolshub.util.FileUtil;

@Service
public class PDFSecurityService {

	@Autowired
	private FileUtil fileUtil;
	
	public byte[] lockPdf(MultipartFile file,
						  String userPassword,
						  String ownerPassword,
						  boolean allowPrinting,
						  boolean allowCopying) throws Exception {
		fileUtil.validatePdf(file);
		
		if(userPassword == null || userPassword.isBlank()) {
			throw new CustomException("User password is required to lock a PDF");
		}
		 String ownerPwd = (ownerPassword == null || ownerPassword.isBlank())
	                ? userPassword : ownerPassword;

		Path tempPath = null;
		try {
			tempPath = fileUtil.saveToTemp(file, "pdf");
			
			try(PDDocument pdf = PDDocument.load(tempPath.toFile());
					ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				 AccessPermission permissions = new AccessPermission();
	                permissions.setCanPrint(allowPrinting);
	                permissions.setCanExtractContent(allowCopying);
	                permissions.setCanModify(false);
	                permissions.setCanAssembleDocument(false);
	                permissions.setCanExtractForAccessibility(true);
	                
	                StandardProtectionPolicy policy = new StandardProtectionPolicy(
	                        ownerPwd, userPassword, permissions);
	                policy.setEncryptionKeyLength(256); // AES-256

	                pdf.protect(policy);
	                pdf.save(out);

	                
	                return out.toByteArray();
			}
			
		} finally {
			fileUtil.deleteFile(tempPath);
		}
		
	}
	 
	public byte[] unlockPdf(MultipartFile file, String password) throws Exception {
		fileUtil.validatePdf(file);
		
		if(password == null || password.isBlank()) {
			throw new CustomException("Password is required to unlock a PDF.");
		}
		Path tempPath = null;
		
		try {
			tempPath = fileUtil.saveToTemp(file, "pdf");
			
			try (PDDocument pdf = PDDocument.load(tempPath.toFile(),password);
					ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				
				if( !pdf.isEncrypted()) {
					throw new CustomException("The provided PDF is not encrypted");
				}
				
				pdf.setAllSecurityToBeRemoved(true);
				pdf.save(out);
				
				return out.toByteArray();
			} catch(InvalidPasswordException e) {
				throw new CustomException("Incorrect Password");
			}
		} finally {
			fileUtil.deleteFile(tempPath);
		}
	}
}
