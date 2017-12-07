package com.websocketchat.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import com.websocketchat.util.Message;

import net.sf.json.JSONObject;

public class Client {

	private JFrame frame;
	private JTextArea textArea;
	private JTextField textField;
	private JTextField txt_port;
	private JTextField txt_hostIp;
	private JTextField txt_name;
	private JButton btn_start;
	private JButton btn_stop;
	private JButton btn_send;
	private JPanel northPanel;
	private JPanel southPanel;
	private JScrollPane rightScroll;
	private WebSocketClient websocketclient = null;
	private boolean isConnected = false;

	public static void main(String[] args) {
		new Client();
	}

	public void send() {
		if (!isConnected) {
			textArea.append("Server Not Connect！！！\r\n");
			return;
		}
		String message = textField.getText().trim();
		if (message == null || message.equals("")) {
			return;
		}
		sendMessage(message);
		textField.setText(null);
	}

	public Client() {
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setForeground(Color.blue);
		textField = new JTextField();
		txt_port = new JTextField("6666");
		txt_hostIp = new JTextField("127.0.0.1");
		txt_name = new JTextField("maoge");
		btn_start = new JButton("Conn");
		btn_stop = new JButton("Discon");
		btn_send = new JButton("Send");
		btn_stop.setEnabled(false);

		northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 7));
		northPanel.add(new JLabel("Port"));
		northPanel.add(txt_port);
		northPanel.add(new JLabel("Server IP"));
		northPanel.add(txt_hostIp);
		northPanel.add(new JLabel("name"));
		northPanel.add(txt_name);
		northPanel.add(btn_start);
		northPanel.add(btn_stop);
		northPanel.setBorder(new TitledBorder("Connect Infomation"));

		rightScroll = new JScrollPane(textArea);
		rightScroll.setBorder(new TitledBorder("Message Display"));
		southPanel = new JPanel(new BorderLayout());
		southPanel.add(textField, "Center");
		southPanel.add(btn_send, "East");
		southPanel.setBorder(new TitledBorder("Write Message"));

		frame = new JFrame("Client");
		frame.setIconImage(Toolkit.getDefaultToolkit().createImage(Client.class.getResource("qq.png")));
		frame.setLayout(new BorderLayout());
		frame.add(northPanel, "North");
		frame.add(rightScroll, "Center");
		frame.add(southPanel, "South");
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				send();
			}
		});

		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int port;

				try {
					try {
						port = Integer.parseInt(txt_port.getText().trim());
					} catch (NumberFormatException e2) {
						throw new Exception("Port Illegal");
					}
					String hostIp = txt_hostIp.getText().trim();
					String name = txt_name.getText().trim();
					if (name.equals("") || hostIp.equals("")) {
						throw new Exception("name,hostip Cannot be Empty");
					}
					boolean flag = connectServer(port, hostIp, name);
					if (flag == false) {
						throw new Exception("Connect To Server Fail!!!\r\n");
					}
					frame.setTitle(name);
					btn_start.setEnabled(false);
					btn_stop.setEnabled(true);
					txt_hostIp.setEnabled(false);
					txt_port.setEnabled(false);
					txt_name.setEnabled(false);
				} catch (Exception exc) {
					textArea.append("Connect To Server Fail!!!\r\n");
				}
			}
		});

		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					boolean flag = closeConnection();// 断开连接
					if (flag == false) {
						throw new Exception("Disconnect Exception");
					}

					textArea.append("Disconnect Success！！！\r\n");
					btn_start.setEnabled(true);
					btn_stop.setEnabled(false);
					txt_hostIp.setEnabled(true);
					txt_port.setEnabled(true);
					txt_name.setEnabled(true);
					isConnected = false;
				} catch (Exception exc) {
					textArea.append("Disconnect Fail!!!\r\n");
				}
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (isConnected) {
					closeConnection();
				}
				System.exit(0);
			}
		});
	}

	public boolean connectServer(int port, String hostIp, String name) {

		String url = "ws://" + hostIp + ":" + port;
		try {
			websocketclient = new WebSocketClient(new URI(url), new Draft_6455()) {
				@Override
				public void onOpen(ServerHandshake arg0) {
				}

				@Override
				public void onMessage(String message) {

					JSONObject j = JSONObject.fromObject(message);
					Message m = (Message) JSONObject.toBean(j, Message.class);
					textArea.append(m.getUsername() + ":" + m.getMessage() + "\r\n");
				}

				@Override
				public void onError(Exception arg0) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onClose(int arg0, String arg1, boolean arg2) {
					// TODO Auto-generated method stub
					textArea.append("Server Close\r\n");
					isConnected = false;
					
					btn_start.setEnabled(true);
					btn_stop.setEnabled(false);
					txt_hostIp.setEnabled(true);
					txt_port.setEnabled(true);
					txt_name.setEnabled(true);
					
				}
			};
			websocketclient.connect();
			textArea.append("Server Connect Succcess\r\n");
			isConnected = true;
			return true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			isConnected = false;
			return false;

		}

	}

	public void sendMessage(String message) {
		String username = txt_name.getText();
		Message m = new Message();
		m.setCode(0);
		m.setMessage(message);
		m.setUsername(username);

		JSONObject jsonObj = JSONObject.fromObject(m);
		websocketclient.send(jsonObj.toString());

	}

	public synchronized boolean closeConnection() {
		websocketclient.close();
		isConnected = false;
		return true;
	}

}