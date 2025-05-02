package com.example.moduleticket.feign;

import com.example.moduleticket.global.exception.ErrorCode;
import com.example.moduleticket.global.exception.ServerException;
import com.example.moduleticket.global.exception.UnknownException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Response.Body;
import feign.Util;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignClientErrorDecoder implements ErrorDecoder {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public Exception decode(String s, Response response) {
		try {

			Body responseBody = response.body();
			if (responseBody == null) {
				return new RuntimeException("No content returned from server");
			}

			String body = Util.toString(response.body().asReader());
			Map<String, String> map = mapper.readValue(body, Map.class);
			if(map.get("code") == null) {
				return new UnknownException();
			} else {
				return new ServerException(ErrorCode.valueOf(map.get("code")));
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
