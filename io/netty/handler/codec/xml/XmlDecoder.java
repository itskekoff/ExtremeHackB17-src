 * Could not load the following classes:
 *  com.fasterxml.aalto.AsyncByteArrayFeeder
 *  com.fasterxml.aalto.AsyncXMLInputFactory
 *  com.fasterxml.aalto.AsyncXMLStreamReader
 *  com.fasterxml.aalto.stax.InputFactoryImpl
 */
package io.netty.handler.codec.xml;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.xml.XmlAttribute;
import io.netty.handler.codec.xml.XmlCdata;
import io.netty.handler.codec.xml.XmlCharacters;
import io.netty.handler.codec.xml.XmlComment;
import io.netty.handler.codec.xml.XmlDTD;
import io.netty.handler.codec.xml.XmlDocumentEnd;
import io.netty.handler.codec.xml.XmlDocumentStart;
import io.netty.handler.codec.xml.XmlElementEnd;
import io.netty.handler.codec.xml.XmlElementStart;
import io.netty.handler.codec.xml.XmlEntityReference;
import io.netty.handler.codec.xml.XmlNamespace;
import io.netty.handler.codec.xml.XmlProcessingInstruction;
import io.netty.handler.codec.xml.XmlSpace;
import java.util.List;
import javax.xml.stream.XMLStreamException;

public class XmlDecoder
extends ByteToMessageDecoder {
    private static final AsyncXMLInputFactory XML_INPUT_FACTORY = new InputFactoryImpl();
    private static final XmlDocumentEnd XML_DOCUMENT_END = XmlDocumentEnd.INSTANCE;
    private final AsyncXMLStreamReader<AsyncByteArrayFeeder> streamReader = XML_INPUT_FACTORY.createAsyncForByteArray();
    private final AsyncByteArrayFeeder streamFeeder = (AsyncByteArrayFeeder)this.streamReader.getInputFeeder();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        byte[] buffer = new byte[in2.readableBytes()];
        in2.readBytes(buffer);
        try {
            this.streamFeeder.feedInput(buffer, 0, buffer.length);
        }
        catch (XMLStreamException exception) {
            in2.skipBytes(in2.readableBytes());
            throw exception;
        }
        while (!this.streamFeeder.needMoreInput()) {
            int type = this.streamReader.next();
            switch (type) {
                case 7: {
                    out.add(new XmlDocumentStart(this.streamReader.getEncoding(), this.streamReader.getVersion(), this.streamReader.isStandalone(), this.streamReader.getCharacterEncodingScheme()));
                    break;
                }
                case 8: {
                    out.add(XML_DOCUMENT_END);
                    break;
                }
                case 1: {
                    int x2;
                    XmlElementStart elementStart = new XmlElementStart(this.streamReader.getLocalName(), this.streamReader.getName().getNamespaceURI(), this.streamReader.getPrefix());
                    for (x2 = 0; x2 < this.streamReader.getAttributeCount(); ++x2) {
                        XmlAttribute attribute = new XmlAttribute(this.streamReader.getAttributeType(x2), this.streamReader.getAttributeLocalName(x2), this.streamReader.getAttributePrefix(x2), this.streamReader.getAttributeNamespace(x2), this.streamReader.getAttributeValue(x2));
                        elementStart.attributes().add(attribute);
                    }
                    for (x2 = 0; x2 < this.streamReader.getNamespaceCount(); ++x2) {
                        XmlNamespace namespace = new XmlNamespace(this.streamReader.getNamespacePrefix(x2), this.streamReader.getNamespaceURI(x2));
                        elementStart.namespaces().add(namespace);
                    }
                    out.add(elementStart);
                    break;
                }
                case 2: {
                    XmlElementEnd elementEnd = new XmlElementEnd(this.streamReader.getLocalName(), this.streamReader.getName().getNamespaceURI(), this.streamReader.getPrefix());
                    for (int x3 = 0; x3 < this.streamReader.getNamespaceCount(); ++x3) {
                        XmlNamespace namespace = new XmlNamespace(this.streamReader.getNamespacePrefix(x3), this.streamReader.getNamespaceURI(x3));
                        elementEnd.namespaces().add(namespace);
                    }
                    out.add(elementEnd);
                    break;
                }
                case 3: {
                    out.add(new XmlProcessingInstruction(this.streamReader.getPIData(), this.streamReader.getPITarget()));
                    break;
                }
                case 4: {
                    out.add(new XmlCharacters(this.streamReader.getText()));
                    break;
                }
                case 5: {
                    out.add(new XmlComment(this.streamReader.getText()));
                    break;
                }
                case 6: {
                    out.add(new XmlSpace(this.streamReader.getText()));
                    break;
                }
                case 9: {
                    out.add(new XmlEntityReference(this.streamReader.getLocalName(), this.streamReader.getText()));
                    break;
                }
                case 11: {
                    out.add(new XmlDTD(this.streamReader.getText()));
                    break;
                }
                case 12: {
                    out.add(new XmlCdata(this.streamReader.getText()));
                }
            }
        }
    }
}

