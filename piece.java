//package com.example.java;

enum type{ aircraftCarrier, battleShip, submarine, destroyer, patrolBoats }

public class piece {

    private int startX;
    private int startY;

    private int endX;
    private int endY;
    private boolean destroied;

    private type pieceType;

  public   piece(type t){
      startX=-1;
      startY=-1;

      endX=-1;
      endY=-1;
      destroied=false;

      pieceType= t;

  }


  public boolean hasStartPoint(){

      if(startX ==-1 &&startY==-1)
      {
          return false;
      }


  return true;
  }

    public boolean hasEndPoint(){

        if(endX ==-1 &&endY==-1)
        {
            return false;
        }


        return true;
    }


    public void setStartX(int startX) {
        this.startX = startX;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }
}

