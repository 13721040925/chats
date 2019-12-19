<%--
  Created by IntelliJ IDEA.
  User:
  Date: 2018/6/14
  Time: 9:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <title>websocket</title>
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
        #box {
            width: 593px;
            height: 409px;
            border: 1px solid;
            margin: 10px auto;

        }

        #content {
            width: 100%;
            height: 261px;
            overflow: auto;
        }

        .form-control {
            border-bottom-style: hidden;
            outline: none;
            resize: none; /*取消文本框被拉大*/

        }

        .btn-default {
            width: 105px;
            position: absolute;
            right: 385px;
        }

        .red {
            color: red;
        }
    </style>
</head>
<body>
<div id="box" class="panel panel-primary">
    <div id="title" class="panel-heading">
        <h3 class="panel-title">塔塔-聊天室
                <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
        </h3>
    </div>
    <div id="content" class="panel-body"></div>
    <div id="send"><textarea class="form-control" id="message" placeholder="内容" rows="3"></textarea>
        <span class="emotion glyphicon glyphicon-plus" style="font-size: 20px" title="表情包(caps lock)"></span>
        <button class="btn btn-default" id="sendMessage">发送</button>
    </div>

</div>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery.qqFace.js"></script>
<script type="text/javascript">
    var socket = null;
    // 判断是否支持websocket
    if ('WebSocket' in window) {
        socket = new WebSocket("ws://localhost:8080/myChatServer");
    } else {
        alert('当前浏览器不支持 websocket');
    }
    // 接收服务器发送的数据时触发
    socket.onmessage = function message(mes) {
        // 调动打印方法
        printfMessage(mes.data);
        // 设置滑动条始终在下方
        $(".panel-body").scrollTop($(".panel-body")[0].scrollHeight);
    }

    // 打印消息
    function printfMessage(mes) {
        var info = mes + "<br />"
        $(".panel-body").append(info);
    }

    $(function () {

        $('.emotion').qqFace({

            id: 'facebox',

            assign: 'message',

            path: 'arclist/'	//表情存放的路径
        });

        $("#sendMessage").click(function () {
            var str = $("#message").val().trim();
            if (str == "" || str == null) {
                return;
            }
            // 发送内容
            var text = replace_em(str);
            // 发送数据
            socket.send(text);
            socket

            $("#message").val("");

        });
    });
    window.onkeydown = function (ev) {
        var key = ev.keyCode;

        if (key == 13) {
            $("#sendMessage").click();
        } else if (key == 20) {
            $('.emotion').click();
        }
    }

    //查看结果
    function replace_em(str) {

        str = str.replace(/\</g, '&lt;');

        str = str.replace(/\>/g, '&gt;');

        str = str.replace(/\n/g, '<br/>');

        str = str.replace(/\[em_([0-9]*)\]/g, '<img src="arclist/$1.gif" border="0" />');
        return str;

    }

</script>
</body>
</html>
