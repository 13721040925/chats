package cn.controller;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * 聊天服务
 */
@ServerEndpoint("/myChatServer")
public class ChatServer {
	// 用于统计用户数量
	private static AtomicInteger userNumber = new AtomicInteger(1);
	// 用户姓名
	private String name = "";
	// 使用集合来保存用户
	private static CopyOnWriteArraySet<ChatServer> users = new CopyOnWriteArraySet<ChatServer>();
	// 定义一个会话
	private Session session;

	/**
	 * @param session
	 *            当连接成功是调用此方法
	 */
	@OnOpen
	public void onOpen(Session session) {
		name = "博客" + userNumber.getAndIncrement();
		this.session = session;
		users.add(this);
		String info = "<span class='red'>:加入了聊天室</span>";
		sendMessage(info, session);

	}

	/**
	 * @param message
	 *            当服务器接收到客服端发送数据时调用此方法
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println(message);
		sendMessage("说:" + message, session);
	}

	/**
	 * 关闭时执行
	 */
	@OnClose
	public void onClose() {
		users.remove(this);
		String info = "已离开聊天室";
		userNumber.decrementAndGet();
		sendMessage(info, session);
	}

	/**
	 * @param mes
	 *            向客服端发送消息
	 */
	private void sendMessage(String mes, Session session) {
		for (ChatServer websocket : users) {
			try {
				// 防止线程安全
				synchronized (ChatServer.class) {
					String info = this.name;
					if (session == websocket.session) {
						info = "我";
					}
					String text = info + mes;
					websocket.session.getBasicRemote().sendText(text);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
