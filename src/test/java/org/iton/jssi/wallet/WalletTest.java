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

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.bitcoinj.core.Base58;
import org.iton.jssi.wallet.model.Item;
import org.iton.jssi.wallet.record.WalletRecord;
import org.iton.jssi.wallet.store.PreexistingEntityException;
import org.iton.jssi.wallet.store.StoreHelper;
import org.iton.jssi.wallet.util.Utils;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.libsodium.jni.NaCl;
import org.libsodium.jni.SodiumException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(Wallet.class);
    
    public WalletTest() {
        NaCl.sodium();
    }

     /**
     * Test of find method, of class Wallet.
     * @throws IOException
     * @throws SodiumException
     */
    @Test
    @Order(2)
    public void testFindRecord() throws InterruptedException {
        StoreHelper.getSource();
        JSONObject credentials = new JSONObject();
        credentials.put("id", "ubicua");
        credentials.put("key", "wallet_key");
        WalletService service = new WalletService(credentials);

        String type = "AIPE:alice";
        String name = "OTP";
        
        String expResult = "";
        service.open().subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                 LOG.debug("Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Wallet wallet) {
                try {
                    LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                    WalletRecord record = wallet.findRecord(type, name);
                    LOG.debug(String.format("Found record: name: %s type %s value: %s", record.getName(), record.getType(), record.getValue()));
                    for(String key : record.getTags().keySet()){
                        LOG.debug(String.format("%s: name: %s value: %s", key.startsWith("~") ? "Plaintext" : "Encrypted", key, record.getTags().get(key)));
                    }
                    assertNotNull(record);
                } catch (SodiumException e){}
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
                LOG.debug("Received COMPLETED event");
            }
           
        });
        Thread.sleep(1000);
    }
    
    @Test
    public void testFindAllRecords() throws InterruptedException {
        JSONObject credentials = new JSONObject();
        credentials.put("id", "ubicua");
        credentials.put("key", "wallet_key");
        WalletService service = new WalletService(credentials);
        
        service.open().subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                 LOG.debug("Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Wallet wallet) {
                try {
                    LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                    List<WalletRecord> records = wallet.findAllRecords();
                    for (WalletRecord record : records) {
                        LOG.debug(String.format("Record: name: %s type: %s value: %s", record.getName(), record.getType(), record.getValue()));
                        for(String key : record.getTags().keySet()){
                            LOG.debug(String.format("%s: name: %s value: %s", key.startsWith("~") ? "Plaintext" : "Encrypted", key, record.getTags().get(key)));
                        }
                    }
                    assertNotNull(records);
                } catch (SodiumException | SQLException e) {
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
                LOG.debug("Received COMPLETED event");
            }
        });
        Thread.sleep(1000);
    }

    @Test
    public void testFindAllKeys() throws InterruptedException {
        JSONObject credentials = new JSONObject();
        credentials.put("id", "ubicua");
        credentials.put("key", "wallet_key");
        WalletService service = new WalletService(credentials);

        service.open().subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                LOG.debug("Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Wallet wallet) {
                try {
                    LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                    List<WalletRecord> records = wallet.findAllRecords();
                    for (WalletRecord record : records) {
                        if (record.getType().equals("Indy::Key")) {
                            JSONObject keys = new JSONObject(record.getValue());
                            String verkey = Utils.bytesToHex(Base58.decode(keys.getString("verkey")));
                            String signkey = Utils.bytesToHex(Base58.decode(keys.getString("signkey")));
                            LOG.debug(String.format("Public key: %s Private key: %s", verkey, signkey));
                        }
                    }
                    assertNotNull(records);
                } catch (SodiumException | SQLException e) {
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
                LOG.debug("Received COMPLETED event");
            }
        });
        Thread.sleep(1000);
    }
    
    @Test
    @Order(5)
    public void testDeleteRecord() throws InterruptedException {

        JSONObject credentials = new JSONObject();
        credentials.put("id", "ubicua");
        credentials.put("key", "wallet_key");
        WalletService service = new WalletService(credentials);
        service.open().subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                 LOG.debug("Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Wallet wallet) {

                LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                String type = "AIPE:alice";
                String name = "OTP";

                try {
                    wallet.deleteRecord(type, name);
                    WalletRecord record = wallet.findRecord(type, name);
                    assertNull(record);
                } catch (SodiumException e) {
                    LOG.error(String.format("Error: %s", e.getMessage()));
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
                LOG.debug("Received COMPLETED event");
            }
        
        });
        Thread.sleep(1000);
    }

    @Test
    @Order(1)
    public void testAddRecord() throws InterruptedException {

        JSONObject credentials = new JSONObject();
        credentials.put("id", "ubicua");
        credentials.put("key", "wallet_key");
        WalletService service = new WalletService(credentials);

        service.open().subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                LOG.debug("Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Wallet wallet) {

                LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                String type = "AIPE:alice";
                String name = "OTP";
                String value = "Hello Alice";

                try {
                    WalletRecord record = new WalletRecord(type, name, value);
                    Item item = wallet.addRecord(record);
                    assertNotNull(item);
                } catch (SodiumException | PreexistingEntityException  e) {
                    LOG.error(String.format("Error: %s", e.getMessage()));
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
                LOG.debug("Received COMPLETED event");
            }

        });
        Thread.sleep(1000);
    }

    @Test
    @Order(4)
    public void testUpdateRecordTags() throws InterruptedException {

        JSONObject credentials = new JSONObject();
        credentials.put("id", "ubicua");
        credentials.put("key", "wallet_key");
        WalletService service = new WalletService(credentials);

        service.open().subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                LOG.debug("Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Wallet wallet) {

                LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                String type = "AIPE:alice";
                String name = "OTP";

                String plaintextName = "~WELCOME";
                String plaintextValue = "Hello Alice";
                String encryptedName = "WELCOME";
                String encryptedValue = "Hello again Alice";

                try {

                    Map<String, String> tags = new HashMap<>();
                    tags.put(plaintextName, plaintextValue);
                    tags.put(encryptedName, encryptedValue);
                    LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                    WalletRecord record = wallet.findRecord(type, name);

                    wallet.updateRecordTags(record, tags);

                    record = wallet.findRecord(type, name);
                    LOG.debug(String.format("Found record: name: %s type %s value: %s", record.getName(), record.getType(), record.getValue()));
                    for(String key : record.getTags().keySet()){
                        LOG.debug(String.format("%s: name: %s value: %s", key.startsWith("~") ? "Plaintext" : "Encrypted", key, record.getTags().get(key)));
                    }
                    assertNotNull(record);
                } catch (SodiumException e) {
                    LOG.error(String.format("Error: %s", e.getMessage()));
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
                LOG.debug("Received COMPLETED event");
            }

        });
        Thread.sleep(1000);
    }

    @Test
    @Order(3)
    public void testUpdateRecordValue() throws InterruptedException {

        JSONObject credentials = new JSONObject();
        credentials.put("id", "ubicua");
        credentials.put("key", "wallet_key");
        WalletService service = new WalletService(credentials);

        service.open().subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                LOG.debug("Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Wallet wallet) {

                LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                String type = "AIPE:alice";
                String name = "OTP";
                String value = "Hello again Alice";

                try {


                    LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                    WalletRecord record = wallet.findRecord(type, name);
                    wallet.updateRecordValue(record, value);

                    record = wallet.findRecord(type, name);
                    LOG.debug(String.format("Found record: name: %s type %s value: %s", record.getName(), record.getType(), record.getValue()));
                    for(String key : record.getTags().keySet()){
                        LOG.debug(String.format("%s: name: %s value: %s", key.startsWith("~") ? "Plaintext" : "Encrypted", key, record.getTags().get(key)));
                    }
                    assertNotNull(record);
                } catch (SodiumException e) {
                    LOG.error(String.format("Error: %s", e.getMessage()));
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                LOG.debug("Received COMPLETED event");
            }

        });
        Thread.sleep(1000);
    }

    @Test
    @Order(7)
    public void testDeleteRecordTags() throws InterruptedException {

        JSONObject credentials = new JSONObject();
        credentials.put("id", "ubicua");
        credentials.put("key", "wallet_key");
        WalletService service = new WalletService(credentials);

        service.open().subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                LOG.debug("Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Wallet wallet) {

                LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                String type = "AIPE:bob";
                String name = "OTP";
                String value = "Hello Bob";

                String tagName = "WELCOME";
                String tagValue = "";

                try {
                    Map<String, String> tags = new HashMap<>();
                    tags.put(tagName, tagValue);
                    WalletRecord record = wallet.findRecord(type, name);
                    if(record == null){
                        wallet.addRecord(new WalletRecord(type, name, value));
                        record = wallet.findRecord(type, name);
                    }
                    wallet.deleteRecordTags(record, tags);
                    record = wallet.findRecord(type, name);

                    LOG.debug(String.format("Record: name: %s type: %s value: %s", record.getName(), record.getType(), record.getValue()));
                    for(String key : record.getTags().keySet()){
                        LOG.debug(String.format("%s: name: %s value: %s", key.startsWith("~") ? "Plaintext" : "Encrypted", key, record.getTags().get(key)));
                    }
                    assertNotNull(tags);
                } catch (SodiumException | PreexistingEntityException e) {
                    LOG.error(String.format("Error: %s", e.getMessage()));
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                LOG.debug("Received COMPLETED event");
            }

        });
        Thread.sleep(1000);
    }


    @Test
    @Order(6)
    public void testAddRecordTags() throws InterruptedException {

        JSONObject credentials = new JSONObject();
        credentials.put("id", "ubicua");
        credentials.put("key", "wallet_key");
        WalletService service = new WalletService(credentials);

        service.open().subscribe(new Observer<Wallet>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                LOG.debug("Received SUBSCRIBED event");
            }

            @Override
            public void onNext(Wallet wallet) {

                LOG.debug(String.format("Wallet opened: id=%s", wallet.getId()));
                String type = "AIPE:bob";
                String name = "OTP";
                String value = "Hello Bob";

                String plaintextName = "~WELCOME";
                String plaintextValue = "Hello Bob";
                String encryptedName1 = "WELCOME";
                String encryptedValue1 = "Hello again Bob";
                String encryptedName2 = "ADDRESS";
                String encryptedValue2 = "Milky Way";

                Map<String, String> tags = new HashMap<>();
                tags.put(plaintextName, plaintextValue);
                tags.put(encryptedName1, encryptedValue1);
                tags.put(encryptedName2, encryptedValue2);


                try {
;
                    WalletRecord record = wallet.findRecord(type, name);

                    if(record == null){
                        wallet.addRecord(new WalletRecord(type, name, value));
                        record = wallet.findRecord(type, name);
                    }
                    wallet.addRecordTags(record, tags);
                    record = wallet.findRecord(type, name);

                    LOG.debug(String.format("Record: key: %s type: %s value: %s", record.getName(), record.getType(), record.getValue()));
                    for(String key : record.getTags().keySet()){
                        LOG.debug(String.format("%s: key: %s value: %s", key.startsWith("~") ? "Plaintext" : "Encrypted", key, record.getTags().get(key)));
                    }
                    assertNotNull(tags);
                } catch (SodiumException | PreexistingEntityException e) {
                    LOG.error(String.format("Error: %s", e.getMessage()));
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
                LOG.debug("Received COMPLETED event");
            }

        });
        Thread.sleep(1000);
    }
}
