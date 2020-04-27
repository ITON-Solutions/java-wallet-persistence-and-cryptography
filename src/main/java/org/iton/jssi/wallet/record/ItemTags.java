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
import org.iton.jssi.wallet.model.Encrypted;
import org.iton.jssi.wallet.model.Item;
import org.iton.jssi.wallet.model.Plaintext;
import org.libsodium.jni.SodiumException;

import java.util.*;

public class ItemTags {
    
    private Collection<Encrypted> encrypted = new ArrayList<>();
    private Collection<Plaintext> plaintext = new ArrayList<>();

    public ItemTags(){}
    
    public ItemTags(Item item){
        encrypted.addAll(item.getEncrypted());
        plaintext.addAll(item.getPlaintext());
    }

    public Map<String, String> decrypt(byte[] tagNameKey, byte[] tagValueKey) throws SodiumException{

        Map<String, String> decrypted = new HashMap<>();

        for (Encrypted tag : encrypted) {
            String name = new String(Crypto.decryptMerged(tag.getName(), tagNameKey));
            String value = new String(Crypto.decryptMerged(tag.getValue(), tagValueKey));
            decrypted.put(name, value);
        }
        for (Plaintext tag : plaintext) {
            String name = new String(Crypto.decryptMerged(tag.getName(), tagNameKey));
            String value = new String(tag.getValue());
            decrypted.put(String.format("~%s", name), value);
        }
        return decrypted;
    }

    public void encrypt(Item item, Map<String, String> tags, byte[] tagNameKey, byte[] tagValueKey, byte[] tagsHmacKey) throws SodiumException{

        for(String name : tags.keySet()) {
            if(name.startsWith("~")){
                byte[] encryptedValue = tags.get(name).getBytes();
                name = name.substring(1);
                byte[] encryptedName  = Crypto.encryptAsSearchable(name.getBytes(), tagNameKey, tagsHmacKey);
                plaintext.add(new Plaintext(item, encryptedName, encryptedValue));
            } else {
                String value = tags.get(name);
                byte[] encryptedName  = Crypto.encryptAsSearchable(name.getBytes(), tagNameKey, tagsHmacKey);
                byte[] encryptedValue = Crypto.encryptAsSearchable(value.getBytes(), tagValueKey, tagsHmacKey);
                encrypted.add(new Encrypted(item, encryptedName, encryptedValue));
            }
        }
    }

    public Collection<Encrypted> getEncrypted() {
        return encrypted;
    }

    public Collection<Plaintext> getPlaintext() {
        return plaintext;
    }
    
}
