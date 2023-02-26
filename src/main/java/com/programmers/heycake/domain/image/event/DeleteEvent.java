package com.programmers.heycake.domain.image.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteEvent {

	private final String subPath;
	private final String savedFilename;

}