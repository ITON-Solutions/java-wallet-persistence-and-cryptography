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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import org.iton.jssi.wallet.crypto.KeyDerivationData;
import org.iton.jssi.wallet.crypto.Keys;
import org.iton.jssi.wallet.crypto.KeysMetadata;
import org.iton.jssi.wallet.model.Metadata;
import org.iton.jssi.wallet.store.MetadataDao;
import org.json.JSONObject;
import org.libsodium.api.Crypto_randombytes;
import org.libsodium.jni.SodiumException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES;
/**
 *
 * @author ITON Solutions
 */
public class WalletService {
    
    private static final Logger LOG = LoggerFactory.getLogger(WalletService.class);
    
    private KeysMetadata keysMetadata;
    private KeyDerivationData keyDerivationData;
    private Keys keys;
    private final JSONObject credentials;
    private final MetadataDao metadataDao;
    private Wallet wallet;

    public WalletService(final JSONObject credentials) {
        this.credentials = credentials;
        this.metadataDao = new MetadataDao();
    }
    
    public Observable<Wallet> open(){
        if(wallet == null) {
            LOG.debug("Open wallet");
            return Observable.fromCallable(new Callable<Wallet>() {
                @Override
                public Wallet call() throws IOException, SodiumException {
                    Metadata metadata = new MetadataDao().getMetadata();
                    keysMetadata = new ObjectMapper()
                            .readerFor(KeysMetadata.class)
                            .readValue(metadata.getValue());
                    keyDerivationData = new KeyDerivationData(credentials.getString("key"), keysMetadata);
                    keys = new Keys().deserialize(keysMetadata.getKeys(), keyDerivationData.deriveMasterKey());
                    return new Wallet(credentials.getString("id"), keys);
                }
            });
        } else {
            LOG.debug("Wallet already open");
            return Observable.just(wallet);
        }
    }

    public Observable<Boolean> close(){
        wallet = null;
        return Observable.just(Boolean.TRUE);
    }

    public Observable<Integer> export(JSONObject config) {
        return open().flatMap(new Function<Wallet, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Wallet wallet) {
                WalletExport export = new WalletExport(wallet);
                return export.export(config);
            }
        });
    }
    
    public Observable<Integer> restore(JSONObject config) {
        
        return open().flatMap(new Function<Wallet, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Wallet wallet) {
                WalletImport restore = new WalletImport(wallet);
                return restore.restore(config);
            }
        });
    }
    
    public Observable<Boolean> create() {
        return Observable.fromCallable(new Callable<Boolean>(){
            @Override
            public Boolean call() throws SodiumException, IOException {

                byte[] salt = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES];
                Crypto_randombytes.buf(salt);
                keyDerivationData = new KeyDerivationData(credentials.getString("key"), salt);
                keys = new Keys().init();
                keysMetadata = new KeysMetadata(keys.serialize(keyDerivationData.deriveMasterKey()), salt);

                Metadata metadata = new Metadata(keysMetadata.toString().getBytes());
                metadataDao.create(metadata);
                return Boolean.TRUE;
            }
        });
    }

    public Wallet getWallet() {
        return wallet;
    }
}
