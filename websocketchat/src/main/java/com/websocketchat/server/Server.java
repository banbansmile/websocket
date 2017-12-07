package com.websocketchat.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.BindException;
import java.net.InetSocketAddress;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import com.websocketchat.util.Message;
import net.sf.json.JSONObject;

public class Server {

	private JFrame frame;
	private JTextArea contentArea;
	private JTextField txt_message;
	private JTextField txt_max;
	private JTextField txt_port;
	private JButton btn_start;
	private JButton btn_stop;
	private JButton btn_send;
	private JPanel northPanel;
	private JPanel southPanel;
	private JScrollPane rightPanel;
	private JScrollPane leftPanel;
	private JSplitPane centerSplit;
	private WebSocketServer websocketserver = null;
	private boolean isStart = false;

	public static void main(String[] args) {
		new Server();
	}

	public void send() {
		if (!isStart) {
			contentArea.append("Server Not Start\r\n");
			return;
		}

		String message = txt_message.getText().trim();
		if (message == null || message.equals("")) {
			return;
		}
		sendMessage(message);
		contentArea.append("server:" + txt_message.getText() + "\r\n");
		txt_message.setText(null);
	}

	public Server() {

		frame = new JFrame("Server");
		frame.setIconImage(Toolkit.getDefaultToolkit().createImage(Server.class.getResource("qq.png")));
		contentArea = new JTextArea();
		contentArea.setEditable(false);
		contentArea.setForeground(Color.blue);
		txt_message = new JTextField();
		txt_max = new JTextField("30");
		txt_port = new JTextField("6666");
		btn_start = new JButton("Start");
		btn_stop = new JButton("Stop");
		btn_send = new JButton("Send");
		btn_stop.setEnabled(false);

		southPanel = new JPanel(new BorderLayout());
		southPanel.setBorder(new TitledBorder("Write Message"));
		southPanel.add(txt_message, "Center");
		southPanel.add(btn_send, "East");
		rightPanel = new JScrollPane(contentArea);
		rightPanel.setBorder(new TitledBorder("Message Display"));
		centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		centerSplit.setDividerLocation(100);
		northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 6));
		northPanel.add(new JLabel("Chat Count"));
		northPanel.add(txt_max);
		northPanel.add(new JLabel("Port"));
		northPanel.add(txt_port);
		northPanel.add(btn_start);
		northPanel.add(btn_stop);
		northPanel.setBorder(new TitledBorder("Setting INFO"));

		frame.setLayout(new BorderLayout());
		frame.add(northPanel, "North");
		frame.add(centerSplit, "Center");
		frame.add(southPanel, "South");
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (isStart) {
					closeServer();
				}
				System.exit(0);
			}
		});

		txt_message.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				send();
			}
		});

		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int max;
				int port;
				try {
					try {
						max = Integer.parseInt(txt_max.getText());
					} catch (Exception e1) {
						throw new Exception("Count Cannot Less 0");
					}
					if (max <= 0) {
						throw new Exception("Count Cannot Less 0");
					}
					try {
						port = Integer.parseInt(txt_port.getText());
					} catch (Exception e1) {
						throw new Exception("Port Need Be Positive");
					}
					if (port <= 0) {
						throw new Exception("Port Need Be Positive");
					}
					serverStart(max, port);
					contentArea.append("Server Start Success!Max Count：" + max + ",Port：" + port + "\r\n");
					txt_max.setEnabled(false);
					txt_port.setEnabled(false);
					btn_stop.setEnabled(true);
					btn_start.setEnabled(false);
				} catch (Exception exc) {
					contentArea.append("Server Start Fail！！！");
				}

			}
		});

		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					closeServer();
					btn_start.setEnabled(true);
					txt_max.setEnabled(true);
					txt_port.setEnabled(true);
					btn_stop.setEnabled(false);
					contentArea.append("Server Stop Success!\r\n");
				} catch (Exception exc) {
					contentArea.append("Server Stop Fail!\r\n");
				}
			}
		});
	}

	public void serverStart(int max, int port) throws java.net.BindException {
		try {

			websocketserver = new WebSocketServer(new InetSocketAddress(port)) {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onOpen(WebSocket arg0, ClientHandshake arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMessage(WebSocket conn, String message) {

					JSONObject j = JSONObject.fromObject(message);
					Message m = (Message) JSONObject.toBean(j, Message.class);
					if (m.getCode() == 0) {
						contentArea.append(m.getUsername() + ":" + m.getMessage() + "\r\n");
						transmitMessage(message);
					}

				}

				@Override
				public void onError(WebSocket arg0, Exception arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
					// TODO Auto-generated method stub

				}
			};
			websocketserver.start();
			isStart = true;
		} catch (Exception e1) {
			e1.printStackTrace();
			isStart = false;
			throw new BindException("Server Start Exception！");
		}
	}

	public void closeServer() {
		try {
			websocketserver.stop(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		System.out.println(message);
		Message m = new Message();
		m.setCode(0);
		m.setMessage(message);
		m.setUsername("server");
		websocketserver.broadcast(message);
	}
	public void transmitMessage(String message) {
		websocketserver.broadcast(message);
	}

}
