package com.programmers.heycake.domain.image.scheduler;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.programmers.heycake.domain.image.model.entity.Image;
import com.programmers.heycake.domain.image.repository.ImageRepository;
import com.programmers.heycake.domain.image.service.ImageStorageService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final ImageRepository imageRepository;
	private final ImageStorageService imageStorageService;

	@Bean
	public Job deleteS3ImagesJob() {
		return jobBuilderFactory.get("DeleteS3ImagesJob")
				.start(deleteS3ImagesStep())
				.build();
	}

	@Bean
	public Step deleteS3ImagesStep() {
		return stepBuilderFactory.get("DeleteS3ImagesStep")
				.tasklet(((contribution, chunkContext) -> {
					List<Image> deletedImages = imageRepository.findDeletedImages();

					deletedImages
							.forEach(image -> {
								imageStorageService.delete(image.getSubPath(), image.getFilename());
							});
					imageRepository.deleteAllInBatch(deletedImages);

					return RepeatStatus.FINISHED;
				}))
				.build();
	}
}
