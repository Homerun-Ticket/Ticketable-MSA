package com.example.moduleticket.domain.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketChangeOwnerDto {

	private Long ticketId;
	private Long newOwnerId;
	private Long bidPoint;
}


