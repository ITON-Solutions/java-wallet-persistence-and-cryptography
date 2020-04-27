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
package org.iton.jssi.wallet.record;

import org.iton.jssi.wallet.crypto.Crypto;
import org.iton.jssi.wallet.model.Item;
import org.libsodium.api.Crypto_randombytes;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES;
import org.libsodium.jni.SodiumException;

public class ItemValue {
    
    private byte[] value;
    private byte[] key;
    
    ItemValue(){
    }

    public ItemValue(Item item){
        this.value = item.getValue();
        this.key = item.getKey();
    }

    public String decrypt(byte[] value_key) throws SodiumException{
        byte[] decrypt_key = Crypto.decryptMerged(key, value_key);
        return new String(Crypto.decryptMerged(value, decrypt_key));
    }
    
    public ItemValue encrypt(byte[] value, byte[] value_key) throws SodiumException{
        byte[] encrypt_key = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES];
        Crypto_randombytes.buf(encrypt_key);
        this.value = Crypto.encryptAsNotSearchable(value, encrypt_key);
        this.key   = Crypto.encryptAsNotSearchable(encrypt_key, value_key);
        return this;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getKey() {
        return key;
    }
    
}
