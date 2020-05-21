<%@ include file="modal-join.jsp"%>
<%@ include file="modal-find-email.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>

<title>LoginHome</title>
<%@ include file="../includes/header.jsp"%>
<%@ include file="../includes/meta.jsp"%>
<link href="/css/user/loginHome.css" rel="stylesheet">

<script type="text/javascript">
	var message = "${msg}";
	if (message != "") {
		alert(message);
	}
</script>
</head>

<body>
	<div id="loginHome">
		<div id="loginFrame">

			<div id="title">
				<h1>DeathMatch</h1>
			</div>

			<form action="/auth/user/local-login" method="post" id="loginProc">
				<div class="row loginForm-row" id="loginDiv">
					<div class ="loginDiv">
						<input type="email" class="form-control" name="userEmail"
							id="email" placeholder="이메일">
					</div>
					<div class ="loginDiv">
						<input type="password" class="form-control" name="pw"
							id="password" placeholder="비밀번호" maxlength="15">
					</div>
					<div class ="loginDiv">
						<c:if test="${empty login}">
							<button type="submit" id="loginBtn"
								class="btn btn-default btn-block login-btn">로그인</button>
						</c:if>
						<div id="checkbox">
							<input type="checkbox" id="checkEmail"><span>이메일
								저장</span>
						</div>
					</div>
				</div>
			</form>

			<div id="loginBtns">
				<button type="button" class="btn btn-lg" id="regiBtn">회원가입</button>
				<button type="button" class="btn btn-lg" id="findPw">비밀 번호
					찾기</button>
			</div>

			<c:if test="${empty login}">
				<div id="snsBtns">
					<div>
						<a
							href="https://kauth.kakao.com/oauth/authorize?client_id=24ff4b9dce4ffc7531bff6ac4abb6bc2&redirect_uri=http://localhost:8003/auth/user/kakaoLogin&response_type=code">
							<img
							src="${pageContext.request.contextPath}/images/btn_kakao_login.gif"
							width="250px">
						</a>
					</div>
				</div>
			</c:if>

			<form action="/auth/user/logout" method="get">
				<div>
					<c:if test="${!empty login}">
						<button type="submit" id="logout"
							class="btn btn-lg btn-block login-btn">로그아웃</button>
					</c:if>
				</div>
			</form>

			<%@ include file="../includes/footer.jsp"%>
		</div>
	</div>
	<script src="/js/user/loginHome.js?ver=1"></script>
</body>
<script type="text/javascript">
	var loginForm = $("#loginProc");
	$("#loginBtn").click(function(e) {
		if ($("#email").val() == "" || $("#email").val() == null) {
			alert("ID를 입력해주세요");
			return false;
		}
		if ($("#password").val() == "" || $("#password").val() == null) {
			alert("PW를 입력해주세요");
			return false;
		}
		loginForm.submit();
	});
	
	
	//셀렉트 박스에서 이메일 선택시 email2에 자동 기입
	function changeEmail() {
		var select_email = $("#select_email").val();
		$("#userEmail2").val(select_email);
	}

	//pw 찾기 셀렉트 박스에서 이메일 선택시 email2에 자동 기입
	function changeEmail2() {
		var select_email2 = $("#select_email2").val();
		$("#findPwEmail2").val(select_email2);
	}
	
	
</script>