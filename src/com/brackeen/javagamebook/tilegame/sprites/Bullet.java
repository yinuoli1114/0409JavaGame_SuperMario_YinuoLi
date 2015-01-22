package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;
import com.brackeen.javagamebook.graphics.Sprite;

public class Bullet extends Sprite{
	private boolean onGround;
	public boolean enemy;
	public boolean dispear = false;
	public boolean inb = false;
	public Bullet(Animation anim){
		super(anim);
		
		
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
	

}
