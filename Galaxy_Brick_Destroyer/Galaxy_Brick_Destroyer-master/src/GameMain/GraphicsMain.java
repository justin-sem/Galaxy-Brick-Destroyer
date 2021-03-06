/*
 *  Brick Destroy - A simple Arcade video game
 *   Copyright (C) 2017  Justin Sem
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package GameMain;

import java.awt.*;
import Component.Music;

/**
 * This is the Main class
 * @author Justin Sem Ee Qing
 * @version 2.0
 * @since 13/12/2021
 */


public class GraphicsMain {

    /**
     * @param args
     * main method which will invoke the GameFrame and play the music
     */
    public static void main(String[] args){

        EventQueue.invokeLater(() -> new GameFrame().initialize());
        String filepath = "Galaxy_Brick_Destroyer/Galaxy_Brick_Destroyer-master/Others/Loyalty_Freak_Music_-_04_-_Cant_Stop_My_Feet_.wav";
        Music musicObj =  new Music();
        musicObj.playMusic(filepath);


    }



}
