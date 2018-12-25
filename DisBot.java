package dis;

import java.awt.Color;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;


/**
 * A modular bot adhering to the RoboPart Interface.
 * 
 * @author David Cheng, Steve Dou, Iman Haq
 * @version 5/29/18
 * 
 * @author Period - 3
 * @author Assignment - Intro to CS Final Project
 * 
 * @author Sources - PartsBot
 */
public class DisBot extends AdvancedRobot
{
    private AdvancedEnemyBot enemy = new AdvancedEnemyBot();

    private RobotPart[] parts = new RobotPart[3]; // make three parts

    private final static int RADAR = 0;

    private final static int GUN = 1;

    private final static int TANK = 2;

    private int moveDirection = 1;

    // starts the tank
    public void run()
    {
        parts[RADAR] = new Radar();
        parts[GUN] = new Gun();
        parts[TANK] = new Tank();

        // initialize each part
        for ( int i = 0; i < parts.length; i++ )
        {
            // behold, the magic of polymorphism
            parts[i].init();
        }

        // iterate through each part, moving them as we go
        for ( int i = 0; true; i = ( i + 1 ) % parts.length )
        {
            // polymorphism galore!
            parts[i].move();
            if ( i == 0 )
                execute();
        }
    }



    /**
     * sends information of enemy robot to radar
     * @param e ScannedRobotEvent
     */
    public void onScannedRobot( ScannedRobotEvent e )
    {
        Radar radar = (Radar)parts[RADAR];
        if ( radar.shouldTrack( e ) )
            enemy.update( e, this );

    }


    /**
     * 
     * prints "Ouch" when hit by a bullet
     * 
     * @param e
     */
    public void onHitByBullet( BulletHitEvent e )
    {
        System.out.println( "Ouch!" );
    }


    /**
     * switches direction when it hits wall
     * 
     * @param e
     *            Enemy Bot
     */
    public void onHitWall( HitWallEvent e )
    {
        moveDirection = (byte)-moveDirection;
    }


    /**
     * Fires with a harder round when the gun is ready to fire again.
     */
    public void onHitRobot( HitRobotEvent e )
    {
        // checks if the gun heat is 0 before firing again
        if ( getGunHeat() == 0 )
        {
            fire( 4 );
        }

    }


    /**
     * Checks if dead robot is the enemy and resets their location
     * 
     * @param e
     *            Enemy Bot
     */
    public void onRobotDeath( RobotDeathEvent e )
    {
        Radar radar = (Radar)parts[RADAR];
        if ( radar.wasTracking( e ) )
            enemy.reset();
    }


    // ... put normalizeBearing and absoluteBearing methods here
    /**
     * 
     * finds angle between two arbitrary points
     * 
     * @param x1
     *            x1
     * @param y1
     *            y1
     * @param x2
     *            x2
     * @param y2
     *            y2
     * @return double
     */
    double absoluteBearing( double x1, double y1, double x2, double y2 )
    {
        double xo = x2 - x1;
        double yo = y2 - y1;
        double hyp = Point2D.distance( x1, y1, x2, y2 );
        double arcSin = Math.toDegrees( Math.asin( xo / hyp ) );
        double bearing = 0;

        if ( xo > 0 && yo > 0 )
        { // both pos: lower-Left
            bearing = arcSin;
        }
        else if ( xo < 0 && yo > 0 )
        { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actually 360 -
                                    // ang
        }
        else if ( xo > 0 && yo < 0 )
        { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        }
        else if ( xo < 0 && yo < 0 )
        { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 +
                                    // ang
        }

        return bearing;
    }


    /**
     * normalizes a bearing to between +180 and -180
     * 
     * @param angle
     *            angle
     * @return double
     */
    double normalizeBearing( double angle )
    {
        while ( angle > 180 )
            angle -= 360;
        while ( angle < -180 )
            angle += 360;
        return angle;
    }


    // ... declare the RobotPart interface and classes that implement it here
    // They will be _inner_ classes.

    public interface RobotPart
    {
        public void init();


        public void move();
    }


    /**
     * 
     * Uses factor lock to lock on to enemy
     *
     * @author David Cheng, Steve Dou, Iman Haq
     * @version May 29, 2018
     * @author Period: 3
     * @author Assignment: FinalProject
     *
     * @author Sources: http://old.robowiki.net/robowiki?Radar
     */
    public class Radar implements RobotPart
    {
        // This factor makes the radar start wide and narrows down as much as
        // possible
        private double factor;


        /**
         * Initializes the radar to keep searching until an enemy is found
         */
        public void init()
        {
            setAdjustRadarForGunTurn( true );
            factor = 1.9; // gives fluid narrow down motion
        }


        /**
         * Moves the radar until an enemy is found and then narrows down, never
         * losing its lock because it is impossible for the Robot to move out in
         * one tick.
         */
        public void move()
        {
            if ( enemy.none() )
            {
                // Looks around until an enemy is found, generating an
                // onScannedRobotEvent
                setTurnRadarRight( 36000 );
            }
            else
            {
                double radarTurn = getHeadingRadians()
                    + Math.toRadians( enemy.getBearing() ) // get enemy location
                                                           // relative to us
                    - getRadarHeadingRadians(); // subtract distance robot is
                                                // facing to turn to enemy
                setTurnRadarRightRadians(
                    factor * Utils.normalRelativeAngle( radarTurn ) ); // normalize
                                                                       // angle
                                                                       // to
                                                                       // relative
                                                                       // angle
                                                                       // between
                                                                       // -pi
                                                                       // and pi
            }
        }


        /**
         * 
         * Tracks enemy bot.
         * 
         * @param e
         *            Enemy Bot
         * @return boolean
         */
        public boolean shouldTrack( ScannedRobotEvent e )
        {
            // track if we have no enemy, the one we found is significantly
            // closer, or we scanned the one we've been tracking.
            return ( enemy.none() || e.getDistance() < enemy.getDistance() - 70
                || e.getName().equals( enemy.getName() ) );
        }


        /**
         * 
         * Checks enemy bot name
         * 
         * @param e
         *            Enemy Bot
         * @return boolean
         */
        public boolean wasTracking( RobotDeathEvent e )
        {
            return e.getName().equals( enemy.getName() );
        }
    }


    /**
     * 
     * Implements linear targeting to target and shoot at the enemy bot
     *
     * @author David Cheng, Steve Dou, Iman Haq
     * @version May 29, 2018
     * @author Period: 3
     * @author Assignment: FinalProject
     *
     * @author Sources: predictiveshooter.java in robolessons
     */
    public class Gun implements RobotPart
    {
        public void init()
        {
            setAdjustGunForRobotTurn( true );
        }


        /**
         * shoots at enemy bot.
         * 
         * sources: predictive shooter from robolessons
         */
        public void move()
        {
            // don't shoot if I've got no enemy
            if ( enemy.none() )
                return;

            // calculate firepower based on distance
            double firePower = Math.min( 600 / enemy.getDistance(), 3 );
            // shoots harder if enemy is closer
            if ( enemy.getDistance() < 800 )
            {
                firePower *= 0.75;
            }
            else if ( enemy.getDistance() < 500 )
            {
                firePower *= 1.25;
            }
            else if ( enemy.getDistance() < 200 )
            {
                firePower *= 2;
            }
            else if ( enemy.getDistance() < 100 )
            {
                firePower *= 5;
            }
            // calculate speed of bullet
            double bulletSpeed = 20 - firePower * 3;
            // distance = rate * time, solved for time
            long time = (long)( enemy.getDistance() / bulletSpeed );

            // calculate gun turn to predicted x,y location
            double futureX = enemy.getFutureX( time );
            double futureY = enemy.getFutureY( time );
            double absDeg = absoluteBearing( getX(), getY(), futureX, futureY );
            // non-predictive firing can be done like this:
            // double absDeg = absoluteBearing(getX(), getY(), enemy.getX(),
            // enemy.getY());

            // turn the gun to the predicted x,y location
            setTurnGunRight( normalizeBearing( absDeg - getGunHeading() ) );

            // if the gun is cool and we're pointed in the right direction,
            // shoot!
            if ( getGunHeat() == 0 && Math.abs( getGunTurnRemaining() ) < 10 )
            {
                setFire( firePower );
            }
        }

    }


    /**
     * 
     * Three different movement strategies: strafing, spiraling, and ramming
     *
     * @author David Cheng, Steve Dou, Iman Haq
     * @version May 24, 2018
     * @author Period: 3
     * @author Assignment: FinalProject
     *
     * @author Sources: strafecloser.java and spiral.java from robolessons
     */
    public class Tank implements RobotPart
    {

        int tick = 20; //initiates a certain amount time 


        /**
         * sets colors
         */
        public void init()
        {
            setBodyColor( Color.black );
        }


        /**
         * Moves the tank in respect to enemy bot
         */
        public void move()
        {
            // directly aims and rams at enemy bot if distance less than 200
            if ( enemy.getDistance() < 200 )
            {
                setBodyColor( Color.red );
                double turn = enemy.getBearing();
                setTurnRight( normalizeBearing( turn ) );
                setAhead( enemy.getDistance() );
            }
            //spiral towards enemy if distance less than 600
            else if ( enemy.getDistance() < 600 )
            {
                setBodyColor( Color.black );
                // spiral toward our enemy
                setTurnRight( normalizeBearing(
                    enemy.getBearing() + 90 - ( 15 * moveDirection ) ) );
                setAhead( enemy.getDistance() * moveDirection );
            }
            //if not close at all (>600), strafe closer
            else if ( enemy.getDistance() < 800 )
            {
                setBodyColor( Color.green );
                // turn slightly toward our enemy
                setTurnRight( normalizeBearing(
                    enemy.getBearing() + 90 - ( 15 * moveDirection ) ) );

                // strafe toward him
                if ( getTime() % tick == 0 )
                {
                    tick = (int)Math.random() * 30 + 10;
                    moveDirection *= -1;
                    setAhead( 150 * moveDirection );
                }
            }
        }
    }
}
