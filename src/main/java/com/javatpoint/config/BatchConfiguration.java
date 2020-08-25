package com.javatpoint.config;

import java.util.stream.IntStream;

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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.michaelcgood.dao.SystemRepository;

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
	SystemRepository systemRepository;

    @Override
    public void setDataSource(DataSource dataSource) {
        //This BatchConfigurer ignores any DataSource
    }

	private TaskletStep taskletStep(String step) {
		return stepBuilderFactory.get(step).tasklet((contribution, chunkContext) -> {
			IntStream.range(1, 10).forEach(token -> logger.info("This is slave #" + step + " token:" + token));
			return RepeatStatus.FINISHED;
		}).build();

	}

	private TaskletStep mainStep() {
		return stepBuilderFactory.get("THIS IS MAIN STEP").tasklet((contribution, chunkContext) -> {
//			System.out.println("Our DataSource is = " + dataSource);
//			Iterable<com.michaelcgood.model.System> systemlist = systemRepository.findAll();
//			for (com.michaelcgood.model.System systemmodel : systemlist) {
//				System.out.println("Here is a system: " + systemmodel.toString());
//			}
			
			System.out.println("this is main step");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Job parallelStepsJob1() {

		Flow masterFlow = (Flow) new FlowBuilder("JOB #1 masterFlow").start(mainStep()).build();

		Flow flowJob1 = (Flow) new FlowBuilder("flow1").start(taskletStep("2")).build();
		Flow flowJob2 = (Flow) new FlowBuilder("flow2").start(taskletStep("3")).build();
		Flow flowJob3 = (Flow) new FlowBuilder("flow3").start(taskletStep("4")).build();

		Flow slaveFlow = (Flow) new FlowBuilder("slaveFlow").split(new SimpleAsyncTaskExecutor())
				.add(flowJob1, flowJob2, flowJob3).build();

		return (jobBuilderFactory.get("parallelFlowJob1").preventRestart().incrementer(new RunIdIncrementer())
				.start(masterFlow).next(slaveFlow).build()).build();

	}
	
	@Bean
	public Job parallelStepsJob2() {

		Flow masterFlow = (Flow) new FlowBuilder("JOB #2 masterFlow").start(mainStep()).build();

		Flow flowJob1 = (Flow) new FlowBuilder("flow1").start(taskletStep("2")).build();
		Flow flowJob2 = (Flow) new FlowBuilder("flow2").start(taskletStep("3")).build();
		Flow flowJob3 = (Flow) new FlowBuilder("flow3").start(taskletStep("4")).build();

		Flow slaveFlow = (Flow) new FlowBuilder("slaveFlow").split(new SimpleAsyncTaskExecutor())
				.add(flowJob1, flowJob2, flowJob3).build();

		return (jobBuilderFactory.get("parallelFlowJob2").preventRestart().incrementer(new RunIdIncrementer())
				.start(masterFlow).next(slaveFlow).build()).build();

	}

}