import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.TimeUnit;
import static java.lang.Math.toIntExact;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;

class YouTubeDownload extends JFrame {
	public static String path = "";
	public static String title = null;
	public static String urlvideosave = "";
	public static String[] links;
	public static int qualLink = 0;
	public static int qualLinkChoise = 0;
	public static int sizeFile = 0;

	public Vector<String> videoQuality = new Vector<String>();
	public static final ArrayList<String> BAD_KEYS = new ArrayList<String>();
    static {
		BAD_KEYS.add("stereo3d");
		BAD_KEYS.add("type");
		BAD_KEYS.add("fallback_host");
		BAD_KEYS.add("quality");
    }

	private JButton buttonGetLinks = new JButton("Get links");
	private JButton buttonChoisePath = new JButton("browse");
	private JButton buttonDownload = new JButton("Download");
	private JTextField input = new JTextField("", 5);
	private JTextField input2 = new JTextField("", 5);
	private JLabel label = new JLabel("Enter URL:");
	private JLabel label2 = new JLabel("Enter the path to save the video:");
	public JLabel labelVideoquality = new JLabel("Select video quality:");
	public JLabel labelRVideotitle = new JLabel("Video title:");
	public JLabel labelVideotitle = new JLabel("");
	public static JLabel labelStatus = new JLabel("");
	public static JLabel label5 = new JLabel("");

	private JComboBox<String> comboLink = new JComboBox<String>(videoQuality);


	public YouTubeDownload() {
	    super("Download video from YouTube");
	    this.setBounds(500,500,600,300);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    Container container = this.getContentPane();
	    container.add(label);
	    container.add(label2);
	    container.add(labelStatus);
	    container.add(labelRVideotitle);
	    container.add(labelVideotitle);
	    container.add(labelVideoquality);

	    container.add(input);
	    container.add(input2);

	    container.add(comboLink);

	    container.add(buttonGetLinks);
		container.add(buttonChoisePath);
		container.add(buttonDownload);
		container.add(labelStatus);
		container.add(label5);

		label.setBounds(10,10,200,30);
		label2.setBounds(10,55,280,30);
		labelRVideotitle.setBounds(10,100,80,30);
		labelVideotitle.setBounds(95,100,500,30);
		labelVideotitle.setForeground(Color.black);
		labelVideoquality.setBounds(10,140,150,30);

		input.setBounds(120,12,360,30);
		input2.setBounds(260,57,220,30);

		comboLink.setBounds(160,140,200,30);

		buttonGetLinks.setBounds(30,185,200,40);
		buttonGetLinks.addActionListener(new ButtonLinksRequest());
		buttonChoisePath.setBounds(485,58,90,27);
		buttonChoisePath.addActionListener(new ButtonSelectPath());
		buttonDownload.setBounds(350,185,200,40);
		buttonDownload.addActionListener(new ButtonDownload());
		buttonDownload.setEnabled(false);

		labelStatus.setBounds(10,230,580,30);

	}

	// Links request
	class ButtonLinksRequest implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (input.getText().isEmpty()) {
				String mess = "";
				mess += "Enter URL to upload";
				JOptionPane.showMessageDialog(null,
		    		mess,
		    		"ERROR",
		    	    JOptionPane.PLAIN_MESSAGE);
				return;
			}
			labelStatus.setText("Links received");
			labelStatus.repaint();

			String message = "";
			message += "Received links from url: " + input.getText() + "\n";

			String userlink = new String(input.getText());
			String[] id = userlink.split("=");	
			links = getURLS(id[1]);
			if (links == null) {
				String mess = "";
				mess += "Failed to get links for downloading";
				JOptionPane.showMessageDialog(null,
		    		mess,
		    		"ERROR",
		    	    JOptionPane.PLAIN_MESSAGE);
				return;
			}
			labelVideotitle.setText(title);
			videoQuality.clear();
			comboLink.getSelectedIndex();
		
			if (qualLink > 0) {
				videoQuality.addElement("This is 140p");
				comboLink.setSelectedItem("This is 140p");
			}
			if (qualLink > 1) {
				videoQuality.addElement("This is 240p");
				comboLink.setSelectedItem("This is 240p");
			}
			if (qualLink > 2) {
				videoQuality.addElement("This is 360p");
				comboLink.setSelectedItem("This is 360p");
			}
			if (qualLink > 4) {
				videoQuality.addElement("This is 720p");
				comboLink.setSelectedItem("This is 720p");
			}
			buttonDownload.setEnabled(true);
			JOptionPane.showMessageDialog(null,
		    		message,
		    		"Output",
		    	    JOptionPane.PLAIN_MESSAGE);
		}
	}
	// Selecting a path
	class ButtonSelectPath implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
		    chooser.setCurrentDirectory(new java.io.File("."));
		    chooser.setDialogTitle("choosertitle");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    chooser.setAcceptAllFileFilterUsed(false);

		    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		    	//System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
		    	//System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
				File pat = chooser.getSelectedFile();
				String x = pat.toString();
				input2.setText(x+"/");
		    } else {
		    	System.out.println("No Selection Directory");
		    }
		}
	}
	// Download
	class ButtonDownload implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (input2.getText().isEmpty()) {
				String mess = "";
				mess += "Enter the path to save";
				JOptionPane.showMessageDialog(null,
		    		mess,
		    		"ERROR",
		    	    JOptionPane.PLAIN_MESSAGE);
				return;
			}

			path = new String(input2.getText());
			char[] strArray = path.toCharArray();
			if (strArray[strArray.length-1] != '/') {
				path = path + '/';
			}
			String userlink = new String(input.getText());
			String[] id = userlink.split("=");
			labelStatus.setVisible(true);
			if (comboLink.getSelectedItem() == "This is 720p") {
				qualLinkChoise = 0;
			} else if (comboLink.getSelectedItem() == "This is 360p") {
				qualLinkChoise = 1;
			} else if (comboLink.getSelectedItem() == "This is 240p") {
				qualLinkChoise = 2;
			} else if (comboLink.getSelectedItem() == "This is 140p") {
				qualLinkChoise = 3;
			}
			urlvideosave = links[qualLinkChoise];
			path += decode(title);
			Thread first = new Download();
            first.start();
			buttonDownload.setEnabled(false);
		}
	}
	// get links video
	private static String[] getURLS(String id) {
		try {
			InputStream is = getVideoInfo(id);
			if(is == null) {
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			LinkedList<String> urls = new LinkedList<String>();
			title = null;
			for (String l; (l = reader.readLine()) != null; ) {
				for (String p : l.split("&")) {
					String key = p.substring(0, p.indexOf('='));
					String value = p.substring(p.indexOf('=') + 1);
					if (key.equals("url_encoded_fmt_stream_map")) {
						value = decode(value);
						for (String u : value.split("url=")) {
							u = getCorrectURL(decode(u));
							if (!u.startsWith("http") && !u.contains("signature=") && !u.contains("factor=")) {
								continue;
							}
							urls.add(u);
						}
					} else if (key.equals("title")) {
						title = value.replace("+", "%20");
					}
				}
			}
			if (title == null) {
				throw new RuntimeException("Failed to find title can't complete url");
			}
			String[] url_map = urls.toArray(new String[urls.size()]);
			for (int i = 0; i < url_map.length; i++) {
				url_map[i] += url_map[i].endsWith("&") ? "title=" + title : "&title=" + title;
			}
			qualLink = url_map.length;
			path += decode(title);
			title = decode(title);
			return url_map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static InputStream getVideoInfo(String id) {
		try {
			HttpURLConnection connection = connect("http://www.youtube.com/get_video_info?video_id=" + id + "&asv=3&el=detailpage&hl=en_US");
			connection.setRequestMethod("GET");
			return connection.getInputStream();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
    private static HttpURLConnection connect(String url) throws Exception {
		URL u = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) u.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.3");
		return connection;
	}
	public static String decode(String content) {
		try {
			return URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return content;
	}
    private static String getCorrectURL(String input) {
		StringBuilder builder = new StringBuilder(input.substring(0, input.indexOf('?') + 1));
		String[] params = input.substring(input.indexOf('?') + 1).split("&");
		LinkedList<String> keys = new LinkedList<String>();
		boolean first = true;
		for (String param : params) {
			String key = param;
			try {
				key = param.substring(0, param.indexOf('='));
			} catch (Exception ex) {
			}
			if (keys.contains(key) || BAD_KEYS.contains(key)) {
				continue;
			}
			keys.add(key);
			if (key.equals("sig")) {
				builder.append(first ? "" : "&").append("signature=").append(param.substring(4));
			} else {
				if (param.contains(",quality=")) {
					param = remove(param, ",quality=", "_end_");
				}
				if (param.contains(",type=")) {
					param = remove(param, ",type=", "_end_");
				}
				if (param.contains(",fallback_host")) {
					param = remove(param, ",fallback_host", ".com");
				}
				builder.append(first ? "" : "&").append(param);
			}
			if (first)
				first = false;
		}
		return builder.toString();
	}
    private static String remove(String text, String start, String end) {
		int l = text.indexOf(start);
		return text.replace(text.substring(l, end.equals("_end_") ? text.length() : text.indexOf(end, l)), "");
	}

	class Download extends Thread
	{
		@Override
	    public void run() {
	        try {
	    		Downloadfile(urlvideosave);
	    		urlvideosave = "";
			} catch (Exception e) {
				e.printStackTrace();
				String mess = "";
				mess += "Download error\ntry getting links and downloading videos again";
				JOptionPane.showMessageDialog(null,
		    		mess,
		    		"ERROR",
		    	    JOptionPane.PLAIN_MESSAGE);
				return;
			}
	    }
		public void Downloadfile(String urlfile) throws Exception {
			labelStatus.setText("Downloading: " + path+".mp4");
			labelStatus.repaint();
			URL website = new URL(urlfile);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(path+".mp4");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			String mess = "";
			mess += "Download end";
			JOptionPane.showMessageDialog(null,
				mess,
				"Completed",
				JOptionPane.PLAIN_MESSAGE);
			labelStatus.setText("Downloaded: " + path+".mp4");
			labelStatus.repaint();
			return;
	    	}
	}

	public static void main (String[] args) {
		YouTubeDownload app = new YouTubeDownload();
		app.setVisible(true);
	}
	
}
