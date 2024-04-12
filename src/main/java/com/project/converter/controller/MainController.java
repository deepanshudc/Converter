package com.project.converter.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.converter.service.JobService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/")
public class MainController {
	
	@Autowired
	public JobService service;
	
	@PostMapping
	public ResponseEntity<T> convertFile(@RequestParam MultipartFile file,
			@RequestParam String convertFrom,@RequestParam String convertTo)
	{
		
		
			if ((convertFrom==null)||(convertTo==null)){

	            String errorMessage = "PLs fill all required fields,oneof the file is null";
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage); 
	        
			}
		
		   String fileExtension = getFileExtension(file);

	        if (!convertFrom.equalsIgnoreCase(fileExtension)) {
	            String errorMessage = "File extension does not match the 'convertFrom' format.";
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage); 
	        }
	        else {
	        	System.out.println("convertFrom and file extension matches.");
	        	
	        }
	        // Save the MultipartFile to a temporary file
	        Path tempFile = Files.createTempFile("temp", getFileExtension(file));
	        file.transferTo(tempFile.toFile());

	        JobParametersBuilder builder = new JobParametersBuilder();
	        builder.addLong("Current Time",System.currentTimeMillis());
	        builder.addString("filePath", tempFile.toString());
	        JobParameters jobParameters = builder.toJobParameters();
		    service.runJob(convertFrom.toUpperCase()+"to"+convertTo.toUpperCase() ,jobParameters);
		    
		    //loading the resource file
		    Resource resource = new UrlResource(tempFile.toUri());
		    
		    //delete the resource field after response send
		    Files.delete(tempFile);
	}

	        	

	
	public String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ""; 
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex > 0) ? fileName.substring(lastDotIndex + 1) : "";
    }
	
}



