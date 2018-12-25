package dis;

import robocode.*;


/**
 * Record the advanced state of an enemy bot.
 * 
 * @author David Cheng
 * @version 5/10/18
 * 
 * @author Period - 3
 * @author Assignment - AdvancedEnemyBot
 * 
 * @author Sources - list collaborators
 */

public class AdvancedEnemyBot extends EnemyBot
{
    private double x;

    private double y;


    /**
     * runs robot
     */
    public AdvancedEnemyBot()
    {
        reset();
    }


    /**
     * returns x
     * 
     * @return x val
     */
    public double getX()
    {
        return x;
    }


    /**
     * returns y
     * 
     * @return y val
     */
    public double getY()
    {
        return y;
    }


    /**
     * computes absolute bearing between robot and enemy
     * 
     * @param e
     *            ScannedRobotEvent
     * @param robot
     *            Robot
     */
    public void update( ScannedRobotEvent e, Robot robot )
    {
        super.update( e );
        double absBearingDeg = ( robot.getHeading() + e.getBearing() );
        if ( absBearingDeg < 0 )
        {
            absBearingDeg += 360;
        }

        // yes, you use the _sine_ to get the X value because 0 deg is North
        x = robot.getX()
            + Math.sin( Math.toRadians( absBearingDeg ) ) * e.getDistance();

        // yes, you use the _cosine_ to get the Y value because 0 deg is North
        y = robot.getY()
            + Math.cos( Math.toRadians( absBearingDeg ) ) * e.getDistance();

    }


    /**
     * returns future x
     * 
     * @param when
     *            long
     * @return x double
     */
    public double getFutureX( long when )
    {
        return x
            + Math.sin( Math.toRadians( getHeading() ) ) * getVelocity() * when;
    }


    /**
     * returns future y
     * 
     * @param when
     *            long
     * @return y double
     */
    public double getFutureY( long when )
    {
        return y
            + Math.cos( Math.toRadians( getHeading() ) ) * getVelocity() * when;
    }


    /**
     * resets all data
     */
    public void reset()
    {
        super.reset();
        x = 0.0;
        y = 0.0;
    }

}