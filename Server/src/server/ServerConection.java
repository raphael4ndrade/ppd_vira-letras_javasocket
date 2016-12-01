package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerConection{

	private static final int MAXCONECTIONS = 2;

	private List<DataOutputStream> listWriters;
	private ServerSocket server;
	private Socket socket;
	private DataOutputStream outputStream;
	
	public ServerConection(int port) {
		System.out.println("Servidor Vira-Letras!");
		try {
			server = new ServerSocket(port);
			listWriters = new ArrayList<DataOutputStream>();

			while(listWriters.size() < MAXCONECTIONS ){
				socket = server.accept();
				System.out.println("Conexão com o cliente " + (listWriters.size()+1) + " estabelecida.");
				
				
				outputStream = new DataOutputStream(socket.getOutputStream());
				listWriters.add(outputStream);
				
				new Thread(new ClientListenerChat()).start();
			}
		} catch (IOException e) {
			System.out.println("Não foi possível estabelecer conexão com o servidor através da porta " + port);
		}
	}
	
	private class ClientListenerChat extends Thread{
		@Override
		public void run(){
			String messageFromClient = null;
			try {
				
				DataInputStream inputStream = new DataInputStream(socket.getInputStream());
				
				while((messageFromClient = inputStream.readUTF()) != null) {
					System.out.println("Mensagem do cliente: " + messageFromClient);
					System.out.println("Quantidade de clientes: " + listWriters.size());
					
					for(DataOutputStream p : listWriters){
						p.writeUTF(messageFromClient);
						p.flush();
					}
				}
			} catch (IOException e) {
				System.out.println("A mensagem do cliente é vazia..");
			}
		}
	}
	
	public static void main(String args[]){
		
		new ServerConection(12345);
	}

}
