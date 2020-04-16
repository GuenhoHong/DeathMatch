package com.deathmatch.genius.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.deathmatch.genius.dao.IndianSettingDAOImpl;
import com.deathmatch.genius.domain.IndianCardDTO;
import com.deathmatch.genius.domain.IndianGameDTO;
import com.deathmatch.genius.domain.IndianGameRoom;
import com.deathmatch.genius.domain.IndianPlayerDTO;
import com.deathmatch.genius.domain.IndianServiceDTO;
import com.deathmatch.genius.domain.UnionPlayerDTO;
import com.deathmatch.genius.domain.IndianGameDTO.MessageType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j;

/**
 * 
 * IndianService 실제 내용을 구현한다
 *
 */
@Log4j
@Service
public class IndianService {

	private final ObjectMapper objectMapper;
	private final IndianSettingDAOImpl indianDao;
	

	public IndianService(ObjectMapper objectMappper,IndianSettingDAOImpl indianDao) {
		this.objectMapper = objectMappper;
		this.indianDao = indianDao;
	}
	
	/* Web Socket Handler */
	
	public void handleActions(WebSocketSession session, IndianGameDTO indianGameDTO, IndianGameRoom indianRoom) {
		log.info("Type : " + indianGameDTO.getType());
		switch (indianGameDTO.getType()) {
		case JOIN:
			joinUser(session, indianGameDTO, indianRoom);
			break;
		case TALK:
			sendMessageAll(indianRoom.getSessions(), indianGameDTO);
			break;
		case READY:
			readyAct(session,indianGameDTO,indianRoom);
			break;
		}
	}
	
	/* From Server to Client need to Parsing Json */
	
	public Map<String,Object> convertMap(MessageType type, String roomId){
		Map<String,Object> jsonMap = new HashMap<>();
		
		jsonMap.put("type",type.toString());
		jsonMap.put("roomId",roomId);
		
		return jsonMap;
	}
	
	public IndianServiceDTO processing(Map<String,Object> jsonMap) {
		JSONObject jsonObject = new JSONObject(jsonMap);
		String jsonString = jsonObject.toJSONString();
		IndianServiceDTO indianServiceDTO = null;
		log.info("jsonString " + jsonString);
		
		try {
			indianServiceDTO = objectMapper.readValue(jsonString, IndianServiceDTO.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return indianServiceDTO;
	}
		
	public void quitSession(WebSocketSession session,IndianGameRoom indianRoom) {
		indianRoom.removeSession(session);
		log.info("session close");
	}
	
	/* Act SendMessage */
	
	public <T> void sendMessageAll(Set<WebSocketSession> sessions, T message) {
		log.info("sendMessageAll");
		sessions.parallelStream().forEach(session -> sendMessage(session, message));
	}

	public <T> void sendMessage(WebSocketSession session, T message) {
		try {
			session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Act Ready and Join */
	
	public boolean readyCheck(IndianGameRoom indianRoom) {
		List<IndianPlayerDTO> players = indianRoom.getPlayers();
		return players.get(0).getReady() && players.size() == 2;
	}
	
	public IndianServiceDTO readyUser(WebSocketSession session,IndianGameDTO indianGameDTO,IndianGameRoom indianRoom) {
		Map<String,Object> jsonMap = convertMap(MessageType.READY,indianRoom.getRoomId());
		IndianPlayerDTO player = (IndianPlayerDTO) session.getAttributes().get("player");
		player.setReady(true);
		log.info("indianGameDTO Sender : " + indianGameDTO.getSender());
		jsonMap.put("message", indianGameDTO.getSender() +"님이 준비하셨습니다 ! ");
		jsonMap.put("player", indianGameDTO.getSender());
		IndianServiceDTO indianServiceDTO = processing(jsonMap);
		return indianServiceDTO;
	}
	
	public IndianServiceDTO allReady(IndianGameRoom indianRoom) {
		Map<String,Object> jsonMap = convertMap(MessageType.READY, indianRoom.getRoomId());
		jsonMap.put("message", "플레이어가 모두 준비하였습니다! 게임을 시작합니다 ");
		IndianServiceDTO indianServiceDTO = processing(jsonMap);
		List<IndianCardDTO> cardList = getList();
		return indianServiceDTO;
	}

	public void readyAct(WebSocketSession session,IndianGameDTO indianGameDTO,IndianGameRoom indianRoom) {
		sendMessageAll(indianRoom.getSessions(),readyUser(session,indianGameDTO,indianRoom));
		if(readyCheck(indianRoom)) {
			sendMessageAll(indianRoom.getSessions(),allReady(indianRoom));
		}
	}
	
	public void register(WebSocketSession session, IndianGameDTO indianGameDTO, IndianGameRoom indianRoom) {
		IndianPlayerDTO player = IndianPlayerDTO.builder()
					.userId(indianGameDTO.getSender())
					.roomId(indianRoom.getRoomId())
					.ready(false)
					.chip(30)
					.build();
		log.info("register User: " + player.toString());
		
		Map<String,Object> map = session.getAttributes();
		map.put("player",player);
		indianRoom.addPlayer(player);
	}
	
	public void joinUser(WebSocketSession session, IndianGameDTO indianGameDTO, IndianGameRoom indianRoom) {
		Map<String,Object> jsonMap = convertMap(MessageType.JOIN, indianGameDTO.getRoomId());	
		indianRoom.addSession(session);
		loadGame(session,indianRoom);
		register(session,indianGameDTO,indianRoom);
		jsonMap.put("message", indianGameDTO.getSender() +"님이 방에 입장하셨습니다! ");
		jsonMap.put("player", indianGameDTO.getSender());
		IndianServiceDTO indianServiceDTO = processing(jsonMap);
		sendMessageAll(indianRoom.getSessions(),indianServiceDTO);
	}
	
	/* Load Player */
	
	Queue<Object> playerQueue = new LinkedList<>();
	
	public IndianServiceDTO loadPlayer(IndianPlayerDTO player, IndianGameRoom indianRoom) {
		Map<String,Object> jsonMap = convertMap(MessageType.LOAD,indianRoom.getRoomId());
		jsonMap.put("message", "PLAYER");
		jsonMap.put("player",player.getUserId());
		
		IndianServiceDTO indianServiceDTO = processing(jsonMap);
		return indianServiceDTO;
	}
	
	public void loadGame(WebSocketSession session, IndianGameRoom indianRoom) {
		log.info("playerNum : " + indianRoom.getPlayers().size());
		switch(indianRoom.getPlayers().size()) {
			case 2:
				IndianServiceDTO getPlayer1 = 
					loadPlayer(indianRoom.getPlayers().get(1),indianRoom);
				playerQueue.add(getPlayer1);
			case 1:
				IndianServiceDTO getPlayer2 =
					loadPlayer(indianRoom.getPlayers().get(0),indianRoom);
				playerQueue.add(getPlayer2);
				break;
		}
		loadPlayer(session,indianRoom);
	}
	
	public void loadPlayer(WebSocketSession session, IndianGameRoom indianRoom) {
		while(!playerQueue.isEmpty()) {
			sendMessage(session,playerQueue.poll());
		}
	}
	
	/* Act Make Problem */
	
	public List<IndianCardDTO> getList(){
		List<IndianCardDTO> cardDeck = indianDao.problemList();
		for(IndianCardDTO card : cardDeck) {
			log.info("Card: " + card.toString());
		}
		return cardDeck;
	}
}