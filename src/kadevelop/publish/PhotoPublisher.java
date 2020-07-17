package kadevelop.publish;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PhotoPublisher extends JFrame {

	private JLabel jLabelPhoto = new JLabel("Photo");
	private JComboBox<String> jcomboBoxPhoto;
	private JButton jButtonEnvoyer = new JButton("Envoyer");
	private PanelPhoto panelPhoto = new PanelPhoto();
	
	public PhotoPublisher() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		JPanel jPanel = new JPanel();
		File file = new File("assets");
		String[] photos = file.list();
		jcomboBoxPhoto = new JComboBox<String>(photos);
		jPanel.setLayout(new FlowLayout());
		jPanel.add(jLabelPhoto);
		jPanel.add(jcomboBoxPhoto);
		jPanel.add(jButtonEnvoyer);
		this.add(jPanel, BorderLayout.NORTH);
		this.add(panelPhoto, BorderLayout.CENTER);
		this.setBounds(10, 10, 400, 300);
		this.setVisible(true);
		jcomboBoxPhoto.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					String photo = (String) jcomboBoxPhoto.getSelectedItem();
					File file = new File("assets/"+photo);
					BufferedImage bi = ImageIO.read(file);
					 panelPhoto.setBufferedImage(bi);
					panelPhoto.repaint();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
		jButtonEnvoyer.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Properties jndiProperties = new Properties();
				jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
				//jndiProperties.put(Context.PROVIDER_URL, "remote://localhost:4447");
				jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
				//jndiProperties.put("jboss.naming.client.ejb.context", true);  
				jndiProperties.put(Context.SECURITY_PRINCIPAL, "root");
				jndiProperties.put(Context.SECURITY_CREDENTIALS, "root");
				
				Context ctx;
				try {
					ctx = new InitialContext(jndiProperties);
					ConnectionFactory cf = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
					Connection connection = cf.createConnection("root", "root");
					Queue destination = (Queue) ctx.lookup("jms/queue/kdevelopQueue");
					Session session = connection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
					MessageProducer producer = session.createProducer(destination);
					connection.start();
					
					File file = new File("assets/"+(String) jcomboBoxPhoto.getSelectedItem());
					FileInputStream fis = new FileInputStream(file);
					byte[] data= new byte[(int) file.length()];
					fis.read(data);
					StreamMessage message = session.createStreamMessage();
					message.writeString((String) jcomboBoxPhoto.getSelectedItem());
					message.writeInt(data.length);
					message.writeBytes(data);
					producer.send(message);
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args) { 
		// TODO Auto-generated method stub
		new PhotoPublisher();
	}

}
