package net.minecraft.src;

import static net.minecraft.src.mod_MMM_MMMLib.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;

public class MMM_Helper {

	public static final Package fpackage;
	public static final boolean isClient;
	public static final boolean isForge = ModLoader.isModLoaded("Forge");
	public static final Minecraft mc;
	public static Method methGetSmeltingResultForge = null;
	public static final String packegeBase;
	public static final Map<Class, Class> replaceEntitys = new HashMap<Class, Class>();

	static {
		fpackage = ModLoader.class.getPackage();
		packegeBase = fpackage == null ? "" : fpackage.getName().concat(".");

		Minecraft lm = null;
		try {
			lm = ModLoader.getMinecraftInstance();
		} catch (Exception e) {
			//			e.printStackTrace();
		} catch (Error e) {
			//			e.printStackTrace();
		}
		mc = lm;
		isClient = mc != null;
		if (isForge) {
			try {
				methGetSmeltingResultForge = FurnaceRecipes.class.getMethod("getExperience", ItemStack.class);
			} catch (Exception e) {
			}
		}

	}

	// 状況判断要関数群
	public static boolean canBlockBeSeen(Entity pEntity, int x, int y, int z, boolean toTop, boolean do1, boolean do2) {
		// ブロックの可視判定
		Vec3 vec3d = Vec3.createVectorHelper(pEntity.posX, pEntity.posY + pEntity.getEyeHeight(), pEntity.posZ);
		Vec3 vec3d1 = Vec3.createVectorHelper(x + 0.5D, y + (toTop ? 0.9D : 0.5D), z + 0.5D);

		MovingObjectPosition movingobjectposition = pEntity.worldObj.rayTraceBlocks_do_do(vec3d, vec3d1, do1, do2);
		if (movingobjectposition == null) {
			return false;
		}
		if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE) {
			if (movingobjectposition.blockX == MathHelper.floor_double(vec3d1.xCoord) &&
					movingobjectposition.blockY == MathHelper.floor_double(vec3d1.yCoord) &&
					movingobjectposition.blockZ == MathHelper.floor_double(vec3d1.zCoord)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定されたリビジョンよりも古ければ例外を投げてストップ
	 */
	public static void checkRevision(String pRev) {
		if (convRevision() < convRevision(pRev)) {
			// 適合バージョンではないのでストップ
			ModLoader.getLogger().warning("you must check MMMLib revision.");
			throw new RuntimeException("The revision of MMMLib is old.");
		}
	}

	public static float convRevision() {
		return convRevision(mod_MMM_MMMLib.Revision);
	}

	public static float convRevision(String pRev) {
		Pattern lp = Pattern.compile("(\\d+)(\\w*)");
		Matcher lm = lp.matcher(pRev);
		float lf = 0;
		if (lm.find()) {
			lf = Integer.valueOf(lm.group(1));
			if (!lm.group(2).isEmpty()) {
				lf += (lm.group(2).charAt(0) - 96) * 0.01;
			}
		}
		return lf;
	}

	/**
	 * プレーヤのインベントリからアイテムを減らす
	 */
	public static ItemStack decPlayerInventory(EntityPlayer par1EntityPlayer, int par2Index, int par3DecCount) {
		if (par1EntityPlayer == null) {
			return null;
		}

		if (par2Index == -1) {
			par2Index = par1EntityPlayer.inventory.currentItem;
		}
		ItemStack itemstack1 = par1EntityPlayer.inventory.getStackInSlot(par2Index);
		if (itemstack1 == null) {
			return null;
		}

		if (!par1EntityPlayer.capabilities.isCreativeMode) {
			// クリエイティブだと減らない
			itemstack1.stackSize -= par3DecCount;
		}

		if (itemstack1.getItem() instanceof ItemPotion) {
			if (itemstack1.stackSize <= 0) {
				par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem,
						new ItemStack(Item.glassBottle, par3DecCount));
				return null;
			} else {
				par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.glassBottle, par3DecCount));
			}
		} else {
			if (itemstack1.stackSize <= 0) {
				par1EntityPlayer.inventory.setInventorySlotContents(par2Index, null);
				return null;
			}
		}

		return itemstack1;
	}

	/**
	 * 変数「avatar」から値を取り出し戻り値として返す。
	 * avatarが存在しない場合は元の値を返す。
	 * avatarはEntityLiving互換。
	 */
	public static Entity getAvatarEntity(Entity pEntity) {
		// littleMaid用コードここから
		try {
			// 射手の情報をEntityLittleMaidAvatarからEntityLittleMaidへ置き換える
			Field field = pEntity.getClass().getField("avatar");
			pEntity = (EntityLiving) field.get(pEntity);
		} catch (NoSuchFieldException e) {
		} catch (Exception e) {
		}
		// ここまで
		return pEntity;
	}

	/**
	 * 変数「maidAvatar」から値を取り出し戻り値として返す。
	 * maidAvatarが存在しない場合は元の値を返す。
	 * maidAvatarはEntityPlayer互換。
	 */
	public static Entity getAvatarPlayer(Entity entity) {
		// メイドさんチェック
		try {
			Field field = entity.getClass().getField("maidAvatar");
			entity = (Entity) field.get(entity);
		} catch (NoSuchFieldException e) {
		} catch (Exception e) {
		}
		return entity;
	}

	/**
	 * Entityを返す。
	 */
	public static Entity getEntity(byte[] pData, int pIndex, World pWorld) {
		return pWorld.getEntityByID(MMM_Helper.getInt(pData, pIndex));
	}

	public static float getFloat(byte[] pData, int pIndex) {
		return Float.intBitsToFloat(getInt(pData, pIndex));
	}

	/**
	 * Forge用クラス獲得。
	 */
	public static Class getForgeClass(BaseMod pMod, String pName) {
		if (isForge) {
			pName = pName.concat("_Forge");
		}
		return getNameOfClass(pName);
	}

	public static int getInt(byte[] pData, int pIndex) {
		return (pData[pIndex + 3] & 0xff) | ((pData[pIndex + 2] & 0xff) << 8) | ((pData[pIndex + 1] & 0xff) << 16)
				| ((pData[pIndex + 0] & 0xff) << 24);
	}

	/**
	 * 名前からクラスを獲得する
	 */
	public static Class getNameOfClass(String pName) {
		if (fpackage != null) {
			pName = fpackage.getName() + "." + pName;
		}
		Class lclass = null;
		try {
			lclass = Class.forName(pName);
		} catch (Exception e) {
		}

		return lclass;
	}

	/**
	 * Modloader環境下で空いているEntityIDを返す。
	 * 有効な値を獲得できなければ-1を返す。
	 */
	public static int getNextEntityID(boolean isLiving) {
		if (isLiving) {
			// 生物用
			for (int li = 1; li < 256; li++) {
				if (EntityList.getClassFromID(li) == null) {
					return li;
				}
			}
		} else {
			// 物用
			for (int li = mod_MMM_MMMLib.startVehicleEntityID; li < mod_MMM_MMMLib.startVehicleEntityID + 2048; li++) {
				if (EntityList.getClassFromID(li) == null) {
					return li;
				}
			}
		}
		return -1;
	}

	public static short getShort(byte[] pData, int pIndex) {
		return (short) ((pData[pIndex] & 0xff) | ((pData[pIndex + 1] & 0xff) << 8));
	}

	/**
	 * Forge対策用のメソッド
	 */
	public static ItemStack getSmeltingResult(ItemStack pItemstack) {
		if (methGetSmeltingResultForge != null) {
			try {
				return (ItemStack) methGetSmeltingResultForge.invoke(FurnaceRecipes.smelting(), pItemstack);
			} catch (Exception e) {
			}
		}
		return FurnaceRecipes.smelting().getSmeltingResult(pItemstack.itemID);
	}

	public static String getStr(byte[] pData, int pIndex) {
		return getStr(pData, pIndex, pData.length - pIndex);
	}

	public static String getStr(byte[] pData, int pIndex, int pLen) {
		String ls = new String(pData, pIndex, pLen);
		return ls;
	}

	/**
	 * 現在の実行環境がローカルかどうかを判定する。
	 */
	public static boolean isLocalPlay() {
		return isClient && mc.isIntegratedServerRunning();
	}

	/**
	 * バイオームの設定Entityを置き換えられたEntityへ置き換える。
	 * 基本的にMMMLib以外からは呼ばれない。
	 */
	public static void replaceBaiomeSpawn() {
		// バイオームの発生処理をのっとる
		if (replaceEntitys.isEmpty()) {
			return;
		}
		for (BiomeGenBase element : BiomeGenBase.biomeList) {
			if (element == null) {
				continue;
			}
			List<SpawnListEntry> mobs;
			Debug("ReplaceBaiomeSpawn:%s", element.biomeName);
			Debug("[Creature]");
			replaceCreatureList(element.spawnableCreatureList);
			Debug("[WaterCreature]");
			replaceCreatureList(element.spawnableWaterCreatureList);
			Debug("[CaveCreature]");
			replaceCreatureList(element.spawnableCaveCreatureList);
			Debug("[Monster]");
			replaceCreatureList(element.spawnableMonsterList);
		}
	}

	private static void replaceCreatureList(List<SpawnListEntry> pMobs) {
		if (pMobs == null) {
			return;
		}
		for (Entry<Class, Class> le : replaceEntitys.entrySet()) {
			for (int j = 0; j < pMobs.size(); j++) {
				if (pMobs.get(j).entityClass == le.getKey()) {
					pMobs.get(j).entityClass = le.getValue();
					Debug("ReplaceCreatureList: %s -> %s", le.getKey().getSimpleName(), le.getValue().getSimpleName());
				}
			}
		}
	}

	/**
	 * EntityListに登録されていいるEntityを置き換える。
	 */
	public static void replaceEntityList(Class pSrcClass, Class pDestClass) {
		// EntityList登録情報を置き換え
		try {
			// stringToClassMapping
			Map lmap;
			int lint;
			String ls;
			lmap = (Map) ModLoader.getPrivateValue(EntityList.class, null, 0);
			for (Entry<String, Class> le : ((Map<String, Class>) lmap).entrySet()) {
				if (le.getValue() == pSrcClass) {
					le.setValue(pDestClass);
				}
			}
			// classToStringMapping
			lmap = (Map) ModLoader.getPrivateValue(EntityList.class, null, 1);
			if (lmap.containsKey(pSrcClass)) {
				ls = (String) lmap.get(pSrcClass);
				lmap.remove(pSrcClass);
				lmap.put(pDestClass, ls);
			}
			// IDtoClassMapping
			lmap = (Map) ModLoader.getPrivateValue(EntityList.class, null, 2);
			for (Entry<Integer, Class> le : ((Map<Integer, Class>) lmap).entrySet()) {
				if (le.getValue() == pSrcClass) {
					le.setValue(pDestClass);
				}
			}
			// classToIDMapping
			lmap = (Map) ModLoader.getPrivateValue(EntityList.class, null, 3);
			if (lmap.containsKey(pSrcClass)) {
				lint = (Integer) lmap.get(pSrcClass);
				lmap.remove(pSrcClass);
				lmap.put(pDestClass, lint);
			}
			replaceEntitys.put(pSrcClass, pDestClass);
			Debug("Replace %s -> %s", pSrcClass.getSimpleName(), pDestClass.getSimpleName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setFloat(byte[] pData, int pIndex, float pVal) {
		setInt(pData, pIndex, Float.floatToIntBits(pVal));
	}

	public static void setInt(byte[] pData, int pIndex, int pVal) {
		pData[pIndex + 3] = (byte) (pVal & 0xff);
		pData[pIndex + 2] = (byte) ((pVal >>> 8) & 0xff);
		pData[pIndex + 1] = (byte) ((pVal >>> 16) & 0xff);
		pData[pIndex + 0] = (byte) ((pVal >>> 24) & 0xff);
	}

	public static boolean setPathToTile(EntityLiving pEntity, TileEntity pTarget, boolean flag) {
		// Tileまでのパスを作る
		PathNavigate lpn = pEntity.getNavigator();
		float lspeed = 0.3F;
		// 向きに合わせて距離を調整
		int i = (pTarget.yCoord == MathHelper.floor_double(pEntity.posY) && flag) ? 2 : 1;
		switch (pEntity.worldObj.getBlockMetadata(pTarget.xCoord, pTarget.yCoord, pTarget.zCoord)) {
		case 3:
			return lpn.tryMoveToXYZ(pTarget.xCoord, pTarget.yCoord, pTarget.zCoord + i, lspeed);
		case 2:
			return lpn.tryMoveToXYZ(pTarget.xCoord, pTarget.yCoord, pTarget.zCoord - i, lspeed);
		case 5:
			return lpn.tryMoveToXYZ(pTarget.xCoord + 1, pTarget.yCoord, pTarget.zCoord, lspeed);
		case 4:
			return lpn.tryMoveToXYZ(pTarget.xCoord - i, pTarget.yCoord, pTarget.zCoord, lspeed);
		default:
			return lpn.tryMoveToXYZ(pTarget.xCoord, pTarget.yCoord, pTarget.zCoord, lspeed);
		}
	}

	public static void setShort(byte[] pData, int pIndex, int pVal) {
		pData[pIndex++] = (byte) (pVal & 0xff);
		pData[pIndex] = (byte) ((pVal >>> 8) & 0xff);
	}

	public static void setStr(byte[] pData, int pIndex, String pVal) {
		byte[] lb = pVal.getBytes();
		for (int li = pIndex; li < pData.length; li++) {
			pData[li] = lb[li - pIndex];
		}
	}

	/**
	 * 送信用データのセット
	 */
	public static void setValue(byte[] pData, int pIndex, int pVal, int pSize) {
		for (int li = 0; li < pSize; li++) {
			pData[pIndex++] = (byte) (pVal & 0xff);
			pVal = pVal >>> 8;
		}
	}

	/**
	 * マルチ対応用。
	 * ItemStackに情報更新を行うと、サーバー側との差異からSlotのアップデートが行われる。
	 * その際、UsingItemの更新処理が行われないため違うアイテムに持替えられたと判定される。
	 * ここでは比較用に使われるスタックリストを強制的に書換える事により対応した。
	 */
	public static void updateCheckinghSlot(Entity pEntity, ItemStack pItemstack) {
		if (pEntity instanceof EntityPlayerMP) {
			// サーバー側でのみ処理
			EntityPlayerMP lep = (EntityPlayerMP) pEntity;
			Container lctr = lep.openContainer;
			for (int li = 0; li < lctr.inventorySlots.size(); li++) {
				ItemStack lis = lctr.getSlot(li).getStack();
				if (lis == pItemstack) {
					lctr.inventoryItemStacks.set(li, pItemstack.copy());
					break;
				}
			}
		}
	}

}
