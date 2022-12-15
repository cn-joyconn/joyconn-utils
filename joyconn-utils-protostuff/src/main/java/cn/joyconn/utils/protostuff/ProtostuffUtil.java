package cn.joyconn.utils.protostuff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.StringMapSchema;
import io.protostuff.runtime.RuntimeSchema;
 
 
public class ProtostuffUtil {
 
	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();
 
	private static Map<Class<?>, StringMapSchema<?>> mapSchema = new ConcurrentHashMap<Class<?>, StringMapSchema<?>>();
 
	private static Objenesis objenesis = new ObjenesisStd(true);
 
	/**
	 * 缓存Schema对象
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> Schema<T> getSchema(Class<T> clazz) {
		Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
		if (schema == null) {
			schema = RuntimeSchema.getSchema(clazz);
			if (schema != null) {
				cachedSchema.put(clazz, schema);
			}
		}
		return schema;
	}
 
	/**
	 * 缓存StringMapSchema
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> StringMapSchema<T> getMapSchema(Class<T> clazz) {
		StringMapSchema<T> schema = (StringMapSchema<T>) mapSchema.get(clazz);
		if (schema == null) {
			schema = new StringMapSchema<>(getSchema(clazz));
			if (schema != null) {
				mapSchema.put(clazz, schema);
			}
		}
		return schema;
	}
 
	/**
	 * 序列化对象
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> byte[] serialize(T obj) {
		if (obj == null) {
			throw new IllegalArgumentException("不能序列化空对象!");
		}
		Schema<T> schema = getSchema((Class<T>) obj.getClass());
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		byte[] protostuff = null;
		try {
			protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			throw new RuntimeException("序列化(" + obj.getClass().getName() + ")对象时发生异常!", e);
		} finally {
			buffer.clear();
		}
		return protostuff;
	}
 
	/**
	 * 反序列化
	 * 
	 * @param dataByte
	 * @param clazz
	 * @return
	 */
	public static <T> T deserialize(byte[] dataByte, Class<T> clazz) {
		if (dataByte == null || dataByte.length == 0) {
			throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
		}
		T instance = objenesis.newInstance(clazz);
		Schema<T> schema = getSchema(clazz);
		ProtostuffIOUtil.mergeFrom(dataByte, instance, schema);
		return instance;
	}
 
	/**
	 * 序列化list<br>
	 * <p>
	 * <strong>此方法T != Object，因为如果是List<Object>则反序列化时会抛出异常</strong>
	 * </p>
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> byte[] serializeListNotObjectClass(List<T> list) {
		if (list == null || list.isEmpty()) {
			throw new IllegalArgumentException("不能序列化空列表!");
		}
		Schema<T> schema = getSchema((Class<T>) list.get(0).getClass());
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		byte[] protostuff = null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			ProtostuffIOUtil.writeListTo(bos, list, schema, buffer);
			protostuff = bos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("序列化对象列表时发生异常!", e);
		} finally {
			buffer.clear();
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return protostuff;
	}
 
	/**
	 * 反序列化list
	 * <p>
	 * <strong>此方法T != Object，否则会抛出异常</strong>
	 * </p>
	 * 
	 * @param dataByte
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> deserializeListNotObjectClass(byte[] dataByte, Class<T> clazz) {
		if (dataByte == null || dataByte.length == 0) {
			throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
		}
 
		Schema<T> schema = getSchema(clazz);
		List<T> result = null;
		try {
			result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(dataByte), schema);
		} catch (IOException e) {
			throw new RuntimeException("反序列化对象列表发生异常!", e);
		}
		return result;
	}
 
	/**
	 * 序列化List集合，如果T==Object，则放到ListDto再序列化ListDto对象
	 * 
	 * @param list
	 * @param clazz
	 * @return
	 */
	public static <T> byte[] serializeList(List<T> list, Class<T> clazz) {
		if (list == null || list.isEmpty()) {
			throw new IllegalArgumentException("不能序列化空列表!");
		}
		if (Object.class == clazz) {
			ListDto listDto = new ListDto();
			listDto.setList(list);
			return serialize(listDto);
		} else {
			return serializeListNotObjectClass(list);
		}
	}
 
	/**
	 * 反序列化List集合，如果T==Object，则反序列化为ListDto对象再取list
	 * 
	 * @param dataByte
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> deserializeList(byte[] dataByte, Class<T> clazz) {
		if (dataByte == null || dataByte.length == 0) {
			throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
		}
		if (Object.class == clazz) {
			return (List<T>) deserialize(dataByte, ListDto.class).getList();
		}
		return deserializeListNotObjectClass(dataByte, clazz);
	}
 
	/**
	 * 序列化Map对象，V不应为Object
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V> byte[] serializeMapNotObjectClassValue(Map<String, V> map) {
		if (map == null) {
			throw new IllegalArgumentException("不能序列化空对象!");
		}
		StringMapSchema<V> schema = getMapSchema((Class<V>) map.values().iterator().next().getClass());
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		byte[] protostuff = null;
		try {
			protostuff = ProtostuffIOUtil.toByteArray(map, schema, buffer);
		} catch (Exception e) {
			throw new RuntimeException("序列化map对象时发生异常!", e);
		} finally {
			buffer.clear();
		}
		return protostuff;
	}
 
	/**
	 * 反序列化Map集合，clazz不应为Object
	 * 
	 * @param dataByte
	 * @param clazz
	 * @return
	 */
	public static <V> Map<String, V> deserializeMapNotObjectClassValue(byte[] dataByte, Class<V> clazz) {
		if (dataByte == null || dataByte.length == 0) {
			throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
		}
		if (Object.class == clazz) {
			throw new IllegalArgumentException("不能反序列化成Map<String, Object>对象!");
		}
		Map<String, V> instance = new HashMap<String, V>();
		StringMapSchema<V> schema = getMapSchema(clazz);
		ProtostuffIOUtil.mergeFrom(dataByte, instance, schema);
		return instance;
	}
 
	/**
	 * 序列化Map集合
	 * 
	 * @param map
	 * @param clazz
	 * @return
	 */
	public static <V> byte[] serializeMap(Map<String, V> map, Class<V> clazz) {
		if (map == null) {
			throw new IllegalArgumentException("不能序列化空对象!");
		}
		if (clazz == Object.class) {
			MapDto mapDto = new MapDto();
			mapDto.setMap(map);
			return serialize(mapDto);
		} else {
			return serializeMapNotObjectClassValue(map);
		}
 
	}
 
	/**
	 * 反序列化Map集合
	 * 
	 * @param dataByte
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V> Map<String, V> deserializeMap(byte[] dataByte, Class<V> clazz) {
		if (dataByte == null || dataByte.length == 0) {
			throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
		}
		if (Object.class == clazz) {
			return (Map<String, V>) deserialize(dataByte, MapDto.class).getMap();
		} else {
			return deserializeMapNotObjectClassValue(dataByte, clazz);
		}
 
	}
 
	private static class MapDto {
		private Map<String, ? extends Object> map;
 
		public Map<String, ? extends Object> getMap() {
			return map;
		}
 
		public void setMap(Map<String, ? extends Object> map) {
			this.map = map;
		}
	}
 
	private static class ListDto {
		private List<? extends Object> list;
 
		public List<? extends Object> getList() {
			return list;
		}
 
		public void setList(List<? extends Object> list) {
			this.list = list;
		}
 
	}
}