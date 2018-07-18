package net.survival.world.gen.layer;

public interface GenLayerFactory
{
    GenLayer create(int lengthX, int lengthZ, long seed);

    public static GenLayer createBiomeLayer(int lengthX, int lengthZ, long baseSeed) {
        // TODO: Replace this mess.
        return
                new FuzzyMagnifiedGenLayer.Factory(
                        new FuzzyMagnifiedGenLayer.Factory(
                                new FuzzyMagnifiedGenLayer.Factory(
                                        new FuzzyMagnifiedGenLayer.Factory(
                                                new FuzzyMagnifiedGenLayer.Factory(
                                                        new FuzzyMagnifiedGenLayer.Factory(
                                                                new FuzzyMagnifiedGenLayer.Factory(
                                                                        new AdditionalLandGenLayer.Factory(
                                                                                new FuzzyMagnifiedGenLayer.Factory(
                                                                                        new LandGenLayer.Factory()
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ).create(lengthX, lengthZ, baseSeed);
    }
    
    public static GenLayer createStoneLayer(int lengthX, int lengthZ, long baseSeed) {
        // TODO: Replace this mess.
        return
                new FuzzyMagnifiedGenLayer.Factory(
                        new FuzzyMagnifiedGenLayer.Factory(
                                new FuzzyMagnifiedGenLayer.Factory(
                                        new FuzzyMagnifiedGenLayer.Factory(
                                                new FuzzyMagnifiedGenLayer.Factory(
                                                        new FuzzyMagnifiedGenLayer.Factory(
                                                                new FuzzyMagnifiedGenLayer.Factory(
                                                                        new FuzzyMagnifiedGenLayer.Factory(
                                                                                new StoneGenLayer.Factory()
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ).create(lengthX, lengthZ, baseSeed);
    }
}