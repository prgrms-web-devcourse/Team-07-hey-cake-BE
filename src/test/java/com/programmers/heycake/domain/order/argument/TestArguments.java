package com.programmers.heycake.domain.order.argument;

import static com.programmers.heycake.domain.order.model.vo.BreadFlavor.*;
import static com.programmers.heycake.domain.order.model.vo.CakeCategory.*;
import static com.programmers.heycake.domain.order.model.vo.CakeHeight.*;
import static com.programmers.heycake.domain.order.model.vo.CakeSize.*;
import static com.programmers.heycake.domain.order.model.vo.CreamFlavor.*;
import static org.springframework.http.MediaType.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.mock.web.MockMultipartFile;

import com.programmers.heycake.domain.order.model.vo.BreadFlavor;

public class TestArguments {
	private static Stream<Arguments> OrderCreateRequestSuccessArguments() {
		return Stream.of(
				Arguments.of(
						10000,
						"강남구",
						"제목",
						LocalDateTime.now(),
						PHOTO, MINI, ONE_LAYER, BreadFlavor.CHOCO, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						10000,
						"강남구",
						"제목",
						LocalDateTime.now(),
						PHOTO, MINI, ONE_LAYER, BreadFlavor.CHOCO, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						20000,
						"강남구",
						"제목1",
						LocalDateTime.now(),
						LETTERING, NO_1, TWO_LAYER, CARROT, WHIPPED_CREAM
						, "추가 요구 사항111", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				)
		);
	}

	private static Stream<Arguments> OrderCreateRequestFailArguments() {
		return Stream.of(
				// 파라미터 누락
				Arguments.of(
						null,
						"강남구",
						"제목",
						LocalDateTime.now(),
						PHOTO, MINI, ONE_LAYER, BreadFlavor.CHOCO, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						100000,
						null,
						"제목",
						LocalDateTime.now(),
						PHOTO, MINI, ONE_LAYER, BreadFlavor.CHOCO, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						100000,
						"강남구",
						null,
						LocalDateTime.now(),
						PHOTO, MINI, ONE_LAYER, BreadFlavor.CHOCO, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						null,
						"강남구",
						"제목",
						null,
						PHOTO, MINI, ONE_LAYER, BreadFlavor.CHOCO, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),

				// 잘못된 enum 값
				Arguments.of(
						10000,
						"강남구",
						"제목",
						LocalDateTime.now(),
						null, MINI, ONE_LAYER, BreadFlavor.CHOCO, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						10000,
						"강남구",
						"제목",
						LocalDateTime.now(),
						PHOTO, null, ONE_LAYER, GREEN_TEA, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						10000,
						"강남구",
						"제목",
						LocalDateTime.now(),
						PHOTO, MINI, null, VANILLA, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						10000,
						"강남구",
						"제목",
						LocalDateTime.now(),
						PHOTO, MINI, ONE_LAYER, null, CREAM_CHEESE
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						10000,
						"강남구",
						"제목",
						LocalDateTime.now(),
						PHOTO, MINI, ONE_LAYER, GREEN_TEA, null
						, "추가 요구 사항", List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						10000,
						"강남구",
						"제목",
						LocalDateTime.now(),
						PHOTO, MINI, ONE_LAYER, GREEN_TEA, null
						, null, List.of(
								new MockMultipartFile("name1", "fileName1", IMAGE_PNG_VALUE, "image".getBytes()),
								new MockMultipartFile("name2", "fileName2", IMAGE_PNG_VALUE, "image".getBytes())
						)
				),
				Arguments.of(
						10000,
						"강남구",
						"제목",
						LocalDateTime.now(),
						PHOTO, MINI, ONE_LAYER, GREEN_TEA, null
						, "추가 요구 사항", null
				)
		);
	}
}