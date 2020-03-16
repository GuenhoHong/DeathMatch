package com.deathmatch.genious.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnionPlayerDTO {

	private String userEmail;
	private String roomId;
	private String status;
	private boolean ready;
	private int score;
}
