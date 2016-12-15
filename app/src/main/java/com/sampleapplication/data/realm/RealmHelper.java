package com.sampleapplication.data.realm;

import java.util.Locale;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import timber.log.Timber;

public final class RealmHelper {

    /**
     * Version should not be in YY MM DD Rev. format as it makes upgrading harder.
     */
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "default.realm";

    private RealmHelper() {
    }

    /**
     * Initializes the default realm instance, being the user database. This database holds all
     * user specific data, e.g. favorite lines/bus stops or trips.
     */
    public static void init() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(DB_NAME)
                .schemaVersion(DB_VERSION)
                .migration(new Migration())
                .build();

        Realm.setDefaultConfiguration(config);
    }

    private static class Migration implements RealmMigration {

        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            Timber.e("Upgrading realm from %s to %s", oldVersion, newVersion);

            if (oldVersion < newVersion) {
                throw new IllegalStateException(String.format(Locale.getDefault(),
                        "Migration missing from v%d to v%d", oldVersion, newVersion));
            }
        }
    }
}
