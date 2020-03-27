package com.deathmatch.genious.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnionDealerDTO {

    public enum MessageType {
    	TURN, ROUND, END, UNI, ON, 
        JOIN, TALK, READY, OUT
    }
    private MessageType type;
    private String roomId;
    private String gameId;
    private String sender;
    private String message;
    private String user1;
    private String answer;
    private int countDown;
    private int score;
    private int round;
}
