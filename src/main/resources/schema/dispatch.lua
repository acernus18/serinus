local strategy_id = KEYS[1];
local preset_key = "bwlist:" .. strategy_id;
local dispatched_key = "popped:" .. strategy_id;
local argv = ARGV[1]

local matches = {}
for word in string.gmatch(argv, "[^:]+") do
    matches[#matches + 1] = word;
end

local device_id = matches[1];
local type = tonumber(matches[2]);
local preset_type = tonumber(matches[3]);
local ab_size = tonumber(matches[4]);
local max_count = tonumber(matches[5]);
local always_return = tonumber(matches[6])

local ab_type = ab_size > 0;
local is_preset_type = (preset_type ~= 0) and (redis.call("exists", preset_key));

local reach_max_count = false;
if ab_type then
    reach_max_count = redis.call("zcard", dispatched_key) >= max_count;
else
    reach_max_count = redis.call("scard", dispatched_key) >= max_count;
end

local has_dispatched = false
if ab_type then
    has_dispatched = redis.call("zscore", dispatched_key, device_id);
else
    has_dispatched = redis.call("sismember", dispatched_key, device_id) > 0;
end

if is_preset_type then
    local is_white_preset = preset_type == 1;
    local exists_in_preset = redis.call("sismember", preset_key, device_id);

    if is_white_preset then
        if exists_in_preset ~= 1 then
            return '0';
        end
    else
        if exists_in_preset == 1 then
            return '0';
        end
    end

end

if has_dispatched then
    if always_return == 1 then
        if ab_type then
            return redis.call("zscore", dispatched_key, device_id);
        else
            return '1';
        end
    else
        return '0';
    end
else
    if reach_max_count then
        if (type == 1) or (max_count == -1) then
            return '1';
        else
            return '0';
        end
    else
        if ab_type then
            local min_score = 0;
            local min_count = redis.call("zcount", dispatched_key, 0, 0);

            for i = 1, ab_size - 1 do
                local count = redis.call("zcount", dispatched_key, i, i);
                if min_count > count then
                    min_count = count;
                    min_score = i;
                end
            end
            redis.call("zadd", dispatched_key, min_score, device_id);
            return tostring(min_score + 1);
        else
            redis.call("sadd", dispatched_key, device_id);
            return '1';
        end
    end
end
