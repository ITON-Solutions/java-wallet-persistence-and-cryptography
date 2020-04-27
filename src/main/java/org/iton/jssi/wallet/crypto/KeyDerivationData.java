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

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.libsodium.api.Crypto_randombytes;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES;
import static org.libsodium.jni.SodiumConstants.CRYPTO_PWHASH_ARGON2I_SALTBYTES;
import org.libsodium.jni.SodiumException;

/**
 *
 * @author ITON Solutions
 */
public class KeyDerivationData {
    
    private final String passphrase;
    private byte[] salt;
    private final Method method;
    
    
    public KeyDerivationData(final String passphrase, byte[] salt){
        this(passphrase, salt, Method.ARGON2I_MOD);
    }

    public KeyDerivationData(final String passphrase, byte[] salt, Method method){
        this.method = method;
        this.passphrase = passphrase;
        
        if(method != Method.RAW){
            this.salt = salt;
        }
    }
    
    public KeyDerivationData(final String passphrase) throws SodiumException{
        this(passphrase, Method.ARGON2I_MOD);
    }

    public KeyDerivationData(final String passphrase, final Method method) throws SodiumException{
        this.method = method;
        this.passphrase = passphrase;
        
        if(method != Method.RAW){
            salt = new byte[CRYPTO_PWHASH_ARGON2I_SALTBYTES];
            Crypto_randombytes.buf(salt);
        }
    }
    
    public KeyDerivationData(final String passphrase, final KeysMetadata metadata){
        this(passphrase, metadata, Method.ARGON2I_MOD);
    }
    
    private KeyDerivationData(final String passphrase, final KeysMetadata metadata, Method method){
        this.method = method;
        this.passphrase = passphrase;
        
        if(method != Method.RAW){
            salt = metadata.getMasterKeySalt();
        }
    }
    
    public byte[] deriveMasterKey() throws SodiumException{
        
        byte[] masterKey;
        
        if(method == Method.RAW){
            masterKey = Base58.decode(passphrase);
            
            if(masterKey.length != CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES){
                throw new AddressFormatException("Incorrect RAW passphrase");
            }
            
        } else {
            masterKey = Crypto.deriveKey(passphrase, salt, method);
        }
        return masterKey;
    }

    public Method getDerivationMethod() {
        return method;
    }

    public byte[] getSalt() {
        return salt;
    }
}
