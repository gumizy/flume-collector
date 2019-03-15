package com.datacloudsec.source.netflow.bean;

import com.datacloudsec.config.tools.IPv4Kit;

public class Address {

    public long address;

    public Address(long address) {
        this.address = address;
    }

    public String toString() {
        return IPv4Kit.long2ip(address);
    }

    public boolean equals(Address o) {
        return address == o.address;
    }
}
