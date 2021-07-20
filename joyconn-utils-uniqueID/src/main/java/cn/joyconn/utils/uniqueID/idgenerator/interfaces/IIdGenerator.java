package cn.joyconn.utils.uniqueID.idgenerator.interfaces;

import cn.joyconn.utils.uniqueID.idgenerator.IdGeneratorException;

public interface IIdGenerator {
    long newLong() throws IdGeneratorException;
}
