package controller;

import model.ViraLetrasModel;
import view.ViraLetrasView;


public class Driver {

	/*
	 * Professor, devido as intensas modificações em cima da hora não pude implementar o identificador unico
	 * que era planejado, por isso, ao executar a aplicação garantir que o valor inteiro (999) não seja o mesmo para
	 * os dois processos. Peço mais uma vez desculpa por esse inconveniente e garanto que não se repetirá.
	 * Obrigado!
	 */
	public static void main(String[] args) {
		ViraLetrasView view = new ViraLetrasView();
		ViraLetrasModel model = new ViraLetrasModel("Raphael Andrade");
		ViraLetrasController controller = new ViraLetrasController(view, model,999);
		
		view.setVisible(true);
		
//		try{
//			javax.swing.UIManager.LookAndFeelInfo[] installedLookAndFeels=javax.swing.UIManager.getInstalledLookAndFeels();
//			for(LookAndFeelInfo i : installedLookAndFeels)
//				System.out.println(i.getClassName());
//		} catch(Exception e){
//			e.printStackTrace();
//		}
		
//		javax.swing.plaf.metal.MetalLookAndFeel
//		javax.swing.plaf.nimbus.NimbusLookAndFeel
//		com.sun.java.swing.plaf.motif.MotifLookAndFeel
//		com.apple.laf.AquaLookAndFeel
		
		
	}

}
