package goldenage.potatotech.blocks.entities;

public class TileEntityDiamondPipe extends TileEntityPipe {
	public TileEntityDiamondPipe() {
		super();

		maxInputTimer = 2;
		inputTimer = maxInputTimer;
		maxPipeStackTimer = 1;
	}
}
