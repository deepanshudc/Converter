package com.project.converter.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;

public class DynamicFlatFileItemReader extends FlatFileItemReader<Map<String, Object>>{
	
	private FieldSetMapper<Map<String, Object>> fieldSetMapper;
    
    
    public DynamicFlatFileItemReader(FieldSetMapper<Map<String, Object>> fieldSetMapper) {
    	this.fieldSetMapper=fieldSetMapper;
	}


	@Override
    public void setResource(Resource resource) {
        // Read the first line of the file
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            line = reader.readLine();
        } catch (IOException e) {
        	System.out.println("Error"+e.getStackTrace());
        }

        // Split the line into field names
        String[] fieldNames = line.split(","); // Assuming comma as delimiter

        // Set up the tokenizer with the field names
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(fieldNames);

        // Set up the line mapper with the tokenizer and field set mapper
        DefaultLineMapper<Map<String, Object>> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper); // Replace with your FieldSetMapper

        setLineMapper(lineMapper);

        // Call the superclass method to set the resource
        super.setResource(resource);
    }
}
