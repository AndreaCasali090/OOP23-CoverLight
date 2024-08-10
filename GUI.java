package MyClasses;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JFrame {
	static final int DIM_SQUARE = 50;	//un valore divisibile per 2 e per cui width e height sono divisibili
	static final int WIDTH = 1000;		//10 metri
	static final int HEIGHT = 800;		//8 metri
	static final int MIN_INT = -90;		//un segnale sotto -90 decibel milliwatt non è percepibile dalla comune apparecchiatura Wi-Fi
	static final Color COL0 = Color.RED;		//valore più basso
	static final Color COL1 = Color.ORANGE;
	static final Color COL2 = Color.YELLOW;
	static final Color COL3 = Color.GREEN;
	static final Color COL4 = Color.CYAN;		//valore più alto
	static final Color[] TONI = {COL0,COL1,COL2,COL3,COL4};//TODO:assicurarsi che il set di colori sia accessibile ai daltonici
	static final String BASSO = "Basso";
	static final String MEDIO = "Medio";
	static final String ALTO = "Alto";
	static String impactSel = "Medio";
	static int i, j, risultato;
	
	private static final long serialVersionUID = 1L;

	static HashMap<Emitters,Boolean> apparati = new HashMap<Emitters,Boolean>();		//mappa di emittenti
	static HashMap<Walls, Boolean> planimetria = new HashMap<Walls,Boolean>();			//mappa di muri
	static HashMap<Utilizers,Boolean> consumatori = new HashMap<Utilizers,Boolean>();	//mappa di utilizzatori
	
	private void result(Graphics graphics) {
		for(Map.Entry<Emitters,Boolean> entry : apparati.entrySet()) {
			if(entry.getValue()) {
				drawEmitter(graphics,entry.getKey());
			}
		}
		for(Map.Entry<Walls, Boolean> entry : planimetria.entrySet()) {
			if(entry.getValue()) {
				drawWall(graphics,entry.getKey());
			}
		}
		for(Map.Entry<Utilizers,Boolean> entry : consumatori.entrySet()) {
			if(entry.getValue()) {
				drawUtilizer((Graphics2D) graphics,entry.getKey().getPosition());
			}
		}
	}
	
	private void redraw(Graphics graphics) {
		graphics.clearRect(0, 0, WIDTH, HEIGHT);
		result(graphics);
	}
	
	private boolean validatePosition(Point val) {
		if(val.x>=0 && val.x<=WIDTH && val.y>=0 && val.y<=HEIGHT) {
			return true;
		}
		System.out.println("Errore: la posizione deve essere una coppia di numeri positivi minori rispettivamente di " + WIDTH + " e " + HEIGHT);
		return false;
	}
	
	private boolean validateWallPosition(Line2D val) {
		if(val.getX1()>=0 && val.getX1()<=WIDTH && val.getY1()>=0 && val.getY1()<=HEIGHT && val.getX2()>=0 && val.getX2()<=WIDTH && val.getY2()>=0 && val.getY2()<=HEIGHT) {
			if(val.getX1() % DIM_SQUARE == 0 && val.getX2() % DIM_SQUARE == 0 && val.getY1() % DIM_SQUARE == 0 && val.getY2() % DIM_SQUARE == 0) {
				if(val.getX1() == val.getX2() ^ val.getY1() == val.getY2()) {		//muri diagonali non nulli
					return true;
				}
				System.out.println("Errore: la posizione deve essere una coppia di numeri che definiscano una linea non nulla parallela ad un asse");
				return false;
			}
			System.out.println("Errore: la posizione deve essere una coppia di numeri multipli di " + DIM_SQUARE + " (centimetri), che definiscano una linea non nulla parallela ad un asse");
			return false;
		}
		System.out.println("Errore: la posizione deve essere una coppia di numeri positivi minori rispettivamente di " + WIDTH + " e " + HEIGHT + ", multipli di " + DIM_SQUARE + " (centimetri), che definiscano una linea non nulla parallela ad un asse");
		return false;
	}
	
	private void drawUtilizer(Graphics2D graphics, Point draw) {
		int x = draw.x - draw.x % DIM_SQUARE;
		int y = draw.y - draw.y % DIM_SQUARE;
		final float dim_dash = DIM_SQUARE/5;
		final BasicStroke black_dash = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {dim_dash,dim_dash}, 0);
		final BasicStroke white_dash = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {dim_dash,dim_dash}, dim_dash);
		graphics.setColor(Color.BLACK);
		graphics.setStroke(black_dash);
		graphics.drawRect(x, y, DIM_SQUARE, DIM_SQUARE);
		graphics.setColor(Color.WHITE);
		graphics.setStroke(white_dash);
		graphics.drawRect(x, y, DIM_SQUARE, DIM_SQUARE);
	} /*l'utente è un quadrato dai bordi bianchi e neri a zig-zag; ha il solo scopo di evidenziare il colore al proprio interno.*/
	

	private void drawEmitter(Graphics graphics, Emitters emittente) {
		graphics.setColor(Color.BLACK);
		graphics.fillArc(emittente.getPosition().x,emittente.getPosition().y,DIM_SQUARE,DIM_SQUARE,emittente.getAngles().x,Math.abs(emittente.getAngles().y-emittente.getAngles().x));
	}

	private void drawWall(Graphics graphics, Walls muro) {
		final BasicStroke muratura = new BasicStroke(5);
		((Graphics2D) graphics).setStroke(muratura);
		switch(muro.getImpact()) {
			case BASSO: graphics.setColor(Color.GRAY);		break;
			case MEDIO: graphics.setColor(Color.DARK_GRAY);	break;
			case ALTO:  graphics.setColor(Color.BLACK);		break;
			default: return;
		}
        graphics.drawLine((int) muro.getPosition().getX1(),(int) muro.getPosition().getY1(),(int) muro.getPosition().getX2(),(int) muro.getPosition().getY2());
	}

	private void paintComponent(Graphics graphics) {
		graphics.setColor(TONI[risultato]);
		graphics.fillRect(j - DIM_SQUARE/2+1,i - DIM_SQUARE/2+1, DIM_SQUARE-1, DIM_SQUARE-1);
	}

	
	public GUI() {
		
    //<Area di disegno
		
		//Didascalia
		final JPanel captionPanel = new JPanel(new BorderLayout());
		captionPanel.setBorder(new TitledBorder("Didascalia"));
		final JTextArea captionTxtArea = new JTextArea();
		captionTxtArea.setLineWrap(true);
		captionTxtArea.setText("Effetto del materiale dell'ostacolo su un segnale radio\r\n"
				+ "A seconda del materiale, gli ostacoli possono riflettere le onde radio, assorbirle, privandole di una parte della potenza, o non avere alcun effetto sul segnale radio. Tali materiali sono chiamati radiotrasparenti. Più alto è il coefficiente di assorbimento del segnale e più spesso è l'ostacolo, più forte è l'impatto sulla trasmissione radio.\r\n"
				+ "Coefficiente di assorbimento del segnale\r\n"
				+ "Basso\r\n"
				+ "Perdita di potenza del 50%\r\n"
				+ "- Mattone rosso secco di 90 mm di spessore\r\n"
				+ "- Pannello di gesso di 100 mm di spessore\r\n"
				+ "- Legno secco di 80 mm di spessore\r\n"
				+ "- Vetro di 15 mm di spessore\r\n"
				+ "\r\n"
				+ "Medio\r\n"
				+ "La potenza si riduce di 10 volte\r\n"
				+ "- Mattone di 250 mm di spessore\r\n"
				+ "- Blocchi di calcestruzzo di 200 mm di spessore\r\n"
				+ "- Calcestruzzo di 100 mm di spessore\r\n"
				+ "- Muratura di 200 mm di spessore\r\n"
				+ "\r\n"
				+ "Alto\r\n"
				+ "La potenza si riduce di 100 volte\r\n"
				+ "- Calcestruzzo di 300 mm di spessore\r\n"
				+ "- Calcestruzzo armato di 200 mm di spessore\r\n"
				+ "- Travi in alluminio e acciaio\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "Si provvede uno spazio di " + WIDTH/100 + " metri per " + HEIGHT/100 + " in cui riprodurre la pianta dell'edificio; valori illegali saranno considerati 0.\r\n"
				+ "Porte, finestre interne ed esterne od altre aperture fra stanze sono sempre considerate chiuse ai fini della rilevazione del segnale: si ipotizza il segnale minimo nel caso peggiore.\r\n"
				+ "La precisione massima nel piazzamento di un muro è di " + DIM_SQUARE + " centimetri, sugli assi x e y; non si accettano muri diagonali.\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "Per selezionare un campo da abilitare/disabilitare/cancellare è sufficiente indicarne la posizione e premere il relativo pulsante; non serve impostare correttamente il resto dei campi.\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "Secondo le normative ETSI EN, la potenza di emittenti wireless in un edificio non può superare i 200 milliWatt e la frequenza deve essere compresa nella banda 5150-5350 MegaHertz \r\n"
				+ "Gli emittenti devono essere distanziati di almeno " + DIM_SQUARE/2 + " centimetri ed i muri si possono intersecare ma non compenetrare.\r\n"
				+ "Gli angoli di inizio e di fine sono 0 e 360 per antenne omnidirezionali; per antenne direzionali, l'ampiezza è calcolata in senso antiorario con 0° = 360° = ore 3.\r\n");
		final JScrollPane scroll = new JScrollPane(captionTxtArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		captionPanel.add(scroll);
		
		
		//Canvas
		final JPanel canvasPanel = new JPanel(new BorderLayout());
		canvasPanel.setBorder(new TitledBorder("Canvas"));
		
		final JPanel canvasContainerPanel = new JPanel(new BorderLayout());// panel che contiene lo scroll
		canvasPanel.add(canvasContainerPanel);

		final JPanel drawPanel = new JPanel();// panel che contiene il disegno
		final JScrollPane scrollCanvas = new JScrollPane(canvasContainerPanel);
		scrollCanvas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		canvasPanel.add(scrollCanvas);
		canvasContainerPanel.add(drawPanel);
		
    //fine Area Di Disegno>
		
		
    //<Componenti 
		
		final JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

		//Emittente 
		final JPanel panelEmitter = new JPanel();
		final GroupLayout layoutEmitter = new GroupLayout(panelEmitter);
		panelEmitter.setLayout(layoutEmitter);
		panelEmitter.setBorder(new TitledBorder("Emittente"));

		final JButton createEmitter = new JButton("Crea");
		JButton deleteEmitter = new JButton("Cancella");
		JButton enableDisableEmitter = new JButton("Abilita/Disabilita");
		JLabel xEmitLbl = new JLabel("X:");
		JTextField xEmitTxt = new JTextField();
		JLabel yEmitLbl = new JLabel("Y:");
		JTextField yEmitTxt = new JTextField();
		JLabel powEmitLbl = new JLabel("Potenza:");
		JTextField powEmitTxt = new JTextField();
		JLabel freqEmitLbl = new JLabel("Frequenza:");
		JTextField freqEmitterTxt = new JTextField();
		JLabel emitAngStartLbl = new JLabel("AngStart:");
		JTextField angStartEmitTxt = new JTextField("0");
		JLabel emitAngEndLbl = new JLabel("AngEnd:");
		JTextField angEndEmitTxt = new JTextField("360");
		JLabel emitEnabled = new JLabel("");
		JLabel emitDisabled = new JLabel("");
		
		layoutEmitter.setAutoCreateGaps(true);
		layoutEmitter.setAutoCreateContainerGaps(true);
		/*
		layoutEmitter.setHorizontalGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(createEmitter)
				.addGroup(layoutEmitter.createSequentialGroup().addComponent(deleteEmitter)
						.addComponent(enableDisableEmitter))
				.addGroup(layoutEmitter.createSequentialGroup().addGroup(layoutEmitter
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layoutEmitter.createSequentialGroup().addComponent(xEmitLbl).addComponent(xEmitTxt))
						.addGroup(layoutEmitter.createSequentialGroup().addComponent(yEmitLbl).addComponent(yEmitTxt))
						.addGroup(layoutEmitter.createSequentialGroup().addComponent(emitAngStartLbl)
								.addComponent(angStartEmitTxt)))
						.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(layoutEmitter.createSequentialGroup().addComponent(powEmitLbl)
										.addComponent(powEmitTxt))
								.addGroup(layoutEmitter.createSequentialGroup().addComponent(freqEmitLbl)
										.addComponent(freqEmitterTxt))
								.addGroup(layoutEmitter.createSequentialGroup().addComponent(emitAngEndLbl)
										.addComponent(angEndEmitTxt))))
				.addGroup(layoutEmitter.createSequentialGroup()
						.addGroup(layoutEmitter.createSequentialGroup().addComponent(emitEnabled).addComponent(emitDisabled))));

		layoutEmitter.setVerticalGroup(layoutEmitter.createSequentialGroup().addComponent(createEmitter)
				.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(deleteEmitter)
						.addComponent(enableDisableEmitter))
				.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layoutEmitter
						.createSequentialGroup()
						.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(xEmitLbl).addComponent(xEmitTxt))
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(powEmitLbl).addComponent(powEmitTxt)))
						.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(yEmitLbl).addComponent(yEmitTxt))
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(freqEmitLbl).addComponent(freqEmitterTxt)))
						.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(emitAngStartLbl).addComponent(angStartEmitTxt))
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(emitAngEndLbl).addComponent(angEndEmitTxt))))
				.addGroup(layoutEmitter.createSequentialGroup()
						.addGroup(layoutEmitter.createSequentialGroup().addComponent(emitEnabled).addComponent(emitDisabled)))));*/
		layoutEmitter.setHorizontalGroup(layoutEmitter.createSequentialGroup()
				.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(createEmitter, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(deleteEmitter, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(enableDisableEmitter, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(layoutEmitter.createSequentialGroup().addGroup(layoutEmitter
						.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layoutEmitter.createSequentialGroup()
								.addComponent(xEmitLbl)
								.addComponent(xEmitTxt))
						.addGroup(layoutEmitter.createSequentialGroup()
								.addComponent(yEmitLbl)
								.addComponent(yEmitTxt))
						.addGroup(layoutEmitter.createSequentialGroup()
								.addComponent(emitAngStartLbl)
								.addComponent(angStartEmitTxt)))
						.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(layoutEmitter.createSequentialGroup()
										.addComponent(powEmitLbl)
										.addComponent(powEmitTxt))
								.addGroup(layoutEmitter.createSequentialGroup()
										.addComponent(freqEmitLbl)
										.addComponent(freqEmitterTxt))
								.addGroup(layoutEmitter.createSequentialGroup()
										.addComponent(emitAngEndLbl)
										.addComponent(angEndEmitTxt)))));

		layoutEmitter.setVerticalGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layoutEmitter.createSequentialGroup()
						.addComponent(createEmitter)
						.addComponent(deleteEmitter)
						.addComponent(enableDisableEmitter))
				.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layoutEmitter
						.createSequentialGroup()
						.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(xEmitLbl).addComponent(xEmitTxt))
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(powEmitLbl).addComponent(powEmitTxt)))
						.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(yEmitLbl).addComponent(yEmitTxt))
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(freqEmitLbl).addComponent(freqEmitterTxt)))
						.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(emitAngStartLbl).addComponent(angStartEmitTxt))
								.addGroup(layoutEmitter.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(emitAngEndLbl).addComponent(angEndEmitTxt))))));


		//Utilizzatore
		final JPanel panelUtilizer = new JPanel();
		final GroupLayout layoutUtilizer = new GroupLayout(panelUtilizer);
		panelUtilizer.setLayout(layoutUtilizer);
		panelUtilizer.setBorder(new TitledBorder("Utilizzatore"));
		
		final JButton createUtilizer = new JButton("Crea");
		JButton deleteUtilizer = new JButton("Cancella");
		JButton enableUtilizer = new JButton("Abilita/Disabilita");
		JLabel xUtilLbl = new JLabel("X:");
		JLabel xUtilCurVal = new JLabel();
		JTextField xUtilTxt = new JTextField();
		JLabel yUtilLbl = new JLabel("Y:");
		JLabel yUtilCurVal = new JLabel();
		JTextField yUtilTxt = new JTextField();

		
		JLabel utilEnabled = new JLabel();
		JLabel utilDisabled = new JLabel();
		/*final JButton createUtilizer = new JButton("Crea");
		JButton deleteUtilizer = new JButton("Cancella");
		JButton enableUtilizer = new JButton("Abilita/Disabilita");
		JLabel xUtilLbl = new JLabel("X:");
		JTextField xUtilTxt = new JTextField();
		JLabel yUtilLbl = new JLabel("Y:");
		JTextField yUtilTxt = new JTextField();
		JLabel utilEnabled = new JLabel();
		JLabel utilDisabled = new JLabel();
		
		layoutUtilizer.setAutoCreateGaps(true);
		layoutUtilizer.setAutoCreateContainerGaps(true);

		layoutUtilizer.setHorizontalGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(createUtilizer)
				.addGroup(layoutUtilizer.createSequentialGroup().addComponent(deleteUtilizer)
						.addComponent(enableUtilizer))
				.addGroup(layoutUtilizer.createSequentialGroup()
						.addGroup(layoutUtilizer.createSequentialGroup().addComponent(xUtilLbl).addComponent(xUtilTxt))
						.addGroup(layoutUtilizer.createSequentialGroup().addComponent(yUtilLbl).addComponent(yUtilTxt))
						.addGroup(layoutUtilizer.createSequentialGroup().addComponent(utilEnabled).addComponent(utilDisabled))));

		layoutUtilizer
				.setVerticalGroup(layoutUtilizer.createSequentialGroup().addComponent(createUtilizer)
						.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(deleteUtilizer).addComponent(enableUtilizer))
						.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(xUtilLbl).addComponent(xUtilTxt))
										.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addGroup(layoutUtilizer
														.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(yUtilLbl).addComponent(yUtilTxt)))
										.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addGroup(layoutUtilizer
														.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(utilEnabled).addComponent(utilDisabled))))));*/
		layoutUtilizer.setAutoCreateGaps(true);
		layoutUtilizer.setAutoCreateContainerGaps(true);

		layoutUtilizer.setHorizontalGroup(layoutUtilizer.createSequentialGroup()
				.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(createUtilizer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(deleteUtilizer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(enableUtilizer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(layoutUtilizer.createSequentialGroup()
						.addGroup(layoutUtilizer.createSequentialGroup().addComponent(xUtilLbl)
								.addComponent(xUtilCurVal).addComponent(xUtilTxt))
						.addGroup(layoutUtilizer.createSequentialGroup().addComponent(yUtilLbl)
								.addComponent(yUtilCurVal).addComponent(yUtilTxt))));

		layoutUtilizer.setVerticalGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layoutUtilizer.createSequentialGroup().addComponent(createUtilizer)
						.addComponent(deleteUtilizer).addComponent(enableUtilizer))
				.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(xUtilLbl).addComponent(xUtilCurVal).addComponent(xUtilTxt))
								.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addGroup(layoutUtilizer.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(yUtilLbl).addComponent(yUtilCurVal)
												.addComponent(yUtilTxt))))));

		//Muro
		final JPanel panelWall = new JPanel();
		final GroupLayout layoutWall = new GroupLayout(panelWall);
		panelWall.setLayout(layoutWall);
		panelWall.setBorder(new TitledBorder("Muro"));

		final JButton createWall = new JButton("Crea");
		JButton deleteWall = new JButton("Cancella");
		JButton enableWall = new JButton("Abilita/Disabilita");
		JLabel wallStartxLbl = new JLabel("Xstart:");
		JTextField wallStartxTxt = new JTextField();
		JLabel wallEndxLbl = new JLabel("Xend:");
		JTextField wallEndxTxt = new JTextField();
		JLabel wallStartyLbl = new JLabel("Ystart:");
		JTextField wallStartyTxt = new JTextField();
		JLabel wallEndyLbl = new JLabel("Yend:");
		JTextField wallEndyTxt = new JTextField();
		JLabel impactLbl = new JLabel("Impatto:");
		JRadioButton rLow = new JRadioButton("Basso", false);
		JRadioButton rMedium = new JRadioButton("Medio", true);
		JRadioButton rHigh = new JRadioButton("Alto", false);
		ButtonGroup impactGroup = new ButtonGroup();
		impactGroup.add(rLow);
		impactGroup.add(rMedium);
		impactGroup.add(rHigh);
		JLabel wallEnabledL = new JLabel("");
		JLabel wallEnabledM = new JLabel("");
		JLabel wallEnabledH = new JLabel("");
		JLabel wallDisabledL = new JLabel("");
		JLabel wallDisabledM = new JLabel("");
		JLabel wallDisabledH = new JLabel("");
		
		layoutWall.setAutoCreateGaps(true);
		layoutWall.setAutoCreateContainerGaps(true);

		/*layoutWall.setHorizontalGroup(
				layoutWall.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(createWall)
						.addGroup(layoutWall.createSequentialGroup().addComponent(deleteWall).addComponent(enableWall))
						.addGroup(layoutWall.createSequentialGroup()
								.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(layoutWall.createSequentialGroup().addComponent(wallStartxLbl)
												.addComponent(wallStartxTxt))
										.addGroup(layoutWall.createSequentialGroup().addComponent(wallStartyLbl)
												.addComponent(wallStartyTxt)))
								.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(layoutWall.createSequentialGroup().addComponent(wallEndxLbl)
												.addComponent(wallEndxTxt))
										.addGroup(layoutWall.createSequentialGroup().addComponent(wallEndyLbl)
												.addComponent(wallEndyTxt))))
						.addGroup(layoutWall.createSequentialGroup().addComponent(impactLbl).addComponent(rLow)
								.addComponent(rMedium).addComponent(rHigh))
						.addGroup(layoutWall.createSequentialGroup()
								.addGroup(layoutWall.createSequentialGroup().addComponent(wallEnabledL).addComponent(wallEnabledM).addComponent(wallEnabledH)))
						.addGroup(layoutWall.createSequentialGroup()
										.addGroup(layoutWall.createSequentialGroup().addComponent(wallDisabledL).addComponent(wallDisabledM).addComponent(wallDisabledH))));

		layoutWall.setVerticalGroup(layoutWall.createSequentialGroup().addComponent(createWall)
				.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(deleteWall)
						.addComponent(enableWall))
				.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layoutWall.createSequentialGroup()
								.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(wallStartxLbl).addComponent(wallStartxTxt))
										.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(wallEndxLbl).addComponent(wallEndxTxt)))
								.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(wallStartyLbl).addComponent(wallStartyTxt))
										.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(wallEndyLbl).addComponent(wallEndyTxt)))))
				.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(impactLbl)
						.addComponent(rLow).addComponent(rMedium).addComponent(rHigh))
				.addGroup(layoutWall.createSequentialGroup()
						.addGroup(layoutWall.createSequentialGroup().addComponent(wallEnabledL).addComponent(wallEnabledM).addComponent(wallEnabledH)))
				.addGroup(layoutWall.createSequentialGroup()
								.addGroup(layoutWall.createSequentialGroup().addComponent(wallDisabledL).addComponent(wallDisabledM).addComponent(wallDisabledH))));
*/
		layoutWall.setHorizontalGroup(layoutWall.createSequentialGroup()
				.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(createWall)
						.addGroup(layoutWall.createSequentialGroup().addComponent(deleteWall).addComponent(enableWall)))
				.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(layoutWall.createSequentialGroup()
								.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(layoutWall.createSequentialGroup().addComponent(wallStartxLbl)
												.addComponent(wallStartxTxt))
										.addGroup(layoutWall.createSequentialGroup().addComponent(wallStartyLbl)
												.addComponent(wallStartyTxt)))
								.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addGroup(layoutWall.createSequentialGroup().addComponent(wallEndxLbl)
												.addComponent(wallEndxTxt))
										.addGroup(layoutWall.createSequentialGroup().addComponent(wallEndyLbl)
												.addComponent(wallEndyTxt))))
						.addGroup(layoutWall.createSequentialGroup().addComponent(impactLbl).addComponent(rLow)
								.addComponent(rMedium).addComponent(rHigh))));

		layoutWall.setVerticalGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layoutWall.createSequentialGroup().addComponent(createWall)
						.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(deleteWall)
								.addComponent(enableWall)))
				.addGroup(layoutWall.createSequentialGroup().addGroup(layoutWall
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layoutWall.createSequentialGroup()
								.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(wallStartxLbl).addComponent(wallStartxTxt))
										.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(wallEndxLbl).addComponent(wallEndxTxt)))
								.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(wallStartyLbl).addComponent(wallStartyTxt))
										.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(wallEndyLbl).addComponent(wallEndyTxt)))))
						.addGroup(layoutWall.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(impactLbl)
								.addComponent(rLow).addComponent(rMedium).addComponent(rHigh))));

		//Aggiunta al panel
		buttonsPanel.add(panelEmitter);
		buttonsPanel.add(panelUtilizer);
		buttonsPanel.add(panelWall);
		
		
		// Panel results
		final JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

		final JButton generateResult = new JButton("Genera risultato");
		generateResult.setAlignmentX(Component.CENTER_ALIGNMENT);

		final JPanel resultContainerPanel = new JPanel(new BorderLayout());
		final JScrollPane scrollResult = new JScrollPane(resultContainerPanel);
		scrollResult.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		final JPanel showResult = new JPanel();// panel per i result
		resultContainerPanel.add(showResult);
		resultPanel.add(generateResult);
		resultPanel.add(scrollResult);
    //Fine componenti>		
		
		
		//Btn listeners
		createEmitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xEmit, yEmit, angS, angE;
				float pow, freq;
				try {
					xEmit = Integer.parseInt(xEmitTxt.getText());
				} catch (NumberFormatException nfe) {
					xEmitTxt.setText("0");
					xEmit = 0;
				}
				try {
					yEmit = Integer.parseInt(yEmitTxt.getText());
				} catch (NumberFormatException nfe) {
					yEmitTxt.setText("0");
					yEmit = 0;
				}
				try {
					pow = Float.parseFloat(powEmitTxt.getText());
					if(pow>200 || pow<=0) {					//normative ETSI EN
						System.out.println("La potenza non deve superare i 200 milliWatt");
						return;
					}
				} catch (NumberFormatException nfe) {
					System.out.println("La potenza non deve superare i 200 milliWatt");
					return;
				}
				try {
					freq = Float.parseFloat(freqEmitterTxt.getText());
					if((freq < 5150 || freq > 5350)) {	//normative ETSI EN
						System.out.println("La frequenza del Wi-Fi all'interno di un edificio deve essere compresa fra 5150 e 5350 MegaHertz");
						return;
					}
				} catch (NumberFormatException nfe) {
					System.out.println("La frequenza del Wi-Fi all'interno di un edificio deve essere compresa fra 5150 e 5350 MegaHertz");
					return;
				}
				try {
					angS = Integer.parseInt(angStartEmitTxt.getText());
					if(angS < 0 || angS > 360) {
						System.out.println("Gli angoli devono avere valore compreso fra 0 e 360");
						return;
					}
				} catch (NumberFormatException nfe) {
					angStartEmitTxt.setText("0");
					angS = 0;
				}
				try {
					angE = Integer.parseInt(angEndEmitTxt.getText());
					if(angE < 0 || angE > 360) {
						System.out.println("Gli angoli devono avere valore compreso fra 0 e 360");
						return;
					}
				} catch (NumberFormatException nfe) {
					angEndEmitTxt.setText("360");
					angE = 0;
				}
				Point emit = new Point(xEmit,yEmit);
				if(validatePosition(emit)) {
					for(Emitters entry : apparati.keySet()) { //controllo compenetrazione
						if(emit.distance(entry.getPosition())<DIM_SQUARE/2) {
							System.out.println("Gli emittenti devono essere distanziati di almeno " + DIM_SQUARE/2 + " centimetri");
							return;
						}
					}
					Emitters emitter = new Emitters(emit, pow, freq, angS, angE);
					apparati.put(emitter, true);
					drawEmitter(drawPanel.getGraphics(),emitter);
					emitEnabled.setText(emitEnabled.getText() + "(" + emit.x + " " + emit.y + ") " + pow + " mW  " + freq + " MHz  [" + angS + "°-" + angE + "°]    ");
				}
			}
		});	

		deleteEmitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xEmit, yEmit;
				try {
					xEmit = Integer.parseInt(xEmitTxt.getText());
				} catch (NumberFormatException nfe) {
					xEmitTxt.setText("0");
					xEmit = 0;
				}
				try {
					yEmit = Integer.parseInt(yEmitTxt.getText());
				} catch (NumberFormatException nfe) {
					yEmitTxt.setText("0");
					yEmit = 0;
				}
				Point emit = new Point(xEmit,yEmit);
				if(validatePosition(emit)) {
					for(Emitters entry : apparati.keySet()) {
						if(entry.getPosition().equals(emit)) {
							if(apparati.get(entry)) {
								emitEnabled.setText(emitEnabled.getText().replace("(" + emit.x + " " + emit.y + ") " + entry.getmW() + " mW  " + entry.getMHz() + " MHz  [" + entry.getAngles().x + "°-" + entry.getAngles().y + "°]    ",""));
							} else {
								emitDisabled.setText(emitDisabled.getText().replace("(" + emit.x + " " + emit.y + ") " + entry.getmW() + " mW  " + entry.getMHz() + " MHz  [" + entry.getAngles().x + "°-" + entry.getAngles().y + "°]    ",""));
							}
							apparati.remove(entry);
							redraw((Graphics2D) drawPanel.getGraphics());
							return;
						}
					}
				}
				System.out.println("Impossibile cancellare un valore assente");
			}
		});	

		enableDisableEmitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xEmit, yEmit, angS, angE;
				float pow, freq;
				try {
					xEmit = Integer.parseInt(xEmitTxt.getText());
				} catch (NumberFormatException nfe) {
					xEmitTxt.setText("0");
					xEmit = 0;
				}
				try {
					yEmit = Integer.parseInt(yEmitTxt.getText());
				} catch (NumberFormatException nfe) {
					yEmitTxt.setText("0");
					yEmit = 0;
				}
				Point emit = new Point(xEmit,yEmit);
				if(validatePosition(emit)) {
					for(Emitters entry : apparati.keySet()) {
						if(entry.getPosition().equals(emit)) {
							boolean flag = !apparati.get(entry);
							apparati.replace(entry,flag);
							if(flag) {
								emitDisabled.setText(emitDisabled.getText().replace("(" + emit.x + " " + emit.y + ") " + entry.getmW() + " mW  " + entry.getMHz() + " MHz  [" + entry.getAngles().x + "°-" + entry.getAngles().y + "°]    ",""));
								emitEnabled.setText(emitEnabled.getText() + "(" + emit.x + " " + emit.y + ") " + entry.getmW() + " mW  " + entry.getMHz() + " MHz  [" + entry.getAngles().x + "°-" + entry.getAngles().y + "°]    ");
							} else {
								emitEnabled.setText(emitEnabled.getText().replace("(" + emit.x + " " + emit.y + ") " + entry.getmW() + " mW  " + entry.getMHz() + " MHz  [" + entry.getAngles().x + "°-" + entry.getAngles().y + "°]    ",""));
								emitDisabled.setText(emitDisabled.getText() + "(" + emit.x + " " + emit.y + ") " + entry.getmW() + " mW  " + entry.getMHz() + " MHz  [" + entry.getAngles().x + "°-" + entry.getAngles().y + "°]    ");
							}
							redraw((Graphics2D) drawPanel.getGraphics());
							return;
						}
					}
					for(Emitters entry : apparati.keySet()) { //controllo compenetrazione
						if(emit.distance(entry.getPosition())<DIM_SQUARE/2) {
							System.out.println("Gli emittenti devono essere distanziati di almeno " + DIM_SQUARE/2 + " centimetri");
							return;
						}
					}
					try {
						pow = Float.parseFloat(powEmitTxt.getText());
						if(pow>200 || pow<=0) {
							System.out.println("La potenza non deve superare i 200 milliWatt");
							return;
						}
					} catch (NumberFormatException nfe) {
						System.out.println("La potenza non deve superare i 200 milliWatt");
						return;
					}
					try {
						freq = Float.parseFloat(freqEmitterTxt.getText());
						if((freq < 5150 || freq > 5350)) {
							System.out.println("La frequenza del Wi-Fi all'interno di un edificio deve essere compresa fra 5150 e 5350 MegaHertz");
							return;
						}
					} catch (NumberFormatException nfe) {
						System.out.println("La frequenza del Wi-Fi all'interno di un edificio deve essere compresa fra 5150 e 5350 MegaHertz");
						return;
					}
					try {
						angS = Integer.parseInt(angStartEmitTxt.getText());
						if(angS < 0 || angS > 360) {
							System.out.println("Gli angoli devono avere valore compreso fra 0 e 360");
							return;
						}
					} catch (NumberFormatException nfe) {
						angStartEmitTxt.setText("0");
						angS = 0;
					}
					try {
						angE = Integer.parseInt(angEndEmitTxt.getText());
						if(angE < 0 || angE > 360) {
							System.out.println("Gli angoli devono avere valore compreso fra 0 e 360");
							return;
						}
					} catch (NumberFormatException nfe) {
						angEndEmitTxt.setText("360");
						angE = 0;
					}					
					Emitters emitter = new Emitters(emit, pow, freq, angS, angE);
					apparati.put(emitter, true);
					drawEmitter(drawPanel.getGraphics(),emitter);
					emitEnabled.setText(emitEnabled.getText() + "(" + emit.x + " " + emit.y + ") " + pow + " mW  " + freq + " MHz  [" + angS + "°-" + angE + "°]    ");
				}
			}
		});	

		rLow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				impactSel = BASSO;
			}
		});	
	
		rMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				impactSel = MEDIO;
			}
		});	
	
		rHigh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				impactSel = ALTO;
			}
		});	

		createWall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xWallS, yWallS, xWallE, yWallE;
				try {
					xWallS = Integer.parseInt(wallStartxTxt.getText());
				} catch (NumberFormatException nfe) {
					wallStartxTxt.setText("0");
					xWallS = 0;
				}
				try {
					yWallS = Integer.parseInt(wallStartyTxt.getText());
				} catch (NumberFormatException nfe) {
					wallStartyTxt.setText("0");
					yWallS = 0;
				}
				try {
					xWallE = Integer.parseInt(wallEndxTxt.getText());
				} catch (NumberFormatException nfe) {
					wallEndxTxt.setText("0");
					xWallE = 0;
				}
				try {
					yWallE = Integer.parseInt(wallEndyTxt.getText());
				} catch (NumberFormatException nfe) {
					wallEndyTxt.setText("0");
					yWallE = 0;
				}
				Line2D wall = new Line2D.Float(xWallS, yWallS, xWallE, yWallE);
				if(validateWallPosition(wall)) {
					for(Walls entry : planimetria.keySet()) { //controllo compenetrazione
						if(wall.intersectsLine(entry.getPosition()) && (!(wall.getX1() == wall.getX2() ^ entry.getPosition().getX1() == entry.getPosition().getX2())) &&
						(wall.relativeCCW(entry.getPosition().getP1()) + wall.relativeCCW(entry.getPosition().getP2()) + entry.getPosition().relativeCCW(wall.getP1()) + entry.getPosition().relativeCCW(wall.getP2()) != -2)) {
							System.out.println("I muri non possono compenetrarsi");
							return;
						}
					}
					Walls walls = new Walls(wall, impactSel);
					planimetria.put(walls, true);
					drawWall((Graphics2D) drawPanel.getGraphics(),walls);
					switch(impactSel) {
						case BASSO: wallEnabledL.setText(wallEnabledL.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    "); break;
						case MEDIO: wallEnabledM.setText(wallEnabledM.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    "); break;
						case ALTO: wallEnabledH.setText(wallEnabledH.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    "); break;
						default: return;
					}
				}
			}
		});	

		deleteWall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xWallS, yWallS, xWallE, yWallE;
				try {
					xWallS = Integer.parseInt(wallStartxTxt.getText());
				} catch (NumberFormatException nfe) {
					wallStartxTxt.setText("0");
					xWallS = 0;
				}
				try {
					yWallS = Integer.parseInt(wallStartyTxt.getText());
				} catch (NumberFormatException nfe) {
					wallStartyTxt.setText("0");
					yWallS = 0;
				}
				try {
					xWallE = Integer.parseInt(wallEndxTxt.getText());
				} catch (NumberFormatException nfe) {
					wallEndxTxt.setText("0");
					xWallE = 0;
				}
				try {
					yWallE = Integer.parseInt(wallEndyTxt.getText());
				} catch (NumberFormatException nfe) {
					wallEndyTxt.setText("0");
					yWallE = 0;
				}
				Line2D wall = new Line2D.Float(xWallS, yWallS, xWallE, yWallE);
				for(Walls entry : planimetria.keySet()) {
					if(entry.getPosition().getP1().equals(wall.getP1()) && entry.getPosition().getP2().equals(wall.getP2())) {
						if(planimetria.get(entry)) {
							switch(entry.getImpact()) {
								case BASSO: wallEnabledL.setText(wallEnabledL.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ","")); break;
								case MEDIO: wallEnabledM.setText(wallEnabledM.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ","")); break;
								case ALTO: wallEnabledH.setText(wallEnabledH.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ","")); break;
								default: return;
							}
						} else {
							switch(entry.getImpact()) {
								case BASSO: wallDisabledL.setText(wallDisabledL.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ","")); break;
								case MEDIO: wallDisabledM.setText(wallDisabledM.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ","")); break;
								case ALTO: wallDisabledH.setText(wallDisabledH.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ","")); break;
								default: return;
							}
						}
						planimetria.remove(entry);
						redraw((Graphics2D) drawPanel.getGraphics());
						return;
					}
				}
				System.out.println("Impossibile cancellare un valore assente");
			}
		});	

		enableWall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xWallS, yWallS, xWallE, yWallE;
				try {
					xWallS = Integer.parseInt(wallStartxTxt.getText());
				} catch (NumberFormatException nfe) {
					wallStartxTxt.setText("0");
					xWallS = 0;
				}
				try {
					yWallS = Integer.parseInt(wallStartyTxt.getText());
				} catch (NumberFormatException nfe) {
					wallStartyTxt.setText("0");
					yWallS = 0;
				}
				try {
					xWallE = Integer.parseInt(wallEndxTxt.getText());
				} catch (NumberFormatException nfe) {
					wallEndxTxt.setText("0");
					xWallE = 0;
				}
				try {
					yWallE = Integer.parseInt(wallEndyTxt.getText());
				} catch (NumberFormatException nfe) {
					wallEndyTxt.setText("0");
					yWallE = 0;
				}
				Line2D wall = new Line2D.Float(xWallS, yWallS, xWallE, yWallE);
				if(validateWallPosition(wall)) {
					for(Walls entry : planimetria.keySet()) {
						if(entry.getPosition().getP1().equals(wall.getP1()) && entry.getPosition().getP2().equals(wall.getP2())) {
							boolean flag = !(planimetria.get(entry));
							planimetria.replace(entry, flag);
							if(flag) {
								drawWall(drawPanel.getGraphics(),entry);
								switch(entry.getImpact()) {
									case BASSO:
										wallDisabledL.setText(wallDisabledL.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ",""));
										wallEnabledL.setText(wallEnabledL.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ");
										break; 
									case MEDIO:
										wallDisabledM.setText(wallDisabledM.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ",""));
										wallEnabledM.setText(wallEnabledM.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ");
										break;
									case ALTO:
										wallDisabledH.setText(wallDisabledH.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ",""));
										wallEnabledH.setText(wallEnabledH.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ");
										break; 
									default: return;
								}
							} else {
								redraw(drawPanel.getGraphics());
								switch(entry.getImpact()) {
									case BASSO:
										wallEnabledL.setText(wallEnabledL.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ",""));
										wallDisabledL.setText(wallDisabledL.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ");
										break; 
									case MEDIO:
										wallEnabledM.setText(wallEnabledM.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ",""));
										wallDisabledM.setText(wallDisabledM.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ");
										break;
									case ALTO:
										wallEnabledH.setText(wallEnabledH.getText().replace("(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ",""));
										wallDisabledH.setText(wallDisabledH.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    ");
										break; 
									default: return;
								}
							}
							return;
						}
					}
					for(Walls entry : planimetria.keySet()) { //controllo compenetrazione
						if(wall.intersectsLine(entry.getPosition()) &&
						((wall.getP1().distance(entry.getPosition().getP1()) + wall.getP2().distance(entry.getPosition().getP1()) == wall.getP1().distance(wall.getP2()) ||
						(wall.getP1().distance(entry.getPosition().getP2()) + wall.getP2().distance(entry.getPosition().getP2()) == wall.getP1().distance(wall.getP2()))))) {
							System.out.println("I muri non possono compenetrarsi");
							return;
						}
					}
					Walls walls = new Walls(wall, impactSel);
					planimetria.put(walls, true);
					drawWall((Graphics2D) drawPanel.getGraphics(),walls);
					switch(impactSel) {
						case BASSO: wallEnabledL.setText(wallEnabledL.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    "); break; 
						case MEDIO: wallEnabledM.setText(wallEnabledM.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    "); break;
						case ALTO: wallEnabledH.setText(wallEnabledH.getText() + "(" + xWallS + " " + yWallS + ")-(" + xWallE + " " + yWallE  + ")    "); break;
						default: return;
					}
				}
			}
		});	

		createUtilizer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xUtil, yUtil;
				try {
					xUtil = Integer.parseInt(xUtilTxt.getText());
				} catch (NumberFormatException nfe) {
					xUtilTxt.setText("0");
					xUtil = 0;
				}
				try {
					yUtil = Integer.parseInt(yUtilTxt.getText());
				} catch (NumberFormatException nfe) {
					yUtilTxt.setText("0");
					yUtil = 0;
				}
				Point util = new Point(xUtil,yUtil);
				if(validatePosition(util)) {
					for(Utilizers entry : consumatori.keySet()) {
						if(entry.getPosition().equals(util)) {
							System.out.println("Valore già presente");
							return;
						}
					}
					Utilizers utilizzatore = new Utilizers(util);
					consumatori.put(utilizzatore, true);
					drawUtilizer((Graphics2D) drawPanel.getGraphics(),util);
					utilEnabled.setText(utilEnabled.getText() + "(" + util.x + " " + util.y + ")    ");
				}
			}
		});	

		deleteUtilizer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xUtil, yUtil;
				try {
					xUtil = Integer.parseInt(xUtilTxt.getText());
				} catch (NumberFormatException nfe) {
					xUtilTxt.setText("0");
					xUtil = 0;
				}
				try {
					yUtil = Integer.parseInt(yUtilTxt.getText());
				} catch (NumberFormatException nfe) {
					yUtilTxt.setText("0");
					yUtil = 0;
				}
				Point util = new Point(xUtil,yUtil);
				if(validatePosition(util)) {
					for(Utilizers entry : consumatori.keySet()) {
						if(entry.getPosition().equals(util)) {
							if(consumatori.get(entry)) {
								utilEnabled.setText(utilEnabled.getText().replace("(" + util.x + " " + util.y + ")    ",""));
							} else {
								utilDisabled.setText(utilDisabled.getText().replace("(" + util.x + " " + util.y + ")    ",""));
							}
							consumatori.remove(entry);
							redraw((Graphics2D) drawPanel.getGraphics());
							return;
						}
					}
				}
				System.out.println("Impossibile cancellare un valore assente");
			}
		});
		
		enableUtilizer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xUtil, yUtil;
				try {
					xUtil = Integer.parseInt(xUtilTxt.getText());
				} catch (NumberFormatException nfe) {
					xUtilTxt.setText("0");
					xUtil = 0;
				}
				try {
					yUtil = Integer.parseInt(yUtilTxt.getText());
				} catch (NumberFormatException nfe) {
					yUtilTxt.setText("0");
					yUtil = 0;
				}
				Point util = new Point(xUtil,yUtil);
				if(validatePosition(util)) {
					for(Utilizers entry : consumatori.keySet()) {
						if(entry.getPosition().equals(util)) {
							boolean flag = !(consumatori.get(entry));
							consumatori.replace(entry, flag);
							if(flag) {
								drawUtilizer((Graphics2D) drawPanel.getGraphics(),util);
								utilEnabled.setText(utilEnabled.getText() + "(" + util.x + " " + util.y + ")    ");
								utilDisabled.setText(utilDisabled.getText().replace("(" + util.x + " " + util.y + ")    ",""));
							} else {
								redraw((Graphics2D) drawPanel.getGraphics());
								utilDisabled.setText(utilDisabled.getText() + "(" + util.x + " " + util.y + ")    ");
								utilEnabled.setText(utilEnabled.getText().replace("(" + util.x + " " + util.y + ")    ",""));
							}
							return;
						}
					}
					Utilizers utilizzatore = new Utilizers(util);
					consumatori.put(utilizzatore, true);
					drawUtilizer((Graphics2D) drawPanel.getGraphics(),util);
					utilEnabled.setText(utilEnabled.getText() + "(" + util.x + " " + util.y + ")    ");
				}
			}
		});	
		
		generateResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double int_tot, int_em, attenuazione, angolo, radStart, radEnd;
				resultPanel.getGraphics().clearRect(0, 0, WIDTH, HEIGHT);
				//coloro la mappa come prima cosa cosicché muri ed emittenti sovrascrivano i colori e non viceversa
				if(apparati.containsValue(true)) {
					for (i = DIM_SQUARE/2; i < HEIGHT; i += DIM_SQUARE) {
						for (j = DIM_SQUARE/2; j < WIDTH; j += DIM_SQUARE) {
							int_tot = 0;
							for(Map.Entry<Emitters,Boolean> entryE : apparati.entrySet()) {
								if(!entryE.getValue()) { continue; }
								int_em = entryE.getKey().getmW();
								if((entryE.getKey().getAngles().y - entryE.getKey().getAngles().x)<360) {
									angolo = (double) Math.atan2(i - entryE.getKey().getPosition().y, j - entryE.getKey().getPosition().x);
									if(angolo<0) {
										angolo += 2*Math.PI;
									}
									radStart = Math.toRadians(entryE.getKey().getAngles().x);
									radEnd = Math.toRadians(entryE.getKey().getAngles().y);
									if(radEnd<radStart) {
										radEnd += 2*Math.PI;
									}
									if(radStart > angolo || radEnd < angolo) {
										continue;
									}
									int_em = int_em * (2*Math.PI) / (radEnd - radStart);		//le antenne direzionali hanno guadagno sul fronte
								}																//1 elemento=1 cm; divido per 100 per convertire in metri
								
								attenuazione = -27.55 + 20*(Math.log10(Math.sqrt(Math.pow(((double) i- (double) entryE.getKey().getPosition().y)/100,2)+Math.pow(((double) j- (double) entryE.getKey().getPosition().x)/100,2))) + Math.log10((double) entryE.getKey().getMHz()));
								for(Map.Entry<Walls,Boolean> entryM : planimetria.entrySet()) {
									if(!entryM.getValue()) { continue; }
									
									if(Line2D.linesIntersect(entryM.getKey().getPosition().getX1(),entryM.getKey().getPosition().getY1(),entryM.getKey().getPosition().getX2(),entryM.getKey().getPosition().getY2(),entryE.getKey().getPosition().x,-entryE.getKey().getPosition().y,j,i)) {
										switch(entryM.getKey().getImpact()) {
											case BASSO: int_em /= 2; break;
											case MEDIO: int_em /= 10; break;
											case ALTO: int_em /= 100; break;
											default: continue;
										}
									}
								}
								int_em = 10*Math.log10(int_em) - attenuazione - MIN_INT;
								if(int_em > 0) {
									int_tot += int_em;
								}
							}
							if(int_tot >= 0) {
							    risultato = (int) Math.floor(int_tot/15);
							    if(risultato>4) {risultato=4;}
							    paintComponent(resultPanel.getGraphics());
							}
						}
					}
				}
				result(resultPanel.getGraphics());	//sarebbe meglio un modo per copiare la mappa con background trasparente
			}
		});
		
    //Setting del frame
		resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setSize(1200, 1030);
		final JPanel coverLightPanel = new JPanel(new GridLayout(2, 2));
		this.getContentPane().add(coverLightPanel);
		coverLightPanel.add(captionPanel);
		coverLightPanel.add(buttonsPanel);
		coverLightPanel.add(canvasPanel);
		coverLightPanel.add(resultPanel);
	}

	public static void main(String[] args) {
	    new GUI();
	}
}
