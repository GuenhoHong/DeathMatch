package com.deathmatch.genius.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.deathmatch.genius.domain.SuggestionBoardDTO;
import com.deathmatch.genius.util.Criteria;

import lombok.extern.log4j.Log4j;

@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/root-context.xml" })
public class SuggestionBoardTest {
	
	@Autowired
	private SuggestionBoardDAO dao;
	
	@Test
	public void testListCriteria() {
		Criteria cri = new Criteria();
		cri.setPage(2);
		cri.setPerPageNum(10);
		
		List<SuggestionBoardDTO> pageList = dao.getListWithPaging(cri);
		
		for(SuggestionBoardDTO board :pageList) {
			log.info(board.getBno() + " : " + board.getTitle() );
		}
	}
}
