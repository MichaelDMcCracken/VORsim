/**
 *
 * VORsim v1.0
 *
 * (c) 2018 IFOLLOWROADS.com
 * Code by Michael McCracken
 * Contact Info: michael.mccracken172@gmail.com
 *
 * Free license for use only by 'EFIS COMP MON' for cfi-notebook.com and any additional websites
 * in the 'EFIS COMP MON' portfolio.
 *
 * See README file for more info.
 *
 */

import java.io.*;
import java.util.*;
public class VORsim
{
    public static void main(String[] args)
    {
        Scanner gets = new Scanner(System.in);
        System.out.print("Enter X coordinate: ");
        double x_coord = gets.nextDouble();
        System.out.print("Enter Y coordinate: ");
        double y_coord = gets.nextDouble();
        System.out.print("Enter OBS: ");
        double crs = gets.nextInt();
        pilotMath pM = new pilotMath();
        String quad = pM.getQuad(x_coord, y_coord);
        double azi = pM.getAzimuth(x_coord, y_coord, quad);
        System.out.println("Quadrant: " + quad);
        System.out.println("Radial: " + pM.getAzimuth(x_coord, y_coord, quad));
        System.out.println("Deflection: " + pM.getDef(crs, azi));
        System.out.println("Flag: " + pM.getFlag(crs, azi));
        System.out.println("Difference: " + pM.getDif(crs, azi));
    }
}
class pilotMath
{
    public static String getQuad(double x, double y)
    {
        /** getQuad
         *
         * getQuad is used to get the cardinal direction in relation to the VOR.
         * This must be used to ensure the appropriate offset is given to the azimuth
         * calculation in order that the correct radial is determined.
         *
         */
        String s = "";
        int vor_x = 0;
        int vor_y = 0;
        int[] vor_01_Location = {vor_x,vor_y};
        if(y < vor_y){
            s = "S";
        } else if(y > vor_y) {
            s = "N";
        }
        if(x < vor_x)
        {
            s = s + "W";
        }else if (x > vor_x){
            s = s + "E";
        }
        return s;
    }
    public static double getAzimuth(double x, double y, String s)
    {
        /**
         *
         * getAzimuth is used to determine the radial/azimuth of the aircraft
         * This is required to determine the degree difference between desired
         * CRS and actual azimuth. The difference is required to determine amount of
         * CDI deflection.
         *
         */
        double offset = 0;
        double deg = x / y;
        double azimuth = 0;
        switch(s){
            case "N":
                azimuth = 360;
                break;
            case "E":
                azimuth = 90;
                break;
            case "S":
                azimuth = 180;
                break;
            case "W":
                azimuth = 270;
                break;
            case "NE":
                offset = Math.toDegrees(Math.atan(deg));
                azimuth = 0 + offset;
                break;
            case "SE":
                offset = Math.toDegrees(Math.atan(deg));
                azimuth = 180 + offset;
                break;
            case "SW":
                offset = Math.toDegrees(Math.atan(deg));
                azimuth = 180 + offset;
                break;
            case "NW":
                offset = Math.toDegrees(Math.atan(deg));
                azimuth = 360 + offset;
                break;
        }
        return Math.round(azimuth * 1000.0) / 1000.0;
    }
    public static String getDef(double crs, double azi)
    {
        /**
         *
         * getDef compares the aircraft's azimuth and desired CRS to determine
         * whether the CDI should be centered or deflected left or right.
         *
         */
        String def = "";
        double rec = getRec(crs);
        if(crs < 180)
        {
            if((crs == azi) || (azi == rec))
            {
                def = "C";
            }
            else if ((crs < azi) && (azi < rec))
            {
                def = "L";
            }
            else
            {
                def = "R";
            }
        }else
        {
            if((crs == azi) || (azi == rec))
            {
                def = "C";
            }
            else if((rec < azi) && (azi < crs))
            {
                def = "R";
            }
            else
            {
                def = "L";
            }
        }
        return def;
    }
    public static String getFlag(double crs, double azi)
    {
        /**
         *
         * getFlag uses the aircraft's azimuth and desired course to determine the
         * status of the TO/FROM flag
         *
         */
        String flag = "";
        double rec = getRec(crs);
        double minus90 = 0;
        double plus90 = 0;
        if(((crs - 90) >= 0))
        {
            minus90 = crs - 90;
            plus90 = getRec(minus90);
        }else
        {
            minus90 = crs + 270;
            plus90 = getRec(minus90);
        }
        if((crs >= 90) && (crs < 270))
        {
            if((azi == minus90) || (azi == plus90))
            {
                flag = "OFF";
            }else if((azi > minus90) && (azi < plus90))
            {
                flag = "FROM";
            }else
            {
                flag = "TO";
            }
        }else
        {
            if((azi == minus90) || (azi == plus90))
            {
                flag = "OFF";
            }else if((azi < minus90) && (azi > plus90))
            {
                flag = "TO";
            }else
            {
                flag = "FROM";
            }
        }
        return flag;
    }
    public static double getRec(double crs)
    {
        /**
         *
         * getRec calculates the reciprocal of the set course and ensures that
         * it never exceeds 359, to avoid any issues.
         *
         */
        double rec = 0;
        if(crs == 360)
        {
            crs = 0;
        }
        if(crs < 180)
        {
            rec = crs + 180;
        }else
        {
            rec = crs - 180;
        }
        return rec;
    }
    public static double getDif(double crs, double azi)
    {
        double rec = getRec(crs);
        double dif = 0;
        if((crs - azi) < 90)
        {
            dif = crs - azi;
        }
        else
        {
            dif = rec - azi;
        }
        return Math.abs(Math.round(dif * 1.0) / 1.0);
    }
}
