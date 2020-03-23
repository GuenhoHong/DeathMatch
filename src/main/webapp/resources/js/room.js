$(function () {
	
// websocket variables
	
	var url = window.location.host;//웹브라우저의 주소창의 포트까지 가져옴
	var pathname = window.location.pathname; /* '/'부터 오른쪽에 있는 모든 경로*/
	var appCtx = pathname.substring(0, pathname.indexOf("/",2));
	var root = url+appCtx;
	
	var sock = new SockJS("http://"+root+"/ws/chat");	
	
// variables
	
	// request variables
	var roomId = $('.content').data('room-id');
	var member = $('.content').data('member');
	
	// resources variables
	var defaultImagePath = "/genious/resources/images/";
	var defaultJpg = ".jpg";
	var defaultPng = ".png";
	
    // button tag
	var selectBtn = $('.select');
	var uniBtn = $('.uni');
	var onBtn = $('.on');
	var readyBtn = $('.ready');
	
	// input tag
	var selectBox = $('.selectbox');
	
	var selectedInput = $('#selected');
	var playerAInput = $('#playerA');
	var playerBInput = $('#playerB');
	var scoreAInput = $('#scoreA');
	var scoreBInput = $('#scoreB');

	// div tag
	var connectionStatus = $('#connectionStatus');
	
	// textarea tag
	var gameBroadcast = $('#broadcast');
	
	// ul tag
    var answerList = $('.answer');
    
    // p tag
    var roundP = $('#round');

// timer variables
    
    const FULL_DASH_ARRAY = 283; // 2*pi*r
    let timerInterval = null;
    var timer;
    
    
// websocket actions
    
    sock.onopen = function () {
        sock.send(JSON.stringify({type: 'JOIN', roomId: roomId, sender: member}));
        connectionStatus.text('connection opened');
        hideUnion();
        setProblemNum();
    }
    
    sock.onmessage = function (event){
    	var content = JSON.parse(event.data);
    	    	
    	switch(content.sender){
    	case "Setting":
    	case "Dealer":
    		fromServer(content);
    		break;
    	default:
    		fromUser(content);
    	}
    }
    
 	sock.onclose = function(event){
 		console.log("sock.onclose");
 		connectionStatus.text('connection closed');
 	}

// websocket functions
 	
 	function fromServer(content){
 		switch(content.type){
 		case "JOIN":
 			notifyJoin(content);
 			break;
 		case "READY":
 			notifyReady(content);
 			break;
 		case "ROUND":
 			notifyRound(content);
 			break;
 		case "PROBLEM":
 			notifyProblem(content);
 			break;
 		case "UNI":
 			notifyUni(content);
 			break;
 		case "ON":
 			notifyOn(content);
 			break;
 		case "TURN":
 			notifyTurn(content);
 			break;
 		case "END":
 			notifyEnd(content);
 			break;
 		default:
 			console.log("fromServer default");
 		}
 	}
 	
 	function fromUser(content){
 		switch(content.type){
 		case "UNI":
 			submitUni(content);
 			break;
 		case "ON":
 			submitOn(content);
 			break;
 		default:
 			console.log("fromUser default");
 		}
 	}
 	
 	function notifyJoin(content){
		gameBroadcast.eq(0).prepend(content.sender + ' : ' + content.message + '\n');
 	}
 	
 	function notifyReady(content){
		gameBroadcast.eq(0).prepend(content.sender + ' : ' + content.message + '\n');
		
		if(content.user1){
			playerAInput.val(content.user1);
			playerBInput.val(content.user2);
			
			scoreAInput.val(0);
			scoreBInput.val(0);
			showUnion();
			hideReady();
			dismantleProblemNum();
		}
 	}

 	function notifyRound(content){
 		gameBroadcast.eq(0).prepend(content.sender + ' : ' + content.message + '\n');	 			
        roundP.text(content.round + ' ROUND');
 	}
 	
 	function notifyProblem(content){
		for(var i=0; i < content.cards.length; i++){
			$(".card:eq("+i+")").attr("src", defaultImagePath + content.cards[i] + defaultJpg);
		}
 	}
 	
 	function notifyUni(content){
 		gameBroadcast.eq(0).prepend(content.sender + ' : ' + content.message + '\n');	
			
		addUp(content);
 	}
 	
 	function notifyOn(content){
 		gameBroadcast.eq(0).prepend(content.sender + ' : ' + content.message + '\n');
 		
 		if(!content.message == "합!"){
 			addUp(content);
 		}
		
		if(parseInt(content.score) > 0){
			console.log(content.answer); 			
        	answerList.append('<li>' + content.answer + '</li>');
		}
 	}
 	
 	function notifyTurn(content){
 		gameBroadcast.eq(0).prepend(content.sender + ' : ' + content.message + '\n');
 		
 		console.log(content.user1);
 		console.log(content.countDown);
 		
 		
		countDown(content); 			
 	}
 	
 	function notifyEnd(content){
 		gameBroadcast.eq(0).prepend(content.sender + ' : ' + content.message + '\n');
 		
 		switch(content.message.substring(0, 4)){
		case "데스매치":
 			announceWinner(content);
 			break;
 		default:
 			resetAnswerList();
 			break;
 		}
 	}
 	
 	function addUp(content){
 		var existingScore;
		var score = parseInt(content.score);
 		
 		if(content.user1 == playerAInput.val()){
			existingScore = parseInt(scoreAInput.val()); 
			scoreAInput.val(existingScore + score);
		}else{
			existingScore = parseInt(scoreBInput.val()); 
			scoreBInput.val(existingScore + score);
		}
 	}
 	
 	function announceWinner(content){
 		
 		if(content.user1 == "무승부"){
 			gameBroadcast.eq(0).prepend(content.sender + ' : ' + '결과는 무승부입니다.\n');
 		} else {
 			gameBroadcast.eq(0).prepend(content.sender + ' : ' 
 					+ '승자는 ' + content.user1 + '입니다. 축하합니다.\n');
 		}
 		showReady();
 		hideUnion();
 		setProblemNum();
 	}
 	
 	function countDown(content){
 		
 		if(timerInterval){
// 			console.log("timeInterval : " + timerInterval);
// 			console.log("timer : " + timer);
 			onTimesUp(); 			
 		}
 		
 		switch (content.user1) {
		case playerAInput.val():
			timer = "timerA";
			setTimer(content.countDown);
			break;
		case playerBInput.val():
			timer = "timerB";
			setTimer(content.countDown);
			break;
		}
 	 	startTimer(content);
 	}
 	
 	function resetAnswerList(){
 		answerList.empty();
 	}
 	
 	function submitUni(content){
 		gameBroadcast.eq(0).prepend(content.sender + ' : ' + content.message + '\n');
 	}
 	
 	function submitOn(content){
 		gameBroadcast.eq(0).prepend(content.sender + ' : ' + content.message + '\n');
 	}
 	
// timer

 	function setTimer(time){
 		document.getElementById(timer).innerHTML = `
 			<div class="base-timer">
 			<svg class="base-timer__svg" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
 			<g class="base-timer__circle">
 			<circle class="base-timer__path-elapsed" cx="50" cy="50" r="45"></circle>
 			<path
 			id="base-timer-path-remaining"
 			stroke-dasharray="283"
 			class="base-timer__path-remaining"
 			d="
 			M 50, 50
 			m -45, 0
 			a 45,45 0 1,0 90,0
 			a 45,45 0 1,0 -90,0
 			"
 			></path>
 			</g>
 			</svg>
 			
 			
 			<span id="base-timer-label" class="base-timer__label">${time}</span>
 			</div>
 			`;
 	}

 	function onTimesUp() {
 	  clearInterval(timerInterval);
 	  timerInterval = null;
 	  document.getElementById(timer).innerHTML = "";
 	}

 	function startTimer(content) {
 	 	let timePassed = 0;
 	 	let timeLimit = content.countDown;
 	 	let timeLeft = timeLimit;
 	 	
 	 	timerInterval = setInterval(() => {
 	    timePassed = timePassed += 1;
 	    timeLeft = timeLimit - timePassed;
 	    document.getElementById("base-timer-label").innerHTML = timeLeft;
 	    setCircleDasharray(timeLimit, timeLeft);

 	    if(timeLeft <= 0){
 	    	timeUp(timeLeft, content); 	    	
 	    }
// 	    console.log("interval : " + timerInterval);
// 	    console.log("interval type : " + typeof timerInterval);
 	    
 	  }, 1000);
 	}

 	function calculateTimeFraction(timeLimit, timeLeft) {
 	  const rawTimeFraction = timeLeft / timeLimit;
 	  return rawTimeFraction - (1 / timeLimit) * (1 - rawTimeFraction);
 	}

 	function setCircleDasharray(timeLimit, timeLeft) {
 	  const circleDasharray = `${(
 	    calculateTimeFraction(timeLimit, timeLeft) * FULL_DASH_ARRAY
 	  ).toFixed(0)} 283`;
 	  document
 	    .getElementById("base-timer-path-remaining")
 	    .setAttribute("stroke-dasharray", circleDasharray);
 	}
 	
 	function timeUp(timeLeft, content){
 		
//		gameBroadcast.bind('input propertychange', function(){
//			timeLeft = 0;	
//		});
 		
		onTimesUp();
		
		if(content.user1 == member){
			console.log("content.message, timeup : " + content.message);
			sock.send(JSON.stringify(
					{type: 'TIMEUP', roomId: roomId, sender: member, message: "TIMEUP"}));
		}
 	      
 	    
 	}
 	
// button/checkbox actions
 	
    uniBtn.click(function(){
    	sock.send(JSON.stringify(
    			{type: 'UNI', roomId: roomId, sender: member, message: "결!"}));
    });
    
    onBtn.click(function(){
    	sock.send(JSON.stringify(
    			{type: 'ON', roomId: roomId, sender: member, message: "합!"}));
    });
    
    readyBtn.click(function(){
    	sock.send(JSON.stringify(
    			{type: 'READY', roomId: roomId, sender: member, message: "READY"}));
    });
    
    
    
    selectBox.change(function(){	
    	$(this).next().children().toggleClass("boundary");
    	var checkedBox = $('input[type="checkbox"]:checked');
    	
    	if(checkedBox.length == 3){
    		var message = "";
    		
    		checkedBox.each(function(){
    			var selected = $(this).attr("name");
    			message += selected;
    		})
    		    		
    		sock.send(JSON.stringify(
        			{type: 'ON', roomId: roomId, sender: member, message: message}));
    		
    		checkedBox.each(function(){
    			$(this).prop("checked", false);
    			$(this).next().children().toggleClass("boundary");
    		})
    	}
    });

    
    
// setting functions

    
    function showReady(){
    	readyBtn.show();
    }
    
    function hideReady(){
    	readyBtn.hide();
    }
    
    function showUnion(){
    	uniBtn.show();
    	onBtn.show();
    }
    
    function hideUnion(){
    	uniBtn.hide();
    	onBtn.hide();
    }
    
    function setProblemNum(){
    	for(var i=0; i < 9; i++){
    		$(".card:eq("+i+")").attr("src", defaultImagePath + (i+1) + defaultPng);
		}
    }
    
    function dismantleProblemNum(){
    	for(var i=0; i < 9; i++){
    		$(".card:eq("+i+")").attr("src", "");
		}
    }
    
    
    
});