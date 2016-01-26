package fr.ecp.sio.gameout.model;

import java.net.InetAddress;

/**
 * Created by erwan on 14/11/2015.
 */

public class Player extends GameObject {
    public byte id;
    public Team parentTeam;
    public InetAddress ip;
    public byte type;
    public byte state;
    public short x;
    public short y;
    public short vx;
    public short vy;


    public Player(byte id, Team parentTeam) {
        this.parentTeam = parentTeam;
        this.id = id;
        ip = null;
        type = PlayerType.GUEST;
        state = PlayerState.ACTIVE;
        x = 6000;
        y = 10000;
        vx = 1;
        vy = 1;
    }
}