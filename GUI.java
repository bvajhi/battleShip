//package com.example.java;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.util.ArrayList;




public class GUI extends JFrame {

    protected MyJButton[][] buttons;
    protected Container pane;
    protected JLabel topLable;
    protected JLabel leftLable;



    public GUI(String name){

        setTitle(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //the mine grid panel...
        JPanel gridPanel= new JPanel();
        gridPanel.setLayout(new GridLayout(10,10));

        //10x10 array of buttons...
        buttons=new MyJButton[10][10];


        for (int row=0; row <10; row ++){
            for (int col=0; col<10; col++){
                buttons[row][col] = new MyJButton();
                buttons[row][col].setBoatPieceType("empty");
                buttons[row][col].setBackground(Color.blue);
                gridPanel.add(buttons[row][col]);

            }

        }




        pane = this.getContentPane();
        pane.setLayout(new BorderLayout());


        pane.add(gridPanel, BorderLayout.CENTER);




        //setSize(500,550);
        //setVisible(true);




    }

    public void enableButtons(){

        for (int row=0; row< 10; row++){
            for (int col= 0; col<10; col++){
                buttons[row][col].setEnabled(true);

            }

        }


    }
    public void disableButtons(){

        for (int row=0; row< 10; row++){
            for (int col= 0; col<10; col++){
                buttons[row][col].setEnabled(false);

            }

        }


    }
    public MyJButton[][] getButtons() {
        return buttons;
    }

    public void setButtons(MyJButton[][] buttons) {
        this.buttons = buttons;
    }
}
