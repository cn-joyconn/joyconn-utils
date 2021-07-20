package cn.joyconn.utils.uniqueID.idgenerator.interfaces;

import cn.joyconn.utils.uniqueID.idgenerator.IdGeneratorException;

public interface ISnowWorker {
    long nextId() throws IdGeneratorException;
}
