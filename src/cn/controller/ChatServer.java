package cn.controller;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * �������
 */
@ServerEndpoint("/myChatServer")
public class ChatServer {
	// ����ͳ���û�����
	private static AtomicInteger userNumber = new AtomicInteger(1);
	// �û�����
	private String name = "";
	// ʹ�ü����������û�
	private static CopyOnWriteArraySet<ChatServer> users = new CopyOnWriteArraySet<ChatServer>();
	// ����һ���Ự
	private Session session;

	/**
	 * @param session
	 *            �����ӳɹ��ǵ��ô˷���
	 */
	@OnOpen
	public void onOpen(Session session) {
		name = "����" + userNumber.getAndIncrement();
		this.session = session;
		users.add(this);
		String info = "<span class='red'>:������������</span>";
		sendMessage(info, session);

	}

	/**
	 * @param message
	 *            �����������յ��ͷ��˷�������ʱ���ô˷���
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println(message);
		sendMessage("˵:" + message, session);
	}

	/**
	 * �ر�ʱִ��
	 */
	@OnClose
	public void onClose() {
		users.remove(this);
		String info = "���뿪������";
		userNumber.decrementAndGet();
		sendMessage(info, session);
	}

	/**
	 * @param mes
	 *            ��ͷ��˷�����Ϣ
	 */
	private void sendMessage(String mes, Session session) {
		for (ChatServer websocket : users) {
			try {
				// ��ֹ�̰߳�ȫ
				synchronized (ChatServer.class) {
					String info = this.name;
					if (session == websocket.session) {
						info = "��";
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
