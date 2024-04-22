package com.project.converter.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Component
public class WriterBatch {
	
	
	// json item writer
	@Bean
	@StepScope
	public JsonFileItemWriter<Map<String,Object>> jsonFileItemWriter() {
				  File convertedFile;
	    try {
            long timeStamp = System.currentTimeMillis(); // Generate timestamp

	    	 convertedFile= new File("F:\\convertFile\\convertedJson"+timeStamp+".json");
	    	if (convertedFile.createNewFile()) {
	    	    System.out.println("File created: " + convertedFile.getName());
	    	} else {
	    	    System.out.println("File already exists.");
	    	}
	    } catch (IOException e) {
	        throw new RuntimeException("Unable to create  file", e);
	    }
	    WritableResource resource = new FileSystemResource(convertedFile);
	    System.out.println("Resource set: " + resource);
		JsonFileItemWriter<Map<String,Object>> jsonFileItemWriter = new JsonFileItemWriter<Map<String,Object>>( resource, new JacksonJsonObjectMarshaller<>());

		jsonFileItemWriter.setName("dataWriterJson");

	    return jsonFileItemWriter;
	}
	
	//csv writer
	
	@Bean
	@StepScope
	public FlatFileItemWriter<Map<String, Object>> flatFileItemWriter() {
		
		  File convertedFile;
		  
		  try {
	            long timeStamp = System.currentTimeMillis(); // Generate timestamp

		    	 convertedFile= new File("F:\\convertFile\\convertedCSV"+timeStamp+".csv");
		    	if (convertedFile.createNewFile()) {
		    	    System.out.println("File created: " + convertedFile.getName());
		    	} else {
		    	    System.out.println("File already exists.");
		    	}
		    } catch (IOException e) {
		        throw new RuntimeException("Unable to create  file", e);
		    }
		
		WritableResource resource = new FileSystemResource(convertedFile);
		System.out.println("Resource set: " + resource);
	    FlatFileItemWriter<Map<String, Object>> flatFileItemWriter = new FlatFileItemWriter<>();

	    // Set the resource
	    flatFileItemWriter.setResource(resource);

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
                List<String> header  =keys.stream().map(key -> key.substring(0, 1).toUpperCase() + key.substring(1)).collect(Collectors.toList());
	                    
	                        
	                       // Write headers to file
	                               try (BufferedWriter writer = new BufferedWriter(new FileWriter(convertedFile, true))) {
	                                    writer.write(String.join(",", header));
	                                    writer.newLine();
	                                } catch (IOException e) {
	                                    throw new RuntimeException("Unable to write headers", e);
	                                }
	                            }

	                            return item.values().toArray(new Object[0]);
	                        }
	            });

	            
	        }
	            });
	        
	      
	    return flatFileItemWriter;
	}


	
	//xml writer
	
	
	@Bean
	@StepScope
	public FlatFileItemWriter<Map<String, Object>> flatFileItemWriterForXml() {
		
		File convertedFile;
		 try {
	            long timeStamp = System.currentTimeMillis(); // Generate timestamp

		    	 convertedFile= new File("F:\\convertFile\\convertedsFile"+timeStamp+".json");
		    	if (convertedFile.createNewFile()) {
		    	    System.out.println("File created: " + convertedFile.getName());
		    	} else {
		    	    System.out.println("File already exists.");
		    	}
		    } catch (IOException e) {
		        throw new RuntimeException("Unable to create  file", e);
		    }
		
		 WritableResource resource = new FileSystemResource(convertedFile);;
		 System.out.println("Resource set: " + resource);
		
	    FlatFileItemWriter<Map<String, Object>> flatFileItemWriter = new FlatFileItemWriter<>();

	    // Set the resource
	    flatFileItemWriter.setResource(resource);

	    // Set the line aggregator
	    flatFileItemWriter.setLineAggregator(new LineAggregator<Map<String, Object>>() {
	    	
	        @Override
	        public String aggregate(Map<String, Object> item) {
	            return hashMapToXml(item);
	        }
	    });

	    return flatFileItemWriter;
	}
	
	//function to convert hashmap object to xml string document
	public String hashMapToXml(Map<String, Object> map) {
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder;

	    try {
	        dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.newDocument();
	        Element rootElement = (Element) doc.createElement("Root");
	        doc.appendChild((Node) rootElement);

	        for (String key : map.keySet()) {
	            Element element = (Element) doc.createElement(key);
	            ((Node) element).appendChild(doc.createTextNode(map.get(key).toString()));
	            ((Node) rootElement).appendChild((Node) element);
	        }

	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer =  transformerFactory.newTransformer();
	        DOMSource source = new DOMSource(doc);

	        StringWriter writer = new StringWriter();
	        StreamResult result = new StreamResult(writer);
	        transformer.transform(source, result);

	        return writer.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}



}
