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
				request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage()
		);

		response.setCharacterEncoding("UTF-8");
		response.setContentType(APPLICATION_JSON.toString());
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		ErrorResponse errorResponse = ErrorResponse.of(
				e.getMessage(),
				request.getRequestURI(),
				null
		);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		PrintWriter writer = response.getWriter();
		writer.write(objectMapper.writeValueAsString(errorResponse));
		writer.flush();
		writer.close();
	}
}
