package com.javatpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.javatpoint.config.BatchConfiguration;

@SpringBootApplication
@EnableJpaRepositories("com.michaelcgood.dao")
@EntityScan("com.michaelcgood.model")
public class SpringBootHelloWorldExampleApplication implements CommandLineRunner {
	Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

	
	
	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	@Qualifier("parallelStepsJob1")
	Job processJob1;
	
	@Autowired
	@Qualifier("parallelStepsJob2")
	Job processJob2;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootHelloWorldExampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		System.out.println("Our DataSource is = " + dataSource);
//		Iterable<com.michaelcgood.model.System> systemlist = systemRepository.findAll();
//		for (com.michaelcgood.model.System systemmodel : systemlist) {
//			System.out.println("Here is a system: " + systemmodel.toString());
//		}
		
		startJob();

	}

	public void startJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		 jobLauncher.run(processJob1,new JobParameters());
		 jobLauncher.run(processJob2, new JobParameters());
		
	}

	

}