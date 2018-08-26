package com.sgr.repositories.redis;

import com.sgr.domain.UserRegistration;
import com.sgr.domain.RandomIdGenerator;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RedisAlbumRepository implements CrudRepository<UserRegistration, String> {
    public static final String ALBUMS_KEY = "albums";

    private final RandomIdGenerator idGenerator;
    private final HashOperations<String, String, UserRegistration> hashOps;

    public RedisAlbumRepository(RedisTemplate<String, UserRegistration> redisTemplate) {
        this.hashOps = redisTemplate.opsForHash();
        this.idGenerator = new RandomIdGenerator();
    }

    @Override
    public <S extends UserRegistration> S save(S album) {
        if (album.getId() == null) {
            album.setId(idGenerator.generateId());
        }

        hashOps.put(ALBUMS_KEY, album.getId(), album);

        return album;
    }

    @Override
    public <S extends UserRegistration> Iterable<S> saveAll(Iterable<S> albums) {
        List<S> result = new ArrayList<>();

        for (S entity : albums) {
            save(entity);
            result.add(entity);
        }

        return result;
    }

    @Override
    public Optional<UserRegistration> findById(String id) {
        return Optional.of(hashOps.get(ALBUMS_KEY, id));
    }

    @Override
    public boolean existsById(String id) {
        return hashOps.hasKey(ALBUMS_KEY, id);
    }

    @Override
    public Iterable<UserRegistration> findAll() {
        return hashOps.values(ALBUMS_KEY);
    }

    @Override
    public Iterable<UserRegistration> findAllById(Iterable<String> ids) {
        return hashOps.multiGet(ALBUMS_KEY, convertIterableToList(ids));
    }

    @Override
    public long count() {
        return hashOps.keys(ALBUMS_KEY).size();
    }

    @Override
    public void deleteById(String id) {
        hashOps.delete(ALBUMS_KEY, id);
    }

    @Override
    public void delete(UserRegistration userRegistration) {
        hashOps.delete(ALBUMS_KEY, userRegistration.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends UserRegistration> albums) {
        for (UserRegistration userRegistration : albums) {
            delete(userRegistration);
        }
    }

    @Override
    public void deleteAll() {
        Set<String> ids = hashOps.keys(ALBUMS_KEY);
        for (String id : ids) {
            deleteById(id);
        }
    }

    private <T> List<T> convertIterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T object : iterable) {
            list.add(object);
        }
        return list;
    }
}
