package com.project.converter.reader;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;


@Component
public class Reader {
	
	
	//csv Reader
	@Bean
	public FlatFileItemReader<Map<String,Object>> flatFileItemReader(@Value("#{jobParameters['filePath']}" )String pathToFile){
		
		FlatFileItemReader<Map<String,Object>> flatFileItemReader=new FlatFileItemReader<Map<String,Object>>();
		flatFileItemReader.setResource(new PathResource(pathToFile));

		    // Create an anonymous class implementing FieldSetMapper
		    FieldSetMapper<Map<String, Object>> fieldSetMapper = new FieldSetMapper<Map<String, Object>>() {
		        @Override
		        public Map<String, Object> mapFieldSet(FieldSet fieldSet) throws BindException {
		            Map<String, Object> map = new HashMap<>();

		            // Get the names of all columns (field names) from the FieldSet
		            String[] fieldNames = fieldSet.getNames();

		            // Iterate through the field names and retrieve the corresponding values from the FieldSet
		            for (String fieldName : fieldNames) {
		                String value = fieldSet.readRawString(fieldName);
		                if (StringUtils.isNumeric(value)) {
		                    map.put(fieldName, Integer.parseInt(value));
		                } else {
		                    map.put(fieldName, value);
		                }
		            }
		            return map;
		        }

				
		    };

		    // Use DefaultLineMapper parameterized with Map<String, Object>
		    DefaultLineMapper<Map<String, Object>> defaultLineMapper = new DefaultLineMapper<>();
		    defaultLineMapper.setLineTokenizer(new DelimitedLineTokenizer()); // Use DelimitedLineTokenizer
		    defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		    flatFileItemReader.setLineMapper(defaultLineMapper);

		    return flatFileItemReader;
		}
		
	}


