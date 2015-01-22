package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Fly extends Creature {
	public boolean flyshooting = false;
	private int tno = 0;

    public Fly(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }
    
    public void update(long elapsedTime){
    	super.update(elapsedTime);
    	if(flyshooting == false){
    		tno += elapsedTime;
    	}
    	if(tno > 1000){
    		flyshooting = true;
    		tno = 0;
    	}
    }


    public float getMaxSpeed() {
        return 0.2f;
    }


    public boolean isFlying() {
        return isAlive();
    }

}
