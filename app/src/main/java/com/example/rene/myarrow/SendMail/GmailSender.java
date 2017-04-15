package com.example.rene.myarrow.SendMail;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class GmailSender extends javax.mail.Authenticator{

     /** Kuerzel fuers Logging. */
     private static final String TAG = GmailSender.class.getSimpleName();

	 private String user;
	 private String password;
	 private Session session;
	 private Context c;

	 static {
	      Security.addProvider(new JSSEProvider());
	 }

	 public GmailSender(Context context) {

         Log.d(TAG, "GmailSender(): Begin");

		 c = context;

		 SharedPreferences einstellungen = PreferenceManager.getDefaultSharedPreferences(c);
		 user = einstellungen.getString("username",null);
		 password = einstellungen.getString("password",null);
		 Log.d(TAG, "Username: " + user);
		 // Log.d(TAG, "Passwort: " + password);

	     Properties props = new Properties();
	     props.setProperty("mail.transport.protocol",  einstellungen.getString("mail_transport_protocol",null));
	     props.setProperty("mail.host",                einstellungen.getString("mail_host",null));
	     props.put("mail.smtp.auth",                   einstellungen.getString("mail_smtp_auth",null));
	     props.put("mail.smtp.port",                   einstellungen.getString("mail_smtp_port",null));
	     props.put("mail.smtp.socketFactory.port",     einstellungen.getString("mail_smtp_socketFactory_port",null));
	     props.put("mail.smtp.socketFactory.class",    einstellungen.getString("mail_smtp_socketFactory_class",null));
	     props.put("mail.smtp.socketFactory.fallback", einstellungen.getString("mail_smtp_socketFactory_fallback",null));
	     props.setProperty("mail.smtp.quitwait",       einstellungen.getString("mail_smtp_quitwait",null));

	     session = Session.getDefaultInstance(props, this);

         Log.d(TAG, "GmailSender(): End");

	 }

	 protected PasswordAuthentication getPasswordAuthentication() {
	 	Log.d(TAG, "getPasswordAuthentication()");
	    return new PasswordAuthentication(user, password);
	 }

     /**
	  *
	  * @param subject
	  * 			Betreff der Email
	  * @param body
	  * 			Text der Email
	  * @param attachment
	  * 			Anhang für die Email
	  * @param sender
	  * 			Absender, kann NULL sein
	  * @param recipients
	  * 			Liste der Empfänger durch Komma getrennt, kann NULL sein
      * @throws Exception
      */
	  public synchronized void sendMail(
		String subject,
		String body,
		File attachment,
		String sender,
		String recipients) throws MessagingException {

		// Was wurde uebergeben
		Log.d(TAG, "---------------------------------------------------");
		Log.d(TAG, "sendMail(): Begin");
		Log.d(TAG, "---------------------------------------------------");
        Log.d(TAG, "sendMail(): subject -    " + subject);
        Log.d(TAG, "sendMail(): body -       " + body);
        Log.d(TAG, "sendMail(): sender -     " + sender);
        Log.d(TAG, "sendMail(): recipients - " + recipients);

	    try{
	    	MimeMessage message = new MimeMessage(session);
            Multipart mp = new MimeMultipart();
            SharedPreferences einstellungen = PreferenceManager.getDefaultSharedPreferences(c);


            // Wer ist der Absender
            if (sender==null) sender = einstellungen.getString("username", null);
            message.setSender(new InternetAddress(sender));

			// Wer ist der Empfänger
            if (recipients==null) recipients = einstellungen.getString("an", null);
            if (recipients.indexOf(',') > 0) {
            	Log.d(TAG, "sendMail(): Mehrere Adressaten -   ");
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
			}
            else {
            	Log.d(TAG, "sendMail(): Ein Adressat -    " + new InternetAddress(recipients).toString());
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
			}

			// Was ist der Betreff
            message.setSubject(subject);

			// Email Text und Anhaenge
            if (!body.isEmpty()) {
				/*
				  Zunächst den Textbody anhängen
				 */
                MimeBodyPart mbp1 = new MimeBodyPart();
                mbp1.setText(body);
                mp.addBodyPart(mbp1);
				/*
				  Anhang anhängen, falls vorhanden
				 */
                if (attachment != null) {
                	MimeBodyPart mbp2 = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(attachment);
                    mbp2.setDataHandler(new DataHandler(fds));
                    mbp2.setFileName(fds.getName());
                    mp.addBodyPart(mbp2);
				}
				/*
				  Beides an die Email anhängen
				 */
                message.setContent(mp);
			}

			// und jetzt noch die Email verschicken
			Log.d(TAG, "---------------------------------------------------");
            Log.d(TAG, "sendMail(): E-Mail wird mit Transport.SEND versandt");
			Log.d(TAG, "---------------------------------------------------");
			Address[] mRecipients = message.getRecipients(Message.RecipientType.TO);
			for (Address address : mRecipients) {
				Log.d(TAG, "sendMail(): An -                  " + address.toString());
			}
			Log.d(TAG, "sendMail(): Von -                 " + message.getSender().toString());
			Log.d(TAG, "sendMail(): Betreff -             " + message.getSubject());

			// getPasswordAuthentication();
			ConnectivityManager conMan = null;
			NetworkInfo Info = null;
			conMan = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
			Info = conMan.getActiveNetworkInfo();
			if (Info == null) {

				Log.d(TAG, "sendMail(): ---------------------------------------------------");
				Log.d(TAG, "sendMail(): ---------------------------------------------------");
				Log.d(TAG, "sendMail(): " + c.getApplicationContext() + "no net connection ");
				Log.d(TAG, "sendMail(): ---------------------------------------------------");
				Log.d(TAG, "sendMail(): ---------------------------------------------------");

			} else {
				Log.d(TAG, "sendMail(): Network DetailState - " + Info.getDetailedState());
				Log.d(TAG, "sendMail(): Network ExtraInfo -   " + Info.getExtraInfo());
				Log.d(TAG, "sendMail(): Network Connected -   " + Info.isConnected());

				Transport.send(message);

				Log.d(TAG, "sendMail(): ----------------------------------------------------------");
				Log.d(TAG, "sendMail(): E-Mail wurde mit Transport.SEND versandt");
				Log.d(TAG, "sendMail(): ----------------------------------------------------------");
				Log.d(TAG, " ");
			}

		} catch(MessagingException e){
			// hat wohl nicht ganz geklappt
			Log.d(TAG, "sendMail(): ----------------------------------------------------------");
			Log.d(TAG, "sendMail(): Messaging-------------------------------------------------");
			Log.d(TAG, "sendMail(): Es ist ein Fehler aufgetreten - " + e.getMessage() + " !!!");
			Log.d(TAG, "sendMail(): ----------------------------------------------------------");
			Log.d(TAG, "sendMail(): ----------------------------------------------------------");

		} catch(Exception e){
			// hat wohl nicht ganz geklappt
			Log.d(TAG, "sendMail(): ----------------------------------------------------------");
			Log.d(TAG, "sendMail(): Exception-------------------------------------------------");
			Log.d(TAG, "sendMail(): Es ist ein Fehler aufgetreten - " + e.getMessage() + " !!!");
			Log.d(TAG, "sendMail(): ----------------------------------------------------------");
			Log.d(TAG, "sendMail(): ----------------------------------------------------------");

		}

		Log.d(TAG, "---------------------------------------------------");
		Log.d(TAG, "sendMail(): End");
		Log.d(TAG, "---------------------------------------------------");

	  }

	   public class ByteArrayDataSource implements DataSource {
	        private byte[] data;   
	        private String type;

	        public ByteArrayDataSource(byte[] data, String type) {
	            super();
                Log.d(TAG, "ByteArrayDataSource 1");
	            this.data = data;   
	            this.type = type;
                Log.d(TAG, "ByteArrayDataSource 1: this.data - "+this.data);
                Log.d(TAG, "ByteArrayDataSource 1: this.type - "+this.type);
	        }   

	        public ByteArrayDataSource(byte[] data) {
                super();
                Log.d(TAG, "ByteArrayDataSource 2");
	            this.data = data;   
	        }   

	        public void setType(String type) {
                Log.d(TAG, "setType");
                this.type = type;
	        }   

	        public String getContentType() {
                Log.d(TAG, "getContentType");
	            if (type == null)   
	                return "application/octet-stream";   
	            else  
	                return type;   
	        }   

	        public InputStream getInputStream() throws IOException {
                Log.d(TAG, "getInputStream");
                Log.d(TAG, "getInputStream: data - " + data);
	            return new ByteArrayInputStream(data);
	        }   

	        public String getName() {
                Log.d(TAG, "getName");
	            return "ByteArrayDataSource";   
	        }   

	        public OutputStream getOutputStream() throws IOException {
                Log.d(TAG, "getOutputStream");
	            throw new IOException("Not Supported");
	        }   
	    }   

}
