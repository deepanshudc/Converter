package com.project.converter.writer;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;

@Component
public class WriterBatch {
	
	
	// json item writer
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
	
	//csv writer
	
	@Bean
	@StepScope
	public FlatFileItemWriter<Map<String, Object>> flatFileItemWriter(@Value("#{jobParameters['outputFile']}") String path) {
	    FlatFileItemWriter<Map<String, Object>> flatFileItemWriter = new FlatFileItemWriter<>();

	    // Set the resource
	    flatFileItemWriter.setResource(new PathResource(path));

	    // Create a list to hold the keys
	    List<String> keys = new ArrayList<>();

	    // Set the line aggregator
	    flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<Map<String, Object>>() {
	        {
	            setDelimiter(",");
	            setFieldExtractor(new FieldExtractor<Map<String, Object>>() {     	
	                @Override
	                public Object[] extract(Map<String, Object> item) {
	                    if (keys.isEmpty()) {
	                        keys.addAll(item.keySet());
	                    }
	                    return item.values().toArray(new Object[0]);
	                }
	            });
	        }
	    });

	    // Set the header callback
	    flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
	        @Override
	        public void writeHeader( Writer writer) throws IOException {
	            writer.write(String.join(",", keys));
	        }
	    });
	    return flatFileItemWriter;
	}



}
