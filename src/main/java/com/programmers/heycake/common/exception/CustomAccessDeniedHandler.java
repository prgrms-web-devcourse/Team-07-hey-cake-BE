package com.programmers.heycake.common.exception;

import static org.springframework.http.MediaType.*;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.programmers.heycake.common.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException e) throws IOException {

		log.info("URL = {}, Exception = {}, Message = {}",
				request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
				"권한이 없습니다.",
				request.getRequestURI(),
				null
		);

		response.setCharacterEncoding("UTF-8");
		response.setContentType(APPLICATION_JSON.toString());
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		PrintWriter writer = response.getWriter();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.writeValueAsString(errorResponse);

		writer.println(errorResponse);
		writer.flush();
		writer.close();
	}
}
