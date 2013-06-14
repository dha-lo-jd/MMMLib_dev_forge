package net.minecraft.src;


public class LMM_EntityLittleMaidAvatar extends EntityPlayer {

	public LMM_EntityLittleMaid avatar;
	/** �����H **/
	public boolean isItemTrigger;
	/** �����H **/
	public boolean isItemReload;
	/** �����H **/
	private boolean isItemPreReload;
	private double appendX;
	private double appendY;
	private double appendZ;

	
	public LMM_EntityLittleMaidAvatar(World par1World, LMM_EntityLittleMaid par2EntityLittleMaid) {
		super(par1World);
		
		// �����ݒ�
		avatar = par2EntityLittleMaid;
		
		inventory = avatar.maidInventory;
		inventory.player = this;
		username = "";
	}

	@Override
	public float getEyeHeight() {
		return avatar.getEyeHeight();
	}

	@Override
	protected String getLivingSound() {
		return null;
	}

	@Override
	protected String getHurtSound() {
		return null;
	}

	@Override
	protected String getDeathSound() {
		return null;
	}

	@Override
	public void sendChatToPlayer(String var1) {
	}

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return false;
	}

	@Override
	public void triggerAchievement(StatBase par1StatBase) {
		// �A�`�[�u�����g�E��
	}

	@Override
	public void addStat(StatBase par1StatBase, int par2) {}

	@Override
	public void addScore(int par1) {}

	@Override
	public void onUpdate() {
//		posX = avatar.posX;
		EntityPlayer lep = avatar.getMaidMasterEntity();
		entityId = avatar.entityId;
		
		if (lep != null) {
			capabilities.isCreativeMode = lep.capabilities.isCreativeMode;
		}
		
		if (xpCooldown > 0) {
			xpCooldown--;
		}
		avatar.experienceValue = experienceTotal;
		
	}

	@Override
	public void onItemPickup(Entity entity, int i) {
		// �A�C�e������̃G�t�F�N�g
		if (worldObj.isRemote) {
			// Client
			LMM_Client.onItemPickup(this, entity, i);
		} else {
			super.onItemPickup(entity, i);
		}
	}

	@Override
	public void onCriticalHit(Entity par1Entity) {
		if (worldObj.isRemote) {
			// Client
			LMM_Client.onCriticalHit(this, par1Entity);
		} else {
			((WorldServer)worldObj).getEntityTracker().sendPacketToAllAssociatedPlayers(avatar, new Packet18Animation(par1Entity, 6));
		}
	}

	@Override
	public void onEnchantmentCritical(Entity par1Entity) {
		if (worldObj.isRemote) {
			LMM_Client.onEnchantmentCritical(this, par1Entity);
		} else {
			((WorldServer)worldObj).getEntityTracker().sendPacketToAllAssociatedPlayers(avatar, new Packet18Animation(par1Entity, 7));
		}
	}

	@Override
	public void attackTargetEntityWithCurrentItem(Entity par1Entity) {
		// TODO:
		int ll = 0;
		if (par1Entity instanceof EntityLiving) {
			ll = ((EntityLiving)par1Entity).health;
		}
		super.attackTargetEntityWithCurrentItem(par1Entity);
		if (par1Entity instanceof EntityLiving) {
			((EntityLiving)par1Entity).setRevengeTarget(avatar);
		}
		if (par1Entity instanceof EntityCreature) {
			((EntityCreature)par1Entity).setTarget(avatar);
		}
		if (ll > 0) {
			mod_LMM_littleMaidMob.Debug(String.format("ID:%d Given Damege:%d", avatar.entityId, ll - ((EntityLiving)par1Entity).health));
		}
	}

	@Override
	public ItemStack getCurrentEquippedItem() {
		return avatar.getCurrentEquippedItem();
	}

	@Override
	public ItemStack getCurrentArmor(int par1) {
		return avatar.getCurrentArmor(par1);
	}

	@Override
	public ItemStack getCurrentItemOrArmor(int par1) {
		return avatar.getCurrentItemOrArmor(par1);
	}

	@Override
	public ItemStack[] getLastActiveItems() {
		return avatar.getLastActiveItems();
	}

	@Override
	protected void alertWolves(EntityLiving par1EntityLiving, boolean par2) {
		// ������ݒ肵���Ⴄ�ƒʏ�ł͂ʂ�ۗ�������
	}

	@Override
	public void destroyCurrentEquippedItem() {
		// �A�C�e������ꂽ�̂Ŏ��̑�����I��
		// TODO:�A���AForge���Ńv���[���[�C�x���g��ݒ肵�Ă�����̂��Ƃʂ�ۗ�������̂ŁA���炩�̑΍􂪕K�v�B
//		super.destroyCurrentEquippedItem();
		inventory.setInventorySlotContents(inventory.currentItem, (ItemStack)null);
		avatar.getNextEquipItem();
	}

	@Override
	public void onKillEntity(EntityLiving entityliving) {
		avatar.onKillEntity(entityliving);
	}

	protected Entity getEntityServer() {
		return worldObj.isRemote ? null : this;
	}

	// Item�g�p�֘A

	public int getItemInUseDuration(int pIndex) {
		return avatar.getSwingStatus(pIndex).getItemInUseDuration();
	}
	@Deprecated
	@Override
	public int getItemInUseDuration() {
		return getItemInUseDuration(avatar.maidDominantArm);
	}

	public ItemStack getItemInUse(int pIndex) {
		return avatar.getSwingStatus(pIndex).getItemInUse();
	}
	@Deprecated
	@Override
	public ItemStack getItemInUse() {
		return getItemInUse(avatar.maidDominantArm);
	}

	public int getItemInUseCount(int pIndex) {
		return avatar.getSwingStatus(pIndex).getItemInUseCount();
	}
	@Deprecated
	@Override
	public int getItemInUseCount() {
		return getItemInUseCount(avatar.maidDominantArm);
	}

	public boolean isUsingItem(int pIndex) {
		return avatar.getSwingStatus(pIndex).isUsingItem();
	}
	@Deprecated
	@Override
	public boolean isUsingItem() {
		return isUsingItem(avatar.maidDominantArm);
	}
	public boolean isUsingItemLittleMaid() {
		return super.isUsingItem() | isItemTrigger;
	}

	public void clearItemInUse(int pIndex) {
		avatar.getSwingStatus(pIndex).clearItemInUse(getEntityServer());
	}
	@Deprecated
	@Override
	public void clearItemInUse() {
//		super.clearItemInUse();
		isItemTrigger = false;
		isItemReload = isItemPreReload = false;
		clearItemInUse(avatar.maidDominantArm);
	}

	public void stopUsingItem(int pIndex) {
		avatar.getSwingStatus(pIndex).stopUsingItem(getEntityServer());
	}
	@Deprecated
	@Override
	public void stopUsingItem() {
//		super.stopUsingItem();
		isItemTrigger = false;
		isItemReload = isItemPreReload = false;
		stopUsingItem(avatar.maidDominantArm);
	}

	public void setItemInUse(int pIndex, ItemStack itemstack, int i) {
		avatar.getSwingStatus(pIndex).setItemInUse(itemstack, i, getEntityServer());
	}
	@Deprecated
	@Override
	public void setItemInUse(ItemStack itemstack, int i) {
//		super.setItemInUse(itemstack, i);
		isItemTrigger = true;
		isItemReload = isItemPreReload;
		setItemInUse(avatar.maidDominantArm, itemstack, i);
	}

	@Override
	public void setEating(boolean par1) {
		avatar.setEating(par1);
	}

	@Override
	public boolean isEating() {
		return avatar.isEating();
	}

	@Override
	public void setAir(int par1) {
		avatar.setAir(par1);
	}

	@Override
	public int getAir() {
		return avatar.getAir();
	}

	@Override
	public void setFire(int par1) {
		avatar.setFire(par1);
	}

	@Override
	public boolean isBurning() {
		return avatar.isBurning();
	}

	@Override
	protected void setFlag(int par1, boolean par2) {
		avatar.setFlag(par1, par2);
	}

	@Override
	public void setEntityHealth(int par1) {
		super.setEntityHealth(par1);
//		avatar.setEntityHealth(par1);
	}

	@Override
	public boolean isBlocking() {
		return avatar.isBlocking();
	}

	@Override
	public void playSound(String par1Str, float par2, float par3) {
		avatar.playSound(par1Str, par2, par3);
	}

	public void getValue() {
		// EntityLittleMaid����l���R�s�[
		setPosition(avatar.posX, avatar.posY, avatar.posZ);
		prevPosX = avatar.prevPosX;
		prevPosY = avatar.prevPosY;
		prevPosZ = avatar.prevPosZ;
		rotationPitch = avatar.rotationPitch;
		rotationYaw = avatar.rotationYaw;
		prevRotationPitch = avatar.prevRotationPitch;
		prevRotationYaw = avatar.prevRotationYaw;
		yOffset = avatar.yOffset;
		renderYawOffset = avatar.renderYawOffset;
		prevRenderYawOffset = avatar.prevRenderYawOffset;
		rotationYawHead = avatar.rotationYawHead;
		attackTime = avatar.attackTime;
	}

	public void getValueVector(double atx, double aty, double atz, double atl) {
		// EntityLittleMaid����l���R�s�[
		double l = MathHelper.sqrt_double(atl);
		appendX = atx / l;
		appendY = aty / l;
		appendZ = atz / l;
		posX = avatar.posX + appendX;
		posY = avatar.posY + appendY;
		posZ = avatar.posZ + appendZ;
		prevPosX = avatar.prevPosX + appendX;
		prevPosY = avatar.prevPosY + appendY;
		prevPosZ = avatar.prevPosZ + appendZ;
		rotationPitch		= avatar.rotationPitch;
		prevRotationPitch	= avatar.prevRotationPitch;
		rotationYaw			= avatar.rotationYaw;
		prevRotationYaw		= avatar.prevRotationYaw;
		renderYawOffset		= avatar.renderYawOffset;
		prevRenderYawOffset	= avatar.prevRenderYawOffset;
		rotationYawHead		= avatar.rotationYawHead;
		prevRotationYawHead	= avatar.prevRotationYawHead;
		yOffset = avatar.yOffset;
		motionX = avatar.motionX;
		motionY = avatar.motionY;
		motionZ = avatar.motionZ;
		isSwingInProgress = avatar.getSwinging();
	}

	/**
	 * �ˌ��ǐ��p�Arotation�𓪂ɍ��킹��
	 */
	public void getValueVectorFire(double atx, double aty, double atz, double atl) {
		// EntityLittleMaid����l���R�s�[
		double l = MathHelper.sqrt_double(atl);
		appendX = atx / l;
		appendY = aty / l;
		appendZ = atz / l;
		posX = avatar.posX + appendX;
		posY = avatar.posY + appendY;
		posZ = avatar.posZ + appendZ;
		prevPosX = avatar.prevPosX + appendX;
		prevPosY = avatar.prevPosY + appendY;
		prevPosZ = avatar.prevPosZ + appendZ;
		rotationPitch		= updateDirection(avatar.rotationPitch);
		prevRotationPitch	= updateDirection(avatar.prevRotationPitch);
		rotationYaw			= updateDirection(avatar.rotationYawHead);
		prevRotationYaw		= updateDirection(avatar.prevRotationYawHead);
		renderYawOffset		= updateDirection(avatar.renderYawOffset);
		prevRenderYawOffset	= updateDirection(avatar.prevRenderYawOffset);
		rotationYawHead		= updateDirection(avatar.rotationYawHead);
		prevRotationYawHead	= updateDirection(avatar.prevRotationYawHead);
		yOffset = avatar.yOffset;
		motionX = avatar.motionX;
		motionY = avatar.motionY;
		motionZ = avatar.motionZ;
		isSwingInProgress = avatar.getSwinging();
	}

	protected float updateDirection(float pValue) {
		pValue %= 360F;
		if (pValue < 0) pValue += 360F;
		return pValue;
	}


	public void setValue() {
		// EntityLittleMiad�֒l���R�s�[
		avatar.setPosition(posX, posY, posZ);
		avatar.prevPosX = prevPosX;
		avatar.prevPosY = prevPosY;
		avatar.prevPosZ = prevPosZ;
		avatar.rotationPitch = rotationPitch;
		avatar.rotationYaw = rotationYaw;
		avatar.prevRotationPitch = prevRotationPitch;
		avatar.prevRotationYaw = prevRotationYaw;
		avatar.yOffset = yOffset;
		avatar.renderYawOffset = renderYawOffset;
		avatar.prevRenderYawOffset = prevRenderYawOffset;
		avatar.getSwingStatusDominant().attackTime = avatar.attackTime = attackTime;
	}

	public void setValueRotation() {
		// EntityLittleMiad�֒l���R�s�[
		avatar.rotationPitch = rotationPitch;
		avatar.rotationYaw = rotationYaw;
		avatar.prevRotationPitch = prevRotationPitch;
		avatar.prevRotationYaw = prevRotationYaw;
		avatar.renderYawOffset = renderYawOffset;
		avatar.prevRenderYawOffset = prevRenderYawOffset;
		avatar.motionX = motionX;
		avatar.motionY = motionY;
		avatar.motionZ = motionZ;
		if (isSwingInProgress) avatar.setSwinging(LMM_EnumSound.Null);
		
	}

	public void setValueVector() {
		// EntityLittleMiad�֒l���R�s�[
		avatar.posX = posX - appendX;
		avatar.posY = posY - appendY;
		avatar.posZ = posZ - appendZ;
		avatar.prevPosX = prevPosX - appendX;
		avatar.prevPosY = prevPosY - appendY;
		avatar.prevPosZ = prevPosZ - appendZ;
		avatar.rotationPitch	 = rotationPitch;
		avatar.prevRotationPitch = prevRotationPitch;
//		avatar.rotationYaw			= rotationYaw;
//		avatar.prevRotationYaw		= prevRotationYaw;
//		avatar.renderYawOffset		= renderYawOffset;
//		avatar.prevRenderYawOffset	= prevRenderYawOffset;
		avatar.motionX = motionX;
		avatar.motionY = motionY;
		avatar.motionZ = motionZ;
		if (isSwingInProgress) avatar.setSwinging(LMM_EnumSound.Null);
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return null;
	}


}