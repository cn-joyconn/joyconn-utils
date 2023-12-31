package cn.joyconn.utils.uniqueID.idgenerator;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.joyconn.utils.uniqueID.idgenerator.interfaces.IIdGenerator;

@Service
public class IDGeneratorHelper {
    @Value("${joyconn.utils.idgenerator.SnowflakeWorkID:1}")
    short SnowflakeWorkID;

    private static IIdGenerator idGenInstance = null;



    @PostConstruct 
    public void init(){
        IdGeneratorOptions opeions = new IdGeneratorOptions(SnowflakeWorkID);
        opeions.SeqBitLength=10;
        opeions.WorkerIdBitLength=12;
        idGenInstance = new DefaultIdGenerator(opeions);
    }

    /**
     * 生成新的Id
     * 调用本方法前，请确保调用了 SetIdGenerator 方法做初始化。
     *
     * @return
     */
    public long nextId() throws IdGeneratorException {
        return idGenInstance.newLong();
    }




}
