package dis;

import robocode.*;


/**
 * Record the state of an enemy bot.
 * 
 * @author David Cheng
 * @version 5/7/18
 * 
 * @author Period - 3
 * @author Assignment - EnemyBot
 * 
 * @author Sources - list collaborators
 */
public class EnemyBot
{
    private double bearing;

    private double distance;

    private double energy;

    private double heading;

    private double velocity;

    private String name;


    /**
     * runs enemyBot
     * 
     */
    public EnemyBot()
    {
        reset();
    }


    /**
     * returns bearing
     * 
     * @return bearing of robot
     */
    public double getBearing()
    {
        return bearing;
    }


    /**
     * returns distance
     * 
     * @return distance of robot
     */
    public double getDistance()
    {
        return distance;
    }


    /**
     * returns energy
     * 
     * @return energy of robot
     */
    public double getEnergy()
    {
        return energy;
    }


    /**
     * returns heading
     * 
     * @return heading of robot
     */
    public double getHeading()
    {
        return heading;
    }


    /**
     * returns velocity
     * 
     * @return velocity of robot
     */
    public double getVelocity()
    {
        return velocity;
    }


    /**
     * returns name
     * 
     * @return name
     */
    public String getName()
    {
        return name;
    }


    /**
     * updates the data
     * 
     * @param srEvt ScannedRobotEvent
     */
    public void update( ScannedRobotEvent srEvt )
    {
        bearing = srEvt.getBearing();
        distance = srEvt.getDistance();
        energy = srEvt.getEnergy();
        heading = srEvt.getHeading();
        velocity = srEvt.getVelocity();
        name = srEvt.getName();
    }


    /**
     * resets all data
     * 
     */
    public void reset()
    {
        bearing = 0.0;
        distance = 0.0;
        energy = 0.0;
        heading = 0.0;
        velocity = 0.0;
        name = "";
    }


    /**
     * checks if name is ""
     * 
     * @return true if name is "" and false otherwise
     */
    public boolean none()
    {
        return name.length() == 0;
    }
}