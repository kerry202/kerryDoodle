package com.locojoy.mydooble.utils;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @author: kerry
 * date: On $ {DATE}
 */
public class StrictLineReader {
    private static final byte CR = 13;
    private static final byte LF = 10;
    private final InputStream in;
    private final Charset charset;
    private byte[] buf;
    private int pos;
    private int end;

    public StrictLineReader(InputStream in, Charset charset) {
        this(in, 8192, charset);
    }

    public StrictLineReader(InputStream in, int capacity, Charset charset) {
        if (in != null && charset != null) {
            if (capacity < 0) {
                throw new IllegalArgumentException("capacity <= 0");
            } else if (!charset.equals(DiskLruUtil.US_ASCII)) {
                throw new IllegalArgumentException("Unsupported encoding");
            } else {
                this.in = in;
                this.charset = charset;
                this.buf = new byte[capacity];
            }
        } else {
            throw new NullPointerException();
        }
    }

    public void close() throws IOException {
        synchronized(this.in) {
            if (this.buf != null) {
                this.buf = null;
                this.in.close();
            }

        }
    }

    public String readLine() throws IOException {
        synchronized(this.in) {
            if (this.buf == null) {
                throw new IOException("LineReader is closed");
            } else {
                if (this.pos >= this.end) {
                    this.fillBuf();
                }

                for(int i = this.pos; i != this.end; ++i) {
                    if (this.buf[i] == 10) {
                        i = i != this.pos && this.buf[i - 1] == 13 ? i - 1 : i;
                        String res = new String(this.buf, this.pos, i - this.pos, this.charset.name());
                        this.pos = i + 1;
                        return res;
                    }
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream(this.end - this.pos + 80) {
                    @Override
                    public String toString() {
                        int length = this.count > 0 && this.buf[this.count - 1] == 13 ? this.count - 1 : this.count;

                        try {
                            return new String(this.buf, 0, length, StrictLineReader.this.charset.name());
                        } catch (UnsupportedEncodingException var3) {
                            throw new AssertionError(var3);
                        }
                    }
                };

                while(true) {
                    out.write(this.buf, this.pos, this.end - this.pos);
                    this.end = -1;
                    this.fillBuf();

                    int i;
                    for(i = this.pos; i != this.end; ++i) {
                        if (this.buf[i] == 10) {
                            if (i != this.pos) {
                                out.write(this.buf, this.pos, i - this.pos);
                            }

                            this.pos = i + 1;
                            return out.toString();
                        }
                    }
                }
            }
        }
    }

    public boolean hasUnterminatedLine() {
        return this.end == -1;
    }

    private void fillBuf() throws IOException {
        int result = this.in.read(this.buf, 0, this.buf.length);
        if (result == -1) {
            throw new EOFException();
        } else {
            this.pos = 0;
            this.end = result;
        }
    }
}
