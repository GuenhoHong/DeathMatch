package com.deathmatch.genious.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.deathmatch.genious.domain.GameRoom;
import com.deathmatch.genious.domain.UnionAnswerDTO;
import com.deathmatch.genious.domain.UnionCardDTO;
import com.deathmatch.genious.domain.UnionCardDTO.BackType;
import com.deathmatch.genious.domain.UnionCardDTO.ColorType;
import com.deathmatch.genious.domain.UnionCardDTO.ShapeType;
import com.deathmatch.genious.domain.UnionSettingDTO;
import com.deathmatch.genious.util.UnionCombination;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@RequiredArgsConstructor
@Service
public class UnionSettingService {
	
	private List<UnionCardDTO> allCardList;
	private final UnionCombination unionCombination;
	private final ObjectMapper objectMapper;// = new ObjectMapper();

	@PostConstruct
	public void init() {
		allCardList = new ArrayList<>();
		enumerateAllCards();
	}
	
	private void enumerateAllCards() {
		
		ShapeType[] shapeValues = UnionCardDTO.ShapeType.values();
		ColorType[] colorValues = UnionCardDTO.ColorType.values();
		BackType[] backValues = UnionCardDTO.BackType.values();
		
		for(ShapeType shape : shapeValues) {
			for(ColorType color : colorValues) {
				for(BackType back : backValues) {
					
					char shapeFirstChar = shape.toString().charAt(0); 
					char colorFirstChar = color.toString().charAt(0);
					char backFirstChar = back.toString().charAt(0);
					
					String name = String.valueOf(shapeFirstChar)
							+ String.valueOf(colorFirstChar)
							+ String.valueOf(backFirstChar);
					
					String resourceAddress = "genious/resources/images/"
							+ name + ".jpg";
					
					UnionCardDTO unionCard = UnionCardDTO.builder()
							.name(name)
							.shape(shape)
							.color(color)
							.background(back)
							.resourceAddress(resourceAddress)
							.build();
					
					allCardList.add(unionCard);
				}
			}
		}
		
		log.info("allCardList : " + allCardList);
	}

	public boolean readyCheck(Map<String, Boolean> readyUser) {
		boolean isReady = false;
		int countReady = 0;
		
		for (Boolean ready : readyUser.values()) {
			if (ready) {
				countReady++;
			}
		}
		
    	if (countReady > 1) {
    		isReady = true;
    	}
    	return isReady;
	}
	
	public UnionSettingDTO standby(GameRoom gameRoom) {
		
		Map<String, String> jsonMap = new HashMap<String, String>();

		jsonMap.put("type", "READY");
		jsonMap.put("roomId", gameRoom.getRoomId());
		jsonMap.put("sender", "Dealer");
		jsonMap.put("message", "참가자들이 모두 준비를 마쳤습니다.\n곧 게임을 시작합니다.");
		jsonMap.put("score", "0");
		
		UnionSettingDTO unionSettingDTO = null;
		JSONObject jsonObject = new JSONObject(jsonMap);
		String jsonString = jsonObject.toJSONString();
		log.info("jsonString : " + jsonString);

		try {
			unionSettingDTO = objectMapper.readValue(jsonString, UnionSettingDTO.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return unionSettingDTO;
	}
	
	public UnionSettingDTO decideRound(GameRoom gameRoom) {
		
		int currentRound = gameRoom.getRound();
		int nextRound = currentRound + 1;
				
		Map<String, String> jsonMap = new HashMap<String, String>();
		
		jsonMap.put("type", "ROUND");
		jsonMap.put("roomId", gameRoom.getRoomId());
		jsonMap.put("sender", "Setting");
		jsonMap.put("round", Integer.toString(nextRound));
		jsonMap.put("message", "이번에는 " + Integer.toString(nextRound) + " ROUND 입니다");
		
		JSONObject jsonObject = new JSONObject(jsonMap);
		String jsonString = jsonObject.toJSONString();
		
		log.info("jsonString : " + jsonString + "\n");
		
		UnionSettingDTO unionSettingDTO = null;
		
		try {
			unionSettingDTO = objectMapper.readValue(jsonString, UnionSettingDTO.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return unionSettingDTO;
	}
	
	public UnionSettingDTO setPlayers(GameRoom gameRoom) {
		
		Object[] players = gameRoom.getReadyUser().keySet().toArray();
		Map<String, String> jsonMap = new HashMap<String, String>();
		
		jsonMap.put("type", "READY");
		jsonMap.put("roomId", gameRoom.getRoomId());
		jsonMap.put("sender", "Setting");
		jsonMap.put("user1", (String)players[0]);
		jsonMap.put("user2", (String)players[1]);
		
		JSONObject jsonObject = new JSONObject(jsonMap);
		String jsonString = jsonObject.toJSONString();
		UnionSettingDTO unionSettingDTO = null;
		
		log.info("jsonString : " + jsonString + "\n");
		
		try {
			unionSettingDTO = objectMapper.readValue(jsonString, UnionSettingDTO.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return unionSettingDTO;
	}
	
	public Map<String, UnionCardDTO> makeUnionProblem() {
		Map<String, UnionCardDTO> problemMap = new LinkedHashMap<>();
		int selectCardNumber = 9;
		List<UnionCardDTO> randomCardList = allCardList;
		
		Collections.shuffle(randomCardList);
		
		for(int i = 0; i < selectCardNumber; i++) {
			problemMap.put(randomCardList.get(i).getName(), randomCardList.get(i));
		}
		
		return problemMap;
	}
	
	public UnionSettingDTO setUnionProblem(GameRoom gameRoom) {
		
		Map<String, UnionCardDTO> problemMap = makeUnionProblem();
		gameRoom.setProblemMap(problemMap);
		
		log.info("problemMap : " + problemMap);
		log.info("problemMap.keySet() : " + problemMap.keySet());
		
		JSONObject jsonObject = new JSONObject();
		Map<String, Object> jsonMap = new HashMap<>();
		List<String> problemKeyList = new ArrayList<String>(problemMap.keySet());
		
		jsonMap.put("type", "PROBLEM");
		jsonMap.put("roomId", gameRoom.getRoomId());
		jsonMap.put("sender", "Setting");
		jsonMap.put("cards", problemKeyList);
		
//		jsonMap.put("card1", problemKeyList.get(0));
//		jsonMap.put("card2", problemKeyList.get(1));
//		jsonMap.put("card3", problemKeyList.get(2));
//		jsonMap.put("card4", problemKeyList.get(3));
//		jsonMap.put("card5", problemKeyList.get(4));
//		jsonMap.put("card6", problemKeyList.get(5));
//		jsonMap.put("card7", problemKeyList.get(6));
//		jsonMap.put("card8", problemKeyList.get(7));
//		jsonMap.put("card9", problemKeyList.get(8));
		
		jsonObject = new JSONObject(jsonMap);
		
		String jsonString = jsonObject.toJSONString();
		
		log.info("jsonString : " + jsonString);
		
		UnionSettingDTO unionProblemDTO = new UnionSettingDTO();
		
		try {
			unionProblemDTO = objectMapper.readValue(jsonString, UnionSettingDTO.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("unionProblemDTO : " + unionProblemDTO + "\n");
		
		return unionProblemDTO;
	}
	
	public Set<UnionAnswerDTO> makeUnionAnswer(Map<String, UnionCardDTO> problemMap
			, Set<UnionAnswerDTO> answerCandidateSet) {
				
		Set<UnionAnswerDTO> answerSet = new HashSet<>();
		
		for(UnionAnswerDTO answerCandidate : answerCandidateSet) {
			
			UnionCardDTO card1 = answerCandidate.getCard1();
			UnionCardDTO card2 = answerCandidate.getCard2();
			UnionCardDTO card3 = answerCandidate.getCard3();
			
			Set<ShapeType> shapeList = new HashSet<>();
			Set<ColorType> colorList = new HashSet<>();
			Set<BackType> backList = new HashSet<>();

			shapeList.add(card1.getShape());
			shapeList.add(card2.getShape());
			shapeList.add(card3.getShape());
			
			colorList.add(card1.getColor());
			colorList.add(card2.getColor());
			colorList.add(card3.getColor());
			
			backList.add(card1.getBackground());
			backList.add(card2.getBackground());
			backList.add(card3.getBackground());
			
			int satisfiedCondition = 0;
			
			if(shapeList.size() == 1 || shapeList.size() == 3) {
				satisfiedCondition++;
			} else {
				continue;
			}
			
			if(colorList.size() == 1 || colorList.size() == 3) {
				satisfiedCondition++;
			} else {
				continue;
			}
			
			if(backList.size() == 1 || backList.size() == 3) {
				satisfiedCondition++;
			} else {
				continue;
			}
			
			if(satisfiedCondition == 3) {
				answerSet.add(answerCandidate);
			}
			
		}
		
		for(UnionAnswerDTO answer : answerSet) {
			
			log.info("answer : " + answer.getCard1().getName()
					+ " " + answer.getCard2().getName() 
					+ " " + answer.getCard3().getName());
				
		}
		log.info("answerSet.size() : " + answerSet.size() + "\n");
		
		return answerSet;
	}
	
	public void setUnionAnswer(GameRoom gameRoom){
		
		Map<String, UnionCardDTO> problemMap = gameRoom.getProblemMap();
		Set<String> problemKeySet = problemMap.keySet();
		List<UnionCardDTO> problemCardList = new ArrayList<UnionCardDTO>();
		Set<UnionAnswerDTO> answerCandidateSet = new HashSet<>();
		Set<UnionAnswerDTO> answerSet = new HashSet<>();
		
		for(String pks : problemKeySet) {
			problemCardList.add(problemMap.get(pks));
		}
		
		answerCandidateSet = unionCombination.makeCombination(problemCardList);
		answerSet = makeUnionAnswer(problemMap, answerCandidateSet);
		gameRoom.setAnswerSet(answerSet);
		
		log.info("answerSet : " + gameRoom.getAnswerSet() + "\n");
	}	
}
