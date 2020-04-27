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

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Arrays;

public class Tag extends BaseDaoEnabled {


    @DatabaseField(columnName = "name", uniqueCombo=true, dataType = DataType.BYTE_ARRAY)
    byte[] name;
    @DatabaseField(columnName = "value", uniqueCombo=true, dataType = DataType.BYTE_ARRAY)
    byte[] value;
    @DatabaseField(columnName = "item_id",
            foreign = true,
            foreignAutoRefresh = true,
            foreignAutoCreate = true,
            canBeNull = false,
            index = true,
            columnDefinition = "INTEGER CONSTRAINT item_id REFERENCES items(id) ON DELETE CASCADE")
    Item item;

    public Tag() {
    }

    public Tag(Item item, byte[] name, byte[] value) {
        this.item = item;
        this.name = name;
        this.value = value;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (item != null ? item.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) object;
        if ((this.item == null && other.item != null)
                || (this.item != null && !this.item.equals(other.item))
                || !Arrays.equals(this.name, other.name)
                || !Arrays.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
}
