package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;
    private static final float DOWN_SPEED = .95f;

    private boolean onGround;
    public boolean nofire = false;
    private long tno = 0;
    public float health = 20;
    private float px = 0;
    private float prex = 0;
    public boolean plshooting = true;
    private int tt = 0;
    public int score = 0;
    public boolean mlc = false;
    public boolean invisible = false;
    public int invt = 0;
    public boolean iscnt = false;
    public int time = 0;
    public boolean gasHit = false;
    private int tgas = 0;
    
    public int ht = 0;

    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }
    
    public void update(long elapsedTime){
    	super.update(elapsedTime);
    	time += elapsedTime;
    	
    	if(gasHit == true){
    		tgas += elapsedTime;
    	}
    	if(tgas > 2000){
    		tgas = 0;
    		gasHit = false;
    	}
    	
    	if(nofire == true){
    		tno += elapsedTime;
    	}
    	if(tno > 1000){
    		nofire = false;
    		tno = 0;
    	}
    	if(plshooting == false){
    		tt += elapsedTime;
    	}
    	if(tt > 80){
    		plshooting = true;
    		tt = 0;
    	}
    	if(mlc == true){
    		ht += elapsedTime;
    	}
    	if(ht > 1000){
    		mlc = false;
    		ht = 0;
    	}
    	if(invisible == true){
    		invt += elapsedTime;
    	}
    
    	//System.out.println("tno="+tno);
    	px = getX();
    	health = health + (float)Math.abs((int)px - (int)prex)/500.0f;
    	prex = px;
    	
    	if(health > 40){
    		health = 40;
    	}
    	if(health < 0){
    		health = 0;
    	}
    	
    }
    

    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }
    
    

    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }
    public void down(boolean forceDown){
    	if(forceDown){
    		setVelocityY(DOWN_SPEED);
    	}
    }


    public float getMaxSpeed() {
        return 0.5f;
    }

}
