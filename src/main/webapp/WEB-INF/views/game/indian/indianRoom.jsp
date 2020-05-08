<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="../../includes/meta.jsp"%>
<title>Genius Death Match</title>
<%@ include file="../../includes/header.jsp"%>
<link href="/css/game/indian/indianRoom.css" rel="stylesheet">
</head>
<body>

	<div id="game">
		<div class="indian-flex">
			<div>
				<img id="logo"
					src="${pageContext.request.contextPath}/images/indianLogo.png">
			</div>
		</div>

		<div id="card-flex">
			<div class="cards">
				<img id="card1"
					src="${pageContext.request.contextPath}/images/indiancards/card.png">
			</div>
			<div class="cards">
				<img id="card2"
					src="${pageContext.request.contextPath}/images/indiancards/card.png">
			</div>
		</div>
		
		<div id="player-flex">
			<div>
				<input id="playerId1" readonly>
			</div>
			<div>
				<input id="playerId2" readonly>
			</div>
		</div>
		
		<div id="player-flex">
			<div class="players">
				<img id="player1"
					src="${pageContext.request.contextPath}/images/player1.png">
			</div>
			<div class="players">
				<img id="player2"
					src="${pageContext.request.contextPath}/images/player2.png">
			</div>
		</div>
		
		<div id ="player-flex">
			<div>
				<button type ="button" id ="readyBtn">Ready</button>
			</div>
		</div>
		
		<div class ="indian-flex">
			<div id="connectionArea"></div>
		</div>
		
		<div class ="indian-flex">
			<textarea id="infoArea" rows="4" cols="30" readonly></textarea><p>
		</div>

		<div class="indian-flex">
			<textarea id="chatArea" rows="4" cols="70" readonly></textarea><p>
		</div>

		<div class=indian-flex">
			<input type="text" id="message" /> <input type="button" id="sendBtn"
				value="submit" />
		</div>

		<div class="indian-flex">
			<button id="leave">나가기</button>
		</div>

		<div class="indian-flex">
			<div class="content" data-room-id="${room.roomId}"
				data-member="${member}">
			</div>
			<%@ include file="../../includes/footer.jsp"%>
		</div>

	</div>

	<script src="/js/game/indian/indianRoom.js"></script>

</body>
</html>