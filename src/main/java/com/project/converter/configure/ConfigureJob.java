package com.project.converter.configure;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.project.converter.constants.Constants;
import com.project.converter.reader.Reader;
import com.project.converter.writer.Writer;

@Configuration
public class ConfigureJob {

	
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private Reader reader;
	
	@Autowired
	private Writer writer;
	
	
	
	@Bean
	public Job CSVtoJSON() {
		return new JobBuilder(Constants.CSV_TO_JSON, jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(chunkStepOne())
			.build();
	}
	
	@Bean
	public Job CSVtoXML() {
		return new JobBuilder(Constants.CSV_TO_XML, jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(chunkStepTwo())
			.build();
	}
	
	@Bean
	public Job XMLtoCSV() {
		return new JobBuilder(Constants.XML_TO_CSV, jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(chunkStepThree())
			.build();
	}
	
	@Bean
	public Job XMLtoJSON() {
		return new JobBuilder(Constants.XML_TO_JSON, jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(chunkStepFour())
			.build();
	}
	
	
	@Bean
	public Job JSONtoXML() {
		return new JobBuilder(Constants.JSON_TO_XML, jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(chunkStepSix())
			.build();
	}
	
	@Bean
	public Job JSONtoCSV() {
		return new JobBuilder(Constants.JSON_TO_CSV, jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(chunkStepFive())
			.build();
	}
	
	@Bean
	public Step chunkStepOne() {
		return new StepBuilder(Constants.CSV_TO_JSON+"ChunkStep", jobRepository)
				.<Map<String, Object>, Map<String, Object>>chunk(3,transactionManager)
				.reader(reader.flatFileItemReader(null))
				.writer(writer.jsonFileItemWriter())
				.build();
	}
	
	@Bean
	public Step chunkStepTwo() {
		return new StepBuilder(Constants.CSV_TO_XML+"ChunkStep", jobRepository)
				.<Map<String, Object>, Map<String, Object>>chunk(3,transactionManager)
				.reader(reader.flatFileItemReader(null))
				.build();
	}
	
	@Bean
	public Step chunkStepThree() {
		return new StepBuilder(Constants.XML_TO_CSV+"ChunkStep", jobRepository)
				.<Map<String, Object>, Map<String, Object>>chunk(3,transactionManager)
				.reader(reader.xmlItemReader(null))
				.build();
	}
	
	@Bean
	public Step chunkStepFour() {
		return new StepBuilder(Constants.XML_TO_JSON+"ChunkStep", jobRepository)
				.<Map<String, Object>, Map<String, Object>>chunk(3,transactionManager)
				.reader(reader.xmlItemReader(null))
				.writer(writer.jsonFileItemWriter())
				.build();
	}
	
	@Bean
	public Step chunkStepFive() {
		return new StepBuilder(Constants.JSON_TO_CSV+"ChunkStep", jobRepository)
				.<Map<String, Object>, Map<String, Object>>chunk(3,transactionManager)
				.reader(reader.jsonItemReader(null))
				.build();
	}
	

	@Bean
	public Step chunkStepSix() {
		return new StepBuilder(Constants.JSON_TO_XML+"ChunkStep", jobRepository)
				.<Map<String, Object>, Map<String, Object>>chunk(3,transactionManager)
				.build();
	}

}
