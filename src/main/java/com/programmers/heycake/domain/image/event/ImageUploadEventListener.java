package com.programmers.heycake.domain.image.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.programmers.heycake.domain.image.service.ImageUploadService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageUploadEventListener {

	private final ImageUploadService imageUploadService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
	public void rollbackUploadImage(UploadRollbackEvent event) {
		imageUploadService.delete(event.getSubPath(), event.getSavedFilename());
	}

}