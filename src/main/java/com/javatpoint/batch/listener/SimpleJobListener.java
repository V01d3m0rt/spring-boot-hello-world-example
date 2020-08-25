package com.javatpoint.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class SimpleJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		// Setting the exception in batch EXIT MESSAGE
		jobExecution.setExitStatus(new ExitStatus("ERROR", "Exception in JOB"));
	}
}