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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.iton.jssi.wallet.crypto.Crypto;
import org.iton.jssi.wallet.crypto.Keys;
import org.iton.jssi.wallet.model.Item;
import org.libsodium.jni.SodiumException;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

/**
 *
 * @author ITON Solutions
 */
public class WalletRecord {
    
    private String name;
    private String value;
    private String type;
    private Map<String, String> tags = new HashMap<>();
    
    public WalletRecord(){}

    public WalletRecord(String type, String name , String value){
        this(type, name, value, null);
    }

    public WalletRecord(String type, String name, String value, Map<String, String> tags){
        this.type = type;
        this.name = name;
        this.value = value;
        this.tags = tags == null ? this.tags : tags;
    }

    public WalletRecord decrypt(final Item item, final Keys keys) throws SodiumException{
  
        name = new String(Crypto.decryptMerged(item.getName(), keys.getNameKey()));
        type = new String(Crypto.decryptMerged(item.getType(), keys.getTypeKey()));
        value = new ItemValue(item).decrypt(keys.getValueKey());
        
        ItemTags itemTags = new ItemTags(item);
        tags = itemTags.decrypt(keys.getTagNameKey(), keys.getTagValueKey());
        return this;
    }
    
    public Item encrypt(final Keys keys) throws SodiumException{
        
        byte[] encryptedType = type == null ? new byte[0]
                : Crypto.encryptAsSearchable(type.getBytes(), keys.getTypeKey(), keys.getItemHmacKey());
        byte[] encryptedName = name == null ? new byte[0]
                : Crypto.encryptAsSearchable(name.getBytes(), keys.getNameKey(), keys.getItemHmacKey());
        
        ItemValue itemValue = new ItemValue();
        byte[] encryptedValue = itemValue.encrypt(value.getBytes(), keys.getValueKey()).getValue();
        byte[] encryptedKey   = itemValue.getKey();
        
        Item item = new Item(encryptedType, encryptedName, encryptedValue, encryptedKey);
        ItemTags itemTags = new ItemTags();
        itemTags.encrypt(item, tags, keys.getTagNameKey(), keys.getTagValueKey(), keys.getTagsHmacKey());
        item.setEncrypted(itemTags.getEncrypted());
        item.setPlaintext(itemTags.getPlaintext());
        return item;
    }
    
    public WalletRecord deserialize(byte[] msg) throws IOException{
        
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(msg);
        unpacker.unpackArrayHeader();
            this.type  = unpacker.unpackString();
            this.name  = unpacker.unpackString();
            this.value = unpacker.unpackString();

            int size = unpacker.unpackMapHeader();
        
                while (size-- > 0) {
                    String key = unpacker.unpackString();
                    String tag = unpacker.unpackString();
                    tags.put(key, tag);
                }
        
        return this;
    }
    
    public byte[] serialize() throws IOException{
        
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(4);
            packer.packString(type);
            packer.packString(name);
            packer.packString(value);

            packer.packMapHeader(tags.size() );
            for(String key : tags.keySet()){
                packer.packString(key);
                packer.packString(tags.get(key));
            }
        return packer.toByteArray();
    }
    

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getTags() {
        return tags;
    }
}
