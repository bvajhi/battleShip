//package com.example.java;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.net.*;
import java.util.ArrayList;


public class Main {

    private  String oppoentBoxToHit;

    private static String userBoxToHit;

    private static int portNumber;
    private static String machineNumber=null;

    private static mainGrid mainBoard;
    private  static scoreBoard score ;


    public static void main(String[] args) {
 // write your code here

       // GUI mainGUIClass= new GUI();

        score = new scoreBoard();
        mainBoard = new mainGrid();

        score.disableButtons();

       // mainBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //score.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

//


    public static int getPortNumber() {
        return portNumber;
    }

    public static void setPortNumber(int portNum) {
        portNumber = portNum;
    }

    public static mainGrid getMainBoard() {
        return mainBoard;
    }

    public static scoreBoard getScore() {
        return score;
    }

    public static String getMachineNumber() {
        return machineNumber;
    }

    public static void setMachineNumber(String machineNum) {
        machineNumber = machineNum;
    }
}
