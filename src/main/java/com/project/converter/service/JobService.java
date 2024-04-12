package com.project.converter.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.project.converter.constants.Constants;

@Service
public class JobService {
	
	@Autowired
	public JobLauncher jobLauncher;
	
	@Autowired
	@Qualifier(Constants.CSV_TO_JSON)
	private Job csvTOjson;
	
	@Autowired
	@Qualifier(Constants.CSV_TO_XML)
	private Job csvTOxml;
	
	@Autowired
	@Qualifier(Constants.JSON_TO_CSV)
	private Job jsonTOcsv;
	
	@Autowired
	@Qualifier(Constants.JSON_TO_XML)
	private Job jsonTOxml;
	
	@Autowired
	@Qualifier(Constants.XML_TO_CSV)
	private Job xmlTOcsv;
	
	@Autowired
	@Qualifier(Constants.XML_TO_JSON)
	private Job xmlTOjson;


	public void runJob(String jobName, JobParameters jobParameters) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		// TODO Auto-generated method stub
		JobExecution jobExecution=null;
		if(jobName.equals(Constants.CSV_TO_JSON)) {
			jobExecution=jobLauncher.run(csvTOjson,jobParameters);
		}
		else if(jobName.equals(Constants.CSV_TO_XML)) {
			jobExecution=jobLauncher.run(csvTOxml,jobParameters);

	}
		else if(jobName.equals(Constants.XML_TO_CSV)) {
			jobExecution=jobLauncher.run(xmlTOcsv,jobParameters);

	}
		else if(jobName.equals(Constants.XML_TO_JSON)) {
			jobExecution=jobLauncher.run(xmlTOjson,jobParameters);

	}
		
		else if(jobName.equals(Constants.CSV_TO_XML)) {
			jobExecution=jobLauncher.run(csvTOxml,jobParameters);

	}
		System.out.println("Job execution id: "+jobExecution.getJobId());
	}

}
