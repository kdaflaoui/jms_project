package kadevelop.publish;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PhotoSubscriber extends JFrame {

	private PanelPhoto jPanelPhoto = new PanelPhoto();
	
	public PhotoSubscriber() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.add(jPanelPhoto, BorderLayout.CENTER);
		this.setBounds(10, 10, 400, 300);
		this.setVisible(true);
		
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
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message message) {
					StreamMessage m = (StreamMessage) message;
					try {
						
						String nomPhoto = m.readString();
						int lenght = m.readInt();
						byte[] data = new byte[lenght];
						m.readBytes(data);
						ByteArrayInputStream bais = new ByteArrayInputStream(data);
						BufferedImage bi = ImageIO.read(bais);
						jPanelPhoto.setBufferedImage(bi);
						jPanelPhoto.repaint();
						
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
				}
				
			});
			
			connection.start();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new PhotoSubscriber();
	}

}
