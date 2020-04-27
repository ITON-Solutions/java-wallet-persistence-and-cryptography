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
package org.iton.jssi.wallet.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

@DatabaseTable(tableName = "items")
public class Item extends BaseDaoEnabled {

    @DatabaseField(columnName = "id", generatedId = true)
    private Integer id;
    @DatabaseField(columnName = "type", uniqueCombo=true, dataType = DataType.BYTE_ARRAY)
    private byte[] type;
    @DatabaseField(columnName = "name", uniqueCombo=true, dataType = DataType.BYTE_ARRAY)
    private byte[] name;
    @DatabaseField(columnName = "value", dataType = DataType.BYTE_ARRAY)
    private byte[] value;
    @DatabaseField(columnName = "key", dataType = DataType.BYTE_ARRAY)
    private byte[] key;
    @ForeignCollectionField(foreignFieldName = "item", eager = false)
    Collection<Plaintext> plaintext;
    @ForeignCollectionField(foreignFieldName = "item", eager = false)
    Collection<Encrypted> encrypted;

    public Item() {
    }

    public Item(Integer id) {
        this.id = id;
    }

    public Item(byte[] type, byte[] name, byte[] value, byte[] key) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.key = key;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getType() {
        return type;
    }

    public void setType(byte[] type) {
        this.type = type;
    }

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = name;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public Collection<Encrypted> getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(Collection<Encrypted> encrypted) {
        this.encrypted = encrypted;
    }

    public Collection<Plaintext> getPlaintext() {
        return plaintext;
    }

    public void setPlaintext(Collection<Plaintext> plaintext) {
        this.plaintext = plaintext;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "Item[ id=" + id + " ]";
    }
}
