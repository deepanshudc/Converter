package com.project.converter.writer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;

@Component
public class Writer {
	
	
	//Json Item writer
	@Bean
	@StepScope
	public JsonFileItemWriter<Map<String,Object>> jsonFileItemWriter() {
				  File tempFile;
	    try {
	        tempFile = File.createTempFile("temp", ".json");
	    } catch (IOException e) {
	        throw new RuntimeException("Unable to create temporary file", e);
	    }

		JsonFileItemWriter<Map<String,Object>> jsonFileItemWriter = new JsonFileItemWriter<Map<String,Object>>((WritableResource) tempFile, new JacksonJsonObjectMarshaller<>());

		jsonFileItemWriter.setName("dataWriterJson");

	    return jsonFileItemWriter;
	} 

}
