package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Grub extends Creature {
	public boolean grubshooting = false;
	private int tno = 0;

    public Grub(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }
    public void update(long elapsedTime){
    	super.update(elapsedTime);
    	if(grubshooting == false){
    		tno += elapsedTime;
    	}
    	if(tno > 1000){
    		grubshooting = true;
    		tno = 0;
    	}
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
