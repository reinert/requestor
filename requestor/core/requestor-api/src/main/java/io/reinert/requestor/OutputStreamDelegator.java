package io.reinert.requestor;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that will receive a delegatee in a posterior time to which it was created.
 *
 * @author Danilo Reinert
 */
class OutputStreamDelegator extends OutputStream {

    private OutputStream delegatee;

    public OutputStream getDelegatee() {
        return delegatee;
    }

    public void setDelegatee(OutputStream delegatee) {
        this.delegatee = delegatee;
    }

    @Override
    public void write(int b) throws IOException {
        delegatee.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        delegatee.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        delegatee.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        delegatee.flush();
    }

    @Override
    public void close() throws IOException {
        delegatee.close();
    }
}
