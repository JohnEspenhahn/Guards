package com.hahn.guards.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.hahn.guards.GuardEventHandler;
import com.hahn.guards.Guards;
import com.hahn.guards.GuiHandler;

public class EntityStoneGolem extends EntityIronGolem implements IWanderer, IOwned {
	private static int FOLLOWING = 13, CHASE = 14, STATION = 15, OWNER = 19;
	
	public EntityStoneGolem(World world) {
        super(world);
        
        this.setAlwaysRenderNameTag(true);
        
        this.tasks.taskEntries.clear();
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAIGuardFollow(this, 1.0f));
        this.tasks.addTask(3, new EntityAIMoveToHome(this, 1.0D));
        this.tasks.addTask(4, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, Entity.class, 5, false, true, new GuardEntitySelector(this)));
    }
	
	@Override
	protected void updateAITick() {
		// Disable iron golem village ticking
	}
	
	@Override
	protected void applyEntityAttributes() {
	    super.applyEntityAttributes(); 

	   // standard attributes registered to EntityLivingBase
	   // getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
	   getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4D);
	   // getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.8D);
	   // getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);

	   // need to register any additional attributes
	   // getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
	   // getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
	}
	
	@Override
    protected void entityInit() {
        super.entityInit();

        dataWatcher.addObject(FOLLOWING, (byte) 1); // following
        dataWatcher.addObject(CHASE, (int) 32); // chase range
        dataWatcher.addObject(STATION, (int) 8); // station distance
        dataWatcher.addObject(OWNER, ""); // owner name
    }
	
	public boolean isFollowing() {
		return dataWatcher.getWatchableObjectByte(FOLLOWING) != 0;
	}
	
	public void setFollowing(boolean following) {
		dataWatcher.updateObject(FOLLOWING, (byte) (following ? 1 : 0));
		if (following) detachHome();
	}
	
	public int getChaseRange() {
		return dataWatcher.getWatchableObjectInt(CHASE);
	}
	
	public void setChaseRange(int val) {
		dataWatcher.updateObject(CHASE, val);
	}
	
	@Override
	public int getStationRadius() {
		return dataWatcher.getWatchableObjectInt(STATION);
	}
	
	public void setStationRadius(int val) {
		dataWatcher.updateObject(STATION, val);
	}
	
	@Override
	public String getOwnerName() {
		return dataWatcher.getWatchableObjectString(OWNER);
	}
	
	public void setOwnerName(String name) {
		dataWatcher.updateObject(OWNER, name);
		this.setCustomNameTag(name);
	}
	
	public EntityPlayer getOwner() {
		return worldObj.getPlayerEntityByName(getOwnerName());
	}
	
	@Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		
		nbt.setString("ownerName", getOwnerName());
		writeGuardStance(nbt);		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		setOwnerName(nbt.getString("ownerName"));
		readGuardStance(nbt);
	}
	
	public void writeGuardStance(NBTTagCompound nbt) {
    	EntityStoneGolem.writeGuardStance(nbt, getChaseRange(), getStationRadius(), isFollowing());
    }

	public static void writeGuardStance(NBTTagCompound nbt, int chaseRange, int stationRadius, boolean following) {
		nbt.setInteger("chaseRange", chaseRange);
		nbt.setInteger("stationRadius", stationRadius);
		nbt.setBoolean("following", following);
	}
	
	public void readGuardStance(NBTTagCompound nbt) {
		int stationRadius = nbt.getInteger("stationRadius");
		if (stationRadius > 0) {
			if (!hasHome()) {
				Vec3 pos = this.getPosition(0);
				setHomeArea((int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord, stationRadius);
			} else {
				setStationRadius(stationRadius);
			}
		} else if (stationRadius <= 0) {
			detachHome();
		}
		
		setChaseRange(nbt.getInteger("chaseRange"));
		setFollowing(nbt.getBoolean("following"));
	}
	
	public boolean isSuitableTarget(Entity e) {
		if (e == null) {
			return false;
		} else if (e instanceof EntityMob && !(e instanceof EntityCreeper)) {
			return true;
		} else {
			String factionName = GuardEventHandler.getFactionName(e);
			if (factionName == null) return false;
			else return GuardEventHandler.getRelations(getOwnerName(), factionName) < 0;
		}
	}
	
	public boolean shouldAttack() {
		return isSuitableTarget(getAttackTarget());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setAttackTarget(EntityLivingBase entity) {
	    // Try targeting old target's family
		EntityLivingBase target = getAttackTarget();
	    if (entity == null && target instanceof IOwned) {
        	String targetOwner = ((IOwned) target).getOwnerName();
        	
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(target.posX-20, target.posY-10, target.posZ-20, target.posX+20, target.posY+10, target.posZ+20);
            List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
            for (EntityLivingBase newTarget: list) {
            	// Target from same owner
            	String factionName = GuardEventHandler.getFactionName(newTarget);
                if (factionName != null && factionName.equals(targetOwner)) {
                    super.setAttackTarget(newTarget);
                    return;
                }
            }
	    }
	    
	    // Otherwise default
	    super.setAttackTarget(entity);
	}
	
	@Override
	public void onDeath(DamageSource sourceObj) {
		if (sourceObj.getSourceOfDamage() != null) {
			Entity sourceEntity = sourceObj.getSourceOfDamage();
			String factionName = GuardEventHandler.getFactionName(sourceEntity);
			if (factionName != null) {
				// Killing guard causes -10 reputation
				GuardEventHandler.updateRelations(worldObj, getOwnerName(), factionName, -10);
			}
		}
		
		GuardEventHandler.addNumGuards(getOwnerName(), -1);
	}
	
	@Override
	protected void dropFewItems(boolean par1, int par2) { }
	
	@Override
    public void setHomeArea(int x, int y, int z, int radius) {
        super.setHomeArea(x, y, z, radius);
        setStationRadius(radius);
    }
	
	@Override
    public void detachHome() {
        super.detachHome();
        
        boolean hasHome = this.hasHome();
        if (getStationRadius() > 0) {
        	setStationRadius(0);
        }
    }
	
	@Override
    public boolean interact(EntityPlayer player) {		
        if (player.getCommandSenderName().equals(getOwnerName())) {
            openGui(player);
            return true;
        } else {
        	return false;
        }
    }

    protected void openGui(EntityPlayer player) {
        if (worldObj.isRemote) {
        	player.openGui(Guards.instance, GuiHandler.GUARD, worldObj, getEntityId(), 0, 0);
        }
    }
}
