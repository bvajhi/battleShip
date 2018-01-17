//package com.example.java;

import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.io.*;


public class mainGrid extends GUI {


    private boolean running;
    private boolean serverMode = false;
    private boolean clientMode = false;
    private JButton ships[];
    private int port;
    private boolean myTurn = false;
    private String shipName[] = {"Aircraft Carrier", "Battle Ship", "Destroyer", "Submarine", "Patrol Boat"};
    private JButton makeConnection;
    private JButton ready;
    private JButton deploy;
     JButton replay;
    private String slectedShip = null;
    private boolean playerReady = false;
    private boolean opponentReady = false;
    private int numAttacks=0;
    private int hits= 0;




    //pieces for the game...
    private piece aircraft = new piece(type.aircraftCarrier);
    private piece battel = new piece(type.battleShip);
    private piece sub = new piece(type.submarine);
    private piece destroyer = new piece(type.destroyer);
    private piece petrol = new piece(type.patrolBoats);
    private int numberOfBoatsOnBoard=0;
    private JTextArea status;

    boolean serverContinue;
    ServerSocket serverSocket;


    public mainGrid() {

        super("Main Board");

        JMenu mainMenu;
        JMenuItem exit;
        JMenuItem about;
        JMenu help;
        JMenuItem stats;
        JMenu connection;
        JMenuItem gameHelp;
        JMenuItem connectionHelp;
        JRadioButtonMenuItem server = new JRadioButtonMenuItem("Server Mode");
        JRadioButtonMenuItem client = new JRadioButtonMenuItem("Client Mode");
        JMenuItem establishConnection;


        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {

                buttons[row][col].addActionListener(new buttonActionListener());
                //  buttons[row][col].setIcon(new ImageIcon("batt100.gif"));
            }

        }


        ships = new JButton[5];

        //Create buttons for ships..
        for (int i = 0; i < 5; i++) {

            ships[i] = new JButton(shipName[i]);
            ships[i].addActionListener(new shipButtonActionListener());


        }


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 2));
        //Create a side panel to put the ship buttons

        JPanel panel2 = new JPanel();
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(8, 1));

        //add all the ship buttons to the side panel
        for (int i = 0; i < 5; i++) {

            sidePanel.add(ships[i]);

        }


        ready = new JButton("Ready");

        //this.setResizable(false);

        makeConnection = new JButton("Establish Connection");
        makeConnection.addActionListener(new establishConnectionListener());
        sidePanel.add(makeConnection);

        deploy = new JButton("Deploy");
        deploy.addActionListener(new deployActionListener());
        sidePanel.add(deploy);


        ready = new JButton("Ready");
        sidePanel.add(ready);
        ready.addActionListener(new readyActionListener());
        replay= new JButton("Replay");
        sidePanel.add(replay);
        replay.addActionListener(new replayEventListener());
        replay.setEnabled(false);
        status = new JTextArea();
        status.setEditable(false);
        //panel2.add(status);

        bottomPanel.add(sidePanel);
        bottomPanel.add(new JScrollPane(status));


       

      
        pane.add(bottomPanel, BorderLayout.SOUTH);


        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);

        mainMenu = new JMenu("Main Menu");
        exit = new JMenuItem("Exit");
        exit.addActionListener(new exitActionListener());
        mainMenu.add(exit);


        about = new JMenuItem("About");
        about.addActionListener(new aboutActionListener());
        mainMenu.add(about);

        stats = new JMenuItem("Stats");
        stats.addActionListener(new statsActionListener());
        mainMenu.add(stats);

        bar.add(mainMenu);

        help = new JMenu("Help");

        gameHelp = new JMenuItem("Game Help");
        gameHelp.addActionListener(new gameHelpActionListener());
        help.add(gameHelp);

        connectionHelp = new JMenuItem("Connection Help");
        connectionHelp.addActionListener(new connectionHelpActionListener());
        help.add(connectionHelp);
        help.add(connectionHelp);
        bar.add(help);

        connection = new JMenu("Connection Mode");

        server.addActionListener(new serverActionListener());
        client.addActionListener(new clientActionListener());
        ButtonGroup grp = new ButtonGroup();
        grp.add(server);
        grp.add(client);

        connection.add(server);

        connection.add(client);



        bar.add(connection);

        running = false;
        String machineAddress = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            machineAddress = addr.getHostAddress();
        } catch (UnknownHostException e) {
            machineAddress = "127.0.0.1";
        }


        setSize(500, 650);
        setVisible(true);





    }

    public  void updateNumAttacks(){

        numAttacks++;
    }

    public void updateHits(){
        hits++;

    }


    public int getNumAttacks() {
        return numAttacks;
    }
    public int getHits(){
        return hits;
    }

    public  boolean isPlayerReady(){
        return playerReady;

}
public boolean isOpponentReady(){
    return opponentReady;
}
    class readyActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(numberOfBoatsOnBoard >= 5)
            {
                String message = "ready";
                playerReady=true;
                Main.getScore().doSendMessage(message);
            }
            else{
                JOptionPane.showMessageDialog(pane, "you didn't put all of your ships on the board!");
            }

        }
    }




    public void createClientSideServer() {


        if (running == false)

        {
            new ConnectionThread(Main.getMainBoard());
            // Main.connectToserver();


            try {
                Thread.sleep(1000);

                System.out.println("in createClientSideServer port num is:  " + Main.getPortNumber());
                String message = Main.getPortNumber() + " " + InetAddress.getLocalHost().getHostAddress();
                Main.getScore().doSendMessage(message);

            } catch (Exception i) {

                JOptionPane.showMessageDialog(pane, "can not get local host");
            }
            // Main.score.doManageConnection();
        } else {
            serverContinue = false;
            //ssButton.setText ("Start Listening");
            //portInfo.setText (" Not Listening ");
        }
    }


    public void refreshShipButtons(){

        for (int i=0; i < 5; i++){

            ships[i].setEnabled(true);
            ships[i].setBackground(null);


        }


    }

    class replayEventListener implements  ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            Main.getScore().doSendMessage("Replay");

        }
    }

    class shipButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {


            pane.validate();

            //reset all the buttons so none is highlighted

            for (int row = 0; row < 5; row++) {

                ships[row].setBackground(null);
                ships[row].setOpaque(true);


            }

            JButton temp = (JButton) e.getSource();

            for (int row = 0; row < 5; row++) {

                if (temp.equals(ships[row])) {
                    slectedShip = ships[row].getText();
                    ships[row].setBackground(Color.RED);
                    ships[row].setOpaque(true);

                } else {

                    ships[row].setEnabled(false);

                }
            }



            if(slectedShip.equalsIgnoreCase("battleship"))
                status.insert(">You selected " + slectedShip + " now select two\n boxes in the same row or column\nthat have two boxes inbetween", 0);
            if(slectedShip.equalsIgnoreCase("petrol"))
                status.insert(">You selected " + slectedShip + " now select two\n boxes in the same row or column\nare right next to each other", 0);
            if(slectedShip.equalsIgnoreCase("battleship"))
                status.insert(">You selected " + slectedShip + " now select two\n boxes in the same row or column\nthat have two boxes inbetween", 0);

        }


    }


    class deployActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //int row;
            //int col;

            if (slectedShip.equals("Aircraft Carrier")) {
                deployCarrier();
            }

            else if (slectedShip.equalsIgnoreCase("Battle Ship")){
                deployBattle();
            }
            else if (slectedShip.equalsIgnoreCase("Destroyer")){
                deployDestroyer();
            }
            else if (slectedShip.equalsIgnoreCase("Submarine")){
                deploySub();
            }
            else if (slectedShip.equalsIgnoreCase("Patrol Boat")){
                deployPatrol();
            }
            refreshShipButtons();
            slectedShip=null;
            numberOfBoatsOnBoard++;

        }
        public  void deployPatrol() {
            if (petrol.getStartX() == petrol.getEndX()) {
                if (petrol.getStartY() - petrol.getEndY() == 1) {


                    buttons[petrol.getEndX()][petrol.getEndY() ].setIcon(new ImageIcon("batt1.gif"));
                    buttons[petrol.getEndX()][petrol.getEndY() ].setBoatPieceType("EH");
                    buttons[petrol.getStartX()][petrol.getStartY()].setIcon(new ImageIcon("batt5.gif"));
                    buttons[petrol.getStartX()][petrol.getStartY()].setBoatPieceType("SH");


                }


                if (petrol.getStartY() - petrol.getEndY() == -1) {
                    buttons[petrol.getEndX()][petrol.getEndY() ].setIcon(new ImageIcon("batt5.gif"));
                    buttons[petrol.getEndX()][petrol.getEndY() ].setBoatPieceType("SH");
                    buttons[petrol.getStartX()][petrol.getStartY()].setIcon(new ImageIcon("batt1.gif"));
                    buttons[petrol.getStartX()][petrol.getStartY()].setBoatPieceType("EH");


                }

            }
            if (petrol.getStartY() == petrol.getEndY()) {

                if (petrol.getStartX() - petrol.getEndX() == 1) {
                    buttons[petrol.getEndX()][petrol.getEndY()].setIcon(new ImageIcon("batt6.gif"));
                    buttons[petrol.getEndX()][petrol.getEndY()].setBoatPieceType("EV");
                    buttons[petrol.getStartX()][petrol.getStartY()].setIcon(new ImageIcon("batt10.gif"));
                    buttons[petrol.getStartX()][petrol.getStartY()].setBoatPieceType("SV");


                }
                if (petrol.getStartX() - petrol.getEndX() == -1) {

                    buttons[petrol.getEndX()][petrol.getEndY()].setIcon(new ImageIcon("batt10.gif"));
                    buttons[petrol.getEndX()][petrol.getEndY()].setBoatPieceType("SV");
                    buttons[petrol.getStartX()][petrol.getStartY()].setIcon(new ImageIcon("batt6.gif"));
                    buttons[petrol.getStartX()][petrol.getStartY()].setBoatPieceType("EV");


                }


            }


        }

        public void  deploySub(){
            if (sub.getStartX() == sub.getEndX()) {
                if (sub.getStartY() - sub.getEndY() == 2) {
                    for (int col = sub.getEndY(); col <= sub.getStartY(); col++) {
                        if (col == sub.getEndY()) {

                            buttons[sub.getStartX()][col].setIcon(new ImageIcon("batt1.gif"));
                            buttons[sub.getStartX()][col].setBoatPieceType("EH");
                        } else if (col == sub.getStartY()) {

                            buttons[sub.getStartX()][col].setIcon(new ImageIcon("batt5.gif"));
                            buttons[sub.getStartX()][col].setBoatPieceType("SH");
                        } else {
                            buttons[sub.getStartX()][col].setIcon(new ImageIcon("batt3.gif"));
                            buttons[sub.getStartX()][col].setBoatPieceType("MH");
                        }


                    }

                }
                if (sub.getStartY() - sub.getEndY() == -2) {
                    for (int col = sub.getStartY(); col <= sub.getEndY(); col++) {
                        if (col == sub.getEndY()) {

                            buttons[sub.getStartX()][col].setIcon(new ImageIcon("batt5.gif"));
                            buttons[sub.getStartX()][col].setBoatPieceType("SH");
                        } else if (col == sub.getStartY()) {

                            buttons[sub.getStartX()][col].setIcon(new ImageIcon("batt1.gif"));
                            buttons[sub.getStartX()][col].setBoatPieceType("EH");
                        } else {
                            buttons[sub.getStartX()][col].setIcon(new ImageIcon("batt3.gif"));
                            buttons[sub.getStartX()][col].setBoatPieceType("MH");
                        }


                    }

                }

            }


            if (sub.getStartY() == sub.getEndY()) {

                if (sub.getStartX() - sub.getEndX() == 2) {
                    for (int row = sub.getEndX(); row <= sub.getStartX(); row++) {
                        if (row == sub.getEndX()) {

                            buttons[row][sub.getStartY()].setIcon(new ImageIcon("batt6.gif"));
                            buttons[row][sub.getStartY()].setBoatPieceType("EV");
                        } else if (row == sub.getStartX()) {

                            buttons[row][sub.getStartY()].setIcon(new ImageIcon("batt10.gif"));
                            buttons[row][sub.getStartY()].setBoatPieceType("SV");
                        } else {
                            buttons[row][sub.getStartY()].setIcon(new ImageIcon("batt8.gif"));
                            buttons[row][sub.getStartY()].setBoatPieceType("MV");
                        }


                    }

                }
                if (sub.getStartX() - sub.getEndX() == -2) {
                    for (int row = sub.getStartX(); row <= sub.getEndX(); row++) {
                        if (row == sub.getEndX()) {

                            buttons[row][sub.getStartY()].setIcon(new ImageIcon("batt10.gif"));
                            buttons[row][sub.getStartY()].setBoatPieceType("SV");
                        } else if (row == sub.getStartX()) {

                            buttons[row][sub.getStartY()].setIcon(new ImageIcon("batt6.gif"));
                            buttons[row][sub.getStartY()].setBoatPieceType("EV");
                        } else {
                            buttons[row][sub.getStartY()].setIcon(new ImageIcon("batt8.gif"));
                            buttons[row][sub.getStartY()].setBoatPieceType("MV");
                        }


                    }

                }


            }


        }


        public void deployDestroyer(){
            if (destroyer.getStartX() == destroyer.getEndX()) {
                if (destroyer.getStartY() - destroyer.getEndY() == 2) {
                    for (int col = destroyer.getEndY(); col <= destroyer.getStartY(); col++) {
                        if (col == destroyer.getEndY()) {

                            buttons[destroyer.getStartX()][col].setIcon(new ImageIcon("batt1.gif"));
                            buttons[destroyer.getStartX()][col].setBoatPieceType("EH");
                        } else if (col == destroyer.getStartY()) {

                            buttons[destroyer.getStartX()][col].setIcon(new ImageIcon("batt5.gif"));
                            buttons[destroyer.getStartX()][col].setBoatPieceType("SH");
                        } else {
                            buttons[destroyer.getStartX()][col].setIcon(new ImageIcon("batt3.gif"));
                            buttons[destroyer.getStartX()][col].setBoatPieceType("MH");
                        }


                    }

                }
                if (destroyer.getStartY() - destroyer.getEndY() == -2) {
                    for (int col = destroyer.getStartY(); col <= destroyer.getEndY(); col++) {
                        if (col == destroyer.getEndY()) {

                            buttons[destroyer.getStartX()][col].setIcon(new ImageIcon("batt5.gif"));
                            buttons[destroyer.getStartX()][col].setBoatPieceType("SH");
                        } else if (col == destroyer.getStartY()) {

                            buttons[destroyer.getStartX()][col].setIcon(new ImageIcon("batt1.gif"));
                            buttons[destroyer.getStartX()][col].setBoatPieceType("EH");
                        } else {
                            buttons[destroyer.getStartX()][col].setIcon(new ImageIcon("batt3.gif"));
                            buttons[destroyer.getStartX()][col].setBoatPieceType("MH");
                        }


                    }

                }

            }


            if (destroyer.getStartY() == destroyer.getEndY()) {

                if (destroyer.getStartX() - destroyer.getEndX() == 2) {
                    for (int row = destroyer.getEndX(); row <= destroyer.getStartX(); row++) {
                        if (row == destroyer.getEndX()) {

                            buttons[row][destroyer.getStartY()].setIcon(new ImageIcon("batt6.gif"));
                            buttons[row][destroyer.getStartY()].setBoatPieceType("EV");
                        } else if (row == destroyer.getStartX()) {

                            buttons[row][destroyer.getStartY()].setIcon(new ImageIcon("batt10.gif"));
                            buttons[row][destroyer.getStartY()].setBoatPieceType("SV");
                        } else {
                            buttons[row][destroyer.getStartY()].setIcon(new ImageIcon("batt8.gif"));
                            buttons[row][destroyer.getStartY()].setBoatPieceType("MV");
                        }


                    }

                }
                if (destroyer.getStartX() - destroyer.getEndX() == -2) {
                    for (int row = destroyer.getStartX(); row <= destroyer.getEndX(); row++) {
                        if (row == destroyer.getEndX()) {

                            buttons[row][destroyer.getStartY()].setIcon(new ImageIcon("batt10.gif"));
                            buttons[row][destroyer.getStartY()].setBoatPieceType("SV");
                        } else if (row == destroyer.getStartX()) {

                            buttons[row][destroyer.getStartY()].setIcon(new ImageIcon("batt6.gif"));
                            buttons[row][destroyer.getStartY()].setBoatPieceType("EV");
                        } else {
                            buttons[row][destroyer.getStartY()].setIcon(new ImageIcon("batt8.gif"));
                            buttons[row][destroyer.getStartY()].setBoatPieceType("MV");
                        }


                    }

                }


            }


        }


        public void deployBattle(){
            if (battel.getStartX() == battel.getEndX()) {
                if (battel.getStartY() - battel.getEndY() == 3) {
                    for (int col = battel.getEndY(); col <= battel.getStartY(); col++) {
                        if (col == battel.getEndY()) {

                            buttons[battel.getStartX()][col].setIcon(new ImageIcon("batt1.gif"));
                            buttons[battel.getStartX()][col].setBoatPieceType("EH");
                        } else if (col == battel.getStartY()) {

                            buttons[battel.getStartX()][col].setIcon(new ImageIcon("batt5.gif"));
                            buttons[battel.getStartX()][col].setBoatPieceType("SH");
                        } else {
                            buttons[battel.getStartX()][col].setIcon(new ImageIcon("batt3.gif"));
                            buttons[battel.getStartX()][col].setBoatPieceType("MH");
                        }


                    }

                }
                if (battel.getStartY() - battel.getEndY() == -3) {
                    for (int col = battel.getStartY(); col <= battel.getEndY(); col++) {
                        if (col == battel.getEndY()) {

                            buttons[battel.getStartX()][col].setIcon(new ImageIcon("batt5.gif"));
                            buttons[battel.getStartX()][col].setBoatPieceType("SH");
                        } else if (col == battel.getStartY()) {

                            buttons[battel.getStartX()][col].setIcon(new ImageIcon("batt1.gif"));
                            buttons[battel.getStartX()][col].setBoatPieceType("EH");
                        } else {
                            buttons[battel.getStartX()][col].setIcon(new ImageIcon("batt3.gif"));
                            buttons[battel.getStartX()][col].setBoatPieceType("MH");
                        }


                    }

                }

            }


            if (battel.getStartY() == battel.getEndY()) {

                if (battel.getStartX() - battel.getEndX() == 3) {
                    for (int row = battel.getEndX(); row <= battel.getStartX(); row++) {
                        if (row == battel.getEndX()) {

                            buttons[row][battel.getStartY()].setIcon(new ImageIcon("batt6.gif"));
                            buttons[row][battel.getStartY()].setBoatPieceType("EV");
                        } else if (row == battel.getStartX()) {

                            buttons[row][battel.getStartY()].setIcon(new ImageIcon("batt10.gif"));
                            buttons[row][battel.getStartY()].setBoatPieceType("SV");
                        } else {
                            buttons[row][battel.getStartY()].setIcon(new ImageIcon("batt8.gif"));
                            buttons[row][battel.getStartY()].setBoatPieceType("MV");
                        }


                    }

                }
                if (battel.getStartX() - battel.getEndX() == -3) {
                    for (int row = battel.getStartX(); row <= battel.getEndX(); row++) {
                        if (row == battel.getEndX()) {

                            buttons[row][battel.getStartY()].setIcon(new ImageIcon("batt10.gif"));
                            buttons[row][battel.getStartY()].setBoatPieceType("SV");
                        } else if (row == battel.getStartX()) {

                            buttons[row][battel.getStartY()].setIcon(new ImageIcon("batt6.gif"));
                            buttons[row][battel.getStartY()].setBoatPieceType("EV");
                        } else {
                            buttons[row][battel.getStartY()].setIcon(new ImageIcon("batt8.gif"));
                            buttons[row][battel.getStartY()].setBoatPieceType("MV");
                        }


                    }

                }


            }


        }


        public void deployCarrier() {

            System.out.println("in deploy Carrier is selected");
            //row = aircraft.getStartX();
            //col= aircraft.getStartY();

            if (aircraft.getStartX() == aircraft.getEndX()) {
                if (aircraft.getStartY() - aircraft.getEndY() == 4) {
                    for (int col = aircraft.getEndY(); col <= aircraft.getStartY(); col++) {
                        if (col == aircraft.getEndY()) {

                            buttons[aircraft.getStartX()][col].setIcon(new ImageIcon("batt1.gif"));
                            buttons[aircraft.getStartX()][col].setBoatPieceType("EH");
                        } else if (col == aircraft.getStartY()) {

                            buttons[aircraft.getStartX()][col].setIcon(new ImageIcon("batt5.gif"));
                            buttons[aircraft.getStartX()][col].setBoatPieceType("SH");
                        } else {
                            buttons[aircraft.getStartX()][col].setIcon(new ImageIcon("batt3.gif"));
                            buttons[aircraft.getStartX()][col].setBoatPieceType("MH");
                        }


                    }

                }
                if (aircraft.getStartY() - aircraft.getEndY() == -4) {
                    for (int col = aircraft.getStartY(); col <= aircraft.getEndY(); col++) {
                        if (col == aircraft.getEndY()) {

                            buttons[aircraft.getStartX()][col].setIcon(new ImageIcon("batt5.gif"));
                            buttons[aircraft.getStartX()][col].setBoatPieceType("SH");
                        } else if (col == aircraft.getStartY()) {

                            buttons[aircraft.getStartX()][col].setIcon(new ImageIcon("batt1.gif"));
                            buttons[aircraft.getStartX()][col].setBoatPieceType("EH");
                        } else {
                            buttons[aircraft.getStartX()][col].setIcon(new ImageIcon("batt3.gif"));
                            buttons[aircraft.getStartX()][col].setBoatPieceType("MH");
                        }


                    }

                }

            }


            if (aircraft.getStartY() == aircraft.getEndY()) {

                if (aircraft.getStartX() - aircraft.getEndX() == 4) {
                    for (int row = aircraft.getEndX(); row <= aircraft.getStartX(); row++) {
                        if (row == aircraft.getEndX()) {

                            buttons[row][aircraft.getStartY()].setIcon(new ImageIcon("batt6.gif"));
                            buttons[row][aircraft.getStartY()].setBoatPieceType("EV");
                        } else if (row == aircraft.getStartX()) {

                            buttons[row][aircraft.getStartY()].setIcon(new ImageIcon("batt10.gif"));
                            buttons[row][aircraft.getStartY()].setBoatPieceType("SV");
                        } else {
                            buttons[row][aircraft.getStartY()].setIcon(new ImageIcon("batt8.gif"));
                            buttons[row][aircraft.getStartY()].setBoatPieceType("MV");
                        }


                    }

                }
                if (aircraft.getStartX() - aircraft.getEndX() == -4) {
                    for (int row = aircraft.getStartX(); row <= aircraft.getEndX(); row++) {
                        if (row == aircraft.getEndX()) {

                            buttons[row][aircraft.getStartY()].setIcon(new ImageIcon("batt10.gif"));
                            buttons[row][aircraft.getStartY()].setBoatPieceType("SV");
                        } else if (row == aircraft.getStartX()) {

                            buttons[row][aircraft.getStartY()].setIcon(new ImageIcon("batt6.gif"));
                            buttons[row][aircraft.getStartY()].setBoatPieceType("EV");
                        } else {
                            buttons[row][aircraft.getStartY()].setIcon(new ImageIcon("batt8.gif"));
                            buttons[row][aircraft.getStartY()].setBoatPieceType("MV");
                        }


                    }

                }


            }

        }

    }

    class buttonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {


            //System.out.println("Button pressed in mainGrid");

            pane.validate();

            JButton temp = (JButton) e.getSource();

            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 10; col++) {

                    if (temp.equals(buttons[row][col])  ) {

                        if (buttons[row][col].getBoatPieceType().equalsIgnoreCase("Empty")) {

                            if (slectedShip == "Aircraft Carrier") {

                                if (!aircraft.hasStartPoint()) {
                                    aircraft.setStartX(row);
                                    aircraft.setStartY(col);

                                } else if (!aircraft.hasEndPoint() && checkAvailibityOfBox(aircraft.getStartX(),aircraft.getStartY(), row, col)) {

                                    if (Math.abs(row - aircraft.getStartX()) == 4 || Math.abs(col - aircraft.getStartY()) == 4) {
                                        aircraft.setEndX(row);
                                        aircraft.setEndY(col);
                                    } else {
                                        status.insert(">Please  select a box that is 4 apart\n", 0);
                                    }

                                }

                                else {

                                    if (aircraft.hasEndPoint()){
                                        status.insert(">This ship is already deployed\n please choose another one", 0);
                                        refreshShipButtons();}
                                }

                            } else if (slectedShip.equalsIgnoreCase("Battle Ship")) {

                                if (!battel.hasStartPoint()) {
                                    battel.setStartX(row);
                                    battel.setStartY(col);

                                } else if (!battel.hasEndPoint()&& checkAvailibityOfBox(battel.getStartX(),battel.getStartY(), row, col)) {

                                    if (Math.abs(row - battel.getStartX()) == 3 || Math.abs(col - battel.getStartY()) == 3) {
                                        battel.setEndX(row);
                                        battel.setEndY(col);
                                    } else {
                                        status.insert(">Please  select a box that is 3 apart\n", 0);
                                    }

                                }
                                else {

                                    if (battel.hasEndPoint()){
                                        status.insert(">This ship is already deployed\n please choose another one", 0);
                                        refreshShipButtons();}
                                }

                            } else if (slectedShip.equalsIgnoreCase("destroyer")) {

                                if (!destroyer.hasStartPoint()) {
                                    destroyer.setStartX(row);
                                    destroyer.setStartY(col);

                                } else if (!destroyer.hasEndPoint()&& checkAvailibityOfBox(destroyer.getStartX(),destroyer.getStartY(), row, col)) {

                                    if (Math.abs(row - destroyer.getStartX()) == 2 || Math.abs(col - destroyer.getStartY()) == 2) {
                                        destroyer.setEndX(row);
                                        destroyer.setEndY(col);
                                    } else {
                                        status.insert(">Please  select a box that is 2 apart\n", 0);
                                    }

                                }
                                else {

                                    if (destroyer.hasEndPoint()){
                                        status.insert(">This ship is already deployed\n please choose another one", 0);
                                        refreshShipButtons();}
                                }

                            } else if (slectedShip.equalsIgnoreCase("submarine")) {

                                if (!sub.hasStartPoint()) {
                                    sub.setStartX(row);
                                    sub.setStartY(col);

                                } else if (!sub.hasEndPoint()&& checkAvailibityOfBox(sub.getStartX(),sub.getStartY(), row, col)) {

                                    if (Math.abs(row - sub.getStartX()) == 2 || Math.abs(col - sub.getStartY()) == 2) {
                                        sub.setEndX(row);
                                        sub.setEndY(col);
                                    } else {
                                        status.insert(">Please  select a box that is 2 apart\n", 0);
                                    }

                                }
                                else {
                                    if (sub.hasEndPoint()){
                                    status.insert(">This ship is already deployed\n please choose another one", 0);
                                    refreshShipButtons();}
                                }

                            } else if (slectedShip.equalsIgnoreCase("patrol boat")) {

                                if (!petrol.hasStartPoint()) {
                                    petrol.setStartX(row);
                                    petrol.setStartY(col);

                                } else if (!petrol.hasEndPoint()&& checkAvailibityOfBox(petrol.getStartX(),petrol.getStartY(), row, col)){

                                    if (Math.abs(row - petrol.getStartX()) == 1 || Math.abs(col - petrol.getStartY()) == 1) {
                                        petrol.setEndX(row);
                                        petrol.setEndY(col);
                                    } else {
                                        status.insert(">Please  select a box that is 1 apart\n", 0);
                                    }

                                }
                                else {

                                    if (petrol.hasEndPoint()){
                                        status.insert(">This ship is already deployed\n please choose another one", 0);
                                        refreshShipButtons();}
                                }

                            }
                        }
                        else {

                            status.insert(">Please  select an empty box\n", 0);
                        }
                    }


                }

            }

        }


    }

    public void disableShipAndDeployButtons(){

        for (int i = 0; i < 5; i++) {

            ships[i].setEnabled(false);

        }

        deploy.setEnabled(false);
    }



    class serverActionListener implements  ActionListener{

        public void actionPerformed(ActionEvent e) {
            serverMode=true;
            clientMode=false;
        }

    }
    class clientActionListener implements  ActionListener{

        public void actionPerformed(ActionEvent e) {
            serverMode=false;
            clientMode=true;
        }

    }

    boolean checkAvailibityOfBox(int Srow, int Scol, int Erow, int Ecol){

        if (Srow==Erow){

            if ((Scol-Ecol)>0){
                for (int i = Ecol; i <= Scol; i++){
                    if (!buttons[Srow][i].getBoatPieceType().equalsIgnoreCase("Empty")){
                        status.insert("The path between start and\n end point is not empty\n choose another path\n",0);
                        return false;
                    }

                }

            }
            else{

                for (int i = Scol; i <= Ecol; i++){
                    if (!buttons[Srow][i].getBoatPieceType().equalsIgnoreCase("Empty")){
                        status.insert("The path between start and\n end point is not empty\n choose another path\n",0);
                        return false;
                    }

                }

            }


        }
        else if (Scol==Ecol){
            if ((Srow-Erow)>0){
                for (int i = Erow; i <= Srow; i++){
                    if (!buttons[i][Scol].getBoatPieceType().equalsIgnoreCase("Empty")){
                        status.insert("The path between start and\n end point is not empty\n choose another path\n",0);
                        return false;
                    }

                }


            }
            else {

                for (int i = Srow; i <= Erow; i++){
                    if (!buttons[i][Scol].getBoatPieceType().equalsIgnoreCase("Empty")){
                        status.insert("The path between start and\n end point is not empty\n choose another path\n",0);
                        return false;
                    }

                }

            }



        }



    return true;
    }

    class establishConnectionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {

            if (clientMode){
                System.out.println("I am the client");
                String input;
                Integer portNum;
                //boolean correctInput= false;

                input = JOptionPane.showInputDialog("Port Number");


                while (true){
                    try{

                        if (input== null){

                            break;
                        }

                        portNum = Integer.parseInt(input);
                        //correctInput= true;
                        Main.setPortNumber(portNum);

                        System.out.println ("the port num is "+ Main.getPortNumber());
                        break;


                    }
                    catch (Exception i){

                        JOptionPane.showMessageDialog(pane,"Only Digits are expected");

                    }

                    input = JOptionPane.showInputDialog("Port Number");

                }
                //String  machine  =  JOptionPane.showInputDialog("Machine Number");
//                Main.setMachineNumber(machine);
//
                Main.getScore().doManageConnection();


            }



//            Main.modeStatus();

            else if (serverMode) {

                System.out.println("I am the server");

                if (running == false)
                { new ConnectionThread(Main.getMainBoard());
                    // Main.connectToserver();

                    // Main.score.doManageConnection();
                }
                else {
                    serverContinue = false;
                    //ssButton.setText ("Start Listening");
                    //portInfo.setText (" Not Listening ");
                }
            }


            else {

                JOptionPane.showMessageDialog(pane, "Select Client or Server mode. ");
            }



        }
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public boolean getMyTurn()
    {

     return myTurn;
    }

    public JTextArea getStatus() {
        return status;
    }

    class  statsActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {

            JOptionPane.showMessageDialog(pane,"Hits to  shots ratio: "+(hits/numAttacks)+"\n" +
                    "Total Hits: "+hits+"\nTotal shots: "+numAttacks);



        }
    }

    class connectionHelpActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(pane,"To connect....." +
                    "\n1. open two instances of the program." +
                    "\n2. in one instance in Connection Mode menu select Client \n and in the other select Server." +
                    "3. in the Server mode instance of the program it click the Establish Connection button\n" +
                    "it will show you the port number, remember it and then click OK TO CLOSE THE WINDOW\n" +
                    "then in the client instance of the program click Establish Connection and enter the port number\n");


        }
    }

    class aboutActionListener implements  ActionListener{


        public void actionPerformed(ActionEvent e) {

            JOptionPane.showMessageDialog(pane,"Authors: Bilal Vajhi -- bvajhi2\n" +
                                                                    "Sean Walker -- swalke30\n" +
                                                        "Purpose: CS-342 project 4 ");
        }
    }



    class gameHelpActionListener implements  ActionListener{
        public void actionPerformed(ActionEvent e) {
            //System.out.println("in game help action");

            String stringToDisplay= "To place the ships first select the ship and the " +
                    "Start and End boxes, then press deploy. when both players are done placing all of their ships\n" +
                    "press the ready button and the game will begin. players take turns attacking each others ships \n" +
                    "";
            JOptionPane.showMessageDialog( pane, stringToDisplay);
        }

    }


    public void setOpponentReady(boolean opponentReady) {
        this.opponentReady = opponentReady;
    }


    class  exitActionListener implements  ActionListener{
        public void actionPerformed(ActionEvent e) {
            System.out.println("in Exit action");
            System.exit(0);
        }
    }




    public boolean isClientMode() {
        return clientMode;
    }

    public boolean isServerMode() {
        return serverMode;
    }

    public boolean opponentWon(){

        for (int row=0; row< 10; row++){
            for (int col=0; col<10; col++){
                if (!buttons[row][col].getBoatPieceType().equalsIgnoreCase("Empty")){
                    return false;
                }
            }
        }

        return true;
    }



    public void resetBoard(){
        for (int row=0; row< 10; row++){

            for (int col=0; col< 10; col++){

                this.buttons[row][col].setIcon(new ImageIcon("batt100.gif"));
                this.buttons[row][col].setBoatPieceType("Empty");
            }
        }

        aircraft.setStartX(-1);
        aircraft.setStartY(-1);
        aircraft.setEndX(-1);
        aircraft.setEndY(-1);

        battel.setStartX(-1);
        battel.setStartY(-1);
        battel.setEndX(-1);
        battel.setEndY(-1);

        sub.setStartX(-1);
            sub.setStartY(-1);
        sub.setEndX(-1);
        sub.setEndY(-1);

            destroyer.setStartX(-1);
        destroyer.setStartY(-1);
        destroyer.setEndX(-1);
            destroyer.setEndY(-1);

        petrol.setStartX(-1);
        petrol.setStartY(-1);
        petrol.setEndX(-1);
        petrol.setEndY(-1);



        numAttacks=0;
        numberOfBoatsOnBoard=0;
        hits=0;
        opponentReady=false;
        playerReady=false;
        this.myTurn =false;


        for (int row = 0; row < 5; row++) {
            
            ships[row].setBackground(null);
            ships[row].setOpaque(true);
            ships[row].setEnabled(true);
            
            
        }
        deploy.setEnabled(true);
        status.insert("> New Game Deploy ships\n", 0);

    
    }





}




class ConnectionThread extends Thread
{
    mainGrid gui;

    public ConnectionThread (mainGrid es3)
    {
        gui = es3;
        start();
    }

    public void run()
    {
        gui.serverContinue = true;

        try
        {

//                if (gui.isServerMode())
//                {
//                    gui.serverSocket= new ServerSocket(0);
//                    Main.portNumber=gui.serverSocket.getLocalPort();}
//                else if(gui.isClientMode()){
//
//                    gui.serverSocket= new ServerSocket(10008);
//                    Main.portNumber=10008;
//                }


            gui.serverSocket= new ServerSocket(0);
            Main.setPortNumber(gui.serverSocket.getLocalPort());

            System.out.println("I have the port number: "+gui.serverSocket.getLocalPort());



            //gui.portInfo.setText("Listening on Port: " + gui.serverSocket.getLocalPort());
            if (Main.getMainBoard().isServerMode()){


                JOptionPane.showMessageDialog(gui,"Listening on Port: " + gui.serverSocket.getLocalPort() + "\n and Machine name is: "+InetAddress.getLocalHost().getHostAddress());
            }

            System.out.println ("Connection Socket Created");
            //Main.score.doManageConnection();
            try {
                while (gui.serverContinue)
                {
                    System.out.println ("Waiting for Connection");
                    //gui.ssButton.setText("Stop Listening");
                    new CommunicationThread (gui.serverSocket.accept(), gui);

                }
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(gui,"Accept failed.");
                System.exit(1);
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(gui,"Could not listen on port:"+ gui.serverSocket.getLocalPort());
            System.exit(1);
        }
        finally
        {
            try {
                gui.serverSocket.close();
            }
            catch (IOException e)
            {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }
}

class CommunicationThread extends Thread
{
    //private boolean serverContinue = true;
    private Socket clientSocket;
    private mainGrid gui;



    public CommunicationThread (Socket clientSoc, mainGrid sB)
    {
        clientSocket = clientSoc;
        gui = sB;
        //gui.history.insert ("Comminucating with Port" + clientSocket.getLocalPort()+"\n", 0);
        start();
    }

    public void run()
    {
        System.out.println ("New Communication Thread Started");

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader( clientSocket.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                if ( Character.isDigit( inputLine.charAt(0))&&inputLine.length()>5){

                    Main.setPortNumber( Integer.parseInt(inputLine.substring(0, inputLine.indexOf(" "))));
                    Main.setMachineNumber( inputLine.substring(inputLine.indexOf(" ")));

                    System.out.println("The Port number I am connecting to is "+Main.getPortNumber());
                    System.out.println("The Machine number I am connecting to is "+Main.getMachineNumber());

                    Main.getScore().doManageConnection();
                    out.println(inputLine.toUpperCase());
                }

                else if ( Character.isDigit( inputLine.charAt(0))&&inputLine.length()<=5){
                    int row= Integer.parseInt(inputLine.substring(0, inputLine.indexOf(" ")));
                    int col=  Integer.parseInt(inputLine.substring(inputLine.indexOf(" ")+1));
                    gui.setMyTurn(true);
                    System.out.println("box to attack: "+row+", "+col);
                    if (gui.buttons[row][col].getBoatPieceType().equalsIgnoreCase("empty")){
                        out.println("Miss");
                    }
                    else {
                        out.println("Hit");

                        if(gui.buttons[row][col].getBoatPieceType().equalsIgnoreCase("EV")){
                            gui.buttons[row][col].setIcon(new ImageIcon("batt204.gif"));
                        }
                        else if (gui.buttons[row][col].getBoatPieceType().equalsIgnoreCase("SV")){
                            gui.buttons[row][col].setIcon(new ImageIcon("batt206.gif"));
                        }else if (gui.buttons[row][col].getBoatPieceType().equalsIgnoreCase("MV")){
                            gui.buttons[row][col].setIcon(new ImageIcon("batt205.gif"));
                        }else if (gui.buttons[row][col].getBoatPieceType().equalsIgnoreCase("EH")){
                            gui.buttons[row][col].setIcon(new ImageIcon("batt201.gif"));
                        }else if (gui.buttons[row][col].getBoatPieceType().equalsIgnoreCase("SH")){
                            gui.buttons[row][col].setIcon(new ImageIcon("batt203.gif"));
                        }else if (gui.buttons[row][col].getBoatPieceType().equalsIgnoreCase("MH")){
                            gui.buttons[row][col].setIcon(new ImageIcon("batt202.gif"));
                        }

                        gui.buttons[row][col].setBoatPieceType("Empty");
                        if (gui.opponentWon()){
                        Main.getScore().doSendMessage("You Won");
                        JOptionPane.showMessageDialog(gui.pane, "The Opponent won");
                        gui.replay.setEnabled(true);

                        }

                    }


                }

                System.out.println ("Server: " + inputLine);
                //gui.history.insert (inputLine+"\n", 0);

                if (inputLine.equalsIgnoreCase("Replay")){



                    int ans;
                    ans =  JOptionPane.showConfirmDialog(gui.pane,
                            "Opponent requested another game","Replay???",JOptionPane.YES_NO_OPTION);

                    if (ans== JOptionPane.YES_OPTION){

                        out.println("yes");
                        gui.resetBoard();
                        Main.getScore().resetBoard();
                    }
                    else {

                        out.println("no");
                    }

                }


                if (inputLine.equalsIgnoreCase("You Won")){

                    JOptionPane.showMessageDialog(gui.pane, "You Won");
                    out.println("won");
                    gui.replay.setEnabled(true);
                }

                if (inputLine.equals("ready")){
                    gui.getStatus().insert("> Opponent is ready\n", 0);
                    gui.setOpponentReady(true);

                    if (gui.isPlayerReady()&&gui.isOpponentReady()){
                        gui.disableShipAndDeployButtons();
                        out.println("Both ready");
                        Main.getScore().enableButtons();
                        if (gui.isServerMode()){

                        gui.setMyTurn(true);

                        }
                    }else {

                        out.println("ready");
                    }


                }
                if (inputLine.equals("Bye."))
                    break;

                if (inputLine.equals("End Server."))
                    gui.serverContinue = false;
//
//                Integer row = Integer.parseInt(inputLine.substring(0, inputLine.indexOf(" ")));
//                Integer col = Integer.parseInt(inputLine.substring(inputLine.indexOf(" "), inputLine.length()));
//                JOptionPane.showMessageDialog(gui.pane,"Client Wants To attack box "+row+" "+col);

            }

            out.close();
            in.close();
            clientSocket.close();
        }
        catch (IOException e)
        {
            System.err.println("Problem with Communication Server");
            //System.exit(1);
        }
    }


}
