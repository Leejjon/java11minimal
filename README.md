#The stuff below has been deprecated, I migrated to firestore.

gcloud beta emulators firestore start

set FIRESTORE_EMULATOR_HOST=::1:8782

# Sharded counter with Java11 and the new Google Cloud Datastore client.

Since I have been using the Google Datastore, I had a count function based off:
https://cloud.google.com/appengine/articles/sharding_counters

Now since the Java 11 runtime is in beta, they tell you to use:
https://googleapis.github.io/google-cloud-java/google-cloud-clients/