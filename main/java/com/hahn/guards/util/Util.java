package com.hahn.guards.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class Util {
    private static final float[] log_table = new float[64];
    static { for (int i=0; i<log_table.length; i++) log_table[i] = (float) Math.log((i / log_table.length) * 9 + 1); }

    /**
     * @return Squared distance from (x1, y1) to (x2, y2)
     */
    public static int distanceSq(int x1, int y1, int x2, int y2) {
        return (x2-x1)*(x2-x1)+(y2-y1)*(y2-y1);
    }

    /**
     * Get the nearest base at or above the given y value
     * @param world The world searching in
     * @param x The X coordinate 
     * @param y The Min-Y coordinate
     * @param z The Z coordinate
     * @return The new Y coordinate of the base
     */
    public static int getBaseAt(World world, int x, int y, int z) {
        Block b1 = world.getBlock(x, y, z);
        Block b2 = world.getBlock(x, y + 1, z);
        while (y < 127 && (b1.getBlocksMovement(world, x, y, z) || b2.getBlocksMovement(world, x, y+1, z))) {
            y += 1;
            
            b1 = world.getBlock(x, y, z);
            b2 = world.getBlock(x, y + 1, z);
        }
        
        return y;
    }
    
    /**
     * Tries to teleport an entity to a point near a player
     * @param entity The entity to teleport
     * @param player The player to teleport near
     * @return True if did teleport
     */
    public static boolean teleportToPlayer(EntityLivingBase entity, EntityPlayer player) {
        return teleportNear(entity, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.boundingBox.minY), MathHelper.floor_double(player.posZ));
    }
    
    /**
     * Tries to teleport an entity near (but not on) a point
     * @param entity The entity to teleport near
     * @param x The x location to teleport near
     * @param y The y location to teleport near
     * @param z The z location to teleport near
     * @return True if did teleport
     */
    public static boolean teleportNear(EntityLivingBase entity, int x, int y, int z) {         
    	Vec3 vec = getLocationNear(entity.worldObj, x, y, z, 2, true);
        if (vec != null) {
            entity.setPositionAndUpdate(vec.xCoord + 0.5F, vec.yCoord, vec.zCoord + 0.5F);
            return true;
        } else {
            return false;
        }
    }
    
    public static Vec3 getLocationNear(World world, int x, int y, int z, final int radius, final boolean checkAbove) {
        if (radius < 1) return null;        
        final int diameter = radius*2;
        
        ChunkCache chunkcache = new ChunkCache(world, x - radius, y - radius, z - radius, x + radius, y + radius, z + radius, 0);
        
        x -= radius;
        z -= radius;
        
        for (int addX = 0; addX <= diameter; ++addX) {
            for (int addZ = 0; addZ <= diameter; ++addZ) {
                if (addX < 1 || addZ < 1 || addX > 3 || addZ > 3) {
                    final int x1 = x + addX;
                    final int z1 = z + addZ;
                            
                    Block down = chunkcache.getBlock(x1, y - 1, z1);
                    if (down != null && down.isSideSolid(world, x1, y - 1, z1, ForgeDirection.UP)) {
                        Block at = chunkcache.getBlock(x1, y, z1);
                        if (at == null || !at.isNormalCube(world, x1, y, z1)) {
                            if (checkAbove) {
                                Block above = chunkcache.getBlock(x1, y + 1, z1);
                                if (above == null || !above.isNormalCube(world, x1, y + 1, z1)) {
                                    return Vec3.createVectorHelper(x1, y, z1);
                                }
                            } else {
                                return Vec3.createVectorHelper(x1, y, z1);
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Converts 'x' to a value on a log scale that reaches 1 at 'scale'
     * @param x The value to convert, must be >= 0
     * @param scale Where the scale reaches 1
     * @return The log converted value
     */
    public static float toLogFunc(int x, int scale) {
        if (x < scale) return log_table[(int) (x * ((float) log_table.length / scale))];
        else return 1;
    }
    
    /**
     * Gets an unsigned version of the byte
     * @param b The byte
     * @return Value 0-255
     */
    public static int getUnsignedByte(int b) {
        return b & 0xff;
    }
    
    public static byte getByte(int b) {
        return (byte) (b & 0xff);
    }
}
