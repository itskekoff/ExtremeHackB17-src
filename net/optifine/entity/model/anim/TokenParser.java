package net.optifine.entity.model.anim;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import net.optifine.entity.model.anim.EnumTokenType;
import net.optifine.entity.model.anim.ParseException;
import net.optifine.entity.model.anim.Token;

public class TokenParser {
    public static Token[] parse(String str) throws IOException, ParseException {
        StringReader reader = new StringReader(str);
        PushbackReader pushbackreader = new PushbackReader(reader);
        ArrayList<Token> list = new ArrayList<Token>();
        while (true) {
            int i2;
            if ((i2 = pushbackreader.read()) < 0) {
                Token[] atoken = list.toArray(new Token[list.size()]);
                return atoken;
            }
            char c0 = (char)i2;
            if (Character.isWhitespace(c0)) continue;
            EnumTokenType enumtokentype = EnumTokenType.getTypeByFirstChar(c0);
            if (enumtokentype == null) {
                throw new ParseException("Invalid character: '" + c0 + "', in: " + str);
            }
            Token token = TokenParser.readToken(c0, enumtokentype, pushbackreader);
            list.add(token);
        }
    }

    private static Token readToken(char chFirst, EnumTokenType type, PushbackReader pr2) throws IOException {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(chFirst);
        while (type.getMaxLen() <= 0 || stringbuffer.length() < type.getMaxLen()) {
            int i2 = pr2.read();
            if (i2 < 0) break;
            char c0 = (char)i2;
            if (!type.hasChar(c0)) {
                pr2.unread(c0);
                break;
            }
            stringbuffer.append(c0);
        }
        return new Token(type, stringbuffer.toString());
    }
}

