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
package org.iton.jssi.wallet;

import org.iton.jssi.wallet.crypto.Crypto;
import org.iton.jssi.wallet.crypto.Keys;
import org.iton.jssi.wallet.model.Item;
import org.iton.jssi.wallet.record.ItemTags;
import org.iton.jssi.wallet.record.ItemValue;
import org.iton.jssi.wallet.record.WalletRecord;
import org.iton.jssi.wallet.store.EncryptedDao;
import org.iton.jssi.wallet.store.ItemDao;
import org.iton.jssi.wallet.store.PlaintextDao;
import org.iton.jssi.wallet.store.PreexistingEntityException;
import org.libsodium.jni.SodiumException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {

    private static final Logger LOG = LoggerFactory.getLogger(Wallet.class);

    private final String id;
    private final Keys keys;
    private final ItemDao itemDao;
    private final EncryptedDao encryptedDao;
    private final PlaintextDao plaintextDao;
    
    Wallet(String id, Keys keys) {
        this.id = id;
        this.keys = keys;
        this.itemDao = new ItemDao();
        this.encryptedDao = new EncryptedDao();
        this.plaintextDao = new PlaintextDao();
    }
    
    public WalletRecord findRecord(String type, String name) throws SodiumException {

        Item item = findItem(type, name);
        if(item == null){
            return null;
        }
        WalletRecord record = new WalletRecord();
        return record.decrypt(item, keys);
    }
    
    public List<WalletRecord> findAllRecords() throws SodiumException, SQLException {
        
        List<WalletRecord> records = new ArrayList<>();
        
        List<Item> items = itemDao.queryForAll();
        for(Item item : items) {
            records.add(new WalletRecord().decrypt(item, keys));
        }
        return records;
    }


    public void addRecordTags(WalletRecord record, Map<String, String> tags) throws SodiumException {

        Item item = findItem(record.getType(), record.getName());

        if(item == null){
            return;
        }

        ItemTags itemTags = new ItemTags();
        itemTags.encrypt(item, tags, keys.getTagNameKey(), keys.getTagValueKey(), keys.getTagsHmacKey());
        encryptedDao.create(itemTags.getEncrypted());
        plaintextDao.create(itemTags.getPlaintext());
    }

    public void deleteRecordTags(WalletRecord record, Map<String, String> tags) throws SodiumException {

        Item item = findItem(record.getType(), record.getName());

        if(item == null){
            return;
        }

        ItemTags itemTags = new ItemTags();
        itemTags.encrypt(item, tags, keys.getTagNameKey(), keys.getTagValueKey(), keys.getTagsHmacKey());
        encryptedDao.delete(itemTags.getEncrypted());
        plaintextDao.delete(itemTags.getPlaintext());
    }


    public Item addRecord(WalletRecord record) throws SodiumException, PreexistingEntityException {
        Item item = record.encrypt(keys);
        int result = itemDao.create(item);

        if(result == 0){
            throw new PreexistingEntityException("Item already exists");
        }

        encryptedDao.create(item.getEncrypted());
        plaintextDao.create(item.getPlaintext());
        return item;
    }
    
    public int count() {
        return itemDao.getCount();
    }

    public void deleteRecord(WalletRecord record) {
        deleteRecord(record.getType(), record.getName());
    }
    
    public void deleteRecord(String type, String name) {

        Item item = findItem(type, name);

        if(item == null){
            return;
        }

        encryptedDao.delete(item.getEncrypted());
        plaintextDao.delete(item.getPlaintext());
        itemDao.delete(item);
    }

    public void updateRecordValue(WalletRecord record, String value) throws SodiumException {

        Item item = findItem(record.getType(), record.getName());

        if(item == null){
            return;
        }

        ItemValue itemValue = new ItemValue(item);
        itemValue = itemValue.encrypt(value.getBytes(), keys.getValueKey());
        item.setValue(itemValue.getValue());
        item.setKey(itemValue.getKey());
        itemDao.update(item);
    }

    public void updateRecordTags(WalletRecord record, Map<String, String> tags) throws SodiumException {

        Item item = findItem(record.getType(), record.getName());
        if(item == null){
            return;
        }

        Map<String, String> aggregated = new HashMap<>();
        for(String element : tags.keySet()){
            if(record.getTags().keySet().contains(element)){
                aggregated.put(element, tags.get(element));
            }
        }

        ItemTags itemTags = new ItemTags();
        itemTags.encrypt(item, aggregated, keys.getTagNameKey(), keys.getTagValueKey(), keys.getTagsHmacKey());
        encryptedDao.update(itemTags.getEncrypted());
        plaintextDao.update(itemTags.getPlaintext());
    }

    public String getId() {
        return id;
    }

    private Item findItem(String type, String name){
        Item item = null;
        try {
            byte[] encryptedType = type == null ? new byte[0]
                    : Crypto.encryptAsSearchable(type.getBytes(), keys.getTypeKey(), keys.getItemHmacKey());
            byte[] encryptedName = name == null ? new byte[0]
                    : Crypto.encryptAsSearchable(name.getBytes(), keys.getNameKey(), keys.getItemHmacKey());

            item = itemDao.queryForFirst(encryptedType, encryptedName);
        } catch (SodiumException e){
            LOG.error(String.format("Error: %s", e.getMessage()));
        }
        return item;
    }
}
