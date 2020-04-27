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
package org.iton.jssi.wallet.crypto;

import java.io.IOException;
import org.libsodium.api.Crypto_aead_chacha20poly1305_ietf;
import org.libsodium.api.Crypto_auth_hmacsha256;
import org.libsodium.jni.SodiumException;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;


/**
 *
 * @author ITON Solutions
 */
public class Keys {
    
    public static final int KEYS_SIZE = 7;
    
    private byte[] typeKey;
    private byte[] nameKey;
    private byte[] valueKey;
    private byte[] itemHmacKey;
    private byte[] tagNameKey;
    private byte[] tagValueKey;
    private byte[] tagsHmacKey;

    
    public Keys(){
    }
    
    public Keys init() throws SodiumException{

        typeKey = Crypto_aead_chacha20poly1305_ietf.keygen();
        nameKey = Crypto_aead_chacha20poly1305_ietf.keygen();
        valueKey = Crypto_aead_chacha20poly1305_ietf.keygen();
        itemHmacKey = Crypto_auth_hmacsha256.keygen();
        tagNameKey = Crypto_aead_chacha20poly1305_ietf.keygen();
        tagValueKey = Crypto_aead_chacha20poly1305_ietf.keygen();
        tagsHmacKey = Crypto_auth_hmacsha256.keygen();
        return this;
    }
    
    public byte[] serialize(byte[] master_key) throws IOException, SodiumException {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

        packer.packArrayHeader(KEYS_SIZE);
        
        packer.packBinaryHeader(typeKey.length);
        packer.writePayload(typeKey);

        packer.packBinaryHeader(nameKey.length);
        packer.writePayload(nameKey);

        packer.packBinaryHeader(valueKey.length);
        packer.writePayload(valueKey);

        packer.packBinaryHeader(itemHmacKey.length);
        packer.writePayload(itemHmacKey);

        packer.packBinaryHeader(tagNameKey.length);
        packer.writePayload(tagNameKey);

        packer.packBinaryHeader(tagValueKey.length);
        packer.writePayload(tagValueKey);
        
        packer.packBinaryHeader(tagsHmacKey.length);
        packer.writePayload(tagsHmacKey);
        
        packer.close();

        
        byte[] encrypted = Crypto.encryptAsNotSearchable(packer.toByteArray(), master_key);
        return encrypted;
    }
    
    public Keys deserialize(byte[] in, byte[] master_key ) throws IOException, SodiumException{
        
        byte[] decrypted = Crypto.decryptMerged(in, master_key);
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(decrypted);
        
        if(KEYS_SIZE != unpacker.unpackArrayHeader()){
            throw new IOException("Invalid array keys size");
        }

        typeKey = unpacker.readPayload(unpacker.unpackBinaryHeader());
        nameKey = unpacker.readPayload(unpacker.unpackBinaryHeader());
        valueKey = unpacker.readPayload(unpacker.unpackBinaryHeader());
        itemHmacKey = unpacker.readPayload(unpacker.unpackBinaryHeader());
        tagNameKey = unpacker.readPayload(unpacker.unpackBinaryHeader());
        tagValueKey = unpacker.readPayload(unpacker.unpackBinaryHeader());
        tagsHmacKey = unpacker.readPayload(unpacker.unpackBinaryHeader());
        
        unpacker.close();
        return this;
    }

    public byte[] getValueKey() {
        return valueKey;
    }

    public byte[] getTagNameKey() {
        return tagNameKey;
    }

    public byte[] getTagValueKey() {
        return tagValueKey;
    }

    public byte[] getNameKey() {
        return nameKey;
    }

    public byte[] getTypeKey() {
        return typeKey;
    }

    public byte[] getItemHmacKey() {
        return itemHmacKey;
    }

    public byte[] getTagsHmacKey() {
        return tagsHmacKey;
    }
    
}
