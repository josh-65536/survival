package net.survival.gen;

import net.survival.block.Column;
import net.survival.block.ColumnPos;
import net.survival.block.ColumnProvider;
import net.survival.blocktype.BlockType;
import net.survival.gen.layer.GenLayer;
import net.survival.gen.layer.GenLayerFactory;
import net.survival.util.DoubleMap2D;
import net.survival.util.DoubleMap3D;
import net.survival.util.ImprovedNoiseGenerator3D;

public class DefaultColumnGenerator implements ColumnProvider
{
    private static final int NBLOCK_YLENGTH = Column.YLENGTH / 32;
    private static final int NBLOCK_ZLENGTH = Column.ZLENGTH / 4;
    private static final int NBLOCK_XLENGTH = Column.XLENGTH / 4;
    private static final int NMAP_YLENGTH = (Column.YLENGTH / NBLOCK_YLENGTH) + 1;
    private static final int NMAP_ZLENGTH = (Column.ZLENGTH / NBLOCK_ZLENGTH) + 1;
    private static final int NMAP_XLENGTH = (Column.XLENGTH / NBLOCK_XLENGTH) + 1;

    private static final double MAIN_NOISE_XSCALE = 1.0 / 128.0;
    private static final double MAIN_NOISE_YSCALE = 1.0 / 128.0;
    private static final double MAIN_NOISE_ZSCALE = 1.0 / 128.0;
    private static final int MAIN_NOISE_OCTAVE_COUNT = 6;

    private static final int BIOME_TRANSITION_XLENGTH = NBLOCK_XLENGTH * 2;
    private static final int BIOME_TRANSITION_ZLENGTH = NBLOCK_ZLENGTH * 2;
    private static final int BIOME_TRANSITION_AREA = BIOME_TRANSITION_XLENGTH
            * BIOME_TRANSITION_ZLENGTH;

    private static final int OCEAN_LEVEL = 64;

    private final ImprovedNoiseGenerator3D mainNoiseGenerator;
    private final GenLayer biomeLayer;

    private final DoubleMap3D densityMap;
    private final DoubleMap2D minElevationMap;
    private final DoubleMap2D elevationRangeMap;

    private final DefaultColumnDecorator columnDecorator;

    private final ColumnPrimer columnPrimer;
    private final int tempSolidID;
    private final int waterID;

    public DefaultColumnGenerator(long seed) {
        mainNoiseGenerator = new ImprovedNoiseGenerator3D(MAIN_NOISE_XSCALE, MAIN_NOISE_YSCALE,
                MAIN_NOISE_ZSCALE, MAIN_NOISE_OCTAVE_COUNT, seed);

        biomeLayer = GenLayerFactory.createBiomeLayer(Column.XLENGTH * 4, Column.ZLENGTH * 4, seed);

        densityMap = new DoubleMap3D(NMAP_XLENGTH, NMAP_YLENGTH, NMAP_ZLENGTH);
        minElevationMap = new DoubleMap2D(Column.XLENGTH, Column.ZLENGTH);
        elevationRangeMap = new DoubleMap2D(Column.XLENGTH, Column.ZLENGTH);

        columnDecorator = new DefaultColumnDecorator();

        columnPrimer = new ColumnPrimer();
        tempSolidID = BlockType.TEMP_SOLID.getFullID();
        waterID = BlockType.WATER.getFullID();
    }

    @Override
    public Column provideColumn(long hashedPos) {
        var cx = ColumnPos.columnXFromHashedPos(hashedPos);
        var cz = ColumnPos.columnZFromHashedPos(hashedPos);
        var offsetX = cx * (NMAP_XLENGTH - 1);
        var offsetZ = cz * (NMAP_ZLENGTH - 1);
        var globalX = ColumnPos.toGlobalX(cx, 0);
        var globalZ = ColumnPos.toGlobalZ(cz, 0);

        mainNoiseGenerator.generate(densityMap, offsetX, 0.0, offsetZ);
        biomeLayer.generate(globalX, globalZ);

        columnPrimer.clear();
        generateBase(columnPrimer);
        columnDecorator.decorate(hashedPos, columnPrimer, biomeLayer);

        return columnPrimer.toColumn();
    }

    private void generateBase(ColumnPrimer columnPrimer) {
        generateElevationMaps(minElevationMap, elevationRangeMap, columnPrimer, biomeLayer);

        for (var y = 0; y < Column.YLENGTH; ++y) {
            var noiseMapY = (double) y / NBLOCK_YLENGTH;

            for (var z = 0; z < Column.ZLENGTH; ++z) {
                var noiseMapZ = (double) z / NBLOCK_ZLENGTH;

                for (var x = 0; x < Column.XLENGTH; ++x) {
                    var noiseMapX = (double) x / NBLOCK_XLENGTH;

                    var density = densityMap.sampleLinear(noiseMapX, noiseMapY, noiseMapZ);

                    var minElevation = minElevationMap.sampleNearest(x, z);
                    var elevationRange = elevationRangeMap.sampleNearest(x, z);
                    var threshold = (y - minElevation) / elevationRange;

                    if (density >= threshold)
                        columnPrimer.setBlockFullID(x, y, z, tempSolidID);
                    else if (y <= OCEAN_LEVEL)
                        columnPrimer.setBlockFullID(x, y, z, waterID);
                }
            }
        }
    }

    private void generateElevationMaps(
            DoubleMap2D minElevationMap,
            DoubleMap2D elevationRangeMap,
            ColumnPrimer columnPrimer,
            GenLayer genLayer)
    {
        for (var z = 0; z < Column.ZLENGTH; ++z) {
            for (var x = 0; x < Column.XLENGTH; ++x) {
                var avgMinElevation = 0.0;
                var avgMaxElevation = 0.0;

                for (var subZ = 0; subZ < BIOME_TRANSITION_ZLENGTH; ++subZ) {
                    for (var subX = 0; subX < BIOME_TRANSITION_XLENGTH; ++subX) {
                        var biome = BiomeType.byID(genLayer.sampleNearest(x + subX, z + subZ));
                        avgMinElevation += biome.getMinElevation();
                        avgMaxElevation += biome.getMaxElevation();
                    }
                }

                avgMinElevation /= BIOME_TRANSITION_AREA;
                avgMaxElevation /= BIOME_TRANSITION_AREA;

                minElevationMap.setPoint(x, z, avgMinElevation);
                elevationRangeMap.setPoint(x, z, avgMaxElevation);
            }
        }
    }
}