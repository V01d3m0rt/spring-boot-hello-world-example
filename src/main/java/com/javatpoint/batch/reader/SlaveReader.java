package com.javatpoint.batch.reader;

import java.util.Queue;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;

import com.michaelcgood.model.ComputerSystem;

public class SlaveReader implements ItemReader<ComputerSystem> {

	private Queue<ComputerSystem> queue;
	
	public SlaveReader(Queue<ComputerSystem> queue) {
		this.queue = queue;
	}

	@Override
	public ComputerSystem read() {
		return queue.poll();
	}

}
