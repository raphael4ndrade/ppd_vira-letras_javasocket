package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import model.ViraLetrasModel;
import view.ViraLetrasView;

public class ViraLetrasController{
	
	
	public class Client{
		
		private Socket socket;
		
		private DataInputStream inputStream;
		private DataOutputStream outputStream;

		public Client(String host, int port){
			try {
				socket = new Socket(host, port);
				System.out.println("Conexão estabelecida com sucesso!");
				
				inputStream = new DataInputStream(socket.getInputStream());
				outputStream = new DataOutputStream(socket.getOutputStream());
				
				new Thread(new ServerListenerChat()).start();
				
			} catch (UnknownHostException e) {
				System.out.println("Host: " + host + " não encontrado.");
			} catch (IOException e) {
				view.getMessageArea().append("Conexão não estabelecida com o host " + host + " porta " + port + "\n");
			}
		}
		
		private class ServerListenerChat extends Thread {
			@Override
			public void run() {
				try {
					String messageFromServer = "";
					
					while((messageFromServer = inputStream.readUTF()) != null) {
						System.out.println("String do servidor: "+ messageFromServer);
						String decodded[] = messageFromServer.split("::", 3);
						
						int protocol = Integer.parseInt(decodded[0]);
						int senderId = Integer.parseInt(decodded[1]);
						String message = decodded[2];

						switch (protocol) {
						case 0: // Protocolo 0 : comunicação do chat
							if (UNIQUE_ID == senderId)
								view.getMessageArea().append(message + "\n");
							else
								view.getMessageArea().append("Rival disse: " + message + "\n");
							break;
						case 1: //Protocolo 1 : botão iniciar!
							if (UNIQUE_ID != senderId){
								model.syncReceiveBoardGame(message);
								model.setMyTurn(false);
							} else{
								model.setMyTurn(true);
								setEnableGamePieces(true);
							}
							view.setButtonsTurn(model.isMyTurn());
							view.getStartButton().setEnabled(false);
							break;
						case 2: //Protocolo 2 : botão desistir!
							if (UNIQUE_ID != senderId){
								model.setWinner(true);
								view.getMessageArea().append("Você venceu!!\n");
							} else {
								model.setWinner(false);
								view.getMessageArea().append("Você perdeu!!\n");
							}
							view.setButtonsSurrender();
							showLetters();
							break;
						case 3: //Protocolo 3 : lançar dados
							if(UNIQUE_ID != senderId)
								view.getMessageArea().append("Seu adversário pode desvirar até " + message + " peças.\n");
							else
								view.getMessageArea().append("Você pode desvirar até " + message + " peças\n");
							break;
						case 4: //Protocolo 4 : reiniciar partida.
							if(UNIQUE_ID != senderId)
								view.getMessageArea().append(message + "\n");
							model.resetGameBoard();
							model.resetFlags();
							initVariables();
							loadButton();
							view.setButtonsTurn(true);
							view.getStartButton().setEnabled(true);
							setEnableGamePieces(false);
							break;
						case 5: //Protocolo 5 : finalizar turno
							String tempValues[] = message.split("::");
							int score = Integer.parseInt(tempValues[0]) - Integer.parseInt(tempValues[1]); //flip= 0 valid=1
							
							if (UNIQUE_ID != senderId) {
								model.setMyTurn(true);
								model.setDicesRolled(false);
								model.setScore(1, model.getScore(1) + score);
								setEnableGamePieces(model.isMyTurn());
							} else {
								model.setMyTurn(false);
								model.setScore(0, model.getScore(1) + score);
							}
							view.setButtonsTurn(model.isMyTurn());
							setEnableGamePieces(model.isMyTurn());
							break;
						case 8: //Protocolo 8 : virada de uma peça e contando...
							if (UNIQUE_ID != senderId) {
								int i = Integer.parseInt(message);
								model.getBoardPieces()[i].setFlipped(true);
								view.getGamePieces()[i].setText(model.getBoardPieces()[i].getFlipped());
							}break;
						case 9: //Protocolo 9: validação de uma peça e contando...
							int i = Integer.parseInt(message);
							view.getGamePieces()[i].setEnabled(false);
							model.getBoardPieces()[i].setValid(false);
							break;
						default:
							view.getMessageArea().append("Erro ao ler o protocolo!\n");
							break;
						}
					}
				} catch (NumberFormatException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*Classes dos ActionListener*/
	private class SendChatMessage implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String message = view.getMessageSend().getText();
				if(!message.isEmpty() && !message.trim().isEmpty()){
					sendToServer(0, UNIQUE_ID, message);
					
					view.getMessageSend().setText("");
					view.getMessageSend().requestFocus();
				} else
					throw new IOException();
			} catch (IOException ex) {
				view.getMessageArea().append("A mensagem não pode ser enviada...\n");
				view.getMessageSend().setText("");
				view.getMessageSend().requestFocus();
			}
		}
	}
	
	private class FlipPiece implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton b = (JButton) e.getSource();
			int i=  (int) b.getClientProperty("index");
			
			if(counter < sum){
				if(!model.getBoardPieces()[i].isFlipped() && model.getBoardPieces()[i].isValid()){
					model.getBoardPieces()[i].setFlipped(true);
					view.getGamePieces()[i].setText(model.getBoardPieces()[i].getFlipped());
					flippedPieces.add(i);
					sendToServer(8, UNIQUE_ID, i+"");
					counter++;
				} 
			}
		}
	}
	
	//TODO: verificar se é util pra regra do jogo adicionar a opção de deselecionar.
	private class ValidatePiece implements MouseListener{
		long start, end;

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			start = System.currentTimeMillis();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			end = System.currentTimeMillis();
			JButton b = (JButton) e.getSource();
			int i=  (int) b.getClientProperty("index");
			
			if((end - start) > 400 && model.getBoardPieces()[i].isFlipped() && model.isMyTurn()){
				invalidPieces.add(i);
				sendToServer(9, UNIQUE_ID, i+"");
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	//TODO: rever como a regra é feita.
	private class Surrender implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			int i = JOptionPane.showConfirmDialog( view, "Deseja desistir deste jogo?", "Pense bem...", JOptionPane.YES_NO_OPTION);
			if (i == 0)
				sendToServer(2, UNIQUE_ID, "true");
		}
	}
	
	private class RollDice implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(model.isMyTurn() && !model.isDicesRolled()){
				int diceOne = model.rollDice();
				int diceTwo = model.rollDice();
				model.setDices(diceOne, diceTwo);
				model.setDicesRolled(true);
				
				counter = 0;
				sum = model.getSumDices();
				
				view.setDiceOneLabel(model.getDices(0));
				view.setDiceTwoLabel(model.getDices(1));
				view.getDicesButton().setEnabled(false);
				sendToServer(3, UNIQUE_ID, model.getSumDices() + "" );
			}
		}
	}
	
	private class StartGame implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			model.setMyTurn(true);
			sendToServer(1, UNIQUE_ID, model.syncSendBoardGame());
		}
	}
	
	//TODO : restart sem iniciar gera um bug...
	private class Restart implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			int value = JOptionPane.showConfirmDialog(view, "Deseja reiniciar o jogo?", "Reiniciar...", JOptionPane.YES_NO_OPTION);
			if(value == 0){
				sendToServer(4, UNIQUE_ID, "O jogo será reiniciado. Ok?");
			}
		}
	}
	
	private class EndTurn implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			sendToServer(5, UNIQUE_ID, flippedPieces.size() + "::" + invalidPieces.size());
			flippedPieces = new ArrayList<Integer>();
			invalidPieces = new ArrayList<Integer>();
		}
	}
	
	private Client client;
	private ViraLetrasView view;
	private ViraLetrasModel model;
	
	private final int UNIQUE_ID;
	private int counter, sum;
	private List<Integer> flippedPieces, invalidPieces;
	
	public ViraLetrasController(ViraLetrasView view, ViraLetrasModel model, int id){
		this.view = view;
		this.model = model;
		this.UNIQUE_ID = id;
//		this.client = new Client("localhost", 12345);
		
		this.initVariables();
		
		view.addSendChatMessageControl(new SendChatMessage());
		view.addFlipPieceControl(new FlipPiece(), new ValidatePiece());
		view.addSurrenderControl(new Surrender());
		view.addDicesControl(new RollDice());
		view.addStartControl(new StartGame());
		view.addRestartControl(new Restart());
		view.addEndTurnControl(new EndTurn());
		
		this.loadButton();
		
	}
	
	public void initVariables(){
		this.counter = 0;
		this.sum = 0;
		this.flippedPieces = new ArrayList<Integer>();
		this.invalidPieces = new ArrayList<Integer>();
	}
	
	private void loadButton(){
		for(int i=0; i< 64; i++){
			String letras = model.getBoardPieces()[i].getUnflipped();
			view.getGamePieces()[i].setText(letras);
		}
	}
	
	public void setEnableGamePieces(boolean bool){
		for(int i=0; i<64; i++){
			if(!model.getBoardPieces()[i].isValid() && model.getBoardPieces()[i].isFlipped()){
				view.getGamePieces()[i].setEnabled(false);
				view.getGamePieces()[i].setText(" ");
			}
			
			if(model.getBoardPieces()[i].isValid() && model.getBoardPieces()[i].isFlipped()){
				view.getGamePieces()[i].setEnabled(bool);
				view.getGamePieces()[i].setText(model.getBoardPieces()[i].getUnflipped());
				model.getBoardPieces()[i].setFlipped(false);
			}
		}
	}
	
	private void showLetters(){
		for(int i=0; i< 64; i++){
			String letras = model.getBoardPieces()[i].getFlipped();
			view.getGamePieces()[i].setText(letras);
		}
	}
	
	private void sendToServer(int protocol, int senderID, String message){
		try {
			if(client != null){
				client.outputStream.writeUTF(protocol + "::" + senderID + "::"+ message);
				client.outputStream.flush();
			} else
				throw new IOException();
		} catch (IOException e) {
			System.out.println("Falha ao enviar mensagem ao servidor...");
		}
	}
}