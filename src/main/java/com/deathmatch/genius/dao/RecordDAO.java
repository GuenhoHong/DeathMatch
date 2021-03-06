package com.deathmatch.genius.dao;

import java.util.List;

import com.deathmatch.genius.domain.Criteria;
import com.deathmatch.genius.domain.RecordDTO;

public interface RecordDAO {

	// insert record of battle
	public void insertHistory(RecordDTO recordDTO);
	
	// select all record by user id
	public List<RecordDTO> selectAllRecord(Criteria criteria);
	
	// select record by game type
	public List<RecordDTO> selectRecordByGameType(String gameType);
	
	// select record of opponent related to gameId
	public RecordDTO selectOpponentRecord(RecordDTO recordDTO);
	
	// count the number of records
	public int countRecord(String userId);
}
