package goldenage.potatotech.blocks.entities;

public class TileEntityGoldPipe extends TileEntityPipe {
	public TileEntityGoldPipe() {
		super();

		maxInputTimer = 6;
		inputTimer = maxInputTimer;
		maxPipeStackTimer = 3;
	}
}
