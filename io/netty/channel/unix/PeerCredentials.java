package io.netty.channel.unix;

public final class PeerCredentials {
    private final int pid;
    private final int uid;
    private final int gid;

    PeerCredentials(int p2, int u2, int g2) {
        this.pid = p2;
        this.uid = u2;
        this.gid = g2;
    }

    public int pid() {
        return this.pid;
    }

    public int uid() {
        return this.uid;
    }

    public int gid() {
        return this.gid;
    }

    public String toString() {
        return "UserCredentials[pid=" + this.pid + "; uid=" + this.uid + "; gid=" + this.gid + "]";
    }
}

