--return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}
--local key=KEYS[1]
--local list=redis.call("lrange",key,0,-1);
--return list;


--delifequals
if redis.call("get",KEYS[1]) == ARGV[1] then
  return redis.call("del",KEYS[1])
else
  return 0
end