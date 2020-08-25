package com.javatpoint.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.javatpoint.batch.listener.SimpleJobListener;
import com.javatpoint.batch.processor.SlaveProcessor;
import com.javatpoint.batch.reader.SlaveReader;
import com.javatpoint.batch.writer.SlaveWriter;
import com.michaelcgood.dao.ComputerSystemRepository;
import com.michaelcgood.model.ComputerSystem;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer {

	Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Autowired
	DataSource dataSource;

	@Autowired
	ComputerSystemRepository systemRepository;

//    @Override
//    public void setDataSource(DataSource dataSource) {
//        //This BatchConfigurer ignores any DataSource
//    }

	private TaskletStep taskletStep(Queue<ComputerSystem> queue) {
		return stepBuilderFactory.get("orderStep1").<ComputerSystem, ComputerSystem>chunk(2)
				.reader(new SlaveReader(queue)).processor(new SlaveProcessor()).writer(new SlaveWriter()).build();
	}

	@Bean
	public Job parallelStepsJob1() {

		Iterable<ComputerSystem> systemIterable = systemRepository.findAll();
		int size = ((Collection<?>) systemIterable).size();

		int threshHold = 100;	//must be equal or greater then 100
		int noOfSlaves = 1;
		int noOfRecordsInASlave = 1;
		if (size > threshHold) {
			noOfSlaves = Integer.parseInt(String.valueOf(size).substring(0, 2));
			noOfRecordsInASlave = size / noOfSlaves;
		} else {
			noOfRecordsInASlave = size;
		}

		Iterator<ComputerSystem> sysItr = systemIterable.iterator();
		Flow[] flowArr = new Flow[noOfSlaves];
		for (int i = 0; i < noOfSlaves; i++) {
			Queue<ComputerSystem> queue = new LinkedList<ComputerSystem>();
			int c = 0;
			while (c++ < noOfRecordsInASlave && sysItr.hasNext()) {
				queue.add(sysItr.next());
			}
			Flow flowJob = (Flow) new FlowBuilder("flow1").start(taskletStep(queue)).build();
			flowArr[i] = flowJob;
		}

		Flow slaveFlow = (Flow) new FlowBuilder("slaveFlow").split(new SimpleAsyncTaskExecutor()).add(flowArr).build();

		return (jobBuilderFactory.get("parallelFlowJob1").incrementer(new RunIdIncrementer())
				.listener(new SimpleJobListener()).start(slaveFlow).build()).build();

	}

}