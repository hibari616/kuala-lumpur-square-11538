package Client27_user1Ex;

import java.awt.*;
import java.awt.event.*;
import java.math.*;

import java.io.*;
import java.net.*;
import java.util.*;

import java.applet.*;

public class user1 extends Frame implements Runnable, ActionListener {
	int dx1, dy1;
	int dx2, dy2;
	static int x1 = 150, y1 = 225, x2 = 150, y2 = 35;
	int dbx1 = 0, dby1 = -5;
	int dbx2 = 0, dby2 = 5;
	int win1, win2;
	static int[] bx1 = new int[5], by1 = new int[5], bflag1 = new int[5];
	static int[] bx2 = new int[5], by2 = new int[5], bflag2 = new int[5];
	Image img1, img2, img3, img4, bufferPage = null;
	Socket socket;
	static String iaddr;
	static int port;
	DataOutputStream outstream;
	DataInputStream instream;

	private FileWriter saveScore;
	private FileReader readScore;
	int send_bx1, send_by1, Ibflag1 = 0;

	int hitflag1 = 0, hitflag2 = 0;
	AudioClip sound1, sound2, soundexplode;
	String message = "";

	Button b;
	boolean nopaint;
	String str_send;
	static String rcv;

	public static void main(String args[]) {
		if (args.length < 2) {
			System.out.println("USAGE: java user1 [iaddr] [port]");
			System.exit(1);
		}

		iaddr = args[0];
		port = Integer.parseInt(args[1]);
		user1 workStart = new user1();
	}

	public user1() {
		super("user1");
		setSize(500, 500);

		Toolkit tk = Toolkit.getDefaultToolkit();
		img1 = tk.getImage("car090.gif");
		img2 = tk.getImage("car180.gif");
		img3 = tk.getImage("hit1.gif");
		img4 = tk.getImage("hit2.gif");

		sound1 = Applet.newAudioClip(getClass().getResource("sound1.au"));
		sound2 = Applet.newAudioClip(getClass().getResource("sound2.au"));
		soundexplode = Applet.newAudioClip(getClass().getResource(
				"soundexplode.au"));

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		enableEvents(AWTEvent.KEY_EVENT_MASK);

		b = new Button("重新開始");
		b.addActionListener((ActionListener) this);
		b.setVisible(true);
		add(b, BorderLayout.NORTH);
		b.setFocusable(false);
		setVisible(true);
		
		char[] ch = new char[100];
		try {
			readScore = new FileReader("SaveDate.txt");
			int i = readScore.read(ch);
			String[] readData = null;
			if(i != -1)
				readData = new String(ch, 0, i).split(",");
			if(readData != null){
				win1 = Integer.parseInt(readData[0]);
				win2 = Integer.parseInt(readData[1]);
			}
			readScore.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			socket = new Socket(InetAddress.getByName(iaddr), port);
			outstream = new DataOutputStream(socket.getOutputStream());
			instream = new DataInputStream(socket.getInputStream());
			new Thread(this).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread thread = new Thread(new ClientRunnable(socket, instream, this));
		thread.start();
	}

	public void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			try{
				saveScore = new FileWriter("SaveDate.txt");
				saveScore.write(win1 + "," + win2);
				saveScore.flush();
				saveScore.close();
			} catch(Exception e1){
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b) {
			requestFocusInWindow();
			try {
				outstream.writeUTF("re");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void processKeyEvent(KeyEvent e) {
		if(y2>=170){
			y2+=10;
		}
		if ((e.getID() == KeyEvent.KEY_PRESSED)&&((y2+5)>170)) {
			System.out.println(y2);
			switch (e.getKeyCode()) {
			case KeyEvent.VK_D:
				dx1 = 5;
				dy1 = 0;
				break;
			case KeyEvent.VK_A:
				dx1 = -5;
				dy1 = 0;
				break;
			case KeyEvent.VK_W:
				dx1 = 0;
				dy1 = -5;
				break;
			case KeyEvent.VK_X:
				dx1 = 0;
				dy1 = 5;
				break;
			case KeyEvent.VK_Q:
				dx1 = -3;
				dy1 = -3;
				break;
			case KeyEvent.VK_E:
				dx1 = 3;
				dy1 = -3;
				break;
			case KeyEvent.VK_Z:
				dx1 = -3;
				dy1 = 3;
				break;
			case KeyEvent.VK_C:
				dx1 = 3;
				dy1 = 3;
				break;
			case KeyEvent.VK_SPACE:
				dx1 = 0;
				dy1 = 0;
				send_bx1 = x1 + 30;
				send_by1 = y1 - 5;
				Ibflag1 = 1;
				sound1.play();
				break;
			default:
				dx1 = 0;
				dy1 = 0;
			}
			try {
				x1 = x1 + dx1;
				y1 = y1 + dy1;

				str_send = "car1," + x1 + "," + y1 + "," + send_bx1 + ","
						+ send_by1 + "," + Ibflag1;
				outstream.writeUTF(str_send);

				send_bx1 = -36;
				send_by1 = -36;
			} catch (Exception f) {
				f.printStackTrace();
			}
		}
	}

	public void run() {
		while (true) {
			while (!nopaint) {
				try {
					for (int i = 0; i < 5; i++) {
						if (by1[i] - 15 <= -35) {
							bflag1[i] = 0;
							bx1[i] = -36;
							by1[i] = -36;
						}
						if (by2[i] + 15 >= 380) {
							bflag2[i] = 0;
							bx2[i] = 521;
							by2[i] = 521;
						}
						by1[i] = by1[i] + dby1;
						by2[i] = by2[i] + dby2;
						if ((bx1[i] >= x2) && (bx1[i] <= x2 + 60)
								&& (by1[i] - 15 <= y2) && (message == "")) {
							hitflag2 = 1;
							soundexplode.play();
							message = "User1 Win!!";
							win1++;
							bufferPage = null;

						}
						if ((bx2[i] >= x1) && (bx2[i] <= x1 + 60)
								&& (by2[i] + 15 >= y1) && (message == "")) {
							hitflag1 = 1;
							soundexplode.play();
							message = "User2 Win!!";
							win2++;
							bufferPage = null;

						}
						repaint();
						Thread.sleep(5);
					}

				} catch (Exception f) {
					f.printStackTrace();
				}
			}
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		Graphics bufferg;
		if (bufferPage == null)
			bufferPage = createImage(500, 500);
		bufferg = bufferPage.getGraphics();

		if (hitflag1 == 1)
			img1 = img3;
		if (hitflag2 == 1)
			img2 = img4;

		bufferg.drawString(message, 150, 160);
		bufferg.drawString(win1 + ":" + win2, 20, 50);

		bufferg.setColor(Color.red);//畫線
		bufferg.drawLine(0, 250, 500, 250);
		
		bufferg.drawImage(img1, x1, y1, this);
		bufferg.drawImage(img2, x2, y2, this);

		for (int i = 0; i < 5; i++) {
			if (bflag1[i] == 1) {
				bufferg.setColor(Color.black);
				bufferg.fillRect(bx1[i], by1[i] + 5, 3, 5);
				bufferg.fillRect(bx1[i], by1[i] + 15, 3, 5);
				bufferg.fillRect(bx1[i], by1[i] + 25, 3, 5);
				bufferg.setColor(Color.white);
				bufferg.fillRect(bx1[i], by1[i] + 10, 3, 5);
				bufferg.fillRect(bx1[i], by1[i] + 20, 3, 5);
				bufferg.fillRect(bx1[i], by1[i] + 30, 3, 5);
			}

			if (bflag2[i] == 1) {
				bufferg.setColor(Color.blue);
				bufferg.fillRect(bx2[i], by2[i] - 5, 3, 5);
				bufferg.fillRect(bx2[i], by2[i] - 15, 3, 5);
				bufferg.fillRect(bx2[i], by2[i] - 25, 3, 5);
				bufferg.setColor(Color.white);
				bufferg.fillRect(bx2[i], by2[i] - 10, 3, 5);
				bufferg.fillRect(bx2[i], by2[i] - 20, 3, 5);
				bufferg.fillRect(bx2[i], by2[i] - 30, 3, 5);
			}
		}
		bufferg.dispose();
		g.drawImage(bufferPage, getInsets().left, getInsets().top, this);
	}
}

class ClientRunnable implements Runnable {
	Socket socket;
	DataOutputStream outstream;
	DataInputStream instream;
	user1 frame;

	public ClientRunnable(Socket socket, DataInputStream instream, user1 frame) {
		this.socket = socket;
		this.instream = instream;
		this.frame = frame;
	}

	public synchronized void rcv() {
		while (true) {
			try {
				user1.rcv = instream.readUTF();
				if (user1.rcv.contains("car1")) {
					String[] data = user1.rcv.split(",");
					user1.x1 = Integer.parseInt(data[1]);
					user1.y1 = Integer.parseInt(data[2]);
					if (Integer.parseInt(data[5]) == 1) {
						for (int i = 0; i < user1.bx1.length; i++) {
							if (user1.bflag1[i] == 0) {
								user1.bx1[i] = Integer.parseInt(data[3]);
								user1.by1[i] = Integer.parseInt(data[4]);
								user1.bflag1[i] = Integer.parseInt(data[5]);
								break;
							}
						}
					}

				}
				if (user1.rcv.contains("car2")) {
					String[] data = user1.rcv.split(",");
					user1.x2 = Integer.parseInt(data[1]);
					user1.y2 = Integer.parseInt(data[2]);
					for (int i = 0; i < user1.bx2.length; i++) {
						if (user1.bflag2[i] == 0) {
							user1.bx2[i] = Integer.parseInt(data[3]) - 5;
							user1.by2[i] = Integer.parseInt(data[4]) + 85;
							user1.bflag2[i] = Integer.parseInt(data[5]);
							break;
						}
					}
				}
				if (user1.rcv.contains("re")) {
					System.out.println("re");
					frame.nopaint = false;
					frame.bufferPage = null;
					frame.message = "";
					user1.x1 = 150;
					user1.y1 = 325;
					user1.x2 = 150;
					user1.y2 = 35;
					for (int i = 0; i < user1.bx1.length; i++) {
						user1.bx1[i] = 36;
						user1.bx2[i] = 521;
						user1.by1[i] = 36;
						user1.by2[i] = 521;
						user1.bflag1[i] = 0;
						user1.bflag2[i] = 0;
					}
					frame.hitflag1 = 0;
					frame.hitflag2 = 0;
					Toolkit tk = Toolkit.getDefaultToolkit();
					frame.img1 = tk.getImage("car090.gif");
					frame.img2 = tk.getImage("car180.gif");
					frame.repaint();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void run() {
		rcv();
	}
}
