package com.example.moduleticket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.moduleticket.domain.ticket.service.TicketTransferStreamConsumer;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class ModuleTicketApplicationTests {

	@MockitoBean
	private TicketTransferStreamConsumer ticketTransferStreamConsumer;

	@Test
	void contextLoads() {
	}

}
