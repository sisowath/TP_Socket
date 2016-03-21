package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class MathConquerClient {
    private JFrame frame = new JFrame("Math Conquer");
    private JPanel panel;
    private WaitingScreen ws;
    private WinnerScreen winnerScreen;
    private LoserScreen loserScreen;
    private JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel lblCountDown = new JLabel("Time : ", SwingConstants.CENTER);
    private JButton buttons[];
    private Socket socket;
    private String couleur;
    private BufferedReader in;
    private PrintWriter out;
    private static int PORT = 12345;
    private static int CASES = 25;        
    
    public MathConquerClient(String serverAddress) throws Exception {
        socket = new Socket(JOptionPane.showInputDialog(null, "Veuillez saisir l'IP :", "Question", JOptionPane.QUESTION_MESSAGE), Integer.parseInt(JOptionPane.showInputDialog(null, "Veuillez saisir le port : ", "Question", JOptionPane.QUESTION_MESSAGE)));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "South");
        panel = new JPanel(new GridLayout(5, 5));        
        buttons = new JButton[CASES];
        for(int i=0; i<CASES; i++) {
            buttons[i] = new JButton("Case " + (i+1));
            buttons[i].setFont(new Font("ARIAL", Font.PLAIN, 12));
            buttons[i].setActionCommand(String.valueOf(i));
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int operande1 = new Random().nextInt(10);
                    int operande2 = new Random().nextInt(10);
                    int resultat = 0;
                    int reponse = 0;
                    switch(new Random().nextInt((3-1)+1) + 1) {
                        case 1 :
                            resultat = operande1 + operande2;
                            reponse = Integer.parseInt( JOptionPane.showInputDialog(null, operande1 + " + " + operande2 + " = ?", "Question", JOptionPane.QUESTION_MESSAGE) );
                            break;
                        case 2 :
                            resultat = operande1 - operande2;
                            reponse = Integer.parseInt( JOptionPane.showInputDialog(null, operande1 + " - " + operande2 + " = ?", "Question", JOptionPane.QUESTION_MESSAGE) );
                            break;
                        case 3 :
                            resultat = operande1 * operande2;
                            reponse = Integer.parseInt( JOptionPane.showInputDialog(null, operande1 + " * " + operande2 + " = ?", "Question", JOptionPane.QUESTION_MESSAGE) );
                            break;
                    }
                    if(resultat == reponse) {
                        //JOptionPane.showMessageDialog(null, "BRAVO !", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        out.println("MOVE");
                        out.flush();
                        out.println(couleur);
                        out.flush();
                        out.println(String.valueOf(e.getActionCommand()));
                        out.flush();
                        System.out.println("Message sent to server : [" + couleur + " :: " + String.valueOf(e.getActionCommand()) + "].");
                    } else {
                        //JOptionPane.showMessageDialog(null, "ECHEC !", "FAIL", JOptionPane.ERROR_MESSAGE);
                        buttons[Integer.parseInt(e.getActionCommand())].setBackground(Color.GRAY);
                        buttons[Integer.parseInt(e.getActionCommand())].setFont(new Font("ARIAL", Font.CENTER_BASELINE, 14));
                        buttons[Integer.parseInt(e.getActionCommand())].setForeground(Color.RED);
                    }
                }                
            });
            panel.add(buttons[i]);
        }
        frame.getContentPane().add(panel, "Center");
        panel.setVisible(false);        
        ecranAttente();
    }
    
    public void play() throws Exception {        
        try {            
            String reponse ,c, m;
            reponse = in.readLine();
            if (reponse.startsWith("WELCOME")) {
                couleur = reponse.substring(8);
                frame.setTitle("Math Conquer - " + couleur);
            }
            while (true) {
                reponse = in.readLine();
                if (reponse.startsWith("READY")){
                    frame.remove(ws);
                    frame.add(panel, BorderLayout.CENTER);
                    lblCountDown.setFont(new Font("ARIAL", Font.BOLD, 20));
                    lblCountDown.setOpaque(true);
                    lblCountDown.setBackground(Color.WHITE);
                    lblCountDown.setForeground(Color.BLUE);
                    frame.add(lblCountDown, BorderLayout.NORTH);
                    frame.setSize(475, 325);
                    //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    panel.setVisible(true);
                    frame.setTitle("Math & Conquer - " + couleur);
                    frame.revalidate();
                    frame.repaint();
                } else if(reponse.startsWith("TIME")) {
                    int temp = Integer.parseInt( in.readLine() );
                    lblCountDown.setText("Time : " + temp);
                }else if (reponse.startsWith("MOVE")){
                    c = in.readLine(); 
                    m = in.readLine();
                    System.out.println("Message received : [" + c + " :: " + m + "].");
                    switch(c) {
                        case "RED" :
                        case "ROUGE" :
                            buttons[Integer.parseInt(m)].setBackground(Color.RED);
                            buttons[Integer.parseInt(m)].setEnabled(false);
                            break;
                        case "ORANGE" :
                            buttons[Integer.parseInt(m)].setBackground(Color.ORANGE);
                            buttons[Integer.parseInt(m)].setEnabled(false);
                            break;
                        case "YELLOW" :
                        case "JAUNE" :
                            buttons[Integer.parseInt(m)].setBackground(Color.YELLOW);
                            buttons[Integer.parseInt(m)].setEnabled(false);
                            break;
                        case "GREEN" :
                        case "VERT" :
                            buttons[Integer.parseInt(m)].setBackground(Color.GREEN);
                            buttons[Integer.parseInt(m)].setEnabled(false);
                            break;
                        case "BLUE" :
                        case "BLEU" :
                            buttons[Integer.parseInt(m)].setBackground(Color.BLUE);
                            buttons[Integer.parseInt(m)].setEnabled(false);
                            break;
                        case "PINK" :
                        case "ROSE" :
                            buttons[Integer.parseInt(m)].setBackground(Color.PINK);
                            buttons[Integer.parseInt(m)].setEnabled(false);
                            break;
                        case "BLACK" :
                        case "NOIR" :
                            buttons[Integer.parseInt(m)].setBackground(Color.BLACK);
                            buttons[Integer.parseInt(m)].setEnabled(false);
                            break;
                    }
                    buttons[Integer.parseInt(m)].setFont(new Font("ARIAL", Font.BOLD, 14));
                    buttons[Integer.parseInt(m)].setForeground(Color.WHITE);
                } else if (reponse.startsWith("WINNER")) {
                    c = in.readLine();
                    if (c.equals(couleur)) {
                        messageLabel.setText("Vous avez gagné");
                        ecranGagnant();
                    } else if (c.equals("TIE")) {
                        messageLabel.setText("Partie nulle");
                    } else {
                        messageLabel.setText("Vous avez perdu");
                        ecranPerdant();
                    }
                    break;
                } else if (reponse.startsWith("MESSAGE")){
                    messageLabel.setFont(new Font("ARIAL", Font.BOLD, 20));
                    messageLabel.setBorder(new LineBorder(Color.YELLOW, 2));
                    messageLabel.setOpaque(true);
                    messageLabel.setBackground(Color.WHITE);
                    messageLabel.setForeground(Color.ORANGE);
                    messageLabel.setText(reponse.substring(8));
                }                
            }
            out.println("QUIT");
        } finally {
            socket.close();
        }
    }
    private boolean wantsToPlayAgain() {
        int reponse = JOptionPane.showConfirmDialog(frame, "Voulez-vous jouer une autre partie?", "Math Conquer", JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return reponse == JOptionPane.YES_OPTION;
    }
    public void ecranAttente(){
        frame.remove(panel);
        ws = new MathConquerClient.WaitingScreen();
        frame.add(ws, BorderLayout.CENTER);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ws.run();
            }
        });
        frame.setResizable(false);
        frame.revalidate();
        frame.repaint();
    }
    public void ecranGagnant(){
        frame.remove(panel);
        winnerScreen = new MathConquerClient.WinnerScreen();
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.add(winnerScreen, BorderLayout.CENTER);
        new Thread(new Runnable() {
            @Override
            public void run() {
                winnerScreen.run();
            }
        });
        frame.revalidate();
        frame.repaint();
    }
    public void ecranPerdant(){
        frame.remove(panel);
        loserScreen = new MathConquerClient.LoserScreen();
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.add(loserScreen, BorderLayout.CENTER);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loserScreen.run();
            }
        });
        frame.revalidate();
        frame.repaint();
    }
    public static void main(String[] args) throws Exception {
        while(true) {
            String serverAddress = (args.length == 0) ? "localhost" : args[1];
            MathConquerClient client = new MathConquerClient(serverAddress);
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setSize(400, 330);
            client.frame.setVisible(true);
            client.frame.setLocationRelativeTo(null);
            client.play();
            if (!client.wantsToPlayAgain()) {
                break;
            }
        }
    }
    class WaitingScreen extends JPanel implements Runnable {
        Image image;  
        public WaitingScreen() {
            image = Toolkit.getDefaultToolkit().createImage("src/images/gangnamStyleWaiting.gif");
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            }
        }
        public void run() {
            while(true) {
                try {
                    this.repaint();
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MathConquerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }
    class WinnerScreen extends JPanel implements Runnable {
        Image image;
        public WinnerScreen() {
            image = Toolkit.getDefaultToolkit().createImage("src/images/winner.gif");
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            }
        }
        public void run() {
            while(true) {
                try {
                    this.repaint();
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MathConquerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } 
    }
    class LoserScreen extends JPanel implements Runnable {
        Image image;
        public LoserScreen() {
            image = Toolkit.getDefaultToolkit().createImage("src/images/game-over.gif");
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            }
        }
        public void run() {
            while(true) {
                try {
                    this.repaint();
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MathConquerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } 
    }
}