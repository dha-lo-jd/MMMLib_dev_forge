package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MMM_TextureBox extends MMM_TextureBoxBase {

	/**
	 * �e�N�X�`���p�b�N�̖��́A���f���w�莌�̑O�܂ł̕�����B
	 */
	public String packegeName;
	/**
	 * �e�N�X�`���t�@�C���̃t�@�C�������X�g�B
	 */
	public Map<Integer, String> textures;
	/**
	 * �A�[�}�[�t�@�C���̃t�@�C�������X�g�B
	 */
	public Map<String, Map<Integer, String>> armors;
	/**
	 * ���f���w�莌
	 */
	public String modelName;
	/**
	 * �}���`���f���N���X
	 */
	public MMM_ModelMultiBase[] models;
	/**
	 * pName, pTextureDir, pClassPrefix
	 */
	public String[] textureDir;
	/**
	 * �e�N�X�`���̊i�[����Ă���p�b�N�̖��O�i���f���Ɋ֌W�Ȃ��j
	 */
	public String fileName;



	public MMM_TextureBox() {
		textures = new HashMap<Integer, String>();
		armors = new TreeMap<String, Map<Integer, String>>();
		modelHeight = modelWidth = modelYOffset = modelMountedYOffset = 0.0F;
	}

	public MMM_TextureBox(String pTextureName, String[] pSearch) {
		this();
		textureName = pTextureName;
		fileName = pTextureName;
		int li = pTextureName.lastIndexOf("_");
		if (li > -1) {
			packegeName = pTextureName.substring(0, li);
			modelName = pTextureName.substring(li + 1);
		} else {
			packegeName = pTextureName;
			modelName = "";
		}
		textureDir = pSearch;
	}

	public void setModels(String pModelName, MMM_ModelMultiBase[] pModels, MMM_ModelMultiBase[] pDefModels) {
		modelName = pModelName;
		models = pModels == null ? pDefModels : pModels;
		textureName = (new StringBuilder()).append(packegeName).append("_").append(modelName).toString();
	}

	/**
	 * �e�N�X�`���̃t���p�X��Ԃ��B
	 * �o�^�C���f�b�N�X�������ꍇ��NULL��Ԃ��B
	 */
	public String getTextureName(int pIndex) {
		if (textures.containsKey(pIndex)) {
			if (textureDir != null) {
				return (new StringBuilder()).append(textureDir[1]).append(fileName.replace('.', '/')).append(textures.get(pIndex)).toString();
			}
		}
		return null;
	}

	public String getArmorTextureName(boolean pInner, ItemStack itemstack) {
		// index��0x40,0x50�ԑ�
		if (armors.isEmpty() || itemstack == null) return null;
		if (!(itemstack.getItem() instanceof ItemArmor)) return null;
		
		int l = 0;
		if (itemstack.getMaxDamage() > 0) {
			l = (10 * itemstack.getItemDamage() / itemstack.getMaxDamage());
		}
		return getArmorTextureName(pInner, MMM_TextureManager.armorFilenamePrefix[((ItemArmor)itemstack.getItem()).renderIndex], l);
	}
	public String getArmorTextureName(boolean pInner, String pArmorPrefix, int pDamage) {
		// index��0x40,0x50�ԑ�
		if (armors.isEmpty() || pArmorPrefix == null) return null;
		
		Map<Integer, String> m = armors.get(pArmorPrefix);
		if (m == null) {
			m = armors.get("default");
			if (m == null) {
//				return null;
				m = (Map)armors.values().toArray()[0];
			}
		}
		String ls = null;
		int lindex = pInner ? MMM_TextureManager.tx_armor1 : MMM_TextureManager.tx_armor2;
		for (int i = lindex + pDamage; i >= lindex; i--) {
			ls = m.get(i);
			if (ls != null) break;
		}
		if (ls == null) {
			return null;
		} else {
			return (new StringBuilder()).append(textureDir[1]).append(fileName.replace('.', '/')).append(ls).toString();
		}
	}

	/**
	 * �_��F�̗L�����r�b�g�z��ɂ��ĕԂ�
	 */
	@Override
	public int getContractColorBits() {
		int li = 0;
		for (Integer i : textures.keySet()) {
			if (i >= 0x00 && i <= 0x0f) {
				li |= 1 << (i & 0x0f);
			}
		}
		return li;
	}
	/**
	 * �쐶�F�̗L�����r�b�g�z��ɂ��ĕԂ�
	 */
	@Override
	public int getWildColorBits() {
		int li = 0;
		for (Integer i : textures.keySet()) {
			if (i >= MMM_TextureManager.tx_wild && i <= (MMM_TextureManager.tx_wild + 0x0f)) {
				li |= 1 << (i & 0x0f);
			}
		}
		return li;
	}

	public boolean hasColor(int pIndex) {
		return textures.containsKey(pIndex);
	}

	public boolean hasColor(int pIndex, boolean pContract) {
		return textures.containsKey(pIndex + (pContract ? 0 : MMM_TextureManager.tx_wild));
	}

	public boolean hasArmor() {
		return !armors.isEmpty();
	}

	@Override
	public float getHeight() {
		return models != null ? models[0].getHeight() : modelHeight;
	}

	@Override
	public float getWidth() {
		return models != null ? models[0].getWidth() : modelWidth;
	}

	@Override
	public float getYOffset() {
		return models != null ? models[0].getyOffset() : modelYOffset;
	}

	@Override
	public float getMountedYOffset() {
		return models != null ? models[0].getMountedYOffset() : modelMountedYOffset;
	}

	public MMM_TextureBox duplicate() {
		MMM_TextureBox lbox = new MMM_TextureBox();
		lbox.textureName = textureName;
		lbox.packegeName = packegeName;
		lbox.fileName = fileName;
		lbox.modelName = modelName;
		lbox.textureDir = textureDir;
		lbox.textures = textures;
		lbox.armors = armors;
		lbox.models = models;
		
		return lbox;
	}

}