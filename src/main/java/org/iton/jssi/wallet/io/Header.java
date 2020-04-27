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

import java.io.IOException;
import java.util.Date;
import org.iton.jssi.wallet.crypto.KeyDerivationData;
import org.iton.jssi.wallet.crypto.Method;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_CHACHA20POLY1305_IETF_NONCEBYTES;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ArrayValue;


/**
 *
 * @author ITON Solutions
 */
public class Header {
    
    public static final int CHUNK_SIZE = 1024;
    
    private KeyDerivationData data;
    private byte[] nonce = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_NONCEBYTES];
    private int chunkSize = CHUNK_SIZE;
    
    private Date date = new Date();
    private int version = 0;
    
    Header(){}
    
    byte[] serialize(KeyDerivationData data) throws IOException{
        
        this.data = data;
        
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        
        packer.packArrayHeader(3);
            packer.packArrayHeader(2);
                packer.packInt(data.getDerivationMethod().getId());
                packer.packArrayHeader(3);
                    packer.packArrayHeader(data.getSalt().length);
                    for(byte item : data.getSalt()){
                        packer.packInt(item & 0xFF);
                    }
                    packer.packArrayHeader(nonce.length);
                    for(byte item : nonce){
                         packer.packInt(item & 0xFF);
                    }
                    
                    packer.packInt(chunkSize);
       
            packer.packLong(date.getTime() / 1000);
            packer.packInt(version);    
        
        return packer.toByteArray();
    }
    
    Header deserialize(byte[] msg, String passphrase) throws IOException{
        
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(msg);
        unpacker.unpackArrayHeader();
            unpacker.unpackArrayHeader();

                Method method = Method.values()[unpacker.unpackInt()];
                
                unpacker.unpackArrayHeader();
                byte[] salt  = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES];
                toBytes(unpacker.unpackValue().asArrayValue(), salt);
                toBytes(unpacker.unpackValue().asArrayValue(), nonce);

                chunkSize = unpacker.unpackInt();
                data = new KeyDerivationData(passphrase, salt, method);
           

        date = new Date(unpacker.unpackInt() * 1000);
        version = unpacker.unpackInt();
        
        return this;
    }
    
    private void toBytes(ArrayValue src, byte[] dst){
        for(int i = 0; i < src.size(); i++){
            dst[i] = src.get(i).asNumberValue().toByte();
        }
    }

    public Method getMethod() {
        return data.getDerivationMethod();
    }


    public byte[] getNonce() {
        return nonce;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public Date getDate() {
        return date;
    }

    public int getVersion() {
        return version;
    }

    public KeyDerivationData getDerivationData() {
        return data;
    }
}
