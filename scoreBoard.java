//package com.example.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;


public class scoreBoard extends GUI {
    boolean connected;
    Socket echoSocket;
    PrintWriter out;
    BufferedReader in;
    int currentX;
    int currentY;
    //String message;
    boolean myTurn=false;


    public scoreBoard(){

        super("Score Board");

        for (int row=0; row< 10; row++){
            for (int col=0; col < 10; col++){
                buttons[row][col].addActionListener(new buttonActionListener());

            }

        }
       


        setSize(500,500);
        setVisible(true);

    }





    class buttonActionListener implements ActionListener {

        public void actionPerformed (ActionEvent e){

            String message = null;


            pane.validate();
            JButton temp = (JButton) e.getSource();

            if (!Main.getMainBoard().getMyTurn()){
                Main.getMainBoard().getStatus().insert("> It is opponent's turn\n",0);
                return;
            }

            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 10; col++) {

                    if (temp.equals(buttons[row][col])  ) {
                        message= row+" "+col;
                        currentX=row;
                        currentY=col;
                        Main.getMainBoard().setMyTurn(false);
                        doSendMessage(message);
                    }
                }
            }




        }



    }


    public void resetBoard(){
        for (int row=0; row< 10; row++){

            for (int col=0; col< 10; col++){

                this.buttons[row][col].setIcon(new ImageIcon("batt100.gif"));
            }
        }


    }

    public void doSendMessage(String message)
    {
        try
        {

            out.println(message);
            String result = in.readLine();

            System.out.println("From Server: " +  result+ "\n" );
               if (result.equalsIgnoreCase("Hit")){
                Main.getMainBoard().getStatus().insert("> Hit\n",0);
                    buttons[currentX][currentY].setIcon(new ImageIcon("batt103.gif"));
                    Main.getMainBoard().updateNumAttacks();
                    Main.getMainBoard().updateHits();
               }
            else if (result.equalsIgnoreCase("Miss")) {
                Main.getMainBoard().getStatus().insert("> Miss\n",0);
                   Main.getMainBoard().updateNumAttacks();
                   buttons[currentX][currentY].setIcon(new ImageIcon("batt102.gif"));
            }else if (result.equalsIgnoreCase("Both ready")){
                   this.enableButtons();
                   if (Main.getMainBoard().isServerMode()){

                       Main.getMainBoard().setMyTurn(true);

                   }
                   Main.getMainBoard().disableShipAndDeployButtons();
               }else if (result.equalsIgnoreCase("yes")){
                this.resetBoard();
                Main.getMainBoard().resetBoard();

               }
               else if (result.equalsIgnoreCase("no")){

                   Main.getMainBoard().getStatus().insert("> Opponent has Declined\n",0);
               }
            else {
                   System.out.println("This is a flag");
               }
            myTurn=false;
        }
        catch (IOException e)
        {
            System.out.println ("Error in processing message ");
        }
    }

    public void doManageConnection()
    {
        if (connected == false)
        {
            String machineName = null;
            int portNum = -1;
            try {

//                if (Main.mainBoard.isServerMode()){
                machineName= "127.0.0.1";
//                }
                //else {
                // machineName = Main.getMachineNumber();

                //}
                portNum = Main.getPortNumber();
                echoSocket = new Socket(machineName, portNum );
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                        echoSocket.getInputStream()));
                //sendButton.setEnabled(true);
                connected = true;
                System.out.println("I am connected to port number: "+ Main.getPortNumber());

                if (Main.getMainBoard().isClientMode()){
                    Main.getMainBoard().createClientSideServer();

                }

                // Display message the connection was good
                Main.getMainBoard().getStatus().insert("> Connected", 0 );
                // new Main.mainBoard.ConnectionThread(Main.mainBoard);
//                if (Main.mainBoard.isClientMode()){
//
//                    new listenForTheEvents(in);
//                    System.out.println("Created the thread");
//                }


                //connectButton.setText("Disconnect from Server");
            } catch (NumberFormatException e) {
                System.out.println( "Server Port must be an integer\n");
            } catch (UnknownHostException e) {
                System.out.println("Don't know about host: " + machineName );
            } catch (IOException e) {
                System.out.println ("Couldn't get I/O for "
                        + "the connection to: " + machineName );
            }

        }
        else
        {
            try
            {
                out.close();
                in.close();
                echoSocket.close();
                //sendButton.setEnabled(false);
                connected = false;
                System.out.println("Connect to Server");
            }
            catch (IOException e)
            {
                System.out.println("Error in closing down Socket ");
            }
        }


    }




}