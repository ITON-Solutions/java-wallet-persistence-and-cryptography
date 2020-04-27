/*
 * The MIT License
 *
 * Copyright 2019 ITON Solutions.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.iton.jssi.wallet.store;


import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.iton.jssi.wallet.WalletConstants;
import org.iton.jssi.wallet.model.Encrypted;
import org.iton.jssi.wallet.model.Item;
import org.iton.jssi.wallet.model.Metadata;
import org.iton.jssi.wallet.model.Plaintext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class StoreHelper {

    private static final Logger LOG = LoggerFactory.getLogger(StoreHelper.class);

    private static final String DEFAULT_DB = "jdbc:sqlite:" + WalletConstants.WALLET_DIR + "ubicua.db";
    private static final String BACKUP_DB  = "jdbc:sqlite:" + WalletConstants.WALLET_DIR + "backup.db";
    public static final StoreHelper INSTANCE =  new StoreHelper();
    private ConnectionSource source = null;


    private StoreHelper(){
        try {
            LOG.debug(String.format("Connect to database: %s", DEFAULT_DB));
            source = new JdbcConnectionSource(DEFAULT_DB);
        } catch(SQLException e) {
            LOG.error(String.format("Error: %s", e.getMessage()));
        }
    }

    public static ConnectionSource getSource() {
        return INSTANCE.source;
    }

    public static void setBackupSource(){
        try {
            LOG.debug(String.format("Connect to database: %s", BACKUP_DB));
            INSTANCE.source = new JdbcConnectionSource(BACKUP_DB);
        } catch(SQLException e) {
            LOG.error(String.format("Error: %s", e.getMessage()));
        }
    }

    public static void setDefaultSource(){
        try {
            LOG.debug(String.format("Connect to database: %s", DEFAULT_DB));
            INSTANCE.source = new JdbcConnectionSource(DEFAULT_DB);
        } catch(SQLException e) {
            LOG.error(String.format("Error: %s", e.getMessage()));
        }
    }

    public static void createTables(){
        try {
            LOG.debug(String.format("Create table: %s", "metadata"));
            TableUtils.createTableIfNotExists(INSTANCE.source, Metadata.class);
            TableUtils.createTableIfNotExists(INSTANCE.source, Item.class);
            TableUtils.createTableIfNotExists(INSTANCE.source, Encrypted.class);
            TableUtils.createTableIfNotExists(INSTANCE.source, Plaintext.class);
        } catch(SQLException e) {
            LOG.error(String.format("Error: %s", e.getMessage()));
        }

    }
}
