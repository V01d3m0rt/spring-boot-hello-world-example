package com.javatpoint.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.michaelcgood.model.ComputerSystem;

public class SlaveProcessor implements ItemProcessor<ComputerSystem, ComputerSystem> {

	@Override
	public ComputerSystem process(ComputerSystem data) throws Exception {
		return data;
	}
}
