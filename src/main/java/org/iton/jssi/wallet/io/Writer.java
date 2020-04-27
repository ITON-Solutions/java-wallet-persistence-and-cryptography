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
package org.iton.jssi.wallet.io;

import io.reactivex.ObservableEmitter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import org.iton.jssi.wallet.Wallet;
import org.iton.jssi.wallet.crypto.Crypto;
import org.iton.jssi.wallet.crypto.KeyDerivationData;
import org.iton.jssi.wallet.record.WalletRecord;
import org.iton.jssi.wallet.store.NonexistentEntityException;
import org.iton.jssi.wallet.util.Utils;
import org.json.JSONObject;
import org.libsodium.jni.SodiumException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Writer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Writer.class);
    
    private Wallet wallet;
    private ObservableEmitter<Integer> emitter;
    private JSONObject config;

    public Writer(Wallet wallet, JSONObject config, ObservableEmitter<Integer> emitter) {
        this.wallet = wallet;
        this.config = config;
        this.emitter = emitter;
    }

    @Override
    public void run() {

        try {
            Path path = Paths.get(config.getString("path"));
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            int count = wallet.count();
            LOG.debug(String.format("Total registers in database %d", count));

            KeyDerivationData data = new KeyDerivationData(config.getString("key"));
            Header header = new Header();
            byte[] header_bytes = header.serialize(data);
            List<WalletRecord> records = wallet.findAllRecords();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(Crypto.hash256(header_bytes));

            for (WalletRecord record : records) {
                byte[] decrypted = record.serialize();
                baos.write(Utils.intToBytes(decrypted.length));
                baos.write(decrypted);
                emitter.onNext(count--);
            }

            baos.write(Utils.intToBytes(0));

            byte[] encrypted = new Encrypter(header.getDerivationData().deriveMasterKey(),
                    header.getNonce(),
                    header.getChunkSize()).encrypt(ByteBuffer.wrap(baos.toByteArray()));

            baos = new ByteArrayOutputStream();
            baos.write(Utils.intToBytes(header_bytes.length));
            baos.write(header_bytes);
            baos.write(encrypted);

            try (FileOutputStream fos = new FileOutputStream(Files.createFile(path).toFile())) {
                baos.writeTo(fos);
            }
            emitter.onComplete();

        } catch (IOException | SodiumException | SQLException e) {
            LOG.error(String.format("Error %s", e.getMessage()));
            emitter.onError(e);
        }
    }
}
