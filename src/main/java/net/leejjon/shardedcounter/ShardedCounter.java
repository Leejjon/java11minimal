package net.leejjon.shardedcounter;


import com.google.cloud.datastore.*;

import java.util.Random;

public class ShardedCounter {
    private static final Datastore DS = DatastoreOptions.getDefaultInstance().getService();

    private static final String POOL_COUNTER_KIND = "poolCounter";
    private static final String COUNT_ATTRIBUTE = "count";

    /**
     * Default number of shards.
     */
    private static final int NUM_SHARDS = 10;

    /**
     * A random number generator, for distributing writes across shards.
     */
    private final Random generator = new Random();

    public final long count() {
        Query<Entity> getAllShardsQuery = Query.newEntityQueryBuilder()
                .setKind(POOL_COUNTER_KIND)
                .build();

        QueryResults<Entity> allShardsResult = DS.run(getAllShardsQuery);

        long counter = 0;
        while (allShardsResult.hasNext()) {
            Entity shard = allShardsResult.next();
            counter += shard.getLong(COUNT_ATTRIBUTE);
        }

        return counter;
    }

    public void increment() {
        int shardNum = generator.nextInt(NUM_SHARDS);

        Transaction tx = DS.newTransaction();

        // I wonder if it actually goes to the datastore on this line...
        Key key = DS.newKeyFactory().setKind(POOL_COUNTER_KIND).newKey(Integer.toString(shardNum));

        Entity currentShard = tx.get(key);

        final long count;
        final Entity incrementedShard;
        if (currentShard != null) {
            count = currentShard.getLong(COUNT_ATTRIBUTE);
            incrementedShard = Entity.newBuilder(currentShard).set(COUNT_ATTRIBUTE, count + 1L).build();
        } else {
            incrementedShard = Entity.newBuilder(key).set(COUNT_ATTRIBUTE, 1L).build();
        }

        // Use a put to insert or update.
        // https://cloud.google.com/datastore/docs/concepts/entities#datastore-datastore-basic-entity-java
        tx.put(incrementedShard);
        tx.commit();
    }
}
