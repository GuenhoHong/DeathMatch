package com.deathmatch.genious.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.deathmatch.genious.domain.GameRoom;
import com.deathmatch.genious.domain.UnionDealerDTO;
import com.deathmatch.genious.domain.UnionGameDTO;

@Repository
public class UnionDealerDAOImpl implements UnionDealerDAO {

	private String namespace = "com.deathmatch.genious.mapper.UnionDealerMapper";
	private SqlSession sqlSession;
	
	public UnionDealerDAOImpl(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	public Boolean checkAnswer(UnionGameDTO gameDTO, GameRoom gameRoom) {
		Boolean isExist = false;
		
		gameDTO.setGameId(gameRoom.getGameId());
		gameDTO.setRound(gameRoom.getRound());
		
		int check = sqlSession.selectOne(namespace + ".checkAnswer", gameDTO);
		
		if(check == 1) {
			isExist = true;
		}
		
		return isExist;
	}

	@Override
	public Boolean checkCorrectSubmittedAnswer(UnionGameDTO gameDTO, GameRoom gameRoom) {
		Boolean isExist = false;
		
		gameDTO.setGameId(gameRoom.getGameId());
		gameDTO.setRound(gameRoom.getRound());
		
		int check = sqlSession.selectOne(namespace + ".checkCorrectSubmittedAnswer", gameDTO);
		
		if(check == 1) {
			isExist = true;
		}
		
		return isExist;
	}

	@Override
	public int countAnswer(UnionGameDTO gameDTO, GameRoom gameRoom) {
		gameDTO.setGameId(gameRoom.getGameId());
		gameDTO.setRound(gameRoom.getRound());
		return sqlSession.selectOne(namespace + ".countAnswer", gameDTO);
	}

	@Override
	public int countCorrectSubmittedAnswer(UnionGameDTO gameDTO, GameRoom gameRoom) {
		gameDTO.setGameId(gameRoom.getGameId());
		gameDTO.setRound(gameRoom.getRound());
		return sqlSession.selectOne(namespace + ".countCorrectSubmittedAnswer", gameDTO);
	}

	@Override
	public void insertSubmittedAnswer(UnionDealerDTO unionDealerDTO) {
		sqlSession.insert(namespace + ".insertSubmittedAnswer", unionDealerDTO);
	}

}
