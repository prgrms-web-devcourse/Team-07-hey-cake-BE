package com.programmers.heycake.domain.image.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.programmers.heycake.domain.image.service.ImageStorageService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageEventListener {

	private final ImageStorageService imageStorageService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
	public void rollbackUploadedImage(RollbackUploadEvent event) {
		imageStorageService.delete(event.getSubPath(), event.getSavedFilename());
	}

}