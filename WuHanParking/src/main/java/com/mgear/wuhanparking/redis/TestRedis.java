package com.mgear.wuhanparking.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TestRedis {
	@Resource(name="redisTemplate")
	private RedisTemplate<String, Object> redis;
	
	//使用管道批量插入
	public boolean getList(final JSONArray ja) {
		boolean result = redis.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				long a = System.currentTimeMillis();
				//redis StringSerializer,将String序列化为byte[]
				RedisSerializer<String> rs = redis.getStringSerializer();
				//以hash方式存储
				for(int i=0;i<ja.size();i++) {
					JSONObject jo = ja.getJSONObject(i);
					byte[] berthcode = rs.serialize(jo.getString("berthcode"));
					jo.remove("berthcode");
					Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
					Set<String> keys = jo.keySet();
					for(String key : keys) {
						map.put(rs.serialize(key), rs.serialize(jo.getString(key)));
					}
					//redis hash存储命令
					connection.hMSet(berthcode, map);
				}
				System.out.println("redis 插入时间：" + (System.currentTimeMillis() - a) + " ms");
				return true;
			}
			
		}, false, true);
		
		return result;
	}
	
	public boolean add() {
		boolean result = redis.execute(new RedisCallback<Boolean>(){

			@Override
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				//写入map
				byte[] key = "redisTest".getBytes();
				Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
				map.put("a".getBytes(), "redis".getBytes());
				map.put("b".getBytes(), "mysql".getBytes());
				connection.hMSet(key, map);
				//读取map
				Map<String, String> result = new HashMap<String, String>();
				Map<byte[], byte[]> resultmap = connection.hGetAll(key);
				Set<byte[]> keys = resultmap.keySet();
				for(byte[] k : keys){
					result.put(new String(k), new String(connection.hGet(key, k)));
				}
				System.out.println(result);
				return true;
			}
		});
		return result;
	}
	
}
