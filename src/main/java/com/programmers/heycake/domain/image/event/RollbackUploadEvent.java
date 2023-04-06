package com.programmers.heycake.domain.image.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RollbackUploadEvent {

	private final String subPath;
	private final String savedFilename;

}