package com.programmers.heycake.domain.image.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageStorageScheduler {

	private final JobLauncher jobLauncher;
	private final BatchConfig batchConfig;

	@Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul")
	public void deleteS3Image() {
		Map<String, JobParameter> parameters = new HashMap<>();
		parameters.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters jobParameters = new JobParameters(parameters);

		try {
			jobLauncher.run(batchConfig.deleteS3ImagesJob(), jobParameters);
		} catch (
				JobExecutionAlreadyRunningException |
				JobRestartException |
				JobInstanceAlreadyCompleteException |
				JobParametersInvalidException e
		) {
			throw new RuntimeException(e);
		}
	}
}
