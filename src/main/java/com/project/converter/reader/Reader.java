package com.project.converter.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.PathResource;
import org.springframework.oxm.UncategorizedMappingException;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;



@Component
public class Reader {
	
	
	//csv reader
	
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
	
	//json reader
	@Bean
	@StepScope
	public JsonItemReader<Map<String, Object>> jsonItemReader(@Value("#{jobParameters['filePath']}" )String pathToFile) {

	    JsonItemReader<Map<String, Object>> jsonItemReader = new JsonItemReader<>();

	    jsonItemReader.setResource(new PathResource(pathToFile));

	    // Use JacksonJsonObjectReader with Map<String, Object>
	    jsonItemReader.setJsonObjectReader(new JacksonJsonObjectReader<>(Map.class));

	    return jsonItemReader;
	}
	
	
	//xml reader
	
	    @Bean
	    public StaxEventItemReader<Map<String, Object>> xmlItemReader(@Value("#{jobParameters['filePath']}" )String pathToFile) {
	        StaxEventItemReader<Map<String, Object>> reader = new StaxEventItemReader<>();
	        reader.setResource(new PathResource(pathToFile));
	        reader.setUnmarshaller(customUnmarshaller());

	        return reader;
	    }

	    private Unmarshaller customUnmarshaller() {
	        return new Unmarshaller() {
	        	
	            @Override
	            public Object unmarshal(Source source) throws IOException, XmlMappingException {
	                XMLInputFactory factory = XMLInputFactory.newInstance();
	                try {
	                	InputStream inputStream;
                        if (source instanceof StreamSource) {
                            inputStream = ((StreamSource) source).getInputStream();	           
	                        } else {
	                            throw new IllegalArgumentException("Unsupported Source type");
	                        }
	                    XMLEventReader xmlEventReader = factory.createXMLEventReader(inputStream);

	                    Map<String, Object> rootMap = new HashMap<>();
	                    while (xmlEventReader.hasNext()) {
	                        XMLEvent event = xmlEventReader.nextEvent();
	                        if (event.isStartElement()) {
	                            StartElement startElement = event.asStartElement();
	                            String elementName = startElement.getName().getLocalPart();
	                            if (rootMap.containsKey(elementName)) {
	                                Object value = rootMap.get(elementName);
	                                if (value instanceof List) {
	                                    ((List<Object>) value).add(parseElement(xmlEventReader));
	                                } else {
	                                    List<Object> list = new ArrayList<>();
	                                    list.add(value);
	                                    list.add(parseElement(xmlEventReader));
	                                    rootMap.put(elementName, list);
	                                }
	                            } else {
	                                rootMap.put(elementName, parseElement(xmlEventReader));
	                            }
	                        }
	                    }
	                    return rootMap;
	                } catch (XMLStreamException e) {
	                    throw new UncategorizedMappingException("Error reading XML", e);
	                }
	            }

	            private Map<String, Object> parseElement(XMLEventReader xmlEventReader) throws XMLStreamException {
	                Map<String, Object> map = new HashMap<>();
	                while (xmlEventReader.hasNext()) {
	                    XMLEvent event = xmlEventReader.nextEvent();
	                    if (event.isEndElement()) {
	                        break;
	                    }
	                    if (event.isStartElement()) {
	                        StartElement startElement = event.asStartElement();
	                        String elementName = startElement.getName().getLocalPart();
	                        String value = xmlEventReader.getElementText();
	                        map.put(elementName, value);
	                    }
	                }
	                return map;
	            }

				@Override
				public boolean supports(Class<?> clazz) {
					// TODO Auto-generated method stub
					return false;
				}	
	        };
	    }
	}
	



