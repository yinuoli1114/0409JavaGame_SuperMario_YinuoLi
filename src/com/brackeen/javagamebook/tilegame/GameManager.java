package com.brackeen.javagamebook.tilegame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import com.brackeen.javagamebook.graphics.Animation;
import com.brackeen.javagamebook.graphics.Sprite;
import com.brackeen.javagamebook.input.GameAction;
import com.brackeen.javagamebook.input.InputManager;
import com.brackeen.javagamebook.sound.EchoFilter;
import com.brackeen.javagamebook.sound.MidiPlayer;
import com.brackeen.javagamebook.sound.Sound;
import com.brackeen.javagamebook.sound.SoundManager;
import com.brackeen.javagamebook.test.GameCore;
import com.brackeen.javagamebook.tilegame.sprites.Bullet;
import com.brackeen.javagamebook.tilegame.sprites.Creature;
import com.brackeen.javagamebook.tilegame.sprites.Fly;

import com.brackeen.javagamebook.tilegame.sprites.Grub;
import com.brackeen.javagamebook.tilegame.sprites.Invisible;
import com.brackeen.javagamebook.tilegame.sprites.Player;
import com.brackeen.javagamebook.tilegame.sprites.PowerUp;
import com.brackeen.javagamebook.tilegame.sprites.PowerUp.Gas;

/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {

    public static void main(String[] args) {
    	try{
           fp = args[0];
    	}catch (Exception ex){
    		fp = "map1";
    	}
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private Sound shootSound;
    private Sound collisionSound;
    private InputManager inputManager;
    private TileMapRenderer renderer;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction up;
    private GameAction down;
    private GameAction exit;
    private GameAction shoot;
    boolean facingLeft = false;
    boolean openfire = true;
    boolean confire = false;
    int numfire = 0;
    boolean addshoot = true;
    int state = -1;
    boolean flyopen = true;
    boolean grubopen = true;
    boolean plopen = true;
    boolean mla = false;
    boolean idle = true;
    int ttt = 0;
    boolean incnt = false;
    public static boolean pinvisible;
    boolean addIn = false;
    public static boolean gasin = false;
    boolean rrrr = false;
    private static String fp;
    


    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
            resourceManager.loadImage("background.png"));

        // load first map
        map = resourceManager.loadArgsMap(fp);

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/boop2.wav");
        shootSound = soundManager.getSound("sounds/shoot.wav");
        collisionSound = soundManager.getSound("sounds/collision.wav");
        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("sounds/music.midi");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();
    }


    /**
        Closes any resurces used by the GameManager.
    */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }


    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        up = new GameAction("up",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        down = new GameAction("down",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        shoot = new GameAction("shoot",
                GameAction.DETECT_INITAL_PRESS_ONLY);

        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(up, KeyEvent.VK_UP);
        inputManager.mapToKey(down, KeyEvent.VK_DOWN);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(shoot, KeyEvent.VK_S);
    }


    private void checkInput(long elapsedTime) {

        if (exit.isPressed()) {
            stop();
        }

        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
        	//boolean facingLeft;
            float velocityX = 0;
            float bs = 0;
            if (moveLeft.isPressed()) {
                velocityX-=player.getMaxSpeed();
                facingLeft = true;
                idle = false;
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
                facingLeft = false;
                idle = false;
            }
            if (jump.isPressed()) {
                player.jump(false);
                idle = false;
            }
            if (up.isPressed()) {
                player.jump(false);
                idle = false;
            }
            if (down.isPressed()) {
                player.down(true);
                idle = false;
                System.out.println("down");
            }
            
            if(shoot.isTyped()){
         	   //System.out.println("typed");
         	   confire = true;
         	  
            }
            if (shoot.isReleased()){
            	confire = false;
            	numfire = 0;
            	//System.out.println("releasedllllllllllllllllllllllllllllll");
            }
            float bx = 0;
            float by = 0;
            
            //float bs;
            if (shoot.isPressed()) {
                //System.out.print("s");
                bx = player.getX()+player.getWidth()/2;
                by = player.getY()+player.getHeight()/4;
                if(facingLeft == true){
                   bs = -0.9f;
                }else{
                   bs = 0.9f;
                }
                //confire = true;
                //addshoot = true;
                //state = 0;
                if(plopen == true){
                	if(facingLeft == true){
                		addBulletLeft(bx, by, bs);
                	}else{
                		addBulletRight(bx, by, bs);
                	}
                   //addBullet(facingLeft, bx, by, bs);
                   plopen = false;
                }
            }
          
           if(idle) {
        	   ttt += elapsedTime;
           }
           else {
        	   idle = true;
        	   ttt = 0;
           }
           if(ttt > 1000){
        	   ttt = 0;
        	   player.health += 5;
           }
           
            
            player.setVelocityX(velocityX);
        }

    }


    public void draw(Graphics2D g) {
        renderer.draw(g, map,
            screen.getWidth(), screen.getHeight());
    }


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }


    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
    	
    	
    	//JOptionPane.showMessageDialog(null,"My first graphic program!\n Thanks, Progressive Java Course!");
        Creature player = (Creature)map.getPlayer();
        Player p = (Player)player;
       
        System.out.println("gasin="+gasin);
        System.out.println("rrrr="+rrrr);
        
        if(p.gasHit == false){
        	gasin = false;
        	p.gasHit = true;
        }
        if(gasin == true){
        	p.gasHit = true;
        }
        
        if(numfire == 10){
        	
    		p.nofire = true;
    		numfire = 0;
    		openfire = false;
    	}
        if (p.nofire == false){
        	openfire = true;
        }
        if(p.plshooting == true){
            //creatureBullet(cx, cy, cs);
        	plopen = true;
        	p.plshooting = false;
        }
       if(p.invt > 5000){
    	   p.invt = 0;
    	   p.invisible = false;
       }
       //System.out.println(p.invisible);
       pinvisible = p.invisible;
       
       //Invisible in = (Invisible)map.
       
       if(p.invisible == true && addIn == true){
    	   //addInvisible(p.getX(),p.getY());
    	   addIn = false;
       }


        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = resourceManager.loadArgsMap(fp);
            return;
        }

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);
        
        ArrayList<Fly> enfly = new ArrayList<Fly>();
        ArrayList<Grub> engrub = new ArrayList<Grub>();
 
        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            // System.out.println(i);
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if(sprite instanceof Fly){
                	Fly fly = (Fly) sprite;
                	enfly.add(fly);
                	
                }
                if(sprite instanceof Grub){
                	Grub grub = (Grub) sprite;
                	engrub.add(grub);
                	
                }
 
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                }
                else {
                    updateCreature(creature, elapsedTime);
                   /* if(sprite instanceof Fly){
                    	Fly fly = (Fly) sprite;
                    	if(p.invisible == false){
                    		if(player.getX() < fly.getX()){
                    			fly.setVelocityX(-0.06f);
                    		}else{
                    			fly.setVelocityX(0.06f);
                    		}
                    	}
                    	
                    	
                    	
                    }*/
                    if(sprite instanceof Grub){
                    	Grub grub = (Grub) sprite;
                    	if(p.invisible == false){
                    		if(player.getX() < grub.getX()){
                    			grub.setVelocityX(-0.06f);
                    		}else{
                    			grub.setVelocityX(0.06f);
                    		}
                    	}
                    	
                    }
                    
                }
            }
            if (sprite instanceof Bullet) {
            	float xx = sprite.getX();
            	float yy = sprite.getY();
            	if(getTileCollision(sprite,xx,yy) != null){
            		i.remove();
            	}
                Bullet bullet = (Bullet)sprite;
                //updateCreature(creature, elapsedTime);
                Sprite cli = getSpriteCollision(bullet);
                if(bullet.enemy == false){
                  if(cli instanceof Creature){
                	  ((Creature) cli).setState(Creature.STATE_DEAD);
                	  i.remove();
                	  p.health += 10;
                	  p.score += 3;
                	  soundManager.play(boopSound);
                   }
                  
                }
                if(bullet.enemy == true && bullet.dispear==true){
                	i.remove();
                	p.health -= 5;
                	p.score -= 1;
                	soundManager.play(prizeSound);
                }
                
            }
            
            sprite.update(elapsedTime);
        }
        for (Fly fly: enfly){
        	float cx = fly.getX()+fly.getWidth()/2;
            float cy = fly.getY();
            float cs = 0;
            if(fly.getVelocityX()>0){
            	cs = 0.4f;
            }else{
            	cs = -0.4f;
            }
            if(fly.flyshooting == true){
                //creatureBullet(cx, cy, cs);
            	flyopen = true;
            }
            if(flyopen == true){
                creatureBullet(cx, cy, cs);
                fly.flyshooting = false;
                flyopen = false;
            }
            
        }
        for (Grub grub: engrub){
        	float cx = grub.getX();
            float cy = grub.getY();
            float cs = 0;
            if(grub.getVelocityX()>0){
            	cs = 0.4f;
            }else{
            	cs = -0.4f;
            }
            if(grub.grubshooting == true){
                //creatureBullet(cx, cy, cs);
            	grubopen = true;
            }
            if(grubopen == true){
                creatureBullet(cx, cy, cs);
                grub.grubshooting = false;
                grubopen = false;
            }
            //creatureBullet(cx, cy, cs);
            
        }
        
    }


    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    
    
    private void updateCreature(Creature creature,
        long elapsedTime)
    {

        // apply gravity
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
        	Player player = (Player) creature;
            boolean canKill = (oldY < creature.getY());
           
            checkPlayerCollision((Player)creature, canKill);
          
        }

    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof Bullet){
        	Bullet bullet = (Bullet)collisionSprite;
            //updateCreature(creature, elapsedTime);
            
        	if(bullet.enemy == true){
        		bullet.dispear = true;
        		rrrr = true;
        	}
        }
        else if (collisionSprite instanceof PowerUp) {
            //acquirePowerUp((PowerUp)collisionSprite);
            if(collisionSprite instanceof PowerUp.Music){
                player.health += 5;
                acquirePowerUp((PowerUp)collisionSprite);
            }else if(collisionSprite instanceof PowerUp.Apple){
            	player.health -= 5;
            	acquirePowerUp((PowerUp)collisionSprite);
            }else if (collisionSprite instanceof PowerUp.Star){
            	player.invisible = true;
            	addIn = true;
            	acquirePowerUp((PowerUp)collisionSprite);
            }
            else if (collisionSprite instanceof PowerUp.Gas){
            	//Gas gas = (Gas)collisionSprite;
            	gasin = true;
            	System.out.println("hhhhhhhhhhhhhhitgase");
            }
            else if (collisionSprite instanceof PowerUp.Explode && player.invisible == false){
            	if(player.invisible == false){
            	    acquirePowerUp((PowerUp)collisionSprite);
            	}
            	player.health -= 10;
            	Sprite gas = (Sprite)collisionSprite;
            	float x = gas.getX();
            	float y = gas.getY();
            	addExplosion(x,y, 0.6f, -0.6f);
            	addExplosion(x,y, -0.6f, -0.6f);
            	addExplosion(x,y, 0.2f, -0.76f);
            	addExplosion(x,y, -0.2f, -0.76f);
            	addExplosion(x,y, 0, -0.8f);
            }
            soundManager.play(collisionSound);
        }
        /*
        else if (collisionSprite instanceof Gas){
        	Gas gas = (Gas)collisionSprite;
        	gasin = true;
        	System.out.println("hhhhhhhhhhhhhhitgase");
        }*/
        
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill && player.invisible == false) {
                // kill the badguy and make player bounce
                soundManager.play(boopSound);
                
                  badguy.setState(Creature.STATE_DYING);
                  player.setY(badguy.getY() - player.getHeight());
                  player.jump(true);
               
                
               
                //player.health -= 5;
            }
            else {
                // player dies!
            	if(player.invisible == false){
                   player.setState(Creature.STATE_DYING);
            	}else{
            	   player.setState(Creature.STATE_NORMAL);
            	}
                //player.health -= 5;
            }
        }
        /*
        else if (collisionSprite instanceof Bullet){
        	Bullet bullet = (Bullet)collisionSprite;
            //updateCreature(creature, elapsedTime);
            
        	if(bullet.enemy == true){
        		bullet.dispear = true;
        		rrrr = true;
        	}
        }*/
        
        
        /*
        else if (player.health < 1){
        	player.setState(Creature.STATE_DYING);
        }*/
    }


    /**
        Gives the player the speicifed power up and removes it
        from the map.
    */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
    	boolean powerup = true;
    	if(powerUp instanceof PowerUp.Gas){
    		powerup = false;
    	}
    	if(powerup == true){
         map.removeSprite(powerUp);
    	}

        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Music) {
            // change the music
            soundManager.play(prizeSound);
            toggleDrumPlayback();
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            map = resourceManager.loadArgsMap(fp);
        }
    }
    public void addBulletRight(float x, float y, float speed){
    	
    	Image bullet_image = loadImage("images/bullet1.png");
    	Animation anim_bullet = new Animation();
        anim_bullet.addFrame(bullet_image, 250);
        Bullet bullet = new Bullet(anim_bullet);
        bullet.setX(x);
        bullet.setY(y);
        bullet.setVelocityX(speed);
        bullet.enemy = false;
        if( openfire == true && gasin == false){
        	//if(gasin == false){
               soundManager.play(shootSound);
               map.addSprite(bullet);
        	//}
         
        }
        //System.out.println("confire="+confire);
        
        if(confire == true){
            numfire += 1;
            //System.out.println("numfire="+numfire);
         }
        
    }
    public void addBulletLeft(float x, float y, float speed){
    	
    	Image bullet_image = loadImage("images/bullet2.png");
    	Animation anim_bullet = new Animation();
        anim_bullet.addFrame(bullet_image, 250);
        Bullet bullet = new Bullet(anim_bullet);
        bullet.setX(x);
        bullet.setY(y);
        bullet.setVelocityX(speed);
        bullet.enemy = false;
        if( openfire == true && gasin == false){
           soundManager.play(shootSound);
           map.addSprite(bullet);
           
           
        }
        //System.out.println("confire="+confire);
        
        if(confire == true){
            numfire += 1;
            //System.out.println("numfire="+numfire);
         }
        
    }
    public void creatureBullet(float x, float y, float speed){
    	Image bullet_image = loadImage("images/green.png");
    	Animation anim_bullet = new Animation();
        anim_bullet.addFrame(bullet_image, 250);
        Bullet bullet = new Bullet(anim_bullet);
        bullet.setX(x);
        bullet.setY(y);
        bullet.setVelocityX(speed);
        bullet.enemy = true;
        //if()
        map.addSprite(bullet);
    }
    public void addExplosion(float x, float y, float sx, float sy){
    	Image img = loadImage("images/explode1.png");
    	Animation anim = new Animation();
    	anim.addFrame(img, 250);
    	//Image img2 = loadImage("images/explode3.png");
    	//anim.addFrame(img2, 250);
    	Gas gas = new Gas(anim);
    	gas.setX(x);
    	gas.setY(y);
    	gas.setVelocityX(sx);
    	gas.setVelocityY(sy);
    	map.addSprite(gas);
    }
    public void addInvisible(float x, float y){
    	Image inv_image = loadImage("images/invisible.png");
    	Animation anim_inv = new Animation();
        anim_inv.addFrame(inv_image, 250);
        Image inv1_image = loadImage("images/invisible1.png");
    	//Animation anim_inv1 = new Animation();
        anim_inv.addFrame(inv1_image, 250);
        Invisible in = new Invisible(anim_inv);
        in.setX(x);
        in.setY(y);
        //in.setVelocityX(sx);
        //in.setVelocityY(sy);
     
        //if()
        map.addSprite(in);
    }

  

}
