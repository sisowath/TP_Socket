/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
/**
 *
 * @author usager
 */
public class MathConquerServer {
    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(Integer.parseInt( JOptionPane.showInputDialog(null, "Veuillez saisir un port pour le serveur : ", "Question", JOptionPane.QUESTION_MESSAGE)));
        JLabel lbl = new JLabel("Le serveur est à l'écoute sur le port #" + listener.getLocalPort(), SwingConstants.CENTER);
        lbl.setFont(new Font("ARIAL", Font.BOLD, 14));
        lbl.setOpaque(true);
        lbl.setBorder(new LineBorder(Color.BLUE, 2));
        lbl.setBackground(Color.WHITE);
        lbl.setForeground(Color.ORANGE);
        JFrame frame = new JFrame("Math & Conquer - Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(340, 360);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        ImageIcon icon = new ImageIcon("src/images/server-icon.png");
        JPanel panel = new JPanel();
        panel.add(new JLabel(icon));
        frame.add(panel);
        frame.add(lbl, BorderLayout.NORTH);        
        frame.revalidate();
        frame.repaint();
        System.out.println("MathConquer is up!");
        try {
            while(true) {
                Game game = new Game();
                Game.Player player1 = game.new Player (listener.accept(), "RED");
                Game.Player player2 = game.new Player (listener.accept(), "YELLOW");
                player1.start();
                player2.start();
            }
        } finally {
            listener.close();
        }
    }
}
class Game {
    private static int CASES = 25;
    private String[] board = new String[CASES];
    private HashSet<PrintWriter> writer = new HashSet<PrintWriter>();
    
    public boolean isWinner() {
        for (String s:board)
        {
            if (s == null)
            {
                return false;
            }            
        }
        return true;
    }
    class Player extends Thread {
        String color;           
        Socket socket;
        BufferedReader input;
        PrintWriter output;
        int nbrSecondes = 120;// 2 minutes
        
        public Player(Socket socket, String color) {
            this.socket = socket;
            this.color = color;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                writer.add(output);
                output.println("WELCOME " + color);
                output.println("MESSAGE En attente d'un joueur");
            }
            catch (IOException e)
            {
                System.out.println("Joueur déconnecté: " + e);
            }
        }
        public void run() {
            try {                
                output.println("MESSAGE Partie commencée");
                output.println("READY");                
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if(nbrSecondes == 0) {
                            timer.cancel();
                            int red = 0, yellow = 0;                            
                            for (String s:board) {
                                switch (s) {
                                    case "RED" : 
                                        red++;
                                        break;
                                    case "YELLOW" : 
                                        yellow++;
                                        break;
                                }
                            }
                            for (PrintWriter p:writer) {
                                p.println("WINNER");
                                p.flush();
                                if (red > yellow) {
                                    p.println("RED");
                                    p.flush();
                                } else if (red == yellow) {
                                    p.println("TIE");
                                    p.flush();
                                } else {
                                    p.println("YELLOW");
                                    p.flush();
                                }
                            }                        
                        } else {
                            output.println("TIME");
                            output.flush();
                            output.println(String.valueOf(--nbrSecondes));
                            output.flush();
                        }   
                    }
                }, 1000, 1000);
                while (true) {                                       
                    String command = input.readLine();
                    if (command.startsWith("MOVE"))
                    {
                        String c = input.readLine(); 
                        String m = input.readLine();
                        System.out.println("Message received : [" + c + " :: " + m + "].");               
                        if(c == null || m == null) {
                            return;
                        }
                        board[Integer.parseInt(m)] = c;
                        for (PrintWriter p:writer)
                        {
                            p.println("MOVE");
                            p.flush();
                            p.println(c);
                            p.flush();
                            p.println(m);
                            p.flush();
                        }
                        if (isWinner())
                        {
                            int red = 0, yellow = 0;                            
                            for (String s:board)
                            {
                                switch (s)
                                {
                                    case "RED" : red++;
                                                 break;
                                    case "YELLOW" : yellow++;
                                                    break;
                                }
                            }
                            for (PrintWriter p:writer)
                            {
                                p.println("WINNER");
                                p.flush();
                                if (red > yellow)
                                {
                                    p.println("RED");
                                    p.flush();
                                } else if (red == yellow) {
                                    p.println("TIE");
                                    p.flush();
                                } else {
                                    p.println("YELLOW");
                                    p.flush();
                                }
                            }
                        }                        
                    } else if (command.startsWith("QUIT"))
                    {
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Joueur déconnecté: " + e);
            } finally {
                try {
                    socket.close();
                }  catch (IOException e) {}
            }
        }
    }
}

