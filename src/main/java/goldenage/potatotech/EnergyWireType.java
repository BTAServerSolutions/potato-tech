package goldenage.potatotech;

import net.minecraft.core.item.Item;

public enum EnergyWireType {
	LV("potatotech:lv_wire", 64, 1.0f, 0.57f, 0.57f, 0.88f, 0.22f, 0.22f),
	MV("potatotech:mv_wire", 128, 1.0f, 0.92f, 0.35f, 0.72f, 0.48f, 0.08f);

	private final String id;
	private final int transferRate;
	private final float[] colors;

	EnergyWireType(String id, int transferRate, float... colors) {
		this.id = id;
		this.transferRate = transferRate;
		this.colors = colors;
	}

	public String getId() {
		return id;
	}

	public int getTransferRate() {
		return transferRate;
	}

	public float[] getColors() {
		return colors;
	}

	public Item getSpoolItem() {
		return this == MV ? PTItems.wireSpoolMV : PTItems.wireSpool;
	}

	public static EnergyWireType fromId(String id) {
		for (EnergyWireType type : values()) {
			if (type.id.equals(id)) {
				return type;
			}
		}
		return LV;
	}
}
