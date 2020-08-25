package com.javatpoint.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.michaelcgood.model.ComputerSystem;

public class SlaveWriter implements ItemWriter<ComputerSystem> {

	@Override
	public void write(List<? extends ComputerSystem> data) throws Exception {
		for (ComputerSystem d : data) {
			System.out.println(d);
		}
	}
}
