



$(function() {
	var websocket = null;

	
	var url=$("#url").val();
	
	$("#btn_connect").click(
			function() {
				if ('WebSocket' in window) {
					websocket = new WebSocket(url);
				} else {
					alert('Browser No Support WebSocket')
				}
				
				websocket.onerror = function() {
					setMessageInnerHTML("WebSocket Connect Cause Error");
				};

				
				websocket.onopen = function() {
					setMessageInnerHTML("WebSocket Connect Success");
				}

				
				websocket.onmessage = function(event) {
					
					var message = JSON.parse(event.data);

				
				
					setMessageInnerHTML(message.username+":"+message.message);
				}

				
				websocket.onclose = function() {
					setMessageInnerHTML("WebSocket Connect Close");
				}

			});
	
	$("#btn_send").click(function(){
		
		 send();
		
	});
	
	
	$("#btn_clear").click(function(){
		document.getElementById('content').innerHTML="";
		
	});
	

	
	window.onbeforeunload = function() {
		closeWebSocket();
	}

	
	function setMessageInnerHTML(innerHTML) {
		document.getElementById('content').innerHTML += innerHTML + '<br/>';
	}

	
	function closeWebSocket() {
		websocket.close();
	}

	// 发送消息
	function send() {
		var message = $("#message_text").val();
		if(message==""){
			return;
		}
		
		var username=$("#name").val();
		 var txt = [];
		 var obj = { "code": 0, "username": username, "message": message };//构造Json
		 
		 var s=JSON.stringify(obj);

		 websocket.send(s);
		
		
	}
	
	
	
	
	

})