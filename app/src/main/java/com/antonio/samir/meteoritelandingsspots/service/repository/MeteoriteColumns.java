package com.antonio.samir.meteoritelandingsspots.service.repository;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public class MeteoriteColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    public static final String ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String MASS = "mass";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String NAMETYPE = "nametype";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String RECCLASS = "recclass";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String NAME = "name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String FALL = "fall";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String YEAR = "year";

    @DataType(DataType.Type.REAL)
    @NotNull
    public static final String RECLONG = "reclong";

    @DataType(DataType.Type.REAL)
    @NotNull
    public static final String RECLAT = "reclat";
}
