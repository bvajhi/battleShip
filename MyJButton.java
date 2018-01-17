//package com.example.java;

import javax.swing.*;


public class MyJButton extends JButton {


    //Boat piece Types: Front, Mid, back, or empty(no piece)...
    private String boatPieceType;
    Icon icon = new ImageIcon("batt100.gif");

    public MyJButton(){

       super("",new ImageIcon("batt100.gif"));

        boatPieceType= "Empty";
    }


    public String getBoatPieceType() {
        return boatPieceType;
    }

    public void setBoatPieceType(String boatPieceType) {
        this.boatPieceType = boatPieceType;
    }


}
